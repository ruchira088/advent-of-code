package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils._

object DayEight {
  case class Line(numbers: Seq[String], output: Seq[String])

  val Segments =
    Set('a', 'b', 'c', 'd', 'e', 'f', 'g')

  val Numbers =
    Map(
      0 -> "abcefg",
      1 -> "cf",
      2 -> "acdeg",
      3 -> "acdfg",
      4 -> "bcdf",
      5 -> "abdfg",
      6 -> "abdfeg",
      7 -> "acf",
      8 -> "abcdefg",
      9 -> "abcdfg"
    )

  object Line {
    def deduce(line: Line) = {
      val mappings: Map[Char, Int] =
        line.numbers.flatMap(digit => digit.toList).groupBy(identity).map { case (char, values) => char -> values.size }

      def getCharForSize(size: Int) =
        mappings
          .find { case (_, count) => count == size }
          .map { case (char, _) => char }
          .get

      val f = getCharForSize(9)
      val e = getCharForSize(4)
      val b = getCharForSize(6)

      val one = line.numbers.find(_.length == 2).get
      val c = (one.toSet - f).head
      val seven = line.numbers.find(_.length == 3).get
      val a = seven.diff(one).head
      val four = line.numbers.find(_.length == 4).get
      val d = (four.toSet - b - c - f).head
      val g = (Set('a', 'b', 'c', 'd', 'e', 'f', 'g') - a - b - c - d - e - f).head

      val charMappings = Map('a' -> a, 'b' -> b, 'c' -> c, 'd' -> d, 'e' -> e, 'f' -> f, 'g' -> g)

      charMappings
    }
  }

  def solve(input: List[String]) =
    parse(input)
      .map { lines =>
        lines.map { line =>
          val mappings = Line.deduce(line).map { case (x, y) => y -> x }

          line.output
            .map { digit =>
              digit.flatMap(char => mappings.get(char))
            }
            .flatMap { chars =>
              Numbers.find { case (_, value) => chars.toSet == value.toSet }.map { case (digit, _) => digit }
            }
            .mkString
            .toIntOption
            .get
        }
          .sum
      }

  def solvePartOne(input: List[String]) =
    parse(input).map { lines =>
      lines.flatMap(_.output.filter(digit => Set(2, 3, 4, 7).contains(digit.length))).size
    }

  def parse(input: List[String]): Either[String, List[Line]] =
    input.traverse { line =>
      line.split('|').toList match {
        case numbers :: output :: Nil =>
          Right {
            Line(
              numbers.split(' ').map(_.trim).filter(_.nonEmpty).toSeq,
              output.split(' ').map(_.trim).filter(_.nonEmpty).toSeq
            )
          }

        case _ => Left(s"""Unable to parse "$line"""")
      }
    }

}
