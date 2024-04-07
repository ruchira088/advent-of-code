package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

import static com.ruchij.twentytwentythree.DaySeventeen.Direction.*;

public class DaySeventeen implements JavaSolution {
    record Coordinate(int x, int y) {
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    record Position(Coordinate coordinate, Direction direction) {
        public Position(int x, int y, Direction direction) {
            this(new Coordinate(x, y), direction);
        }
    }

    record QueueEntry(Position position, int heatLoss, LinkedList<Direction> lastDirections) {
    }

    @Override
    public Object solve(Stream<String> input) {
        Map<Coordinate, Integer> grid = parse(input);

        Comparator<Coordinate> comparatorX = Comparator.comparingInt(Coordinate::x);
        Comparator<Coordinate> comparatorY = Comparator.comparingInt(Coordinate::y);
        Coordinate destination =
                grid.keySet()
                        .stream()
                        .sorted(comparatorX.thenComparing(comparatorY).reversed())
                        .findFirst()
                        .orElseThrow();

        HashSet<Position> visited = new HashSet<>();
        PriorityQueue<QueueEntry> priorityQueue = new PriorityQueue<>(Comparator.comparing(queueEntry -> queueEntry.heatLoss));
        priorityQueue.add(new QueueEntry(new Position(new Coordinate(0, 0), DOWN), 0, new LinkedList<>()));

        while (!priorityQueue.isEmpty()) {
            QueueEntry queueEntry = priorityQueue.poll();

            if (queueEntry.position.coordinate.equals(destination)) {
                return queueEntry.heatLoss;
            }

            if (!visited.contains(queueEntry.position)) {
                visited.add(queueEntry.position);
                List<Position> nextPositions = getNextPositions(queueEntry);

                for (Position nextPosition : nextPositions) {
                    Integer heatLoss = grid.get(nextPosition.coordinate);

                    if (heatLoss != null) {
                        if (queueEntry.lastDirections.size() == 3) {
                            boolean isSameDirection =
                                    queueEntry.lastDirections.stream()
                                            .allMatch(direction -> direction == nextPosition.direction);

                            if (!isSameDirection) {
                                LinkedList<Direction> lastDirections = new LinkedList<>(queueEntry.lastDirections);
                                lastDirections.removeFirst();
                                lastDirections.add(nextPosition.direction);

                                priorityQueue.add(
                                        new QueueEntry(nextPosition, queueEntry.heatLoss + heatLoss, lastDirections)
                                );
                            }
                        } else {
                            LinkedList<Direction> lastDirections = new LinkedList<>(queueEntry.lastDirections);
                            lastDirections.add(nextPosition.direction);

                            priorityQueue.add(
                                    new QueueEntry(nextPosition, queueEntry.heatLoss + heatLoss, lastDirections)
                            );
                        }
                    }
                }
            }
        }

        throw new IllegalStateException("Unable to reach to destination");
    }

    private List<Position> getNextPositions(QueueEntry queueEntry) {
        Coordinate coordinate = queueEntry.position.coordinate;
        int x = coordinate.x;
        int y = coordinate.y;

        List<Position> nextPositions =
                switch (queueEntry.position.direction) {
                    case UP -> List.of(
                            new Position(x, y - 1, UP),
                            new Position(x + 1, y, RIGHT),
                            new Position(x - 1, y, LEFT)
                    );

                    case DOWN -> List.of(
                            new Position(x, y + 1, DOWN),
                            new Position(x + 1, y, RIGHT),
                            new Position(x - 1, y, LEFT)
                    );

                    case LEFT -> List.of(
                            new Position(x - 1, y, LEFT),
                            new Position(x, y - 1, UP),
                            new Position(x, y + 1, DOWN)
                    );

                    case RIGHT -> List.of(
                            new Position(x + 1, y, RIGHT),
                            new Position(x, y - 1, UP),
                            new Position(x, y + 1, DOWN)
                    );
                };

        return nextPositions;
    }

    Map<Coordinate, Integer> parse(Stream<String> input) {
        HashMap<Coordinate, Integer> grid = new HashMap<>();
        List<String> lines = input.toList();

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            char[] charArray = line.toCharArray();

            for (int x = 0; x < charArray.length; x++) {
                Coordinate coordinate = new Coordinate(x, y);
                int heatLoss = Integer.parseInt(String.valueOf(charArray[x]));

                grid.put(coordinate, heatLoss);
            }
        }

        return grid;
    }
}
