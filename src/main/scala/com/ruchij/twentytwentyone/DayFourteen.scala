package com.ruchij.twentytwentyone

object DayFourteen {
  val Rule = "(\\S)(\\S) -> (\\S)".r

  case class Pair(first: Char, second: Char)
  case class Puzzle(start: List[Char], mappings: Map[Pair, Char])

  def solve(input: List[String]) = {
    val puzzle = parse(input)

    val polymer: Map[Pair, Long] =
      puzzle.start.zip(puzzle.start.tail)
      .map { case (first, second) => Pair(first, second) }
      .foldLeft(Map.empty[Pair, Long]) {
        case (acc, pair) => acc ++ Map(pair -> (acc.getOrElse(pair, 0L) + 1))
      }

    val result: Map[Pair, Long] =
      Range(0, 40).foldLeft(polymer) {
        case (acc, _) => runStep(acc, puzzle.mappings)
      }

    val borders = puzzle.start.headOption.toSet ++ puzzle.start.lastOption.toSet

    val counts =
      count(result)
        .map { case (char, count) => if (borders.contains(char)) count + 1 else count }

    counts.max - counts.min
  }

  def runStep(input: Map[Pair, Long], mappings: Map[Pair, Char]) =
    input.foldLeft(Map.empty[Pair, Long]) {
      case (acc, (pair, count)) =>
        mappings.get(pair) match {
          case None => acc ++ Map(pair -> (acc.getOrElse(pair, 0L) + count))
          case Some(char) =>
            acc ++
              Map[Pair, Long](
                Pair(pair.first, char) -> (acc.getOrElse(Pair(pair.first, char), 0L) + count),
                Pair(char, pair.second) -> (acc.getOrElse(Pair(char, pair.second), 0L) + count)
              )
        }
    }

  def count(input: Map[Pair, Long]): Map[Char, Long] =
    input.toList
      .flatMap { case (Pair(first, second), count) => List(first -> count, second -> count) }
      .foldLeft(Map.empty[Char, Long]) {
        case (acc, (char, count)) =>
          acc ++ Map(char -> (acc.getOrElse(char, 0L) + count))
      }
      .map {
        case (char, count) => char -> count / 2
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
