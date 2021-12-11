package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

object DayEleven {
  case class Coordinate(x: Int, y: Int)

  object Coordinate {
    def neighbours(coordinate: Coordinate): Set[Coordinate] =
      for {
        x <- Set(-1, 0, 1).map(_ + coordinate.x)
        y <- Set(-1, 0, 1).map(_ + coordinate.y) if !(x == coordinate.x && y == coordinate.y)
      }
      yield Coordinate(x, y)
  }

  case class OctopusGrid(grid: Map[Coordinate, Int])

  object OctopusGrid {
    def allFlash(count: Int, octopusGrid: OctopusGrid): Int =
      if (octopusGrid.grid.forall { case (_, energy) => energy == 0 }) count
      else allFlash(
        count + 1,
        {
          val (grid, _) = oneStep(octopusGrid)
          grid
        }
      )

    def steps(count: Int, octopusGrid: OctopusGrid, flashes: Int): Int =
      if (count == 0) flashes
      else {
        val (updated, flashCount) = oneStep(octopusGrid)

        steps(count - 1, updated, flashes + flashCount)
      }

    def oneStep(octopusGrid: OctopusGrid): (OctopusGrid, Int) = {
      val updated = reduce {
        OctopusGrid {
          octopusGrid.grid.map { case (coordinate, int) => coordinate -> (int + 1) }
        }
      }

      updated -> updated.grid.count { case (_, energy) => energy == 0 }
    }

    def reduce(octopusGrid: OctopusGrid): OctopusGrid = {
      val maybeFlash = octopusGrid.grid.find { case (_, energy) => energy > 9 }

      maybeFlash match {
        case None =>
          OctopusGrid {
            octopusGrid.grid.map { case (coordinate: Coordinate, energy) => coordinate -> math.max(energy, 0) }
          }

        case Some((coordinate, _)) =>
          val updated =
            Coordinate.neighbours(coordinate)
              .flatMap(neighbour => octopusGrid.grid.get(neighbour).map(value => neighbour -> (if (value >= 0) value + 1 else value)))
              .toMap

        reduce(OctopusGrid(octopusGrid.grid ++ updated ++ Map(coordinate -> -1)))
      }
    }

  }

  def solve(input: List[String]) = {
    val octopusGrid =
      OctopusGrid {
        input.zipWithIndex.toIndexedSeq.flatMap { case (line, y) =>
          line.zipWithIndex
            .map { case (char, x) => Coordinate(x, y) -> IntValue.unapply(char.toString) }
            .collect {
              case (coordinate, Some(int)) => coordinate -> int
            }
        }
          .toMap
    }

    OctopusGrid.allFlash(0, octopusGrid)
  }

}
