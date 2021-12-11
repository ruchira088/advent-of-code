package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils._

object DayNine {
  case class Coordinate(x: Int, y: Int)

  case class Grid(gridValues: IndexedSeq[IndexedSeq[Int]]) {
    def neighbours(coordinate: Coordinate): Set[Coordinate] = {
      val up = Coordinate(coordinate.x, coordinate.y - 1)
      val down = Coordinate(coordinate.x, coordinate.y + 1)
      val left = Coordinate(coordinate.x - 1, coordinate.y)
      val right = Coordinate(coordinate.x + 1, coordinate.y)

      Set(up, down, left, right)
    }

    def getValue(coordinate: Coordinate): Option[Int] =
      for {
        row <- gridValues.getIndex(coordinate.y)
        value <- row.getIndex(coordinate.x)
      } yield value

    override def toString: String =
      gridValues.map(_.mkString).mkString("\n")
  }

  def solve(input: List[String]) = {
    val grid = parse(input)

    val allCoordinates =
      grid.gridValues.indices.flatMap { y =>
        grid.gridValues.getIndex(y).toList.flatMap { row =>
          row.indices.map { x =>
            Coordinate(x, y)
          }
        }
      }

    val lowestPoints: Seq[Coordinate] =
      allCoordinates.filter { coordinate =>
        val neighbours = grid.neighbours(coordinate)
        val maybeValue = grid.getValue(coordinate)

        neighbours.forall { neighbour =>
          grid.getValue(neighbour).forall(neighbourValue => maybeValue.exists(_ < neighbourValue))
        }
      }

    lowestPoints
      .map { coordinate => findBasin(Set(coordinate), Set.empty, grid).size }
      .sorted
      .reverse
      .take(3)
      .product
  }

  def findBasin(coordinates: Set[Coordinate], visited: Set[Coordinate], grid: Grid): Set[Coordinate] =
    coordinates.headOption match {
      case None => visited

      case Some(coordinate) =>
        val updated =
          grid.neighbours(coordinate).diff(visited).filter(coordinate => grid.getValue(coordinate).exists(_ < 9))

        findBasin(coordinates.tail ++ updated, visited + coordinate, grid)
    }

  def parse(input: List[String]): Grid =
    Grid {
      input.toIndexedSeq.map { line =>
        line.toIndexedSeq.flatMap(char => IntValue.unapply(char.toString))
      }
    }

}
