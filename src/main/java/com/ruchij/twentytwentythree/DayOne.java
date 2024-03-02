package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.stream.Stream;

public class DayOne implements JavaSolution {
    @Override
    public Object solve(Stream<String> input) {
        int count = 0;

        for (String line : input.toList()) {
            char[] charArray = line.toCharArray();

            for (char character : charArray) {
                if (Character.isDigit(character)) {
                    count += 10 * Character.digit(character, 10);
                    break;
                }
            }

            for (int i = charArray.length - 1; i >= 0; i--) {
                if (Character.isDigit(charArray[i])) {
                    count += Character.digit(charArray[i], 10);
                    break;
                }
            }
        }

        return count;

    }
}
