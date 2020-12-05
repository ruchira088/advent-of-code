package com.ruchij

import cats.implicits._

object DayFive {
  def solve(input: List[String]) =
    input.traverse {
      line =>
        val (y, x) = line.splitAt(7)

        for {
          row <- navigate(y, 0, 127)
          seat <- navigate(x, 0, 7)
        }
        yield (row, seat)
    }
      .map {
        seats =>
          List.range(0, 127 * 8 + 7).diff {
            seats.map { case (row, seat) => row * 8 + seat }
          }
      }

  def navigate(input: String, min: Int, max: Int): Either[String, Int] =
    input.toList match {
      case ('F' | 'L') :: tail => navigate(tail.mkString, min, math.floor((min.toDouble + max.toDouble)/2).toInt)

      case ('B' | 'R') :: tail => navigate(tail.mkString, math.ceil((min.toDouble + max.toDouble)/2).toInt, max)

      case _ if min == max => Right(min)

      case char => Left(s"$char is NOT a valid input")
    }
}
