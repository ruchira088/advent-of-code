package com.ruchij.twentytwenty

import com.ruchij.twentytwenty.DayTwo.IntValue
import cats.implicits._
import com.ruchij.twentytwenty.DayTwenty.Color.Black

import scala.util.matching.Regex

object DayTwenty {
  sealed trait Color

  object Color {
    val all: List[Color] = List(Black, White, AnyColor)

    case object Black extends Color {
      override def toString: String = "#"
    }

    case object White extends Color {
      override def toString: String = "."
    }

    case object AnyColor extends Color {
      override def toString: String = " "
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

    def removeBorder: List[List[Color]] =
      grid.tail.init
        .map {
          row => row.tail.init
        }

  }

  def placeNext(gridOne: List[List[Color]], gridTwo: List[List[Color]]): List[List[Color]] =
    gridOne.zipAll(gridTwo, List.empty, List.empty).map { case (one, two) => one ++ two }

  val TileNumber: Regex = "Tile (\\d+):".r

  def rotate[A](grid: List[List[A]]): List[List[A]] =
    if (grid.isEmpty || grid.exists(_.isEmpty)) List.empty[List[A]]
    else grid.flatMap(_.headOption).reverse :: rotate(grid.map(_.tail))

  def solve(input: List[String]) =
    for {
      grid <- image(input)
      seaMonster <- seaMonsterCoordinates

      count = Tile(0, grid).allPermutations.map(_.grid).flatMap(image => findSeaMonsters(image, seaMonster)).size

      all = grid.map(_.count(_ == Black)).sum
      seaMonsterCount = seaMonster.size * count
    }
    yield all - seaMonsterCount

  def image(input: List[String]): Either[String, List[List[Color]]] =
    parseInput(input).flatMap {
      tiles =>
        corners(tiles).map {
          tile =>
            tile -> findOrigin(tile, tiles).size
        }
          .maxByOption { case (_, size) => size }
          .map {
            case (tile, _) =>
              (tile :: findRow(tile, tiles.filter(_.id != tile.id), _.bottomBorder, _.topBorder))
                .map {
                  value => value :: findRow(value, tiles.filter(_.id != value.id), _.rightBorder, _.leftBorder)
                }
                .flatMap {
                  row => row.foldLeft(List.empty[List[Color]]) {
                    case (acc, tile) => placeNext(acc, tile.removeBorder)
                  }
                }
          }
          .fold[Either[String, List[List[Color]]]](Left("Unable to create image"))(Right.apply)
    }

  def corners(tiles: List[Tile]) =
    tiles.filter {
      tile =>
        val allBorders: List[List[Color]] = tiles.filter(_.id != tile.id).flatMap(_.borders)

        tile.borders.count(allBorders.contains) == 4
    }

  def findOrigin(corner: Tile, tiles: List[Tile]) =
    (corner :: findRow(corner, tiles.filter(_.id != corner.id), _.rightBorder, _.leftBorder)) ++
      (corner :: findRow(corner, tiles.filter(_.id != corner.id), _.bottomBorder, _.topBorder))


  def findRow(tile: Tile, tiles: List[Tile], borderFn: Tile => List[Color], matcherFn: Tile => List[Color]): List[Tile] =
    tiles
      .find(_.borders.contains(borderFn(tile)))
      .flatMap { _.allPermutations.find(value => borderFn(tile) == matcherFn(value)) }
      .fold[List[Tile]](List.empty) {
        next => next :: findRow(next, tiles.filter(_.id != next.id), borderFn, matcherFn)
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

  implicit class GridWrapper[+A](grid: List[List[A]]) {
    def getValue(x: Int, y: Int): Option[A] =
      for {
        row <- grid.get(y)
        value <- row.get(x)
      }
      yield value
  }

  case class Coordinate(x: Int, y: Int) {
    def shiftRight: Coordinate = Coordinate(x + 1, y)

    def shiftToNextRow: Coordinate = Coordinate(0, y + 1)

    def withOffset(coordinate: Coordinate): Coordinate = Coordinate(x + coordinate.x, y + coordinate.y)
  }

  def allCoordinates[A](grid: List[List[A]]): List[(Coordinate, A)] =
    grid.zipWithIndex.flatMap {
      case (row, y) =>
        row.zipWithIndex.map {
          case (value, x) => Coordinate(x, y) -> value
        }
    }

  val SeaMonster: String =
    """                  #
      |#    ##    ##    ###
      | #  #  #  #  #  #   """.stripMargin

  def parseSeaMonster: Either[String, List[List[Color]]] =
    parseGrid(SeaMonster.split("\n").toList)

  def seaMonsterCoordinates: Either[String, List[Coordinate]] =
    parseSeaMonster.map(allCoordinates)
      .map { coordinates => coordinates.collect { case (coordinate, Black) => coordinate }}

  def hasSeaMonsters(image: List[List[Color]], seaMonster: List[Coordinate], coordinate: Coordinate) =
    seaMonster.map(_.withOffset(coordinate))
      .forall {
        case Coordinate(x, y) => image.getValue(x, y).contains(Black)
      }

  def findSeaMonsters(image: List[List[Color]], seaMonster: List[Coordinate]): List[Coordinate] =
    allCoordinates(image).map { case (coordinate, _) => coordinate }
      .filter {
        coordinate => hasSeaMonsters(image, seaMonster, coordinate)
      }

}
