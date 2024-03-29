package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DayTwelve implements JavaSolution {
    enum State {
        OPERATIONAL('.'), DAMAGED('#'), UNKNOWN('?');

        final char character;

        State(char character) {
            this.character = character;
        }

        @Override
        public String toString() {
            return String.valueOf(character);
        }
    }

    record Line(List<State> springs, List<Integer> damaged) {
    }

    @Override
    public Object solve(Stream<String> input) {
        List<Line> lines = input.map(this::parse).toList();


        return null;
    }

    List<Integer> describe(List<State> springs) {
        ArrayList<Integer> groups = new ArrayList<>();

        int count = 0;

        for (State state : springs) {
            if (state == State.OPERATIONAL) {
                if (count != 0) {
                    groups.add(count);
                    count = 0;
                }
            } else {
                count++;
            }
        }

        if (count != 0) {
            groups.add(count);
        }

        return groups;
    }

    boolean isValid(Line line) {
        return describe(line.springs).equals(line.damaged);
    }

    <T> List<T> addToHead(T head, List<T> tail) {
        ArrayList<T> list = new ArrayList<>();
        list.add(head);
        list.addAll(tail);

        return list;
    }

    int count(Line line) {
        return count(line.springs, line.damaged, false);
    }

    int count(List<State> states, List<Integer> damaged, boolean inGroup) {
//        System.out.println("states=%s, damaged=%s, inGroup=%s".formatted(states, damaged, inGroup));

        if (damaged.isEmpty() & states.isEmpty()) {
            return 1;
        } else if (damaged.isEmpty()) {
            State head = states.getFirst();

            if (head == State.OPERATIONAL || head == State.UNKNOWN) {
                return count(states.subList(1, states.size()), damaged, false);
            } else {
                return 0;
            }
        }
        if (states.isEmpty()) {
            return damaged.getFirst() == 0 ? 1 : 0;
        } else {
            State stateHead = states.getFirst();
            List<State> stateTail = states.subList(1, states.size());
            Integer damagedHead = damaged.getFirst();
            List<Integer> damageTail = damaged.subList(1, damaged.size());

            int count = 0;

            if (inGroup) {
                if (damagedHead > 0) {
                    if (stateHead == State.UNKNOWN || stateHead == State.DAMAGED) {
                        count += count(stateTail, addToHead(damagedHead - 1, damageTail), true);
                    }
                } else {
                    if (stateHead == State.UNKNOWN || stateHead == State.OPERATIONAL) {
                        count += count(stateTail, damageTail, false);
                    }
                }
            } else {
                if (stateHead == State.UNKNOWN) {
                    count += count(addToHead(State.DAMAGED, stateTail), damaged, false);
                    count += count(addToHead(State.OPERATIONAL, stateTail), damaged, false);
                } else if (stateHead == State.DAMAGED) {
                    count += count(stateTail, addToHead(damagedHead - 1, damageTail), true);
                } else {
                    count += count(stateTail, damaged, false);
                }
            }

            System.out.println("states=%s, damaged=%s, count=%s".formatted(states, damaged, count));

            return count;
        }
    }

    List<State> parseStates(String inputLine) {
        ArrayList<State> springs = new ArrayList<>();

        for (char character : inputLine.toCharArray()) {
            State state =
                    Arrays.stream(State.values())
                            .filter(value -> value.character == character).findFirst().orElseThrow();

            springs.add(state);
        }

        return springs;
    }

    Line parse(String inputLine) {
        String[] group = inputLine.split(" ");

        List<State> springs = parseStates(group[0]);

        List<Integer> damaged =
                Arrays.stream(group[1].split(",")).map(Integer::parseInt).toList();

        Line line = new Line(springs, damaged);

        return line;
    }
}
