package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DaySixteen implements JavaSolution {
    record Valve(String id, int flowRate, Set<String> connections) {
    }

    record Entry(String myValveId, String elephantValveId, Set<String> openedValves,
                 int myTimestamp,
                 int elephantTimestamp, int pressure) {
    }

    record Visit(String myValveId, String elephantValveId, int pressure) {
    }

    @Override
    public Object solve(Stream<String> input) {
        Instant start = Instant.now();

        Map<String, Valve> valves = parse(input);
        ArrayDeque<Entry> entries = new ArrayDeque<>();
        Entry seed = new Entry("AA", "AA", new HashSet<>(), 26, 26, 0);
        entries.add(seed);

        HashSet<Visit> visited = new HashSet<>();

        int max = 0;

        while (!entries.isEmpty()) {
            Entry entry = entries.poll();
            Valve myValve = valves.get(entry.myValveId);
            Valve elephantValve = valves.get(entry.elephantValveId);

            Visit visit = new Visit(entry.myValveId, entry.elephantValveId, entry.pressure);

            if (max < entry.pressure) {
                max = entry.pressure;
                System.out.println(max);
            }


            if (!visited.contains(visit)) {
                visited.add(visit);

                if (entry.myTimestamp > 1 || entry.elephantTimestamp > 1) {
                    boolean shouldOpenMyValve = myValve.flowRate > 0 && entry.myTimestamp > 1 && !entry.openedValves.contains(myValve.id);
                    boolean shouldOpenElephantValve = elephantValve.flowRate > 0 && entry.elephantTimestamp > 1 && !entry.openedValves.contains(elephantValve.id);

                    for (String myNext : myValve.connections) {
                        for (String elephantNext : elephantValve.connections) {
                            if (entry.myTimestamp > 1 && entry.elephantTimestamp > 1) {
                                Entry next = new Entry(myNext, elephantNext, entry.openedValves, entry.myTimestamp - 1, entry.elephantTimestamp - 1, entry.pressure);
                                entries.add(next);

                                if (shouldOpenElephantValve && shouldOpenMyValve && !myValve.id.equals(elephantValve.id)) {
                                    HashSet<String> openedValves = new HashSet<>(entry.openedValves);
                                    openedValves.add(myValve.id);
                                    openedValves.add(elephantValve.id);

                                    Entry nextEntry = new Entry(myNext, elephantNext, openedValves, entry.myTimestamp - 2, entry.elephantTimestamp - 2, entry.pressure + (entry.myTimestamp - 1) * myValve.flowRate + (entry.elephantTimestamp - 1) * elephantValve.flowRate);
                                    entries.add(nextEntry);
                                }

                                if (shouldOpenMyValve) {
                                    HashSet<String> openedValves = new HashSet<>(entry.openedValves);
                                    openedValves.add(myValve.id);

                                    Entry nextEntry = new Entry(myNext, elephantNext, openedValves, entry.myTimestamp - 2, entry.elephantTimestamp - 1, entry.pressure + (entry.myTimestamp - 1) * myValve.flowRate);
                                    entries.add(nextEntry);
                                }

                                if (shouldOpenElephantValve) {
                                    HashSet<String> openedValves = new HashSet<>(entry.openedValves);
                                    openedValves.add(elephantValve.id);

                                    Entry nextEntry = new Entry(myNext, elephantNext, openedValves, entry.myTimestamp - 1, entry.elephantTimestamp - 2, entry.pressure + (entry.elephantTimestamp - 1) * elephantValve.flowRate);
                                    entries.add(nextEntry);
                                }
                            } else if (entry.myTimestamp > 1) {
                                Entry next = new Entry(myNext, elephantValve.id, entry.openedValves, entry.myTimestamp - 1, entry.elephantTimestamp, entry.pressure);
                                entries.add(next);

                                if (shouldOpenMyValve) {
                                    HashSet<String> openedValves = new HashSet<>(entry.openedValves);
                                    openedValves.add(myValve.id);

                                    Entry openingValve = new Entry(myNext, elephantValve.id, openedValves, entry.myTimestamp - 2, entry.elephantTimestamp, entry.pressure + (entry.myTimestamp - 1) * myValve.flowRate);
                                    entries.add(openingValve);
                                }
                            } else {
                                Entry next = new Entry(myValve.id, elephantNext, entry.openedValves, entry.myTimestamp, entry.elephantTimestamp - 1, entry.pressure);
                                entries.add(next);

                                if (shouldOpenElephantValve) {
                                    HashSet<String> openedValves = new HashSet<>(entry.openedValves);
                                    openedValves.add(elephantValve.id);

                                    Entry openingValve = new Entry(myValve.id, elephantNext, openedValves, entry.myTimestamp, entry.elephantTimestamp - 2, entry.pressure + (entry.elephantTimestamp - 1) * elephantValve.flowRate);
                                    entries.add(openingValve);
                                }
                            }
                        }
                    }
                }
            }
        }

        // AA -> DD -> CC -> BB -> AA -> II -> JJ -> II -> AA -> DD -> EE -> FF -> GG -> HH -> GG -> FF -> EE -> DD -> CC
        // 1651

        System.out.println("Duration: %sms".formatted(Instant.now().toEpochMilli() - start.toEpochMilli()));

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
