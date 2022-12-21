package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DayThirteen implements JavaSolution {
    interface Packet {
        record PacketList(List<Packet> values) implements Packet {
            @Override
            public String toString() {
                return "[" + values.stream().map(Objects::toString).collect(Collectors.joining(",")) + "]";
            }
        }

        record PacketValue(int value) implements Packet {
            @Override
            public String toString() {
                return Integer.toString(value);
            }
        }
    }

    record PacketPair(Packet first, Packet second) {
    }

    record ParseResult(Packet packet, int index) {
    }

    @Override
    public Object solve(Stream<String> input) {
        List<PacketPair> packetPairs = parse(input);
        int result = 0;

        for (int i = 0; i < packetPairs.size(); i++) {
            if (isOrdered(packetPairs.get(i))) {
                result += (i + 1);
            }
        }

        return result;
    }

    List<PacketPair> parse(Stream<String> input) {
        List<PacketPair> packetPairs = new ArrayList<>();
        Iterator<String> iterator = input.iterator();
        ArrayDeque<Packet> arrayDeque = new ArrayDeque<>();

        while (iterator.hasNext()) {
            String line = iterator.next();

            if (line.isEmpty()) {
                packetPairs.add(new PacketPair(arrayDeque.removeFirst(), arrayDeque.removeFirst()));
            } else {
                arrayDeque.add(parse(line));
            }
        }

        if (!arrayDeque.isEmpty()) {
            packetPairs.add(new PacketPair(arrayDeque.removeFirst(), arrayDeque.removeFirst()));
        }

        return packetPairs;
    }

    Packet parse(String term) {
        if (term.startsWith("[")) {
            ArrayList<Packet> packets = new ArrayList<>();
            int count = 0;

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < term.length(); i++) {
                char character = term.charAt(i);
                if (character == '[') {
                    if (count != 0) {
                        stringBuilder.append(character);
                    }
                    count++;
                } else if (character == ']') {
                    count--;

                    if (count != 0) {
                        stringBuilder.append(character);
                    } else {
                        String string = stringBuilder.toString();

                        if (string.isEmpty()) {
                            return new Packet.PacketList(List.of());
                        } else {
                            packets.add(parse(string));
                        }
                    }
                } else if (character == ',') {
                    if (count == 1) {
                        packets.add(parse(stringBuilder.toString()));
                        stringBuilder = new StringBuilder();
                    } else {
                        stringBuilder.append(character);
                    }
                } else {
                    stringBuilder.append(character);
                }
            }

            return new Packet.PacketList(packets);
        } else {
            return new Packet.PacketValue(Integer.parseInt(term));
        }
    }

    boolean isOrdered(PacketPair packetPair) {
        return isOrdered(packetPair.first, packetPair.second) > 0;
    }

    int isOrdered(Packet packetOne, Packet packetTwo) {
        if (packetOne instanceof Packet.PacketValue packetValueOne) {
            if (packetTwo instanceof Packet.PacketValue packetValueTwo) {
                return packetValueTwo.value - packetValueOne.value;
            } else {
                return isOrdered(new Packet.PacketList(List.of(packetOne)), packetTwo);
            }
        } else {
            Packet.PacketList packetListOne = (Packet.PacketList) packetOne;

            if (packetTwo instanceof Packet.PacketList packetListTwo) {
                int max = Math.min(packetListOne.values.size(), packetListTwo.values.size());

                for (int i = 0; i < max; i++) {
                    int result = isOrdered(packetListOne.values().get(i), packetListTwo.values().get(i));

                    if (result != 0) {
                        return result;
                    }
                }

                return packetListTwo.values.size() - packetListOne.values.size();
            } else {
                return isOrdered(packetOne, new Packet.PacketList(List.of(packetTwo)));
            }
        }
    }
}
