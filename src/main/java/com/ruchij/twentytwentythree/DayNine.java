package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

public class DayNine implements JavaSolution {
    @Override
    public Object solve(Stream<String> input) {
        List<List<Long>> numbers = parse(input);

        long sum = numbers.stream()
                .mapToLong(this::previousValue)
                .sum();

        return sum;
    }

    private List<List<Long>> parse(Stream<String> input) {
        return input
                .map(line -> Arrays.stream(line.split(" "))
                        .map(Long::parseLong).toList()
                )
                .toList();
    }

    Long previousValue(List<Long> numbers) {
        List<Long> current = numbers;
        Stack<List<Long>> stack = new Stack<>();
        stack.add(current);

        while (!isAllZero(current)) {
            current = diffs(current);
            stack.add(current);
        }

        long firstElement = 0;

        while (!stack.isEmpty()) {
            List<Long> longs = stack.pop();
            firstElement = longs.getFirst() - firstElement;
        }

        return firstElement;
    }

    private boolean isAllZero(List<Long> list) {
        return list.stream().allMatch(value -> value == 0);
    }

    private List<Long> diffs(List<Long> numbers) {
        ArrayList<Long> list = new ArrayList<>();
        int size = numbers.size();

        for (int i = 0; i < size - 1; i++) {
            long diff = numbers.get(i + 1) - numbers.get(i);
            list.add(diff);
        }

        return list;
    }

}
