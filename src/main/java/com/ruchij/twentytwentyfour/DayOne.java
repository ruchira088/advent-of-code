package com.ruchij.twentytwentyfour;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayOne implements JavaSolution {
    record Pair<A, B>(A first, B second) {}

    @Override
    public Object solve(Stream<String> input) {
        Pair<List<Long>, List<Long>> listPair = parse(input);

        HashMap<Long, Integer> count = new HashMap<>();

        for (Long number : listPair.second) {
            count.put(number, count.getOrDefault(number, 0) + 1);
        }

        long result = 0;

        for (Long number : listPair.first) {
            result += number * count.getOrDefault(number, 0);
        }

        return result;
    }

    private Pair<List<Long>, List<Long>> parse(Stream<String> input) {
        ArrayList<Long> first = new ArrayList<>();
        ArrayList<Long> second = new ArrayList<>();

        input.forEach(line -> {
            List<String> strings = Arrays.stream(line.split(" ")).filter(word -> !word.isEmpty()).toList();
            first.add(Long.parseLong(strings.get(0)));
            second.add(Long.parseLong(strings.get(1)));
        });

        return new Pair<>(first, second);
    }
}
