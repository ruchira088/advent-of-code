package com.ruchij.twentytwentyfour;

import com.ruchij.JavaSolution;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DayTwo implements JavaSolution {
    @Override
    public Object solve(Stream<String> input) {
        long count = input.map(this::parse)
                .filter(this::isSafe)
                .count();

        return count;
    }

    boolean isSafe(List<Integer> levels) {
        return isSafe(levels, -1) || isSafe(levels, 0);
    }

    boolean isSafe(List<Integer> levels, int skip) {
        Integer diff = null;

        for (int i = 0; i < levels.size() - 1; i++) {
            if (i != skip) {
                int current = levels.get(i);
                int next;
                if (i + 1 != skip) {
                    next = levels.get(i + 1);
                } else {
                    if (i + 2 < levels.size()) {
                        next = levels.get(i + 2);
                    } else {
                        return true;
                    }
                }

                int difference = next - current;

                if (diff == null || diff > 0 && difference > 0 || diff < 0 && difference < 0) {
                    int absDiff = Math.abs(difference);

                    if (absDiff >= 1 && absDiff <= 3) {
                        diff = difference;
                    } else if (skip == -1) {
                        return isSafe(levels, i) || isSafe(levels, i + 1);
                    } else {
                        return false;
                    }
                } else if (skip == -1) {
                    return isSafe(levels, i) || isSafe(levels, i + 1);
                } else {
                    return false;
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < levels.size(); i++) {
            if (skip != i) {
                stringBuilder.append(levels.get(i));
                stringBuilder.append(", ");
            }
        }

        System.out.println(stringBuilder);

        return true;
    }

    List<Integer> parse(String line) {
        return Arrays.stream(line.split(" "))
                .map(Integer::parseInt)
                .toList();
    }

}
