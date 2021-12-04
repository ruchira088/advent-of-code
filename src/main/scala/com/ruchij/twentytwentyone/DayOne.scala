package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

object DayOne {

  def solve(input: List[String]) = {
    val depths = input.collect { case IntValue(number) => number }

    val windows = depths.zip(depths.tail).zip(depths.tail.tail)
      .map {
        case ((first, second), third) => first + second + third
      }

    windows.zip(windows.tail)
      .map {
        case (previous, current) => if (current > previous) 1 else 0
      }
      .sum
  }

  def partOne(input: List[String]) = {
    val depths = input.collect { case IntValue(number) => number }

    depths.zip(depths.tail)
      .map { case (previous, current) => if (current > previous) 1 else 0 }
      .sum
  }

}
