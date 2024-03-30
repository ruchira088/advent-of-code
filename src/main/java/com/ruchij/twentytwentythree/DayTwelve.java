package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
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

    record Key(List<State> springs, List<Integer> damaged, boolean inGroup) {}

    record Line(List<State> springs, List<Integer> damaged) {
    }

    @Override
    public Object solve(Stream<String> input) {
        long result = 0;
        int index = 0;

        HashMap<Key, Long> cache = new HashMap<>();

        for (String inputLine : input.toList()) {
            Line line = unfold(parse(inputLine));
            long count = count(line, cache);
            result += count;

            System.out.println("%s -> %s".formatted(index++, count));
        }

        return result;
    }

    Line unfold(Line line) {
        ArrayList<State> states = new ArrayList<>(line.springs);
        ArrayList<Integer> integers = new ArrayList<>(line.damaged);

        for (int i = 0; i < 4; i++) {
            states.add(State.UNKNOWN);
            states.addAll(line.springs);
            integers.addAll(line.damaged);
        }

        return new Line(states, integers);
    }

    <T> List<T> addToHead(T head, List<T> tail) {
        ArrayList<T> list = new ArrayList<>();
        list.add(head);
        list.addAll(tail);

        return list;
    }

    <T> List<T> addToTail(List<T> init, T last) {
        ArrayList<T> list = new ArrayList<>(init);
        list.add(last);

        return list;
    }

    long count(Line line, Map<Key, Long> cache) {
        return count(line.springs, line.damaged, false, cache);
    }

    long count(List<State> states, List<Integer> damaged, boolean inGroup, Map<Key, Long> cache) {
//        System.out.println("states=%s, damaged=%s, inGroup=%s".formatted(states, damaged, inGroup));

        Long result = cache.get(new Key(states, damaged, inGroup));

        if (result != null) {
            return result;
        }

        if (damaged.isEmpty() & states.isEmpty()) {
            return 1;
        } else if (damaged.isEmpty()) {
            State head = states.getFirst();

            if (head == State.OPERATIONAL || head == State.UNKNOWN) {
                return count(states.subList(1, states.size()), damaged, false, cache);
            } else {
                return 0;
            }
        }
        if (states.isEmpty()) {
            Integer head = damaged.getFirst();

            return head == 0 ? count(states, damaged.subList(1, damaged.size()), inGroup, cache) : 0;
        } else {
            State stateHead = states.getFirst();
            List<State> stateTail = states.subList(1, states.size());
            Integer damagedHead = damaged.getFirst();
            List<Integer> damageTail = damaged.subList(1, damaged.size());

            long count = 0;

            if (inGroup) {
                if (damagedHead > 0) {
                    if (stateHead == State.UNKNOWN || stateHead == State.DAMAGED) {
                        count += count(stateTail, addToHead(damagedHead - 1, damageTail), true, cache);
                    }
                } else {
                    if (stateHead == State.UNKNOWN || stateHead == State.OPERATIONAL) {
                        count += count(stateTail, damageTail, false, cache);
                    }
                }
            } else {
                if (stateHead == State.UNKNOWN) {
                    count += count(addToHead(State.DAMAGED, stateTail), damaged, false, cache);
                    count += count(addToHead(State.OPERATIONAL, stateTail), damaged, false, cache);
                } else if (stateHead == State.DAMAGED) {
                    count += count(stateTail, addToHead(damagedHead - 1, damageTail), true, cache);
                } else {
                    count += count(stateTail, damaged, false, cache);
                }
            }

            cache.put(new Key(states, damaged, inGroup), count);

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
