package com.ruchij;

import com.ruchij.twentytwentyfour.DayTwo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class JavaApp {
    public static void main(String[] args) throws FileNotFoundException {
        Stream<String> fileContents = readFile(Paths.get("./input/2024/day-2.txt"));
        Object result = new DayTwo().solve(fileContents);

        System.out.println(result);
    }

    private static Stream<String> readFile(Path path) throws FileNotFoundException {
        FileReader fileReader = new FileReader(path.toFile());
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        return bufferedReader.lines();
    }
}
