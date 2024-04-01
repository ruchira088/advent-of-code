package com.ruchij.twentytwentythree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DayFifteenTest {
    private final DayFifteen dayFifteen = new DayFifteen();

    @Test
    void shouldHashToCorrectValue() {
        assertEquals(52, dayFifteen.hash("HASH"));
        assertEquals(30, dayFifteen.hash("rn=1"));
        assertEquals(253, dayFifteen.hash("cm-"));
        assertEquals(97, dayFifteen.hash("qp=3"));
        assertEquals(47, dayFifteen.hash("cm=2"));
        assertEquals(14, dayFifteen.hash("qp-"));
        assertEquals(180, dayFifteen.hash("pc=4"));
        assertEquals(9, dayFifteen.hash("ot=9"));
        assertEquals(197, dayFifteen.hash("ab=5"));
        assertEquals(48, dayFifteen.hash("pc-"));
        assertEquals(214, dayFifteen.hash("pc=6"));
        assertEquals(231, dayFifteen.hash("ot=7"));
    }

}