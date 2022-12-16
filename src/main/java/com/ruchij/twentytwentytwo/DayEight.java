package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class DayEight implements JavaSolution {
    record Coordinate(int x, int y) {
    }

    @Override
    public Object solve(Stream<String> input) {
        int[][] trees = parse(input);
        return maxScenicScore(trees);
    }

    Set<Coordinate> visibleTrees(int[][] trees) {
        HashSet<Coordinate> coordinates = new HashSet<>();
        int rows = trees.length;
        int columns = trees[0].length;

        // Left
        find(
                trees,
                new Coordinate(0, 0),
                coordinate -> new Coordinate(coordinate.x + 1, coordinate.y),
                coordinate -> new Coordinate(0, coordinate.y + 1),
                coordinates,
                rows,
                columns
        );

        // Top
        find(
                trees,
                new Coordinate(0, 0),
                coordinate -> new Coordinate(coordinate.x, coordinate.y + 1),
                coordinate -> new Coordinate(coordinate.x + 1, 0),
                coordinates,
                rows,
                columns
        );

        // Right
        find(
                trees,
                new Coordinate(rows - 1, 0),
                coordinate -> new Coordinate(coordinate.x - 1, coordinate.y),
                coordinate -> new Coordinate(rows - 1, coordinate.y + 1),
                coordinates,
                rows,
                columns
        );

        // Bottom
        find(
                trees,
                new Coordinate(rows - 1, columns - 1),
                coordinate -> new Coordinate(coordinate.x, coordinate.y - 1),
                coordinate -> new Coordinate(coordinate.x - 1, columns - 1),
                coordinates,
                rows,
                columns
        );

        return coordinates;
    }

    void find(
            int[][] trees,
            Coordinate current,
            Function<Coordinate, Coordinate> small, Function<Coordinate, Coordinate> big,
            HashSet<Coordinate> coordinates,
            int rows,
            int columns
    ) {
        int max = trees[current.y][current.x];
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        coordinates.add(current);

        while (current != null) {
            current = next(
                    current,
                    small,
                    big,
                    coordinate -> atomicBoolean.set(true),
                    rows,
                    columns
            );

            if (atomicBoolean.get()) {
                max = -1;
                atomicBoolean.set(false);
            }

            if (current != null) {
                int height = trees[current.y][current.x];
                if (height > max) {
                    max = height;
                    coordinates.add(current);
                }
            }
        }
    }

    long maxScenicScore(int[][] trees) {
        int rows = trees.length;
        int columns = trees[0].length;
        long maxScore = -1;

        for (int y = 1; y < rows - 1; y++) {
            for (int x = 1; x < columns - 1; x++) {
                maxScore = Math.max(scenicScore(trees, new Coordinate(x, y), rows, columns), maxScore);
            }
        }

        return maxScore;
    }

    long scenicScore(int[][] trees, Coordinate coordinate, int rows, int columns) {
        long scenicScore = 1;

        Set<Function<Coordinate, Coordinate>> fns =
                Set.of(
                        value -> new Coordinate(value.x + 1, value.y),
                        value -> new Coordinate(value.x - 1, value.y),
                        value -> new Coordinate(value.x, value.y + 1),
                        value -> new Coordinate(value.x, value.y - 1)
                );

        for (Function<Coordinate, Coordinate> fn : fns) {
            scenicScore *= viewableTress(trees, coordinate, fn, rows, columns);

            if (scenicScore == 0) {
                return 0;
            }
        }

        return scenicScore;
    }

    int viewableTress(int[][] trees, Coordinate coordinate, Function<Coordinate, Coordinate> next, int rows, int columns) {
        int treeHeight = trees[coordinate.y][coordinate.x];
        int viewableTress = 0;

        while (true) {
            coordinate = next.apply(coordinate);

            if (isValid(coordinate, rows, columns)) {
                int height = trees[coordinate.y][coordinate.x];
                viewableTress++;

                if (height >= treeHeight) {
                    return viewableTress;
                }
            } else {
                return viewableTress;
            }
        }
    }

    Coordinate next(Coordinate current, Function<Coordinate, Coordinate> small, Function<Coordinate, Coordinate> big, Consumer<Coordinate> onBig, int rows, int columns) {
        List<Function<Coordinate, Coordinate>> functions = List.of(small, big);

        for (Function<Coordinate, Coordinate> f : functions) {
            Coordinate coordinate = f.apply(current);

            if (isValid(coordinate, rows, columns)) {
                if (f == big) {
                    onBig.accept(coordinate);
                }

                return coordinate;
            }
        }

        return null;
    }

    boolean isValid(Coordinate coordinate, int rows, int columns) {
        return coordinate.x >= 0 && coordinate.x < columns && coordinate.y >= 0 && coordinate.y < rows;
    }

    private int[][] parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        List<List<Integer>> chars = new ArrayList<>();

        while (iterator.hasNext()) {
            String line = iterator.next();
            ArrayList<Integer> row = new ArrayList<>();

            for (char character : line.toCharArray()) {
                row.add(Integer.parseInt(Character.toString(character)));
            }

            chars.add(row);
        }

        int[][] characters = new int[chars.size()][chars.get(0).size()];

        int rowIndex = 0;

        for (List<Integer> row : chars) {
            int columIndex = 0;

            for (Integer value : row) {
                characters[rowIndex][columIndex] = value;
                columIndex++;
            }

            rowIndex++;
        }

        return characters;
    }
}
