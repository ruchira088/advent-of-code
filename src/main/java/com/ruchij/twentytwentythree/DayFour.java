package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayFour implements JavaSolution {
    record Card(int id, List<Integer> winningNumbers, List<Integer> cardNumbers) {
    }

    @Override
    public Object solve(Stream<String> input) {
        List<Card> cards = input.map(this::parse).toList();
        Map<Integer, Integer> cardCount = new HashMap<>();

        for (Card card : cards) {
            int count = cardCount.getOrDefault(card.id, 0) + 1;
            cardCount.put(card.id, count);

            int matches = cardPoints(card);

            for (int i = 0; i < matches; i++) {
                int cardId = card.id + i + 1;
                cardCount.put(cardId, cardCount.getOrDefault(cardId, 0) + count);
            }
        }

        int sum = cardCount.values().stream().mapToInt(x -> x).sum();

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

        return matches;
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
