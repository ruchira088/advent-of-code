package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

import static com.ruchij.twentytwentythree.DaySeventeen.Direction.*;

public class DaySeventeen implements JavaSolution {
    record Coordinate(int x, int y) {
    }

    enum Direction {
        UP('^'), DOWN('v'), LEFT('<'), RIGHT('>');

        private final char symbol;

        Direction(char symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return String.valueOf(symbol);
        }
    }

    record Position(Coordinate coordinate, Direction direction) {
        public Position(int x, int y, Direction direction) {
            this(new Coordinate(x, y), direction);
        }
    }

    record Visited(Position position, int count) {}

    record QueueEntry(Position position, int heatLoss, int count) {
    }

    String stringify(Map<Coordinate, Integer> grid, List<Position> path, Coordinate dimensions) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<Coordinate, String> map = new HashMap<>();

        for (Map.Entry<Coordinate, Integer> entry : grid.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toString());
        }

        for (Position position : path) {
            map.put(position.coordinate, position.direction.toString());
        }

        for (int y = 0; y <= dimensions.y; y++) {
            for (int x = 0; x <= dimensions.x; x++) {
                stringBuilder.append(map.get(new Coordinate(x, y)));
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
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

        Set<Visited> visited = new HashSet<>();
        PriorityQueue<QueueEntry> priorityQueue = new PriorityQueue<>(Comparator.comparing(queueEntry -> queueEntry.heatLoss));
        priorityQueue.add(new QueueEntry(new Position(new Coordinate(0, 0), DOWN), 0, 0));
        priorityQueue.add(new QueueEntry(new Position(new Coordinate(0, 0), RIGHT), 0, 0));

        while (!priorityQueue.isEmpty()) {
            QueueEntry queueEntry = priorityQueue.poll();

            if (queueEntry.position.coordinate.equals(destination)) {
                return queueEntry.heatLoss;
            }

            Visited visit = new Visited(queueEntry.position, queueEntry.count);

            if (!visited.contains(visit)) {
                visited.add(visit);
                List<Position> nextPositions = getNextPositions(queueEntry);

                for (Position nextPosition : nextPositions) {
                    Integer heatLoss = grid.get(nextPosition.coordinate);

                    if (heatLoss != null) {
                        boolean sameDirection = queueEntry.position.direction == nextPosition.direction;

                        if (queueEntry.count < 4) {
                            if (sameDirection) {
                                priorityQueue.add(
                                        new QueueEntry(
                                                nextPosition,
                                                queueEntry.heatLoss + heatLoss,
                                                queueEntry.count + 1
                                        )
                                );
                            }
                        } else if (queueEntry.count < 10) {
                            priorityQueue.add(
                                    new QueueEntry(
                                            nextPosition,
                                            queueEntry.heatLoss + heatLoss,
                                            sameDirection ? queueEntry.count + 1 : 1
                                    )
                            );
                        } else {
                            if (!sameDirection) {
                                priorityQueue.add(
                                        new QueueEntry(
                                                nextPosition,
                                                queueEntry.heatLoss + heatLoss,
                                                1
                                        )
                                );
                            }
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
