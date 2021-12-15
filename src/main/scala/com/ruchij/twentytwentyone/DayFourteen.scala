package com.ruchij.twentytwentyone

object DayFourteen {
  val Rule = "(\\S)(\\S) -> (\\S)".r

  case class Pair(first: Char, second: Char)
  case class Puzzle(start: List[Char], mappings: Map[Pair, Char])

  def solve(input: List[String]) = {
    val puzzle = parse(input)

    val result =
      Range(0, 20).foldLeft(puzzle.start) {
        case (acc, _) => runStep(acc, puzzle.mappings, List.empty)
      }

    val counts = result.groupBy(identity).map { case (_, value) => value.size }

    counts.max - counts.min
  }

  def runStep(input: List[Char], mappings: Map[Pair, Char], result: List[Char]): List[Char] =
    input match {
      case first :: second :: tail =>
        runStep(
          second :: tail,
          mappings,
          mappings.get(Pair(first, second)) match {
            case None => first :: result
            case Some(value) => value :: first :: result
          })

      case _ => result.reverse ++ input
    }

  def parse(input: List[String]) = {
    val start = input.take(1).flatMap(_.split("")).flatMap(_.toList)
    val mappings =
      input.drop(2).foldLeft(Map.empty[Pair, Char]) {
        case (acc, Rule(first, second, between)) => acc ++ Map(Pair(first.head, second.head) -> between.head)
        case (_, line) => throw new IllegalArgumentException(s"""Unable to parse "$line"""")
      }

    Puzzle(start, mappings)
  }


}
