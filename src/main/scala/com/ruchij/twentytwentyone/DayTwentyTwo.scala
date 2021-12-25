package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

object DayTwentyTwo {
  sealed trait CubeStatus

  object CubeStatus {
    case object On extends CubeStatus
    case object Off extends CubeStatus
  }

  case class Coordinate(x: Int, y: Int, z: Int)

  case class Instruction(cubeStatus: CubeStatus, rangeX: Range, rangeY: Range, rangeZ: Range)

  def solve(input: List[String]) = {
    val data = input.map(parseLine)

    val result = run(Map.empty, data)

    result
      .count {
        case (_, status) =>  status == CubeStatus.On
      }
  }

  def run(map: Map[Coordinate, CubeStatus], instructions: List[Instruction]): Map[Coordinate, CubeStatus] =
    instructions match {
      case Nil => map

      case Instruction(cubeStatus, rangeX, rangeY, rangeZ) :: tail =>
        val updated =
          coordinateSet(rangeX, rangeY, rangeZ)
            .foldLeft(map) {
              (acc, coordinate) => acc.updated(coordinate, cubeStatus)
            }

        run(updated, tail)
    }

  def coordinateSet(rangeX: Range, rangeY: Range, rangeZ: Range): IndexedSeq[Coordinate] =
    for {
      x <- rangeX if math.abs(x) <= 50
      y <- rangeY if math.abs(y) <= 50
      z <- rangeZ if math.abs(z) <= 50
    }
    yield Coordinate(x, y, z)

  def parseLine(line: String): Instruction = {
    val status = if (line.takeWhile(_ != ' ').mkString == "on") CubeStatus.On else CubeStatus.Off

    line.dropWhile(_ != ' ').trim.split(',').map(range).toList match {
      case x :: y :: z :: Nil => Instruction(status, x, y, z)
      case _ => throw new IllegalArgumentException(s"Unable to parse line: $line")
    }
  }

  def range(input: String): Range =
    input.split('=').toList match {
      case _ :: rangeString :: Nil =>
        rangeString.split("\\.\\.")
          .collect { case IntValue(number) => number }
          .toList match {
          case start :: end :: Nil => Range.inclusive(start, end)
          case _ => throw new IllegalArgumentException(s"Unable to extract start and end for $input")
        }

      case _ => throw new IllegalArgumentException(s"Unable to extract range for $input")
    }

}
