package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DayFourteen implements JavaSolution {
    record Coordinate(int x, int y) {}

    enum State {
        ROUND('O'), SQUARE('#'), EMPTY('.');

        final char symbol;

        State(char symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return String.valueOf(symbol);
        }

        static State parse(char character) {
            return Arrays.stream(values())
                    .filter(state -> state.symbol == character)
                    .findFirst()
                    .orElseThrow();
        }
    }

    @Override
    public Object solve(Stream<String> input) {
        State[][] states = parse(input);

        tiltNorth(states);

        return weight(states);
    }

    long weight(State[][] states) {
        long value = 0;

        for (int x = 0; x < states[0].length; x++) {
            for (int y = 0; y < states.length; y++) {
                State state = states[y][x];

                if (state == State.ROUND) {
                    value += (states.length - y);
                }
            }
        }

        return value;
    }

    void tiltNorth(State[][] states) {
        for (int x = 0; x < states[0].length; x++) {
            int edge = 0;

            for (int y = 0; y < states.length; y++) {
                State state = states[y][x];

                if (state == State.ROUND) {
                    states[y][x] = State.EMPTY;
                    states[edge][x] = State.ROUND;
                    edge++;
                } else if (state == State.SQUARE) {
                    edge = y + 1;
                }
            }
        }
    }

    State[][] parse(Stream<String> input) {
        List<String> lines = input.toList();
        List<State[]> rows = new ArrayList<>();

        for (String line : lines) {
            char[] chars = line.toCharArray();
            State[] row = new State[chars.length];

            for (int column = 0; column < chars.length; column++) {
                State state = State.parse(chars[column]);
                row[column] = state;
            }

            rows.add(row);
        }

        State[][] array = rows.toArray(State[][]::new);

        return array;
    }
}
