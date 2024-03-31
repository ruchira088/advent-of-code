package com.ruchij.twentytwentythree;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ruchij.twentytwentythree.DayFourteen.State.*;

class DayFourteenTest {
    private final DayFourteen dayFourteen = new DayFourteen();

    @Test
    void shouldTiltCorrectly() {
        List<DayFourteen.State> list = List.of(ROUND, ROUND, EMPTY, ROUND, SQUARE, EMPTY, ROUND, EMPTY);
        System.out.println(list);
        List<DayFourteen.State> result = dayFourteen.tiltNorth(List.of(ROUND, ROUND, EMPTY, ROUND, SQUARE, EMPTY, ROUND, EMPTY));
        System.out.println(result);
    }

}