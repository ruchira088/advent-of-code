package com.ruchij.twentytwentyone

import com.ruchij.twentytwenty.DayTwo.IntValue

import scala.util.matching.Regex

object DaySeventeen {
  val TargetArea: Regex = ".* x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)".r

  lazy val triangleNumbers: LazyList[(Int, Int)] =
    (1, 1) #:: triangleNumbers.map { case (index, value) => (index + 1, value + index + 2) }

  case class Coordinate(x: Int, y: Int)
  case class Velocity(x: Int, y: Int)

  case class State(position: Coordinate, velocity: Velocity)

  def solve(input: List[String]) = {
    val (rangeX, rangeY) = parse(input.head)

    val velocityRangeX = Range.inclusive(1, rangeX.end)
    val velocityRangeY = Range.inclusive(rangeY.start, rangeY.start * -1)

    val feasibleVelocities = ranges(velocityRangeX, velocityRangeY).map { case (x, y) => Velocity(x, y) }
    val targetArea = ranges(rangeX, rangeY).map { case (x, y) => Coordinate(x, y) }

    feasibleVelocities
      .count { velocity =>
        trajectory(State(Coordinate(0, 0), velocity))
          .takeWhile { case State(position, _) => position.x <= rangeX.end && position.y >= rangeY.start }
          .lastOption
          .exists(state => targetArea.contains(state.position))
      }
  }

  def ranges(rangeX: Range, rangeY: Range): Seq[(Int, Int)] =
    for {
      x <- rangeX
      y <- rangeY
    } yield x -> y

  val parse: String => (Range, Range) = {
    case TargetArea(IntValue(x0), IntValue(x1), IntValue(y0), IntValue(y1)) =>
      val rangeX = Range.inclusive(math.min(x0, x1), math.max(x0, x1))
      val rangeY = Range.inclusive(math.min(y0, y1), math.max(y0, y1))

      rangeX -> rangeY

    case input => throw new IllegalArgumentException(s"Unable to parse $input as target area")
  }

  def trajectory(state: State): LazyList[State] = {
    val position = Coordinate(state.position.x + state.velocity.x, state.position.y + state.velocity.y)
    val velocity =
      Velocity(state.velocity.x match {
        case 0 => 0
        case negative if negative < 0 => negative + 1
        case positive => positive - 1
      }, state.velocity.y - 1)

    state #:: trajectory(State(position, velocity))
  }
}
