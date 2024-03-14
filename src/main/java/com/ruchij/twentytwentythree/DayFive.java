package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayFive implements JavaSolution {
  record Mapping(long destinationStart, long sourceStart, long length) {
    public long sourceEnd() {
      return sourceStart + length;
    }
  }

  record Range(long start, long length) {
    public long end() {
      return start + length;
    }

    static Range create(long start, long end) {
      return new Range(start, end - start);
    }
  }

  record RangeMapping(Range range, boolean transformed) {
  }

  record Plan(List<Range> seeds, List<List<Mapping>> mappings) {
  }

  @Override
  public Object solve(Stream<String> input) {
    Plan plan = parse(input.toList());
    List<RangeMapping> current = plan.seeds.stream().map(range -> new RangeMapping(range, false)).toList();

    for (List<Mapping> mappings : plan.mappings) {
      for (Mapping mapping : mappings) {
        current =
            current.stream()
                .flatMap(rangeMapping -> {
                      if (rangeMapping.transformed) {
                        return Stream.of(rangeMapping);
                      } else {
                        return transform(rangeMapping.range, mapping).stream();
                      }
                    }
                )
                .toList();
      }

      current = current.stream().map(rangeMapping -> new RangeMapping(rangeMapping.range, false)).toList();
      long count = current.stream().mapToLong(rangeMapping -> rangeMapping.range.length).sum();
      System.out.println(count);
    }

    Optional<RangeMapping> first = current.stream().sorted(Comparator.comparingLong(rangeMapping -> rangeMapping.range.start)).findFirst();

    return first;
  }

  List<RangeMapping> transform(Range range, Mapping mapping) {
    if (range.end() <= mapping.sourceStart || range.start >= mapping.sourceEnd()) {
      return List.of(new RangeMapping(range, false));
    } else if (range.start >= mapping.sourceStart && range.end() <= mapping.sourceEnd()) {
      long diff = range.start - mapping.sourceStart;
      return List.of(new RangeMapping(new Range(mapping.destinationStart + diff, range.length), true));
    } else if (range.end() >= mapping.sourceEnd() && range.start() <= mapping.sourceStart) {
      Range left = Range.create(range.start, mapping.sourceStart);
      Range middle = new Range(mapping.destinationStart, mapping.length);
      Range right = Range.create(mapping.sourceEnd(), range.end());

      return List.of(new RangeMapping(left, false), new RangeMapping(middle, true), new RangeMapping(right, false));
    } else if (range.end() >= mapping.sourceEnd()) {
      long diff = range.start - mapping.sourceStart;
      Range left = new Range(mapping.destinationStart + diff, mapping.length - diff);
      Range right = new Range(mapping.sourceEnd(), range.end() - mapping.sourceEnd());

      return List.of(new RangeMapping(left, true), new RangeMapping(right, false));
    } else {
      Range left = new Range(range.start, mapping.sourceStart - range.start);
      Range right = new Range(mapping.destinationStart, range.end() - mapping.sourceStart);

      return List.of(new RangeMapping(left, false), new RangeMapping(right, true));
    }
  }


  private Plan parse(List<String> lines) {
    List<Long> seedNumbers = parseNumbers(lines.get(0).split(":")[1]);
    List<Range> ranges = new ArrayList<>();

    for (int i = 0; i < seedNumbers.size(); i += 2) {
      Range range = new Range(seedNumbers.get(i), seedNumbers.get(i + 1));
      ranges.add(range);
    }

    List<List<Mapping>> mappings = new ArrayList<>();
    List<Mapping> mapping = null;

    for (String line : lines.subList(2, lines.size())) {
      if (line.contains(":")) {
        if (mapping != null) {
          mappings.add(mapping);
        }

        mapping = new ArrayList<>();
      } else if (!line.trim().isBlank()) {
        mapping.add(parseMapping(line));
      }
    }

    mappings.add(mapping);

    Plan plan = new Plan(ranges, mappings);

    return plan;
  }

  private List<Long> parseNumbers(String line) {
    List<Long> numbers = Arrays.stream(line.split(" "))
        .map(String::trim)
        .filter(string -> !string.isBlank())
        .map(Long::parseLong)
        .toList();

    return numbers;
  }

  private Mapping parseMapping(String line) {
    List<Long> numbers = parseNumbers(line);

    Mapping mapping = new Mapping(numbers.get(0), numbers.get(1), numbers.get(2));

    return mapping;
  }
}
