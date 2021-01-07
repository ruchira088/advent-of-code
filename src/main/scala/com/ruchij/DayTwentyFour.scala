package com.ruchij

import cats.implicits._

object DayTwentyFour {
  case class Coordinate(x: Double, y: Double) {
    override def toString: String = s"[$x, $y]"
  }

  sealed trait TileSide {
    val label: String

    def move(coordinate: Coordinate): Coordinate
  }

  object TileSide {
    case object West extends TileSide {
      override val label: String = "w"

      override def move(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x - 1, coordinate.y)
    }

    case object NorthWest extends TileSide {
      override val label: String = "nw"

      override def move(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x - 0.5, coordinate.y + 1)
    }

    case object NorthEast extends TileSide {
      override val label: String = "ne"

      override def move(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x + 0.5, coordinate.y + 1)
    }

    case object East extends TileSide {
      override val label: String = "e"

      override def move(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x + 1, coordinate.y)
    }

    case object SouthEast extends TileSide {
      override val label: String = "se"

      override def move(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x + 0.5, coordinate.y - 1)
    }

    case object SouthWest extends TileSide {
      override val label: String = "sw"

      override def move(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x - 0.5, coordinate.y - 1)
    }

    val all = List(West, NorthWest, NorthEast, East, SouthEast, SouthWest)

    def unapply(instructions: String): Option[(TileSide, String)] =
      all.find(tile => instructions.startsWith(tile.label))
        .map {
          tile => tile -> instructions.drop(tile.label.length)
        }

  }

  def solve(input: List[String]) =
    input.traverse { line => parse(line, List.empty) }
      .map { _.map(move) }
      .map {
        _.groupBy(identity)
          .toList
          .map { case (coordinate, values) => values.length -> coordinate }
          .groupBy { case (count, _) => count }
          .map { case (count, values) => count -> values.length }
      }

  def parse(line: String, result: List[TileSide]): Either[String, List[TileSide]] =
    line match {
      case "" => Right(result)

      case TileSide(tile, rest) => parse(rest, result :+ tile)

      case _ => Left(s"""Unable to parse "$line"""")
    }

  def move(tileSides: List[TileSide]): Coordinate =
    tileSides.foldLeft(Coordinate(0, 0)) {
      case (coordinate, tileSide) => tileSide.move(coordinate)
    }

}
