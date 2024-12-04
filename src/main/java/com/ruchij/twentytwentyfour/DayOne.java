package com.ruchij.twentytwentyfour;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class DayOne implements JavaSolution {
    record Pair<A, B>(A first, B second) {}

    @Override
    public Object solve(Stream<String> input) {
        Pair<List<Long>, List<Long>> listPair = parse(input);

        listPair.first.sort(Comparator.naturalOrder());
        listPair.second.sort(Comparator.naturalOrder());

        long result = 0;

        for (int i = 0; i < listPair.first.size(); i++) {
            result += Math.abs(listPair.first.get(i) - listPair.second.get(i));
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
