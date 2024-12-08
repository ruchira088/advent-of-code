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
        Integer diff = null;

        for (int i = 0; i < levels.size() - 1; i++) {
            int current = levels.get(i);
            int next = levels.get(i + 1);
            int difference = next - current;

            if (diff == null || diff > 0 && difference > 0 || diff < 0 && difference < 0) {
                int absDiff = Math.abs(difference);

                if (absDiff >= 1 && absDiff <= 3) {
                    diff = difference;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    List<Integer> parse(String line) {
        return Arrays.stream(line.split(" "))
                .map(Integer::parseInt)
                .toList();
    }

}
