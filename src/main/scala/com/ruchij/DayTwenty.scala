package com.ruchij

import com.ruchij.DayTwo.IntValue

import cats.implicits._
import scala.util.matching.Regex

object DayTwenty {
  sealed trait Color

  object Color {
    val all: List[Color] = List(Black, White)

    case object Black extends Color {
      override def toString: String = "#"
    }

    case object White extends Color {
      override def toString: String = "."
    }
  }

  case class Tile(id: Int, grid: List[List[Color]]) { self =>
    val topBorder: List[Color] = grid.headOption.toList.flatten

    val bottomBorder: List[Color] = grid.lastOption.toList.flatten

    val leftBorder: List[Color] = grid.flatMap(_.headOption)

    val rightBorder: List[Color] = grid.flatMap(_.lastOption)

    val borders: List[List[Color]] = {
      val sub = List(topBorder, rightBorder, bottomBorder, leftBorder)

      sub ++ sub.map(_.reverse)
    }

    def verticalFlip: Tile = Tile(id, grid.reverse)

    def horizontalFlip: Tile = Tile(id, grid.map(_.reverse))

    def rotateClockwise: Tile = Tile(id, rotate(grid))

    def allPermutations: Set[Tile] =
      allRotations ++ verticalFlip.allRotations ++ horizontalFlip.allRotations ++ horizontalFlip.verticalFlip.allRotations

    def allRotations: Set[Tile] = {
      val one = rotateClockwise
      val two = one.rotateClockwise
      val three = two.rotateClockwise

      Set(self, one, two, three)
    }
  }

  val TileNumber: Regex = "Tile (\\d+):".r

  def rotate[A](grid: List[List[A]]): List[List[A]] =
    if (grid.isEmpty || grid.exists(_.isEmpty)) List.empty[List[A]]
    else grid.flatMap(_.headOption).reverse :: rotate(grid.map(_.tail))

  def solve(input: List[String]) =
    parseInput(input).map {
      tiles =>
        noMatch(tiles)
    }

  def noMatch(tiles: List[Tile]) =
    tiles.filter {
      tile =>
        val allBorders: List[List[Color]] = tiles.filter(_.id != tile.id).flatMap(_.borders)

        tile.borders.count(allBorders.contains) == 4
    }
      .map(_.id.toLong)
      .product


  def findRow(tile: Tile, tiles: List[Tile]): List[Tile] =
    tiles
      .find(_.borders.contains(tile.rightBorder))
      .flatMap { _.allPermutations.find(_.leftBorder == tile.rightBorder) }
      .fold[List[Tile]](List.empty) {
        next => next :: findRow(next, tiles.filter(_.id != next.id))
      }

  def prettyPrint[A](grid: List[List[A]]): String =
    grid.map(_.mkString).mkString("\n")

  def parseInput(input: List[String]): Either[String, List[Tile]] =
    group(input).traverse(parseTile)

  def group(input: List[String]): List[List[String]] =
    if (input.isEmpty) List.empty
    else input.takeWhile(_.trim.nonEmpty) :: group(input.dropWhile(_.trim.nonEmpty).dropWhile(_.trim.isEmpty))

  def parseTile(input: List[String]): Either[String, Tile] =
    input.headOption
      .collect {
        case TileNumber(IntValue(number)) => number
      }
      .fold[Either[String, Tile]](Left("Unable to extract tile number")) { number =>
        parseGrid(input.tail).map { grid =>
          Tile(number, grid)
        }
      }

  def parseGrid(input: List[String]): Either[String, List[List[Color]]] =
    input.traverse { line =>
      line.toList.traverse { character =>
        Color.all
          .find(_.toString.equalsIgnoreCase(character.toString))
          .fold[Either[String, Color]](Left(s"""Unable to parse "$character" as a ${classOf[Color].getSimpleName}"""))(
            Right.apply
          )
      }
    }
}
