package com.ruchij.twentytwentyone

object DayTwentyFive {
  case class Coordinate(x: Int, y: Int)

  sealed trait SeaCucumber {
    val char: Char

    def move(coordinate: Coordinate): Coordinate
  }

  object SeaCucumber {
    case object EastMovingCucumber extends SeaCucumber {
      override val char: Char = '>'

      override def move(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x + 1, coordinate.y)
    }

    case object SouthMovingCucumber extends SeaCucumber {
      override val char: Char = 'v'

      override def move(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x, coordinate.y - 1)
    }
  }

  def solve(input: List[String]) = {
    val seaCucumbers = parse(input)
    val dimensions = Coordinate(input.head.length, input.size)

    repeatTilSteadyState(seaCucumbers, dimensions, 0)
  }

  def repeatTilSteadyState(seaCucumbers: Map[Coordinate, SeaCucumber], dimensions: Coordinate, count: Int): Int = {
    println(count)
    val afterOneStep = performStep(seaCucumbers, dimensions)

    if (afterOneStep == seaCucumbers) count + 1 else repeatTilSteadyState(afterOneStep, dimensions, count + 1)
  }

  def parse(input: List[String]): Map[Coordinate, SeaCucumber] =
    input.reverse.zipWithIndex.flatMap {
      case (line, y) =>
        line.zipWithIndex
          .flatMap {
            case (char, x) =>
              Set(SeaCucumber.EastMovingCucumber, SeaCucumber.SouthMovingCucumber)
                .find(_.char == char)
                .map { seaCucumber =>
                  Coordinate(x, y) -> seaCucumber
                }
          }
    }.toMap

  def eastFacingMovement(seaCucumbers: Map[Coordinate, SeaCucumber], dimensions: Coordinate): Map[Coordinate, SeaCucumber] =
    moveSeaCucumbers(seaCucumbers, dimensions, SeaCucumber.EastMovingCucumber)

  def southFacingMovement(seaCucumbers: Map[Coordinate, SeaCucumber], dimensions: Coordinate): Map[Coordinate, SeaCucumber] =
    moveSeaCucumbers(seaCucumbers, dimensions, SeaCucumber.SouthMovingCucumber)

  def performStep(seaCucumbers: Map[Coordinate, SeaCucumber], dimensions: Coordinate): Map[Coordinate, SeaCucumber] =
    southFacingMovement(eastFacingMovement(seaCucumbers, dimensions), dimensions)

  def printCucumbers(seaCucumbers: Map[Coordinate, SeaCucumber], dimensions: Coordinate): String =
    Range.inclusive(dimensions.y - 1, 0, -1)
      .map { y =>
        Range(0, dimensions.x).map(x => seaCucumbers.get(Coordinate(x, y)).map(_.char).getOrElse('.')).mkString
      }
      .mkString("\n")

  def moveSeaCucumbers(
    seaCucumbers: Map[Coordinate, SeaCucumber],
    dimensions: Coordinate,
    seaCucumber: SeaCucumber
  ): Map[Coordinate, SeaCucumber] = {
    seaCucumbers
      .filter {
        case (_, cucumber: SeaCucumber) => cucumber == seaCucumber
      }
      .foldLeft(seaCucumbers) {
        case (result, (coordinate, seaCucumber)) =>
          val possibleNextPosition = seaCucumber.move(coordinate)
          val x = if (possibleNextPosition.x == dimensions.x) 0 else possibleNextPosition.x
          val y = if (possibleNextPosition.y < 0) (dimensions.y - 1) else possibleNextPosition.y

          val nextPosition = Coordinate(x, y)

          val canMove = !seaCucumbers.contains(nextPosition)

          if (canMove) result.removed(coordinate).updated(nextPosition, seaCucumber) else result
      }
  }

}
