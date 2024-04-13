package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayEighteen implements JavaSolution {
    enum Direction {
        UP('U'), DOWN('D'), LEFT('L'), RIGHT('R');

        private final char symbol;

        Direction(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        static Direction fromSymbol(char symbol) {
            return Arrays.stream(values())
                    .filter(direction -> direction.symbol == symbol)
                    .findFirst()
                    .orElseThrow();
        }
    }

    record InputInstruction(Direction direction, int meters, String color) {
        Instruction simple() {
            return new Instruction(direction, meters);
        }
    }

    record Instruction(Direction direction, int meters) {}

    record Coordinate(int x, int y) {
    }

    @Override
    public Object solve(Stream<String> input) {
        List<Instruction> instructions =
                input.map(this::parse)
                        .map(inputInstruction -> hexToInstruction(inputInstruction.color))
//                        .map(InputInstruction::simple)
                        .toList();

        List<Coordinate> holes = dig(instructions);
        HashSet<Coordinate> boundary = new HashSet<>(holes);

        Coordinate start = new Coordinate(1, 1);
        ArrayDeque<Coordinate> queue = new ArrayDeque<>();
        queue.add(start);

        HashSet<Coordinate> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            Coordinate position = queue.pop();

            if (!visited.contains(position)) {
                visited.add(position);

                int x = position.x;
                int y = position.y;

                Stream.of(
                        new Coordinate(x, y + 1),
                        new Coordinate(x, y - 1),
                        new Coordinate(x + 1, y),
                        new Coordinate(x - 1, y)
                )
                        .filter(coordinate -> !boundary.contains(coordinate))
                        .filter(coordinate -> !visited.contains(coordinate))
                        .forEach(queue::add);
            }
        }

        return boundary.size() + visited.size();
    }

    Instruction hexToInstruction(String hex) {
        int digits = HexFormat.fromHexDigits(hex.substring(0, 5));

        Direction direction =
                switch (hex.charAt(5)) {
                    case '0' -> Direction.RIGHT;
                    case '1' -> Direction.DOWN;
                    case '2' -> Direction.LEFT;
                    case '3' -> Direction.UP;
                    default -> throw new IllegalStateException("Unexpected value: " + hex.charAt(5));
                };

        Instruction instruction = new Instruction(direction, digits);

        return instruction;
    }

    void print(HashSet<Coordinate> coordinates) {
        Comparator<Coordinate> sortX = Comparator.comparing(Coordinate::x);
        Comparator<Coordinate> sortY = Comparator.comparing(Coordinate::y);

        int maxX = coordinates.stream().max(sortX).get().x;
        int maxY = coordinates.stream().max(sortY).get().y;

        System.out.println(maxX);
        System.out.println(maxY);

        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                Coordinate coordinate = new Coordinate(x, y);

                if (coordinates.contains(coordinate)) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }

            System.out.println();
        }
    }

    InputInstruction parse(String line) {
        String[] terms = line.trim().split(" ");
        Direction direction = Direction.fromSymbol(terms[0].charAt(0));
        int meters = Integer.parseInt(terms[1]);
        String color = terms[2].substring(2, terms[2].length() - 1);

        return new InputInstruction(direction, meters, color);
    }

    List<Coordinate> dig(List<Instruction> inputInstructions) {
        ArrayList<Coordinate> path = new ArrayList<>();
        Coordinate position = new Coordinate(0, 0);

        for (Instruction instruction : inputInstructions) {
            for (int step = 0; step < instruction.meters; step++) {
                int x = position.x;
                int y = position.y;

                position =
                        switch (instruction.direction) {
                            case UP -> new Coordinate(x, y - 1);
                            case DOWN -> new Coordinate(x, y + 1);
                            case LEFT -> new Coordinate(x - 1, y);
                            case RIGHT -> new Coordinate(x + 1, y);
                        };

                path.add(position);
            }
        }

        return path;
    }
}
