package com.ruchij.twentynineteen;

import com.ruchij.JavaSolution;

import java.util.stream.Stream;

public class DayOne implements JavaSolution {

    @Override
    public Object solve(Stream<String> input) {
        return input.map(line -> totalFuel(Long.decode(line))).mapToLong(value -> value).sum();
    }

    private long totalFuel(long mass) {
        long fuel = calculate(mass);

        if (fuel < 0) {
            return 0;
        } else {
            return fuel + totalFuel(fuel);
        }
    }

    private long calculate(long mass) {
        return mass / 3 - 2;
    }

}
