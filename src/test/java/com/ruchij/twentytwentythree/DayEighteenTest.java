package com.ruchij.twentytwentythree;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.ruchij.twentytwentythree.DayEighteen.Direction.RIGHT;

class DayEighteenTest {
    private final DayEighteen dayEighteen = new DayEighteen();

    @Test
    void shouldParseInstruction() {
        DayEighteen.Instruction instruction = dayEighteen.parse("R 6 (#70c710)");
        DayEighteen.Instruction expected = new DayEighteen.Instruction(RIGHT, 6, "70c710");

        Assertions.assertEquals(expected, instruction);
    }

}