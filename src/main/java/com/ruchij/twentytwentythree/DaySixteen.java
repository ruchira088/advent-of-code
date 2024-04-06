package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ruchij.twentytwentythree.DaySixteen.Direction.*;

public class DaySixteen implements JavaSolution {
    enum Tile {
        EMPTY('.'),
        FORWARD_MIRROR('/'),
        BACKWARD_MIRROR('\\'),
        VERTICAL_SPLITTER('|'),
        HORIZONTAL_SPLITTER('-');

        private final char symbol;

        Tile(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        public static Tile fromSymbol(char symbol) {
            return Arrays.stream(Tile.values())
                    .filter(tile -> tile.symbol == symbol)
                    .findFirst()
                    .orElseThrow();
        }
    }

    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    record Coordinate(int x, int y) {
    }

    record Position(Coordinate coordinate, Direction direction) {
    }

    @Override
    public Object solve(Stream<String> input) {
        Map<Coordinate, Tile> grid = parse(input);
        Position startingPosition = new Position(new Coordinate(0, 0), RIGHT);
        HashSet<Position> visited = new HashSet<>();

        Comparator<Coordinate> comparatorX = Comparator.comparing(Coordinate::x);
        Comparator<Coordinate> comparatorY = Comparator.comparing(Coordinate::y);

        Coordinate dimensions =
                grid.keySet().stream().sorted(comparatorX.thenComparing(comparatorY).reversed()).findFirst().orElseThrow();

        Stack<Position> positions = new Stack<>();
        positions.add(startingPosition);

        while (!positions.empty()) {
            Position position = positions.pop();

            if (!visited.contains(position)) {
                visited.add(position);

                List<Position> nextPositions = next(position, grid);
                List<Position> validPositions = nextPositions.stream()
                        .filter(currentPosition ->
                                currentPosition.coordinate.x <= dimensions.x && currentPosition.coordinate.x >= 0
                                        && currentPosition.coordinate.y <= dimensions.y && currentPosition.coordinate.y >= 0
                        )
                        .toList();

                positions.addAll(validPositions);
            }
        }

        Set<Coordinate> visitedCoordinates =
                visited.stream().map(position -> position.coordinate).collect(Collectors.toSet());

        return visitedCoordinates.size();
    }

    List<Position> next(Position position, Map<Coordinate, Tile> grid) {
        Coordinate coordinate = position.coordinate;
        Direction direction = position.direction;

        int x = coordinate.x;
        int y = coordinate.y;

        Tile tile = grid.get(coordinate);

        if (tile != null) {
            return switch (tile) {
                case EMPTY -> {
                    Coordinate nextCoordinate =
                            switch (direction) {
                                case UP -> new Coordinate(x, y - 1);
                                case DOWN -> new Coordinate(x, y + 1);
                                case LEFT -> new Coordinate(x - 1, y);
                                case RIGHT -> new Coordinate(x + 1, y);
                            };

                    yield List.of(new Position(nextCoordinate, direction));
                }

                case FORWARD_MIRROR -> {
                    Position nextPosition =
                            switch (direction) {
                                case UP -> new Position(new Coordinate(x + 1, y), RIGHT);
                                case DOWN -> new Position(new Coordinate(x - 1, y), LEFT);
                                case LEFT -> new Position(new Coordinate(x, y + 1), DOWN);
                                case RIGHT -> new Position(new Coordinate(x, y - 1), UP);
                            };

                    yield List.of(nextPosition);
                }

                case BACKWARD_MIRROR -> {
                    Position nextPosition =
                            switch (direction) {
                                case UP -> new Position(new Coordinate(x - 1, y), LEFT);
                                case DOWN -> new Position(new Coordinate(x + 1, y), RIGHT);
                                case LEFT -> new Position(new Coordinate(x, y - 1), UP);
                                case RIGHT -> new Position(new Coordinate(x, y + 1), DOWN);
                            };

                    yield List.of(nextPosition);
                }

                case VERTICAL_SPLITTER -> switch (direction) {
                    case UP -> List.of(new Position(new Coordinate(x, y - 1), UP));
                    case DOWN -> List.of(new Position(new Coordinate(x, y + 1), DOWN));
                    case LEFT, RIGHT -> List.of(
                            new Position(new Coordinate(x, y - 1), UP),
                            new Position(new Coordinate(x, y + 1), DOWN)
                    );
                };

                case HORIZONTAL_SPLITTER -> switch (direction) {
                    case LEFT -> List.of(new Position(new Coordinate(x - 1, y), LEFT));
                    case RIGHT -> List.of(new Position(new Coordinate(x + 1, y), RIGHT));
                    case UP, DOWN -> List.of(
                            new Position(new Coordinate(x - 1, y), LEFT),
                            new Position(new Coordinate(x + 1, y), RIGHT)
                    );
                };

            };
        } else {
            throw new IllegalStateException("%s is not a valid coordinate".formatted(coordinate));
        }
    }

    Map<Coordinate, Tile> parse(Stream<String> input) {
        Map<Coordinate, Tile> map = new HashMap<>();

        List<String> lines = input.toList();

        for (int y = 0; y < lines.size(); y++) {
            char[] charArray = lines.get(y).toCharArray();

            for (int x = 0; x < charArray.length; x++) {
                Coordinate coordinate = new Coordinate(x, y);
                Tile tile = Tile.fromSymbol(charArray[x]);
                map.put(coordinate, tile);
            }
        }

        return map;
    }
}
