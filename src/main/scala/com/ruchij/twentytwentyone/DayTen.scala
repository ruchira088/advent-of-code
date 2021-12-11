package com.ruchij.twentytwentyone

object DayTen {
  sealed trait Result

  object Result {
    case object Complete extends Result
    case class Incomplete(stack: List[Char]) extends Result
    case class Error(invalidBracket: Char) extends Result
  }

  val Brackets: Map[Char, Char] =
    Map('(' -> ')', '{' -> '}', '[' -> ']', '<' -> '>')

  val reverseBrackets: Map[Char, Char] = Brackets.map { case (x, y) => y -> x }

  val Score =
    Map(
      ')' -> 3,
      ']' -> 57,
      '}' -> 1197,
      '>' -> 25137
    )

  def solve(input: List[String]) =
    input.map { line => check(line.toList, List.empty) }
      .collect {
        case Result.Error(char) => Score.getOrElse(char, 0)
      }
      .sum

  def check(line: List[Char], stack: List[Char]): Result =
    line match {
      case Nil if stack.isEmpty => Result.Complete

      case Nil => Result.Incomplete(stack)

      case head :: tail =>
        if (Brackets.contains(head)) check(tail, head :: stack)
        else {
          val corresponding = reverseBrackets(head)

          if (corresponding == stack.head) {
            check(tail, stack.tail)
          } else Result.Error(head)
        }
    }

}
