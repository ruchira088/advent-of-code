package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

import scala.util.matching.Regex

object DayThirteen {
  val UpFold: Regex = ".* y=(\\d+)".r
  val LeftFold: Regex = ".* x=(\\d+)".r

  case class Coordinate(x: Int, y: Int)

  sealed trait Fold

  object Fold {
    case class Up(y: Int) extends Fold
    case class Left(x: Int) extends Fold

    def fold(instruction: Fold, dots: Set[Coordinate]): Set[Coordinate] =
      instruction match {
        case Up(yAxis) =>
          dots.map { case value @ Coordinate(x, y) =>  if (y < yAxis) value else Coordinate(x, yAxis - (y - yAxis)) }

        case Left(xAxis) =>
          dots.map {
            case value @ Coordinate(x, y) => if (x < xAxis) value else Coordinate(xAxis - (x - xAxis), y)
          }
      }
  }


  case class Game(coordinates: Set[Coordinate], folds: List[Fold])


  def solve(input: List[String]) = {
    val game = parse(input)

    stringify {
      game.folds.foldLeft(game.coordinates) {
        (grid, fold) => Fold.fold(fold, grid)
      }
    }
  }

  def stringify(grid: Set[Coordinate]) = {
    val dimensions = grid.foldLeft(Coordinate(0, 0)) { (max, current) => Coordinate(math.max(max.x, current.x), math.max(max.y, current.y))}

    Range.inclusive(0, dimensions.y)
      .map {
        y => Range.inclusive(0, dimensions.x).map(x => if (grid.contains(Coordinate(x, y))) "# " else ". ").mkString
      }
      .mkString("\n")

  }

  def parse(input: List[String]) = {
    val coordinateLines: List[String] = input.takeWhile(_.nonEmpty)
    val foldLines = input.dropWhile(_.nonEmpty)

    val coordinates =
      coordinateLines
        .map(_.trim)
        .filter(_.nonEmpty)
        .map(_.split(',').toList)
        .collect {
          case IntValue(x) :: IntValue(y) :: Nil => Coordinate(x, y)
        }

    val folds =
      foldLines
        .map(_.trim)
        .filter(_.nonEmpty)
        .collect {
          case UpFold(IntValue(y)) => Fold.Up(y)
          case LeftFold(IntValue(x)) => Fold.Left(x)
        }

    Game(coordinates.toSet, folds)
  }

}
