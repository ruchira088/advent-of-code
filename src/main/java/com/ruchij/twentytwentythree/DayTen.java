package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayTen implements JavaSolution {
    record Coordinate(int x, int y) {}
    record Entry(Coordinate coordinate, int steps) {}

    enum Direction {
        NORTH, WEST, SOUTH, EAST
    }


    enum Pipe {
        VERTICAL('|'),
        HORIZONTAL('-'),
        L_SHAPE('L'),
        J_SHAPE('J'),
        SEVEN_SHAPE('7'),
        F_SHAPE('F'),
        GROUND('.'),
        STARTING_POSITION('S');

        private final char symbol;

        Pipe(char symbol) {
            this.symbol = symbol;
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        Map<Coordinate, Pipe> pipeMap = parse(input);
        Coordinate startingPosition = startingPosition(pipeMap);

        Set<Coordinate> visited = new HashSet<>();
        ArrayDeque<Entry> queue = new ArrayDeque<>();
        int max = 0;
        queue.add(new Entry(startingPosition, 0));

        while (!queue.isEmpty()) {
            Entry entry = queue.poll();
            Coordinate coordinate = entry.coordinate;

            if (!visited.contains(coordinate)) {
                max = Math.max(max, entry.steps);
                visited.add(coordinate);

                List<Coordinate> nextCoordinates = next(coordinate, pipeMap.get(coordinate));

                for (Coordinate next : nextCoordinates) {
                    queue.add(new Entry(next, entry.steps + 1));
                }
            }
        }

        return max;
    }

    private List<Coordinate> next(Coordinate position, Pipe pipe) {
        int x = position.x;
        int y = position.y;

        return switch (pipe) {
            case GROUND -> List.of(position);
            case VERTICAL -> List.of(new Coordinate(x, y + 1), new Coordinate(x, y - 1));
            case HORIZONTAL -> List.of(new Coordinate(x + 1, y), new Coordinate(x - 1, y));
            case L_SHAPE -> List.of(new Coordinate(x, y - 1), new Coordinate(x + 1, y));
            case J_SHAPE -> List.of(new Coordinate(x - 1, y), new Coordinate(x, y - 1));
            case SEVEN_SHAPE -> List.of(new Coordinate(x - 1, y), new Coordinate(x, y + 1));
            case F_SHAPE -> List.of(new Coordinate(x + 1, y), new Coordinate(x, y + 1));
            case STARTING_POSITION -> next(position, Pipe.L_SHAPE);
        };
    }

    private Coordinate startingPosition(Map<Coordinate, Pipe> pipeMap) {
        return pipeMap.entrySet().stream().filter(entry -> entry.getValue() == Pipe.STARTING_POSITION).findAny().orElseThrow().getKey();
    }

    private Map<Coordinate, Pipe> parse(Stream<String> input) {
        HashMap<Coordinate, Pipe> pipeHashMap = new HashMap<>();
        List<String> lines = input.toList();

        for (int y = 0; y < lines.size(); y++) {
            char[] chars = lines.get(y).toCharArray();

            for (int x = 0; x < chars.length; x++) {
                int i = x;
                Coordinate coordinate = new Coordinate(x, y);
                Pipe pipe = Arrays.stream(Pipe.values()).filter(value -> value.symbol == chars[i]).findAny().orElseThrow();
                pipeHashMap.put(coordinate, pipe);
            }
        }

        return pipeHashMap;
    }
}
