package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.Arrays;
import java.util.stream.Stream;

public class DayEighteen implements JavaSolution {
    enum Direction {
        UP('U'), DOWN('D'), LEFT('L'), RIGHT('R');

        private final char symbol;

        Direction(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        static Direction fromSymbol(char symbol) {
            return Arrays.stream(values())
                    .filter(direction -> direction.symbol == symbol)
                    .findFirst()
                    .orElseThrow();
        }
    }

    record Instruction(Direction direction, int meters, String color) {}

    @Override
    public Object solve(Stream<String> input) {
        return null;
    }

    Instruction parse(String line) {
        String[] terms = line.trim().split(" ");
        Direction direction = Direction.fromSymbol(terms[0].charAt(0));
        int meters = Integer.parseInt(terms[1]);
        String color = terms[2].substring(2, terms[2].length() - 1);

        return new Instruction(direction, meters, color);
    }
}
