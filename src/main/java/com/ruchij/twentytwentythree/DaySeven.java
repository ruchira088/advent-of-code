package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DaySeven implements JavaSolution {
    enum Card {
        ACE('A', 14),
        KING('K', 13),
        QUEEN('Q', 12),
        JACK('J', 11),
        TEN('T', 10),
        NINE('9', 9),
        EIGHT('8', 8),
        SEVEN('7', 7),
        SIX('6', 6),
        FIVE('5', 5),
        FOUR('4', 4),
        THREE('3', 3),
        TWO('2', 2);

        private final char character;
        private final int value;

        Card(char character, int value) {
            this.character = character;
            this.value = value;
        }

        public char getCharacter() {
            return character;
        }

        public int getValue() {
            return value;
        }

        static Card parse(char character) {
            return Arrays.stream(Card.values())
                    .filter(card -> card.character == character)
                    .findFirst()
                    .orElseThrow();
        }
    }

    enum Combination {
        FIVE_OF_A_KIND(7),
        FOUR_OF_A_KIND(6),
        FULL_HOUSE(5),
        THREE_OF_A_KIND(4),
        TWO_PAIR(3),
        PAIR(2),
        HIGH_CARD(1);

        private final int value;

        Combination(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    record Hand(List<Card> cards, int bid) { }
    record CalculatedHand(Hand hand, Combination combination) { }

    public class CalculatedHandComparator implements Comparator<CalculatedHand> {
        @Override
        public int compare(CalculatedHand one, CalculatedHand two) {
            int combinationCompare = one.combination.getValue() - two.combination.getValue();

            if (combinationCompare == 0) {
                for (int i = 0; i < one.hand.cards.size(); i++) {
                    int cardCompare = one.hand.cards.get(i).value - two.hand.cards.get(i).value;

                    if (cardCompare != 0) {
                        return cardCompare;
                    }
                }

                return 0;
            } else {
                return combinationCompare;
            }
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        CalculatedHand[] calculatedHands = input.map(this::parse)
                .map(hand -> new CalculatedHand(hand, combination(hand.cards)))
                .sorted(new CalculatedHandComparator())
                .toArray(CalculatedHand[]::new);

        long result = 0;

        for (int i = 0; i < calculatedHands.length; i++) {
            result = result + ((long) (i + 1) * calculatedHands[i].hand.bid);
        }

        return result;
    }

    Hand parse(String line) {
        String[] strings = line.split(" ");

        List<Card> cardList =
                Arrays.stream(strings[0].split(""))
                .map(character -> Card.parse(character.charAt(0)))
                .toList();

        int bid = Integer.parseInt(strings[1]);

        Hand hand = new Hand(cardList, bid);

        return hand;
    }

    private Map<Card, Integer> cardMap(List<Card> cards) {
        HashMap<Card, Integer> map = new HashMap<>();

        for (Card card : cards) {
            map.put(card, map.getOrDefault(card, 0) + 1);
        }

        return map;
    }

    private Combination combination(List<Card> cards) {
        Map<Card, Integer> cardMap = cardMap(cards);

        List<Integer> list = cardMap.values().stream().sorted(Comparator.reverseOrder()).toList();
        Integer first = list.getFirst();

        Combination combination =
                switch (first) {
                    case 5 -> Combination.FIVE_OF_A_KIND;
                    case 4 -> Combination.FOUR_OF_A_KIND;
                    case 3 -> list.get(1) == 2 ? Combination.FULL_HOUSE : Combination.THREE_OF_A_KIND;
                    case 2 -> list.get(1) == 2 ? Combination.TWO_PAIR : Combination.PAIR;
                    case null, default -> Combination.HIGH_CARD;
                };

        return combination;
    }


}
