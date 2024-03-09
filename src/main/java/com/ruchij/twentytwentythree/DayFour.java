package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class DayFour implements JavaSolution {
    record Card(int id, List<Integer> winningNumbers, List<Integer> cardNumbers) {
    }

    @Override
    public Object solve(Stream<String> input) {
        int sum = input.map(this::parse)
                .mapToInt(this::cardPoints)
                .sum();

        return sum;
    }

    int cardPoints(Card card) {
        int matches = 0;
        Set<Integer> winningNumbers = Set.copyOf(card.winningNumbers);

        for (Integer number : card.cardNumbers) {
            if (winningNumbers.contains(number)) {
                matches++;
            }
        }

        if (matches == 0) {
            return 0;
        } else {
            return (int) Math.pow(2, matches - 1);
        }
    }

    Card parse(String input) {
        String[] split = input.split(":");

        Integer id =
                Integer.parseInt(Arrays.stream(split[0].split(" "))
                        .map(String::trim)
                        .filter(string -> !string.isBlank())
                        .toList().get(1)
                );


        String[] numbers = split[1].split("\\|");

        List<Integer> winningNumbers = parseNumbers(numbers[0]);
        List<Integer> cardNumbers = parseNumbers(numbers[1]);

        Card card = new Card(id, winningNumbers, cardNumbers);

        return card;
    }

    List<Integer> parseNumbers(String input) {
        return Arrays.stream(input.split(" "))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Integer::parseInt).toList();
    }
}
