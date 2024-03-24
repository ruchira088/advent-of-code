package com.ruchij.twentytwentythree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class DayElevenTest {

    @Test
    void expandAsExpected() {
        DayEleven dayEleven = new DayEleven();

        String input = """
                ...#......
                .......#..
                #.........
                ..........
                ......#...
                .#........
                .........#
                ..........
                .......#..
                #...#.....
                """;

        Set<DayEleven.Coordinate> result = dayEleven.expand(dayEleven.parse(input.lines()));

        String output = """
                ....#........
                .........#...
                #............
                .............
                .............
                ........#....
                .#...........
                ............#
                .............
                .............
                .........#...
                #....#.......
                """;

        Set<DayEleven.Coordinate> outputResult = dayEleven.parse(output.lines());

        Assertions.assertEquals(outputResult, result);
    }

}