package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

object DaySix {

  def solve(input: List[String]) = {
    val fish: Map[Int, Long] =
      parse(input).groupBy(identity).map { case (days, value) => days -> value.size.toLong }

    after(256, fish)
  }

  def parse(input: List[String]) = input.flatMap(line => line.split(',')).collect { case IntValue(number) => number }

  def afterOneDay(fish: Map[Int, Long]): Map[Int, Long] =
    fish.flatMap {
      case (0, count) => Map(6 -> (count + fish.getOrElse(7, 0L))) ++ Map(8 -> count)
      case (7, count) => if (!fish.contains(0)) Map(6 -> count) else Map.empty
      case (timer, count) => Map(timer - 1 -> count)
    }

  def after(days: Int, fish: Map[Int, Long]): Long =
    if (days == 0) fish.map { case (_, size) => size }.sum
    else after(days - 1, afterOneDay(fish))

}
