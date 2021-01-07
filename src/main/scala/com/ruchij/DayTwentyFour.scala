package com.ruchij

import cats.implicits._

object DayTwentyFour {
  case class Coordinate(x: Double, y: Double) {
    override def toString: String = s"[$x, $y]"
  }

  object Coordinate {
    val neighbours: Coordinate => Set[Coordinate] = {
      case Coordinate(x, y) =>
        Set(
          Coordinate(x + 1, y),
          Coordinate(x + 0.5, y + 1),
          Coordinate(x - 0.5, y + 1),
          Coordinate(x - 1, y),
          Coordinate(x - 0.5, y - 1),
          Coordinate(x + 0.5, y - 1)
        )
    }
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
      all
        .find(tile => instructions.startsWith(tile.label))
        .map { tile =>
          tile -> instructions.drop(tile.label.length)
        }

  }

  def solve(input: List[String]) =
    input
      .traverse { line =>
        parse(line, List.empty)
      }
      .map { _.map(move) }
      .map {
        _.groupBy(identity)
          .filter {
            case (_, values) => values.length % 2 == 1
          }
          .map {
            case (coordinate, _) => coordinate
          }
      }
      .map {
        blackTiles => calculateBlackTiles(blackTiles.toSet, 100).size
      }

  def calculateBlackTiles(blackTiles: Set[Coordinate], days: Int): Set[Coordinate] =
    if (days == 0) blackTiles
    else
      calculateBlackTiles(
        blackTiles
          .flatMap(Coordinate.neighbours)
          .filter { tile =>
            val isBlack = blackTiles.contains(tile)
            val blackNeighbours = Coordinate.neighbours(tile).count(blackTiles.contains)

            if (isBlack) blackNeighbours == 1 || blackNeighbours == 2
            else blackNeighbours == 2
          },
        days - 1
      )


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
