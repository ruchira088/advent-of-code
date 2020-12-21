package com.ruchij

import com.ruchij.DayTwo.IntValue

object DayThirteen {
  val Start: Long = 100000000000000L

  type Remainder = Index

  case class Index(value: Int) extends AnyVal {
    override def toString: String = value.toString
  }

  val solve: List[String] => Either[String, Any] = {
    case _ :: y :: Nil =>
      Right {
        crt(parseBusIds(y))
      }

    case input => Left(s"Expected 2 lines of input, but got ${input.length} lines")
  }

  def parseBusIds(line: String): List[(Index, Int)] =
    line
      .split(",")
      .toList
      .zipWithIndex
      .collect {
        case (IntValue(int), index) => Index(index) -> int
      }

  def crt(values: List[(Remainder, Int)]) = {
    val x: Long = values.map { case (_, mod) => mod.toLong }.product

    values
      .map {
        case (remainder, mod) =>
          val a = x / mod
          val b = solve(x / mod, mod)
          val c = a * b

          val value = (c * (mod - remainder.value))

          value
      }
      .sum % x
  }

  def solve(xt: Long, bt: Long): Long = {
    val a = xt % bt

    val result: Long = find(a, bt).map(_.toLong).getOrElse(1)

    result
  }

  def find(a: Long, bt: Long): Option[Int] =
    LazyList.from(0, 1).find(x => (a * x) % bt == 1)

}
