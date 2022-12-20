package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayTwelve implements JavaSolution {
    record Coordinate(int x, int y) {
    }

    record Game(int[][] grid, Coordinate start, Coordinate end) {
    }

    record Entry(Coordinate position, int steps) {
    }

    @Override
    public Object solve(Stream<String> input) {
        Game game = parse(input);
        int rows = game.grid.length;
        int columns = game.grid[0].length;

        ArrayDeque<Entry> arrayDeque = new ArrayDeque<>();
        arrayDeque.add(new Entry(game.start, 0));

        HashSet<Coordinate> visited = new HashSet<>();

        while (!arrayDeque.isEmpty()) {
            Entry entry = arrayDeque.removeFirst();

            if (entry.position.equals(game.end)) {
                return entry.steps;
            }

            if (!visited.contains(entry.position)) {
                visited.add(entry.position);

                Set<Coordinate> moves = possibleMoves(game.grid, entry.position, rows, columns);

                for (Coordinate coordinate : moves) {
                    arrayDeque.add(new Entry(coordinate, entry.steps + 1));
                }
            }
        }

        throw new IllegalStateException("Unable to find step count");
    }

    Set<Coordinate> possibleMoves(int[][] grid, Coordinate coordinate, int rows, int columns) {
        int value = grid[coordinate.y][coordinate.x];
        Set<Coordinate> coordinates =
                Set.of(
                        new Coordinate(coordinate.x - 1, coordinate.y),
                        new Coordinate(coordinate.x + 1, coordinate.y),
                        new Coordinate(coordinate.x, coordinate.y - 1),
                        new Coordinate(coordinate.x, coordinate.y + 1)
                );
        Set<Coordinate> result = new HashSet<>();

        for (Coordinate next : coordinates) {
            if (next.x >= 0 && next.x < columns && next.y >= 0 && next.y < rows) {
                int currentValue = grid[next.y][next.x];

                if (currentValue <= value + 1) {
                    result.add(next);
                }
            }
        }

        return result;
    }

    Game parse(Stream<String> input) {
        ArrayList<List<Integer>> listGrid = new ArrayList<>();
        Iterator<String> iterator = input.iterator();
        Coordinate start = null;
        Coordinate end = null;

        while (iterator.hasNext()) {
            String line = iterator.next().trim();
            ArrayList<Integer> characters = new ArrayList<>();

            for (char character : line.toCharArray()) {
                if (character == 'S') {
                    start = new Coordinate(characters.size(), listGrid.size());
                    character = 'a';
                } else if (character == 'E') {
                    end = new Coordinate(characters.size(), listGrid.size());
                    character = 'z';
                }

                characters.add(character - 96);
            }

            listGrid.add(characters);
        }

        int[][] grid = new int[listGrid.size()][listGrid.get(0).size()];

        for (int y = 0; y < listGrid.size(); y++) {
            for (int x = 0; x < listGrid.get(y).size(); x++) {
                grid[y][x] = listGrid.get(y).get(x);
            }
        }

        return new Game(grid, start, end);
    }
}
