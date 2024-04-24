package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayTwenty implements JavaSolution {
    enum Pulse {
        HIGH, LOW
    }

    private sealed interface Module {
        String getLabel();

        Optional<Pulse> process(Pulse pulse, String sender);

        default void registerInputs(Set<String> inputs) {
        }

        List<String> getNext();
    }

    private final class FlipFlop implements Module {
        private final String label;
        private final List<String> next;
        private boolean on;

        private FlipFlop(String label, List<String> next) {
            this.label = label;
            this.next = next;
            this.on = false;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public Optional<Pulse> process(Pulse pulse, String sender) {
            if (pulse == Pulse.HIGH) {
                return Optional.empty();
            } else if (on) {
                on = false;
                return Optional.of(Pulse.LOW);
            } else {
                on = true;
                return Optional.of(Pulse.HIGH);
            }
        }

        @Override
        public List<String> getNext() {
            return next;
        }
    }

    private final class Conjunction implements Module {
        private final String label;
        private final List<String> next;
        private final Map<String, Pulse> state;

        private Conjunction(String label, List<String> next) {
            this.label = label;
            this.next = next;
            this.state = new HashMap<>();
        }

        @Override
        public void registerInputs(Set<String> inputs) {
            inputs.forEach(input -> state.put(input, Pulse.LOW));
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public Optional<Pulse> process(Pulse pulse, String sender) {
            state.put(sender, pulse);

            boolean isAllHigh = state.values().stream().allMatch(value -> value.equals(Pulse.HIGH));

            if (isAllHigh) {
                return Optional.of(Pulse.LOW);
            } else {
                return Optional.of(Pulse.HIGH);
            }
        }

        @Override
        public List<String> getNext() {
            return next;
        }
    }

    private final class Broadcaster implements Module {
        private static final String LABEL = "broadcaster";
        private final List<String> connections;

        private Broadcaster(List<String> connections) {
            this.connections = connections;
        }

        @Override
        public String getLabel() {
            return LABEL;
        }

        @Override
        public Optional<Pulse> process(Pulse pulse, String sender) {
            return Optional.of(Pulse.LOW);
        }

        @Override
        public List<String> getNext() {
            return connections;
        }
    }

    @Override
    public Object solve(Stream<String> lines) {
        HashMap<String, Module> modules = new HashMap<>();
        HashMap<String, Set<String>> inputs = new HashMap<>();

        lines.map(this::parse)
                .forEach(module -> {
                    modules.put(module.getLabel(), module);
                    module.getNext().forEach(next -> {
                        Set<String> existing = inputs.getOrDefault(next, new HashSet<>());
                        existing.add(module.getLabel());
                        inputs.put(next, existing);
                    });
                });

        for (Map.Entry<String, Set<String>> entry : inputs.entrySet()) {
            Module module = modules.get(entry.getKey());
            if (module != null) {
                module.registerInputs(entry.getValue());
            }
        }

        HashMap<Pulse, Long> count = new HashMap<>();

        for (int i = 0; i < 1000; i++) {
            sendPulse(modules, count);
        }

        Long result = count.values().stream().reduce(1L, (a, b) -> a * b);

        return result;
    }

    void sendPulse(Map<String, Module> modules, Map<Pulse, Long> count) {
        ArrayDeque<State> deque = new ArrayDeque<>();
        deque.add(new State(Broadcaster.LABEL, Pulse.LOW, null));

        while (!deque.isEmpty()) {
            State state = deque.poll();
            Module module = modules.get(state.label);
            Long current = count.getOrDefault(state.pulse, 0L);
            count.put(state.pulse, current + 1);

            if (module != null) {
                Optional<Pulse> pulseOptional = module.process(state.pulse, state.sender);

                pulseOptional.ifPresent(pulse -> {
                    module.getNext().forEach(next -> {
                        deque.add(new State(next, pulse, module.getLabel()));
                    });
                });
            }
        }
    }

    record State(String label, Pulse pulse, String sender) {
    }

    private Module parse(String input) {
        String[] strings = input.split("->");
        String source = strings[0].trim();
        List<String> next = Arrays.stream(strings[1].trim().split(","))
                .map(String::trim)
                .toList();

        if (source.startsWith("b")) {
            return new Broadcaster(next);
        } else {
            String label = source.substring(1);

            if (input.startsWith("%")) {
                return new FlipFlop(label, next);
            } else {
                return new Conjunction(label, next);
            }
        }
    }
}
