package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DaySeventeen implements JavaSolution {
    record Coordinate(int x, int y) {
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

        public String print() {
            StringBuilder stringBuilder = new StringBuilder();

            for (int y = highest; y > 0; y--) {
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
        Direction[] directions = parse(input).toArray(new Direction[]{});

        Chamber chamber = new Chamber(new HashSet<>());
        Shape shape = Shape.MINUS;
        int index = 0;

        for (int i = 0; i < 2022; i++) {
            index = move(chamber, directions, index, shape);
            shape = shape.next();
        }

        return chamber.highest;
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
