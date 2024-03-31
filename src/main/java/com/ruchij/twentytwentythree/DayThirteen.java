package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DayThirteen implements JavaSolution {
    record Result(int index, boolean smudgeFixed) {
    }

    @Override
    public Object solve(Stream<String> input) {
        List<boolean[][]> grids = parse(input);

        return grids.stream()
//                .peek(this::printGrid)
                .mapToLong(grid ->
                        Stream.concat(
                                        verticalReflection(grid).stream(),
                                        horizontalReflection(grid).stream().map(value -> new Result(value.index * 100, value.smudgeFixed)
                                        )
                                )
                                .filter(result -> result.smudgeFixed)
                                .peek(System.out::println)
                                .mapToInt(result -> result.index)
                                .findFirst()
                                .getAsInt()

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

    List<Result> verticalReflection(boolean[][] grid) {
        int width = grid[0].length;
        int height = grid.length;

        ArrayList<Result> results = new ArrayList<>();

        for (int x1 = 0; x1 < width; x1++) {
            for (int x2 = x1 + 1; x2 < width; x2++) {
                boolean isMatch = true;
                boolean smudgeFixed = false;

                for (int y = 0; y < height; y++) {
                    if (grid[y][x1] != grid[y][x2]) {
                        if (smudgeFixed) {
                            isMatch = false;
                            break;
                        } else {
                            smudgeFixed = true;
                        }
                    }
                }

                if (isMatch) {
                    Optional<Result> verticalReflection = isVerticalReflection(grid, x1, x2, false);

                    verticalReflection.ifPresent(results::add);
                }
            }
        }

        return results;
    }

    Optional<Result> isVerticalReflection(boolean[][] grid, int x1, int x2, boolean smudgeFixed) {
        int height = grid.length;
        int width = grid[0].length;
        int left = x1;
        int right = x2;

        while (left < right) {
            for (int y = 0; y < height; y++) {
                if (grid[y][left] != grid[y][right]) {
                    if (smudgeFixed) {
                        return Optional.empty();
                    } else {
                        smudgeFixed = true;
                    }
                }
            }

            left++;
            right--;

            if (left == right) {
                return Optional.empty();
            }
        }

        int result = left;

        left = x1 - 1;
        right = x2 + 1;

        while (left >= 0 && right < width) {
            for (int y = 0; y < height; y++) {
                if (grid[y][left] != grid[y][right]) {
                    if (smudgeFixed) {
                        return Optional.empty();
                    } else {
                        smudgeFixed = true;
                    }
                }
            }

            left--;
            right++;
        }

        return Optional.of(new Result(result, smudgeFixed));
    }

    List<Result> horizontalReflection(boolean[][] grid) {
        int width = grid[0].length;
        int height = grid.length;
        ArrayList<Result> results = new ArrayList<>();

        for (int y1 = 0; y1 < height; y1++) {
            for (int y2 = y1 + 1; y2 < height; y2++) {
                boolean isMatch = true;
                boolean smudgeFixed = false;

                for (int x = 0; x < width; x++) {
                    if (grid[y1][x] != grid[y2][x]) {
                        if (smudgeFixed) {
                            isMatch = false;
                            break;
                        } else {
                            smudgeFixed = true;
                        }
                    }
                }

                if (isMatch) {
                    Optional<Result> horizontalReflection = isHorizontalReflection(grid, y1, y2, false);

                    horizontalReflection.ifPresent(results::add);
                }
            }
        }

        return results;
    }

    Optional<Result> isHorizontalReflection(boolean[][] grid, int y1, int y2, boolean smudgeFixed) {
        int width = grid[0].length;
        int height = grid.length;
        int top = y1;
        int bottom = y2;

        while (top < bottom) {
            for (int x = 0; x < width; x++) {
                if (grid[top][x] != grid[bottom][x]) {
                    if (smudgeFixed) {
                        return Optional.empty();
                    } else {
                        smudgeFixed = true;
                    }
                }
            }

            top++;
            bottom--;

            if (top == bottom) {
                return Optional.empty();
            }
        }

        int result = top;

        top = y1 - 1;
        bottom = y2 + 1;

        while (top >= 0 && bottom < height) {
            for (int x = 0; x < width; x++) {
                if (grid[top][x] != grid[bottom][x]) {
                    if (smudgeFixed) {
                        return Optional.empty();
                    } else {
                        smudgeFixed = true;
                    }
                }
            }

            top--;
            bottom++;
        }

        return Optional.of(new Result(result, smudgeFixed));
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
