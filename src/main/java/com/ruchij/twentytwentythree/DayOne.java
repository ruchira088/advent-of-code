package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class DayOne implements JavaSolution {
    Map<String, Integer> mappings =
            Map.of(
                    "one", 1,
                    "two", 2,
                    "three", 3,
                    "four", 4,
                    "five", 5,
                    "six", 6,
                    "seven", 7,
                    "eight", 8,
                    "nine", 9
            );

    @Override
    public Object solve(Stream<String> input) {
        int count = 0;

        Set<String> keys = mappings.keySet();

        for (String line : input.toList()) {
            char[] charArray = line.toCharArray();
            count += 10 * get(line, 0, i -> i < charArray.length, 1);
            count += get(line, charArray.length - 1, i -> i >= 0, -1);
        }

        return count;
    }

    int get(String line, int start, Function<Integer, Boolean> isLimit, int diff) {
        char[] charArray = line.toCharArray();

        for (int i = start; isLimit.apply(i); i += diff) {
            if (Character.isDigit(charArray[i])) {
                return Character.digit(charArray[i], 10);
            }

            String substring = line.substring(i);

            for (String word : mappings.keySet()) {
                if (substring.startsWith(word)) {
                    return mappings.get(word);
                };
            }
        }

        return 0;
    }
}
