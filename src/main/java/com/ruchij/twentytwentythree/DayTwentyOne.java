package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayTwentyOne implements JavaSolution {
    enum Cell {
        STARTING_POSITION('S'), GARDEN_PLOT('.'), ROCK('#');

        private final char symbol;

        Cell(char symbol) {
            this.symbol = symbol;
        }

        static Cell fromSymbol(char symbol) {
            return Arrays.stream(Cell.values()).filter(cell -> cell.symbol == symbol).findAny().orElseThrow();
        }
    }

    record Coordinate(int x, int y) {}
    record Position(int stepCount, Coordinate coordinate) {}

    @Override
    public Object solve(Stream<String> input) {
        int stepTarget = 64;
        Map<Coordinate, Cell> grid = parseInput(input);

        Coordinate startingPosition = grid.entrySet().stream()
                .filter(entry -> entry.getValue() == Cell.STARTING_POSITION)
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow();

        ArrayDeque<Position> queue = new ArrayDeque<>();
        queue.add(new Position(0, startingPosition));
        HashSet<Position> visited = new HashSet<>();
        HashSet<Coordinate> reached  = new HashSet<>();

        while (!queue.isEmpty()) {
            Position currentPosition = queue.remove();

            if (!visited.contains(currentPosition)) {
                visited.add(currentPosition);

                if (currentPosition.stepCount < stepTarget) {
                    for (Coordinate next : next(currentPosition.coordinate, grid)) {
                        queue.add(new Position(currentPosition.stepCount + 1, next));
                    }
                } else if (currentPosition.stepCount == stepTarget) {
                    reached.add(currentPosition.coordinate);
                }
            }
        }

        return reached.size();
    }

    Set<Coordinate> next(Coordinate coordinate, Map<Coordinate, Cell> grid) {
        int x = coordinate.x;
        int y = coordinate.y;

        Set<Coordinate> coordinates = Stream.of(
                new Coordinate(x + 1, y),
                new Coordinate(x - 1, y),
                new Coordinate(x, y - 1),
                new Coordinate(x, y + 1)
        ).filter(current -> {
            Cell cell = grid.get(current);
            return cell == Cell.STARTING_POSITION || cell == Cell.GARDEN_PLOT;
        }).collect(Collectors.toSet());

        return coordinates;
    }

    Map<Coordinate, Cell> parseInput(Stream<String> input) {
        List<String> lines = input.toList();
        int rowCount = lines.size();

        HashMap<Coordinate, Cell> grid = new HashMap<>();

        for (int y = 0; y < rowCount; y++) {
            char[] line = lines.get(y).toCharArray();

            for (int x = 0; x < line.length; x++) {
                char symbol = line[x];
                Cell cell = Cell.fromSymbol(symbol);

                Coordinate coordinate = new Coordinate(x, y);
                grid.put(coordinate, cell);
            }
        }

        return grid;
    }

}
