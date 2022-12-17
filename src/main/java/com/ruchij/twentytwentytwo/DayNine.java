package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class DayNine implements JavaSolution {
    record Coordinate(int x, int y) {
    }

    enum Direction {
        UP("U"), DOWN("D"), LEFT("L"), RIGHT("R");

        private final String symbol;

        Direction(String symbol) {
            this.symbol = symbol;
        }

        static Direction parse(String input) {
            for (Direction direction : Direction.values()) {
                if (direction.symbol.equalsIgnoreCase(input)) {
                    return direction;
                }
            }

            throw new IllegalArgumentException("Unable to parse '%s' as Direction".formatted(input));
        }
    }

    record Instruction(Direction direction, int steps) {
        static Instruction parse(String input) {
            String[] strings = input.split(" ");

            if (strings.length != 2) {
                throw new IllegalArgumentException("Invalid input: %s".formatted(input));
            } else {
                return new Instruction(Direction.parse(strings[0]), Integer.parseInt(strings[1]));
            }
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        int knotCount = 10;
        List<Instruction> instructions = parse(input);

        ArrayList<Coordinate> knots = new ArrayList<>();

        for (int i = 0; i < knotCount; i++) {
            knots.add(new Coordinate(0, 0));
        }

        HashSet<Coordinate> visited = new HashSet<>();
        visited.add(knots.get(knotCount - 1));

        for (Instruction instruction : instructions) {
            for (int i = 0; i < instruction.steps; i++) {
                knots.set(0, next(knots.get(0), instruction.direction));

                for (int j = 0; j < knotCount - 1; j++) {
                    Coordinate head = knots.get(j);
                    Coordinate tail = knots.get(j + 1);

                    knots.set(j + 1, tailMove(head, tail));
                }

                visited.add(knots.get(knotCount - 1));
            }
        }

        return visited.size();
    }

    Coordinate tailMove(Coordinate head, Coordinate tail) {
        int xOffset = head.x - tail.x;
        int yOffset = head.y - tail.y;

        int xOffsetAbs = Math.abs(xOffset);
        int yOffsetAbs = Math.abs(yOffset);

        if (xOffsetAbs <= 1 && yOffsetAbs <= 1) {
            return tail;
        } else if (xOffsetAbs >= 1 && yOffsetAbs >= 1 && (xOffsetAbs + yOffsetAbs >= 3)) {
            return new Coordinate(tail.x + (xOffset > 0 ? 1 : -1), tail.y + (yOffset > 0 ? 1 : -1));
        } else if (xOffsetAbs == 2) {
            return new Coordinate(tail.x + (xOffset > 0 ? 1 : -1), tail.y);
        } else if (yOffsetAbs == 2){
            return new Coordinate(tail.x, tail.y + (yOffset > 0 ? 1 : -1));
        }

        throw new IllegalArgumentException("head=%s and tail=%s too far apart".formatted(head, tail));
    }

    Coordinate next(Coordinate coordinate, Direction direction) {
        return switch (direction) {
            case UP -> new Coordinate(coordinate.x, coordinate.y + 1);
            case DOWN -> new Coordinate(coordinate.x, coordinate.y - 1);
            case LEFT -> new Coordinate(coordinate.x - 1, coordinate.y);
            case RIGHT -> new Coordinate(coordinate.x + 1, coordinate.y);
        };
    }

    List<Instruction> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        ArrayList<Instruction> instructions = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();

            instructions.add(Instruction.parse(line));
        }

        return instructions;
    }
}
