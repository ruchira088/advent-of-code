package com.ruchij.twentytwentyone

import Utils._

object DayEight {
  case class Line(numbers: Seq[String], output: Seq[String])

  def solve(input: List[String]) =
    parse(input).map { lines =>
      lines.flatMap(_.output.filter(digit => Set(2, 3, 4, 7).contains(digit.length))).size
    }

  def parse(input: List[String]): Either[String, List[Line]] =
    input.traverse {
      line =>
        line.split('|').toList match {
          case numbers :: output :: Nil =>
            Right {
              Line(
                numbers.split(' ').map(_.trim).filter(_.nonEmpty).toSeq,
                output.split(' ').map(_.trim).filter(_.nonEmpty).toSeq
              )
            }

          case _ => Left(s"Unable to parse \"$line\"")
        }
    }

}
