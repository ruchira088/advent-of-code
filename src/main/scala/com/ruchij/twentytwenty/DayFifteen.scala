package com.ruchij.twentytwenty

import cats.implicits._

object DayFifteen {
  val Index = 30_000_000

  val solve: List[String] => Either[String, Any] = {
    case line :: Nil =>
      line.split(",").toList
        .traverse { number =>
          number.toIntOption.fold[Either[String, Int]](Left(s"""Unable to parse "$number" as a number"""))(Right.apply)
        }
        .map {
          numbers =>
            findValue(
              numbers.lastOption.getOrElse(0),
              numbers.length + 1,
              numbers.zipWithIndex
                .map { case (value, index) => value -> (index + 1) }
                .foldLeft(Map.empty[Int, Int]) {
                  case (past, (value, index)) => past + (value -> index)
                },
              Index
            )
        }

    case _ => Left("Unexpected number of lines")
  }

  def findValue(previous: Int, length: Int, pastValues: Map[Int, Int], destination: Int): Int = {
    val next = solve(previous, length - 1, pastValues)

    if (length % 1000_000 == 0) {
      println(length)
    }

    if (length == destination) next
    else findValue(next, length + 1, pastValues + (previous -> (length - 1)), destination)
  }


  def solve(previous: Int, length: Int, pastValues: Map[Int, Int]) =
    pastValues.get(previous)
      .map(offset => length - offset)
      .getOrElse(0)

}
