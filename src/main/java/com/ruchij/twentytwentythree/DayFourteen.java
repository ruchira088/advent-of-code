package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
        int cycles = 1_000_000_000;
        HashSet<String> visited = new HashSet<>();
        String firstMatch = null;
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < 1_000_000; i++) {
            String summary = summary(states);

           if (firstMatch != null && firstMatch.equals(summary)) {
               list.add(i);
               System.out.println(i);

               if (list.size() > 10) {
                   break;
               }
           }

           if (firstMatch == null) {
               if (visited.contains(summary)) {
                   firstMatch = summary;
               } else {
                   visited.add(summary);
               }
           }

            cycle(states);
        }

        int length = cycles - list.getFirst();
        int period = list.get(1) - list.getFirst();

        int result = length - (length / period) * period;

        State[][] first = parse(Arrays.stream(firstMatch.split("\n")));

        for (int i = 0; i < result; i++) {
            cycle(first);
        }

        return weight(first);
    }

    String summary(State[][] states) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int y = 0; y < states.length; y++) {
            for (int x = 0; x < states[y].length; x++) {
                State state = states[y][x];

                stringBuilder.append(state.symbol);
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
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

    void cycle(State[][] states) {
        tiltNorth(states);
        tiltWest(states);
        tiltSouth(states);
        tiltEast(states);
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

    void tiltSouth(State[][] states) {
        for (int x = 0; x < states[0].length; x++) {
            int edge = states.length - 1;

            for (int y = states.length - 1; y >= 0; y--) {
                State state = states[y][x];

                if (state == State.ROUND) {
                    states[y][x] = State.EMPTY;
                    states[edge][x] = State.ROUND;
                    edge--;
                } else if (state == State.SQUARE) {
                    edge = y - 1;
                }
            }
        }
    }

    void tiltWest(State[][] states) {
        for (int y = 0; y < states.length; y++) {
            int edge = 0;

            for (int x = 0; x < states[y].length; x++) {
                State state = states[y][x];

                if (state == State.ROUND) {
                    states[y][x] = State.EMPTY;
                    states[y][edge] = State.ROUND;
                    edge++;
                } else if (state == State.SQUARE) {
                    edge = x + 1;
                }
            }
        }
    }

    void tiltEast(State[][] states) {
        for (int y = 0; y < states.length; y++) {
            int edge = states[y].length - 1;

            for (int x = states[y].length - 1; x >= 0; x--) {
                State state = states[y][x];

                if (state == State.ROUND) {
                    states[y][x] = State.EMPTY;
                    states[y][edge] = State.ROUND;
                    edge--;
                } else if (state == State.SQUARE) {
                    edge = x - 1;
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
