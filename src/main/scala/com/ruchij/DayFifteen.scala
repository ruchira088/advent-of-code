package com.ruchij

import cats.implicits._

object DayFifteen {
  val solve: List[String] => Either[String, Any] = {
    case line :: Nil =>
      line.split(",").toList
        .traverse { number =>
          number.toIntOption.fold[Either[String, Int]](Left(s"""Unable to parse "$number" as a number"""))(Right.apply)
        }
        .map {
          numbers =>
            expand(numbers, 30000000).get(30000000 - 1)
        }

    case _ => Left("Unexpected number of lines")
  }

  def expand(input: List[Int], remaining: Int): List[Int] = {
    if (remaining % 1000 == 0) {
      println(remaining)
    }

    if (remaining == 0) input else expand(input :+ next(input), remaining - 1)
  }


  def next(input: List[Int]) =
    Option(input.init.lastIndexOf(input.lastOption.getOrElse(0)))
      .filter(_ != -1)
      .map { beforeLastIndex => (input.size - 1) - beforeLastIndex }
      .getOrElse(0)
}
