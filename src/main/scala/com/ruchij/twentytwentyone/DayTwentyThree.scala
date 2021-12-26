package com.ruchij.twentytwentyone

object DayTwentyThree {
  sealed trait Amphipod {
    val letter: Char
    val movementCost: Int
  }

  object Amphipod {
    case object Amber extends Amphipod {
      override val letter: Char = 'A'
      override val movementCost: Int = 1
    }

    case object Bronze extends Amphipod {
      override val letter: Char = 'B'
      override val movementCost: Int = 10
    }

    case object Copper extends Amphipod {
      override val letter: Char = 'C'
      override val movementCost: Int = 100
    }

    case object Desert extends Amphipod {
      override val letter: Char = 'D'
      override val movementCost: Int = 1000
    }

    val all: Seq[Amphipod] = Seq(Amber, Bronze, Copper, Desert)
  }

  sealed trait Cell {
    val print: Char
  }

  object Cell {
    case object Empty extends Cell {
      override val print: Char = '.'
    }

    case object Wall extends Cell {
      override val print: Char = '#'
    }

    case object Outside extends Cell {
      override val print: Char = ' '
    }

    case object Temporary extends Cell {
      override val print: Char = '*'
    }

    case class Occupied(amphipod: Amphipod) extends Cell {
      override val print: Char = amphipod.letter
    }

    def parse(char: Char): Cell =
      Amphipod.all.find(_.letter == char)
        .map(Occupied.apply)
        .getOrElse[Cell] {
          char match {
            case Empty.print => Empty
            case Wall.print => Wall
            case Temporary.print => Temporary
            case _ => Outside
          }
        }
  }

  case class Coordinate(x: Int, y: Int)


  def solve(input: List[String]) = {
    val diagram = parse(input)

    printDiagram(diagram)
  }

  def parse(input: List[String]): Map[Coordinate, Cell] =
    input
      .zipWithIndex
      .flatMap {
        case (line, y) =>
          line.toList.zipWithIndex
          .map {
            case (char, x) => Coordinate(x, y) -> Cell.parse(char)
          }
      }
      .toMap

  def printDiagram(diagram: Map[Coordinate, Cell]): String = {
    Range(0, 5)
      .map {
        y =>
          Range(0, 13)
            .map(x => diagram.getOrElse(Coordinate(x, y), Cell.Outside).print)
            .mkString
      }
      .mkString("\n")
  }

  case class Room(top: Cell, bottom: Cell)

  case class Diagram(hallway: List[Cell], roomA: Room, roomB: Room, roomC: Room, roomD: Room)


}
