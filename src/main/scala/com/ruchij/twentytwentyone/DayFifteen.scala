package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

object DayFifteen {
  case class Coordinate(x: Int, y: Int)
  case class Path(route: List[Coordinate], risk: Int)

  object Coordinate {
    def neighbours(coordinate: Coordinate): Set[Coordinate] =
      Set(
        Coordinate(coordinate.x, coordinate.y + 1),
        Coordinate(coordinate.x, coordinate.y - 1),
        Coordinate(coordinate.x + 1, coordinate.y),
        Coordinate(coordinate.x - 1, coordinate.y)
      )

  }

  case class Cave(grid: Map[Coordinate, Int])

  def solve(input: List[String]) =  {
    val cave = parse(input)


    val destination = cave.foldLeft(Coordinate(0, 0)) { case (cord, (current, _)) => Coordinate(math.max(cord.x, current.x), math.max(cord.y, current.y))}

    deduce(cave, Map.empty, destination)(destination) - cave(Coordinate(0, 0))
  }

  def parse(input: List[String]) =
    input.zipWithIndex
      .flatMap { case (line, y) =>
        line.split("").zipWithIndex.collect {
          case (IntValue(value), x) => Coordinate(x, y) -> value
        }
      }
      .toMap

  def deduce(cave: Map[Coordinate, Int], grid: Map[Coordinate, Int], dimensions: Coordinate): Map[Coordinate, Int] = {
    val (result, hasChanged) = Range.inclusive(0, dimensions.y).foldLeft((grid, false)) {
      case ((grid, changed), y) =>
        Range.inclusive(0, dimensions.x).foldLeft((grid, changed)) {
          case ((grid, changed), x) =>
            val coordinate = Coordinate(x, y)
            val neighbour = Coordinate.neighbours(coordinate).flatMap(grid.get).minOption.getOrElse(0)
            val tileRisk = cave(coordinate)
            val current = grid.get(coordinate)

            if (current.exists(_ <= neighbour + tileRisk))
              (grid, changed)
            else
              (grid.updated(coordinate, neighbour + tileRisk), true)
        }
    }

    if (hasChanged) deduce(cave, result, dimensions) else result
  }

  def findPath(routes: Vector[Path], cave: Map[Coordinate, Int], destination: Coordinate): Path =
    routes.headOption match {
      case Some(path) =>
        path.route match {
          case head :: _ if head == destination => path

          case head :: tail =>
            val next =
              Coordinate.neighbours(head)
                .filter(neighbour => !tail.contains(neighbour))
                .flatMap {
                  neighbour =>
                    cave.get(neighbour).map {
                      risk => Path(neighbour :: head :: tail, path.risk + risk)
                    }
                }
                .toVector

            findPath((routes.tail ++ next).sortBy(_.risk), cave, destination)
        }

      case _ => throw new IllegalArgumentException("Empty routes")
    }

}
