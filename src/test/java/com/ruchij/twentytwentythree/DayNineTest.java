package com.ruchij.twentytwentythree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DayNineTest {
    @Test
    void returnsTheNextNumber() {
        DayNine dayNine = new DayNine();
        Long nextNumber = dayNine.nextNumber(List.of(10L, 13L, 16L, 21L, 30L, 45L));
        Assertions.assertEquals(68, nextNumber);
    }

}