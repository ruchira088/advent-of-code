package com.ruchij.twentytwenty

import scala.util.matching.Regex

object DayTwo {
  val InputLine: Regex = "(\\d+)-(\\d+) (\\S): (\\S+)".r

  def solve[F[_]](input: List[String]): Int =
    input
      .flatMap(parse)
      .count {
        case (min, max, char, string) => isValid(min, max, char, string)
      }

  val parse: String => Option[(Int, Int, Char, String)] = {
    case InputLine(IntValue(x), IntValue(y), CharValue(char), word) =>
      Some((x, y, char, word))

    case _ => None
  }

  def isValid(x: Int, y: Int, char: Char, word: String): Boolean =
    (word.charAt(x - 1) == char && word.charAt(y - 1) != char) || (word.charAt(x - 1) != char && word.charAt(y - 1) == char)

  object IntValue {
    def unapply(input: String): Option[Int] =
      input.toIntOption
  }

  object CharValue {
    def unapply(input: String): Option[Char] =
      if (input.length != 1) None else input.headOption
  }
}
