package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayTwentyThree implements JavaSolution {
    enum Tile {
        PATH('.'), FOREST('#'), UP('^'), DOWN('v'), LEFT('<'), RIGHT('>');

        private final char symbol;

        Tile(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        static Tile fromSymbol(char symbol) {
            return Arrays.stream(Tile.values()).filter(tile -> tile.symbol == symbol).findAny().orElseThrow();
        }
    }

    record Coordinate(int x, int y) {}
    record Game(Map<Coordinate, Tile> map, Coordinate start, Coordinate end) {}
    record Entry(Coordinate position, Set<Coordinate> visited) {}

    @Override
    public Object solve(Stream<String> input) {
        Game game = parse(input);
        ArrayDeque<Entry> entries = new ArrayDeque<>();
        entries.add(new Entry(game.start, new HashSet<>()));
        Set<Integer> count = new HashSet<>();

        while (!entries.isEmpty()) {
            Entry entry = entries.remove();
            Coordinate coordinate = entry.position;

            if (!entry.visited.contains(coordinate)) {
                if (coordinate.equals(game.end)) {
                    count.add(entry.visited.size());
                } else {
                    Set<Coordinate> visited = new HashSet<>(entry.visited);
                    visited.add(coordinate);

                    Tile tile = game.map.get(coordinate);
                    int x = coordinate.x;
                    int y = coordinate.y;

                    if (tile != null && tile != Tile.FOREST) {
                        if (tile == Tile.PATH) {

                            Set<Coordinate> coordinates = Set.of(
                                    new Coordinate(x + 1, y),
                                    new Coordinate(x - 1, y),
                                    new Coordinate(x, y + 1),
                                    new Coordinate(x, y - 1)
                            );

                            for (Coordinate next : coordinates) {
                                entries.add(new Entry(next, visited));
                            }
                        } else if (tile == Tile.UP) {
                            entries.add(new Entry(new Coordinate(x, y - 1), visited));
                        } else if (tile == Tile.DOWN) {
                            entries.add(new Entry(new Coordinate(x, y + 1), visited));
                        } else if (tile == Tile.LEFT) {
                            entries.add(new Entry(new Coordinate(x - 1, y), visited));
                        } else if (tile == Tile.RIGHT) {
                            entries.add(new Entry(new Coordinate(x + 1, y), visited));
                        }
                    }
                }
            }
        }

        return count.stream().mapToInt(x -> x).max();
    }

    Game parse(Stream<String> input) {
        HashMap<Coordinate, Tile> map = new HashMap<>();
        List<String> lines = input.toList();
        Coordinate start = null;
        Coordinate end = null;

        int y = 0;

        for (String line : lines) {
            char[] chars = line.trim().toCharArray();
            int x = 0;

            for (char c : chars) {
                Coordinate coordinate = new Coordinate(x, y);
                Tile tile = Tile.fromSymbol(c);

                if (tile == Tile.PATH) {
                    end = coordinate;
                    if (start == null) {
                        start = coordinate;
                    }
                }

                map.put(coordinate, tile);
                x++;
            }
            y++;
        }

        Game game = new Game(map, start, end);

        return game;
    }
}
