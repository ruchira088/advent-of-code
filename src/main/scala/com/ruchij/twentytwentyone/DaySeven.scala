package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

object DaySeven {

  def solve(input: List[String]) = {
    val crabs: Seq[Int] = input.flatMap(_.split(',')).collect { case IntValue(x) => x }

    minimize(crabs, Range.inclusive(0, crabs.maxOption.getOrElse(0)).toList, -1)
  }

  def costToMove(crabs: Seq[Int], position: Int) =
    crabs.map(current => math.abs(current - position)).sum

  def minimize(crabs: Seq[Int], lines: List[Int], minimum: Int): Int =
    lines match {
      case Nil => minimum

      case head :: tail =>
        val cost = costToMove(crabs, head)
        val updatedMinimum = if (minimum < 0 || cost < minimum) cost else minimum
        minimize(crabs, tail, updatedMinimum)
    }


}
