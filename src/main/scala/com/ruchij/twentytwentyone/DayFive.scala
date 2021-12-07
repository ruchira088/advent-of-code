package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue
import Utils._

import scala.annotation.tailrec

object DayFive {
  case class Coordinate(x: Int, y: Int) {
    override def toString: String = s"($x, $y)"
  }

  case class Line(start: Coordinate, end: Coordinate) {
    def gradient: Option[Int] =
      if (start.x == end.x) None else Some((end.y - start.y)/(end.x - start.x))

    override def toString: String = s"$start -> $end"
  }

  object Line {
    def coordinates(line: Line): Seq[Coordinate] =
      if (line.start.x == line.end.x)
        Range.inclusive(math.min(line.start.y, line.end.y), math.max(line.start.y, line.end.y)).map(y => Coordinate(line.start.x, y))
      else if (line.start.y == line.end.y)
        Range.inclusive(math.min(line.start.x, line.end.x), math.max(line.start.x, line.end.x)).map(x => Coordinate(x, line.start.y))
      else if (line.gradient.exists(gradient => math.abs(gradient) == 1)) {
        val (first, second) = if (line.start.x < line.end.x) (line.start, line.end) else (line.end, line.start)
        val gradient = line.gradient.getOrElse(throw new Exception("Gradient is infinite"))

        @tailrec
        def cords(current: Coordinate, end: Coordinate, result: Seq[Coordinate]): Seq[Coordinate] = {
          val updated = result :+ current

          if (current == end) updated else cords(Coordinate(current.x + 1, current.y + gradient), end, updated)
        }

        cords(first, second, Seq.empty)
      }
      else Seq.empty
  }

  def solve(input: List[String]) =
    parse(input).map { lines =>
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

}
