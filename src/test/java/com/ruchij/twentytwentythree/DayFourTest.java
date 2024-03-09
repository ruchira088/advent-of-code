package com.ruchij.twentytwentythree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DayFourTest {
    private final DayFour dayFour = new DayFour();

    @Test
    void shouldParseInput() {
        String input = "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53";

        DayFour.Card card = dayFour.parse(input);

        Assertions.assertEquals(
                new DayFour.Card(1, List.of(41, 48, 83, 86, 17), List.of(83, 86, 6, 31, 17, 9, 48, 53)),
                card
        );

    }

}