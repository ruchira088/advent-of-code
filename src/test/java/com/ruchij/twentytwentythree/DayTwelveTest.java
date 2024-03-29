package com.ruchij.twentytwentythree;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ruchij.twentytwentythree.DayTwelve.State.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DayTwelveTest {
    private final DayTwelve dayTwelve = new DayTwelve();

    @Test
    void shouldParseInput() {
        assertEquals(
                new DayTwelve.Line(
                        List.of(UNKNOWN, UNKNOWN, UNKNOWN, OPERATIONAL, DAMAGED, DAMAGED, DAMAGED),
                        List.of(1, 1, 3)
                        ),
                dayTwelve.parse("???.### 1,1,3")
        );
    }

    @Test
    void shouldDescribeState() {
        assertEquals(List.of(1,1,3), dayTwelve.describe(dayTwelve.parseStates("#.#.###")));
        assertEquals(List.of(1,1,3), dayTwelve.describe(dayTwelve.parseStates(".#...#....###.")));
        assertEquals(List.of(1,3,1,6), dayTwelve.describe(dayTwelve.parseStates(".#.###.#.######")));
    }

    @Test
    void shouldCount() {
//        assertEquals(1, dayTwelve.count(dayTwelve.parse("???.### 1,1,3")));
//        assertEquals(4, dayTwelve.count(dayTwelve.parse(".??..??...?##. 1,1,3")));
//        assertEquals(1, dayTwelve.count(dayTwelve.parse("?#?#?#?#?#?#?#? 1,3,1,6")));
//        assertEquals(1, dayTwelve.count(dayTwelve.parse("????.#...#... 4,1,1")));
//        assertEquals(4, dayTwelve.count(dayTwelve.parse("????.######..#####. 1,6,5")));
        assertEquals(10, dayTwelve.count(dayTwelve.parse("?###???????? 3,2,1")));
    }
}