package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DaySixteen implements JavaSolution {
    record Valve(String id, int flowRate, Set<String> connections) {
    }

    record Entry(String valveId, Set<String> openedValves, int timestamp, int pressure) {
    }

    record Visit(String position, Set<String> openedValves, int pressure) {
    }

    @Override
    public Object solve(Stream<String> input) {
        Map<String, Valve> valves = parse(input);
        Set<String> noZeroValves =
                valves.values().stream()
                        .filter(valve -> valve.flowRate != 0)
                        .map(Valve::id)
                        .collect(Collectors.toSet());

        ArrayDeque<Entry> entries = new ArrayDeque<>();
        Entry seed = new Entry("AA", new HashSet<>(), 30, 0);
        entries.add(seed);

        HashSet<Visit> visited = new HashSet<>();

        long max = 0;

        while (!entries.isEmpty()) {
            Entry entry = entries.poll();
            Valve valve = valves.get(entry.valveId);

            Visit visit = new Visit(entry.valveId, entry.openedValves, entry.pressure);

            if (max < entry.pressure) {
                max = entry.pressure;
            }

            if (!visited.contains(visit) && !entry.openedValves.containsAll(noZeroValves)) {
                visited.add(visit);

                if (entry.timestamp > 1) {
                    boolean shouldOpenValve = valve.flowRate > 0 && !entry.openedValves.contains(valve.id);
                    HashSet<String> openedValves = new HashSet<>(entry.openedValves);
                    openedValves.add(valve.id);

                    for (String next : valve.connections) {
                        Entry notOpeningValve = new Entry(next, entry.openedValves, entry.timestamp - 1, entry.pressure);
                        entries.add(notOpeningValve);

                        if (shouldOpenValve) {
                            Entry openingValve = new Entry(next, openedValves, entry.timestamp - 2, entry.pressure + (entry.timestamp - 1) * valve.flowRate);
                            entries.add(openingValve);
                        }
                    }
                }
            }
        }

        // AA -> DD -> CC -> BB -> AA -> II -> JJ -> II -> AA -> DD -> EE -> FF -> GG -> HH -> GG -> FF -> EE -> DD -> CC
        // 1651

        return max;
    }

    Map<String, Valve> parse(Stream<String> input) {
        Iterator<String> iterator = input.iterator();
        HashMap<String, Valve> valves = new HashMap<>();

        while (iterator.hasNext()) {
            String line = iterator.next();
            Valve valve = parse(line);
            valves.put(valve.id, valve);
        }

        return valves;
    }

    Valve parse(String line) {
        Pattern pattern = Pattern.compile("Valve (\\S+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String id = matcher.group(1).trim();
            int flowRate = Integer.parseInt(matcher.group(2));
            Set<String> connections = Arrays.stream(matcher.group(3).split(",")).map(String::trim).collect(Collectors.toSet());

            return new Valve(id, flowRate, connections);
        } else {
            throw new IllegalArgumentException("Unable to parse \"%s\"".formatted(line));
        }
    }

}
