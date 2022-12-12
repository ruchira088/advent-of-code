package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DaySix implements JavaSolution {
    @Override
    public Object solve(Stream<String> input) {
        return run(input.findFirst().get());
    }

    private Object run(String input) {
        ArrayDeque<Character> queue = new ArrayDeque<>();
        Map<Character, LinkedList<Integer>> characters = new HashMap<>();

        char[] chars = input.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char character = chars[i];

            if (i >= 14) {
                Character poll = queue.poll();
                LinkedList<Integer> integers = characters.get(poll);
                integers.removeFirst();

                if (integers.isEmpty()) {
                    characters.remove(poll);
                }
            }

            queue.add(character);
            List<Integer> integers = characters.computeIfAbsent(character, __ -> new LinkedList<>());
            integers.add(i);

            if (characters.size() == 14) {
                return i + 1;
            }
        }

        throw new IllegalArgumentException("Unable to find starting pattern");
    }
}
