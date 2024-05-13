package com.ruchij.twentytwentythree;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DayTwentyTwo implements JavaSolution {
  record Coordinate(int x, int y, int z) {}

  record Brick(Coordinate start, Coordinate end, List<Coordinate> coordinates) {
    public Brick(Coordinate start, Coordinate end) {
      this(start, end, calculateCoordinates(start, end));
    }

    @Override
    public String toString() {
      return "Brick{" +
          "end=" + end +
          ", start=" + start +
          '}';
    }

    Brick fall() {
      return new Brick(
          new Coordinate(start.x, start.y, start.z - 1),
          new Coordinate(end.x, end.y, end.z - 1)
      );
    }

    private static List<Coordinate> calculateCoordinates(Coordinate start, Coordinate end) {
      ArrayList<Coordinate> coordinates = new ArrayList<>();
      coordinates.add(start);
      coordinates.add(end);

      if (start.x != end.x) {
        for (int x = start.x; x <= end.x; x++) {
          coordinates.add(new Coordinate(x, start.y, start.z));
        }
      }

      if (start.y != end.y) {
        for (int y = start.y; y <= end.y; y++) {
          coordinates.add(new Coordinate(start.x, y, start.z));
        }
      }

      if (start.z != end.z) {
        for (int z = start.z; z <= end.z; z++) {
          coordinates.add(new Coordinate(start.x, start.y, z));
        }
      }

      return coordinates;
    }
  }

  @Override
  public Object solve(Stream<String> input) {
    List<Brick> bricks = parse(input);
    bricks.sort(Comparator.comparing(brick -> brick.start.z));

    Map<Coordinate, Brick> grid = new HashMap<>();
    Map<Brick, Set<Brick>> blocked = new HashMap<>();

    for (Brick brick : bricks) {
      Brick current = brick;
      boolean isBlocked = false;

      while (!isBlocked) {
        Brick fall = current.fall();
        Blocking blocking = obstructingBricks(grid, fall);

        if (blocking.onFloor || !blocking.bricks.isEmpty()) {
          isBlocked = true;
          blocked.put(current, blocking.bricks);
        } else {
          current = fall;
        }
      }

      for (Coordinate coordinate : current.coordinates) {
        grid.put(coordinate, current);
      }
    }

    HashSet<Brick> cantBeRemoved = new HashSet<>();

    for (Set<Brick> brickSet : blocked.values()) {
      if (brickSet.size() == 1) {
        cantBeRemoved.addAll(brickSet);
      }
    }

    return bricks.size() - cantBeRemoved.size();
  }

  record Blocking(boolean onFloor, Set<Brick> bricks) {}

  Blocking obstructingBricks(Map<Coordinate, Brick> grid, Brick brick) {
    Set<Brick> bricks = new HashSet<>();
    boolean onFloor = false;

    for (Coordinate coordinate : brick.coordinates) {
      Brick blockingBrick = grid.get(coordinate);

      if (blockingBrick != null) {
        bricks.add(blockingBrick);
      }

      if (coordinate.z == 0) {
        onFloor = true;
      }
    }

    return new Blocking(onFloor, bricks);
  }

  List<Brick> parse(Stream<String> input) {
    return new ArrayList<>(input.map(this::parseBrick).toList());
  }

  Brick parseBrick(String input) {
    String[] coordinate = input.split("~");
    Coordinate start = parseCoordinate(coordinate[0]);
    Coordinate end = parseCoordinate(coordinate[1]);

    return new Brick(start, end);
  }

  Coordinate parseCoordinate(String input) {
    String[] points = input.split(",");
    return new Coordinate(Integer.parseInt(points[0]), Integer.parseInt(points[1]), Integer.parseInt(points[2]));
  }
}
