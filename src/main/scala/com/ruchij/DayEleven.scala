package com.ruchij

import cats.implicits._
import com.ruchij.DayEleven.State.{EmptySeat, Floor, OccupiedSeat}

object DayEleven {
  case class Coordinate(x: Int, y: Int)

  case class Environment(floor: Int, emptySeat: Int, occupiedSeat: Int) {
    val next: State => State = {
      case State.Floor => Floor

      case _ if occupiedSeat == 0 => OccupiedSeat

      case _ if occupiedSeat >= 5 => EmptySeat

      case state => state
    }
  }

  object Coordinate {
    def neighbours(coordinate: Coordinate): Set[Coordinate] =
      for {
        x <- Set(coordinate.x - 1, coordinate.x, coordinate.x + 1) if x >= 0

        y <- Set(coordinate.y - 1, coordinate.y, coordinate.y + 1)
        if y >= 0 && !(x == coordinate.x && y == coordinate.y)
      } yield Coordinate(x, y)
  }

  case class Grid(values: List[List[State]]) {
    val height: Int = values.length

    val width: Int = values.headOption.map(_.length).getOrElse(0)

    def coordinate(x: Int, y: Int): Option[State] =
      for {
        line <- values.get(y)
        value <- line.get(x)
      } yield value

    override def toString: String = values.map(_.mkString).mkString("\n", "\n", "\n")
  }

  sealed trait State {
    val label: Char

    override def toString: String = label.toString
  }

  object State {
    case object EmptySeat extends State {
      override val label: Char = 'L'
    }

    case object OccupiedSeat extends State {
      override val label: Char = '#'
    }

    case object Floor extends State {
      override val label: Char = '.'
    }

    val all: List[State] = List(EmptySeat, OccupiedSeat, Floor)
  }

  def parse(input: Char): Either[String, State] =
    State.all
      .find(_.label == input)
      .fold[Either[String, State]](Left(s"Unable to parse '$input' as State"))(Right.apply)

  def solve(input: List[String]) =
    input
      .traverse { _.toList.traverse(parse) }
      .map { data =>
        steadyState(Grid(data)).values.flatten.count(_ == OccupiedSeat)
      }

  def steadyState(grid: Grid): Grid = {
    val updated = calculate(grid)

    if (grid == updated) grid else steadyState(updated)
  }

  def calculate(grid: Grid): Grid =
    Grid {
      allCoordinates(grid)
        .map {
          _.flatMap { coordinate =>
            grid.coordinate(coordinate.x, coordinate.y).map { state =>
              linesOfSight(coordinate, grid)
                .foldLeft(Environment(0, 0, 0)) {
                  case (env, EmptySeat) => env.copy(emptySeat = env.emptySeat + 1)
                  case (env, Floor) => env.copy(floor = env.floor + 1)
                  case (env, OccupiedSeat) => env.copy(occupiedSeat = env.occupiedSeat + 1)
                }
                .next(state)
            }
          }
        }
    }

  def linesOfSight(coordinate: Coordinate, grid: Grid): Seq[State] =
      for {
        x <- Seq(-1, 0, 1)
        y <- Seq(-1, 0, 1) if x != 0 || y != 0

        state <- lineOfSight(coordinate, grid, _ + x, _ + y)
      }
      yield state

  def lineOfSight(coordinate: Coordinate, grid: Grid, fx: Int => Int, fy: Int => Int): Option[State] =
    lineOfSight(coordinate, grid, { case Coordinate(x, y) => Coordinate(fx(x), fy(y)) })

  def lineOfSight(coordinate: Coordinate, grid: Grid, next: Coordinate => Coordinate): Option[State] = {
    val updated = next(coordinate)

    grid.coordinate(updated.x, updated.y)
      .flatMap {
        case State.Floor => lineOfSight(updated, grid, next)
        case value => Some(value)
      }
  }

  def allCoordinates(grid: Grid): List[List[Coordinate]] =
    Range(0, grid.height).toList.map { y =>
      Range(0, grid.width).toList.map(x => Coordinate(x, y))
    }
}
