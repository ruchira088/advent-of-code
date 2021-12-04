package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

object DayTwo {
  type Movement = Position => Position

  case class Position(horizontal: Int, depth: Int, aim: Int)

  case class Instruction(direction: Direction, length: Int)

  object Instruction {
    def unapply(input: String): Option[Instruction] =
      Option(input.split(" ").toList)
        .collect {
          case Direction(direction) :: IntValue(size) :: Nil => Instruction(direction, size)
        }
  }

  sealed trait Direction {
    def move(length: Int): Movement

    val label: String
  }

  object Direction {
    def unapply(input: String): Option[Direction] =
      Set(Forward, Down, Up).find(direction => direction.label.equalsIgnoreCase(input))
  }

  case object Forward extends Direction {
    override def move(length: Int): Movement =
      position => Position(position.horizontal + length, position.depth + (position.aim * length), position.aim)

    override val label: String = "forward"
  }

  case object Down extends Direction {
    override def move(length: Int): Movement =
      position => Position(position.horizontal, position.depth, position.aim + length)

    override val label: String = "down"
  }

  case object Up extends Direction {
    override def move(length: Int): Movement =
      position => Position(position.horizontal, position.depth, position.aim - length)

    override val label: String = "up"
  }

  def solve(input: List[String]) = {
    val finalPosition = input
      .collect { case Instruction(instruction) => instruction }
      .foldLeft(Position(0, 0, 0)) {
        (position, instruction) => instruction.direction.move(instruction.length)(position)
      }

    finalPosition.depth * finalPosition.horizontal
  }

}
