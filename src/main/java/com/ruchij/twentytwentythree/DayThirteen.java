package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayThirteen implements JavaSolution {
    @Override
    public Object solve(Stream<String> input) {
        List<boolean[][]> grids = parse(input);

        return grids.stream()
                .peek(this::printGrid)
                .mapToLong(grid ->
                        verticalReflection(grid)
                                .or(() -> horizontalReflection(grid).map(value -> value * 100))
                                .orElseThrow()
                )
                .peek(System.out::println)
                .sum();
    }

    void printGrid(boolean[][] grid) {
        System.out.println();

        for (boolean[] booleans : grid) {
            for (int x = 0; x < grid[0].length; x++) {
                System.out.print(booleans[x] ? '#' : '.');
            }
            System.out.println();
        }
    }

    List<boolean[][]> parse(Stream<String> input) {
        ArrayList<boolean[][]> list = new ArrayList<>();
        ArrayList<boolean[]> current = new ArrayList<>();

        for (String line : input.toList()) {
            if (line.trim().isEmpty()) {
                boolean[][] booleans = current.toArray(new boolean[0][]);
                list.add(booleans);
                current.clear();
            } else {
                current.add(parseLine(line));
            }
        }

        boolean[][] booleans = current.toArray(new boolean[0][]);
        list.add(booleans);
        current.clear();

        return list;
    }

    Optional<Integer> verticalReflection(boolean[][] grid) {
        int width = grid[0].length;
        int height = grid.length;

        for (int x1 = 0; x1 < width; x1++) {
            for (int x2 = x1 + 1; x2 < width; x2++) {
                boolean isMatch = true;

                for (int y = 0; y < height; y++) {
                    if (grid[y][x1] != grid[y][x2]) {
                        isMatch = false;
                        break;
                    }
                }

                if (isMatch) {
                    Optional<Integer> verticalReflection = isVerticalReflection(grid, x1, x2);

                    if (verticalReflection.isPresent()) {
                        return verticalReflection;
                    }
                }
            }
        }

        return Optional.empty();
    }

    Optional<Integer> isVerticalReflection(boolean[][] grid, int x1, int x2) {
        int height = grid.length;
        int width = grid[0].length;
        int left = x1;
        int right = x2;

        while (left < right) {
            for (int y = 0; y < height; y++) {
                if (grid[y][left] != grid[y][right]) {
                    return Optional.empty();
                }
            }

            left++;
            right--;

            if (left == right) {
                return Optional.empty();
            }
        }

        int result = left;

        left = x1;
        right = x2;

        while (left >= 0 && right < width) {
            for (int y = 0; y < height; y++) {
                if (grid[y][left] != grid[y][right]) {
                    return Optional.empty();
                }
            }

            left--;
            right++;
        }

        return Optional.of(result);
    }

    Optional<Integer> horizontalReflection(boolean[][] grid) {
        int width = grid[0].length;
        int height = grid.length;

        for (int y1 = 0; y1 < height; y1++) {
            for (int y2 = y1 + 1; y2 < height; y2++) {
                boolean isMatch = true;

                for (int x = 0; x < width; x++) {
                    if (grid[y1][x] != grid[y2][x]) {
                        isMatch = false;
                        break;
                    }
                }

                if (isMatch) {
                    Optional<Integer> horizontalReflection = isHorizontalReflection(grid, y1, y2);

                    if (horizontalReflection.isPresent()) {
                        return horizontalReflection;
                    }
                }
            }
        }

        return Optional.empty();
    }

    Optional<Integer> isHorizontalReflection(boolean[][] grid, int y1, int y2) {
        int width = grid[0].length;
        int height = grid.length;
        int top = y1;
        int bottom = y2;

        while (top < bottom) {
            for (int x = 0; x < width; x++) {
                if (grid[top][x] != grid[bottom][x]) {
                    return Optional.empty();
                }
            }

            top++;
            bottom--;

            if (top == bottom) {
                return Optional.empty();
            }
        }

        int result = top;

        top = y1;
        bottom = y2;

        while (top >= 0 && bottom < height) {
            for (int x = 0; x < width; x++) {
                if (grid[top][x] != grid[bottom][x]) {
                    return Optional.empty();
                }
            }

            top--;
            bottom++;
        }

        return Optional.of(result);
    }

    boolean[] parseLine(String line) {
        boolean[] booleans = new boolean[line.trim().length()];
        char[] chars = line.trim().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            booleans[i] = chars[i] == '#';
        }

        return booleans;
    }
}
