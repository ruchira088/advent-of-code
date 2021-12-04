package com.ruchij.twentytwenty

import cats.implicits._
import com.ruchij.twentytwenty.DayTwelve.Direction.{East, North, South, West}
import com.ruchij.twentytwenty.DayTwelve.Instruction.Forward
import com.ruchij.twentytwenty.DayTwo.IntValue

import scala.util.matching.Regex

object DayTwelve {
  case class FerryInstruction(instruction: Instruction, magnitude: Int)

  object FerryInstruction {
    val Format: Regex = "(\\S)(\\d+)".r
  }

  sealed trait Instruction {
    val label: Char
  }

  object Instruction {
    case object Forward extends Instruction {
      override val label: Char = 'F'
    }

    val all: Seq[Instruction] = Seq(Forward) ++ Direction.all ++ Turn.all

    def unapply(input: String): Option[Instruction] =
      input.headOption.flatMap { char =>
        all.find(_.label == char)
      }
  }

  sealed trait Direction extends Instruction {
    def turnLeft: Direction

    def turnRight: Direction
  }

  object Direction {
    case object North extends Direction {
      override val label: Char = 'N'

      override def turnLeft: Direction = West

      override def turnRight: Direction = East
    }

    case object South extends Direction {
      override val label: Char = 'S'

      override def turnLeft: Direction = East

      override def turnRight: Direction = West
    }

    case object East extends Direction {
      override val label: Char = 'E'

      override def turnLeft: Direction = North

      override def turnRight: Direction = South
    }

    case object West extends Direction {
      override val label: Char = 'W'

      override def turnLeft: Direction = South

      override def turnRight: Direction = North
    }

    val all: Seq[Direction] = Seq(North, South, East, West)
  }

  sealed trait Turn extends Instruction

  object Turn {
    case object Left extends Turn {
      override val label: Char = 'L'
    }

    case object Right extends Turn {
      override val label: Char = 'R'
    }

    val all: Seq[Turn] = Seq(Left, Right)
  }

  case class Position(x: Int, y: Int, direction: Direction)

  object Position {
    val Origin: Position = Position(0, 0, Direction.East)
  }

  case class WaypointPosition(x: Int, y: Int) {
    def * (multiplier: Int): WaypointPosition = WaypointPosition(x * multiplier, y * multiplier)
  }

  object WaypointPosition {
    val Origin: WaypointPosition = WaypointPosition(10, 1)
  }

  val parse: String => Either[String, FerryInstruction] = {
    case FerryInstruction.Format(Instruction(instruction), IntValue(value)) =>
      Right(FerryInstruction(instruction, value))
    case input => Left(s"""Unable to parse "$input" as a ${classOf[FerryInstruction].getSimpleName}""")
  }

  def execute(position: Position): FerryInstruction => Position = {
    case FerryInstruction(_, 0) => position

    case FerryInstruction(North, magnitude) => position.copy(y = position.y + magnitude)

    case FerryInstruction(South, magnitude) => position.copy(y = position.y - magnitude)

    case FerryInstruction(West, magnitude) => position.copy(x = position.x - magnitude)

    case FerryInstruction(East, magnitude) => position.copy(x = position.x + magnitude)

    case instruction @ FerryInstruction(Turn.Right, magnitude) =>
      execute(position.copy(direction = position.direction.turnRight))(instruction.copy(magnitude = magnitude - 90))

    case instruction @ FerryInstruction(Turn.Left, magnitude) =>
      execute(position.copy(direction = position.direction.turnLeft))(instruction.copy(magnitude = magnitude - 90))

    case FerryInstruction(Forward, magnitude) =>
      execute(position)(FerryInstruction(position.direction, magnitude))
  }

  def execute(position: Position, waypointPosition: WaypointPosition): FerryInstruction => (Position, WaypointPosition) = {
    case FerryInstruction(_, 0) => position -> waypointPosition

    case FerryInstruction(North, magnitude) => position -> waypointPosition.copy(y = waypointPosition.y + magnitude)

    case FerryInstruction(South, magnitude) => position -> waypointPosition.copy(y = waypointPosition.y - magnitude)

    case FerryInstruction(East, magnitude) => position -> waypointPosition.copy(x = waypointPosition.x + magnitude)

    case FerryInstruction(West, magnitude) => position -> waypointPosition.copy(x = waypointPosition.x - magnitude)

    case FerryInstruction(Forward, magnitude) =>
      val WaypointPosition(x, y) = waypointPosition * magnitude

      Position(position.x + x, position.y + y, position.direction) -> waypointPosition

    case FerryInstruction(Turn.Left, magnitude) =>
      execute(position, WaypointPosition(waypointPosition.y * -1, waypointPosition.x))(FerryInstruction(Turn.Left, magnitude - 90))

    case FerryInstruction(Turn.Right, magnitude) =>
      execute(position, WaypointPosition(waypointPosition.y, waypointPosition.x * -1))(FerryInstruction(Turn.Right, magnitude - 90))
  }

  def solve(input: List[String]) =
    input
      .traverse(parse)
      .map {
        _.foldLeft(Position.Origin -> WaypointPosition.Origin) {
          case ((position, waypoint), instruction) => execute(position, waypoint)(instruction)
        }
      }
      .map {
        case (Position(x, y, _), _) => math.abs(x) + math.abs(y)
      }
}
