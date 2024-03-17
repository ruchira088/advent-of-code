package com.ruchij.twentytwentythree;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DayEightTest {

    @Test
    void lcm() {
        DayEight dayEight = new DayEight();
        assertEquals(504, dayEight.lcm(List.of(8L, 9L, 21L)));
    }
}