package com.ruchij.twentynineteen;

import com.ruchij.JavaSolution;

import java.util.Arrays;
import java.util.stream.Stream;

public class DayTwo implements JavaSolution {

    @Override
    public Object solve(Stream<String> input) {
        Long[] numbers = parse(input);

        return numbers[0];
    }

    private Long[] parse(Stream<String> input) {
        return input.flatMap(
                line -> Arrays.stream(line.split(","))
        ).map(Long::parseLong).toArray(Long[]::new);
    }

}
