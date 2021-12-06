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

  def solve(input: List[String]) = parse(input)

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
