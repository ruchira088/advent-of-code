package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.Arrays;
import java.util.stream.Stream;

public class DayFifteen implements JavaSolution {
    @Override
    public Object solve(Stream<String> input) {
        long result = input.flatMap(line -> Arrays.stream(line.split(",")))
                .mapToLong(this::hash)
                .sum();

        return result;
    }

    int hash(String input) {
        int value = 0;

        for (int c : input.toCharArray()) {
            value += c;
            value *= 17;
            value = value % 256;
        }

        return value;
    }
}
