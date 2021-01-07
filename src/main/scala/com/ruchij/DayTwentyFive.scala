package com.ruchij

import cats.implicits._

object DayTwentyFive {

  def solve(input: List[String]): Option[Long] =
    input.take(2).traverse(_.toLongOption)
      .collect {
        case card :: door :: Nil => solve(card, door)
      }

  def solve(cardPK: Long, doorPK: Long): Long =
    loop(1, doorPK, loopAndTarget(cardPK, 0, 1))

  def loop(value: Long, subject: Long, count: Long): Long =
    if (count == 0) value
    else loop(value * subject % 20201227, subject, count - 1)

  def loopAndTarget(result: Long, count: Long, value: Long): Long =
    if (result == value) count
    else loopAndTarget(result, count + 1, loop(value, 7, 1))


}
