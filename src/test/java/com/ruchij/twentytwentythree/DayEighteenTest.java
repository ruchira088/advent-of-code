package com.ruchij.twentytwentythree;


import org.junit.jupiter.api.Test;

import static com.ruchij.twentytwentythree.DayEighteen.Direction.DOWN;
import static com.ruchij.twentytwentythree.DayEighteen.Direction.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DayEighteenTest {
    private final DayEighteen dayEighteen = new DayEighteen();

    @Test
    void shouldParseInstruction() {
        DayEighteen.InputInstruction inputInstruction = dayEighteen.parse("R 6 (#70c710)");
        DayEighteen.InputInstruction expected = new DayEighteen.InputInstruction(RIGHT, 6, "70c710");

        assertEquals(expected, inputInstruction);
    }

    @Test
    void shouldConvertHexColorToInstruction() {
        assertEquals(new DayEighteen.Instruction(RIGHT, 461937), dayEighteen.hexToInstruction("70c710"));
        assertEquals(new DayEighteen.Instruction(DOWN, 863240), dayEighteen.hexToInstruction("d2c081"));
        assertEquals(new DayEighteen.Instruction(DOWN, 829975), dayEighteen.hexToInstruction("caa171"));
    }

}