package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue
import Utils._

object DayFive {
  case class Coordinate(x: Int, y: Int) {
    override def toString: String = s"($x, $y)"
  }

  case class Line(start: Coordinate, end: Coordinate) {
    override def toString: String = s"$start -> $end"
  }

  object Line {
    def coordinates(line: Line): Seq[Coordinate] =
      if (line.start.x == line.end.x)
        Range.inclusive(math.min(line.start.y, line.end.y), math.max(line.start.y, line.end.y)).map(y => Coordinate(line.start.x, y))
      else if (line.start.y == line.end.y)
        Range.inclusive(math.min(line.start.x, line.end.x), math.max(line.start.x, line.end.x)).map(x => Coordinate(x, line.start.y))
      else Seq.empty
  }

  class Tile(var count: Int) {
    def increment() = count += 1

    override def toString: String = count.toString
  }

  case class Grid(value: Seq[Seq[Tile]]) {
    override def toString: String =
      value.map(_.mkString(" ")).mkString("\n")
  }

  def solve(input: List[String]) =
    parse(input).map { lines =>
      val dimensions = gridSize(lines)

      val grid = Grid(Seq.fill(dimensions.y)(Seq.fill(dimensions.x)(new Tile(0))))

      lines.flatMap(Line.coordinates)
        .groupBy(identity)
        .map {
          case (key, value) => key -> value.size
        }
        .count {
          case (_, value) => value > 1
        }
    }

  def parse(input: List[String]): Either[String, Seq[Line]] =
    input.traverse { line =>
      line.split("->").toList.traverse(parseCoordinate).flatMap {
        case start :: end :: Nil => Right(Line(start, end))
        case _ => Left(s"Unable to parse \"$line\" as a line")
      }
    }

  def parseCoordinate(input: String): Either[String, Coordinate] =
    input.split(',').map(_.trim).toList match {
      case IntValue(x) :: IntValue(y) :: Nil => Right(Coordinate(x, y))
      case _ => Left(s"""Unable to parse "$input" as a coordinate""")
    }

  def gridSize(lines: Seq[Line]): Coordinate =
    lines.foldLeft(Coordinate(0, 0)) {
      (size, line) =>
        Coordinate(
          math.max(math.max(size.x, line.start.x), line.end.x),
          math.max(math.max(size.y, line.start.y), line.end.y)
        )
    }
}
