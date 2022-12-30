package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class DaySeventeen implements JavaSolution {
    record Coordinate(int x, int y) {
    }

    record Pair(int rock, int height) {
        @Override
        public String toString() {
            return rock + "->" + height;
        }
    }

    class Chamber {
        private final Set<Coordinate> grid;
        private int highest = 0;

        Chamber(Set<Coordinate> grid) {
            this.grid = grid;
        }

        boolean isValid(Coordinate position) {
            return position.x > 0 && position.x < 8 && position.y > 0 && !grid.contains(position);
        }

        void add(Set<Coordinate> coordinates) {
            for (Coordinate coordinate : coordinates) {
                if (coordinate.y > highest) {
                    highest = coordinate.y;
                }

                grid.add(coordinate);
            }
        }

        public int highest() {
            return highest;
        }

        @Override
        public String toString() {
            return "Chamber[" +
                    "grid=" + grid + ", " +
                    "highest=" + highest + ']';
        }

        public String print(int rows) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int y = highest; y > 0 && y > highest - rows; y--) {
                for (int x = 1; x < 8; x++) {
                    if (grid.contains(new Coordinate(x, y))) {
                        stringBuilder.append("#");
                    } else {
                        stringBuilder.append(".");
                    }
                }

                stringBuilder.append("\n");
            }

            return stringBuilder.toString();
        }
    }


    enum Direction {
        LEFT('<'), RIGHT('>');

        private final char symbol;

        Direction(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    enum Shape {
        MINUS, PLUS, REVERSE_L, LINE, SQUARE;

        Shape next() {
            return switch (this) {
                case MINUS -> PLUS;
                case PLUS -> REVERSE_L;
                case REVERSE_L -> LINE;
                case LINE -> SQUARE;
                case SQUARE -> MINUS;
            };
        }
    }


    @Override
    public Object solve(Stream<String> input) {
        Instant startTime = Instant.now();
        Direction[] directions = parse(input).toArray(new Direction[]{});

        Map<Integer, Map<Shape, List<Pair>>> state = new HashMap<>();
        Chamber chamber = new Chamber(new HashSet<>());
        Shape shape = Shape.MINUS;
        int index = 0;
        long limit = 1_000_000_000_000L;

        System.out.println("Direction count: %s".formatted(directions.length));

        for (int i = 0; i < directions.length * 5; i++) {
            Map<Shape, List<Pair>> indexMapping = state.getOrDefault(index, new HashMap<>());
            List<Pair> chambers = indexMapping.getOrDefault(shape, new ArrayList<>());
            chambers.add(new Pair(i, chamber.highest));

            indexMapping.put(shape, chambers);
            state.put(index, indexMapping);

            index = move(chamber, directions, index, shape);
            shape = shape.next();
        }

        List<Pair> pairs = state.values().stream()
                .flatMap(map -> map.values().stream())
                .filter(strings -> strings.size() > 1)
                .flatMap(Collection::stream)
                .toList();

        var first = pairs.get(0);
        var second = pairs.get(1);

        var start = pairs.stream().min(Comparator.comparing(Pair::rock)).orElseThrow();

        var interval = second.rock - first.rock;
        var intervalHeight = second.height - first.height;

        var periods = (limit - start.rock) / interval;
        var remaining = limit - periods * interval - start.rock;

        Optional<Pair> rem = pairs.stream()
                .filter(pair -> pair.rock == start.rock + remaining)
                .findFirst();

        System.out.println("Start: %s, Interval: %s, Height: %s".formatted(start, interval, intervalHeight));

        System.out.println("Total Duration: %sms".formatted(Instant.now().toEpochMilli() - startTime.toEpochMilli()));

        return (long) rem.map(Pair::height).orElseThrow() + intervalHeight * periods;
    }

    Set<Coordinate> coordinates(Shape shape, Coordinate coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;

        return switch (shape) {
            case MINUS -> Set.of(
                    coordinate,
                    new Coordinate(x + 1, y),
                    new Coordinate(x + 2, y),
                    new Coordinate(x + 3, y)
            );

            case SQUARE -> Set.of(
                    coordinate,
                    new Coordinate(x + 1, y),
                    new Coordinate(x, y + 1),
                    new Coordinate(x + 1, y + 1)
            );

            case LINE -> Set.of(
                    coordinate,
                    new Coordinate(x, y + 1),
                    new Coordinate(x, y + 2),
                    new Coordinate(x, y + 3)
            );

            case REVERSE_L -> Set.of(
                    coordinate,
                    new Coordinate(x + 1, y),
                    new Coordinate(x + 2, y),
                    new Coordinate(x + 2, y + 1),
                    new Coordinate(x + 2, y + 2)
            );

            case PLUS -> Set.of(
                    coordinate,
                    new Coordinate(x + 1, y),
                    new Coordinate(x + 2, y),
                    new Coordinate(x + 1, y + 1),
                    new Coordinate(x + 1, y - 1)
            );
        };
    }

    int move(Chamber chamber, Direction[] directions, int index, Shape shape) {
        Coordinate position = new Coordinate(3, chamber.highest() + 4 + (shape == Shape.PLUS ? 1 : 0));

        while (true) {
            Direction direction = directions[index];
            index++;
            index = index % directions.length;

            int x = position.x;
            int y = position.y;

            Coordinate afterWind = new Coordinate(x + (direction == Direction.LEFT ? -1 : 1), y);

            if (isValid(chamber, shape, afterWind)) {
                position = afterWind;
            }

            Coordinate afterDrop = new Coordinate(position.x, position.y - 1);

            if (!isValid(chamber, shape, afterDrop)) {
                chamber.add(coordinates(shape, position));

                return index;
            } else {
                position = afterDrop;
            }
        }
    }

    boolean isValid(Chamber chamber, Shape shape, Coordinate coordinate) {
        for (Coordinate position : coordinates(shape, coordinate)) {
            if (!chamber.isValid(position)) {
                return false;
            }
        }

        return true;
    }

    List<Direction> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        ArrayList<Direction> directions = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();

            for (char character : line.toCharArray()) {
                if (Direction.LEFT.symbol == character) {
                    directions.add(Direction.LEFT);
                } else if (Direction.RIGHT.symbol == character) {
                    directions.add(Direction.RIGHT);
                } else {
                    throw new IllegalArgumentException("Unable to parse '%s'".formatted(line));
                }
            }
        }

        return directions;
    }
}
