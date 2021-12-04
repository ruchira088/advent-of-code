package com.ruchij.twentytwenty

import cats.implicits._

object DayTwentyThree {
  def run(cups: Array[Int]): Array[Int] = {
    val currentCup = cups(0)

    val cupOne = cups(currentCup)
    val cupTwo = cups(cupOne)
    val cupsThree = cups(cupTwo)

    val destinationCup = findDestinationCup(currentCup - 1, Set(cupOne, cupTwo, cupsThree), cups.length - 1)
    val dest = cups(destinationCup)

    cups(destinationCup) = cupOne
    cups(0) = cups(cupsThree)
    cups(currentCup) = cups(cupsThree)

    cups(cupsThree) = dest

    cups
  }

  def run(cups: Array[Int], count: Int): Array[Int] =
    if (count == 0) cups else run(run(cups), count - 1)

  def findDestinationCup(index: Int, pickedCups: Set[Int], size: Int): Int =
    if (pickedCups.contains(index))
      findDestinationCup(index - 1, pickedCups, size)
    else if (index < 1) findDestinationCup(size, pickedCups, size)
    else index

  def collect(array: Array[Int], index: Int, result: List[Int]): List[Int] =
    if (result.contains(index)) result
    else collect(array, array(index), if (index == 0) result else result :+ index)

  def solve(input: List[String]) =
    parse("219347865")
      .map(cups => run(cups, 10_000_000))
      .map {
        cups => cups(1).toLong * cups(cups(1)).toLong
      }

  def parse(input: String) =
    input.split("").toList.traverse(_.toIntOption)
      .map {
        values => values.toVector ++ Vector.range(values.size + 1, 1_000_000 + 1)
      }
      .map {
        cups =>
          val array = new Array[Int](cups.length + 1)

          array(0) = cups(0)

          cups.zip(cups.tail ++ cups.take(1)).foreach {
            case (x, y) =>
              array(x) = y
          }

        array
      }
}
