package com.ruchij

import cats.implicits._

object DayNine {
  def solve(input: List[String]): Either[String, Option[Long]] =
    input
      .traverse {
        line => line.toLongOption.fold[Either[String, Long]](Left(s"""Unable to parse "$line" as an Long"""))(Right.apply)
      }
      .flatMap { numbers =>
        solve(numbers, 25)
          .map {
            noMatch =>
              println(s"No match number is $noMatch")
              for {
                set <- findSum(numbers.filter(_ != noMatch), noMatch)
                _ = println(set)
                max <- set.maxOption
                min <- set.minOption
              }
              yield max + min
          }
      }

  def solve(input: List[Long], preamble: Int): Either[String, Long] =
    if (input.isDefinedAt(preamble)) {
      if (find(input.take(preamble), input(preamble))) solve(input.tail, preamble)
      else Right(input(preamble))
    }
    else Left("Finished")

  def find(numbers: List[Long], target: Long): Boolean =
    numbers match {
      case Nil => false
      case head :: tail => tail.exists(num => num + head == target) || find(tail, target)
    }

  def findSum(numbers: List[Long], target: Long): Option[Set[Long]] =
    findSum(numbers, Set.empty, target).orElse(findSum(numbers.tail, target))

  def findSum(numbers: List[Long], acc: Set[Long], target: Long): Option[Set[Long]] =
    if (acc.sum == target && acc.size > 1) Some(acc)
    else if (acc.sum > target) None
    else numbers.headOption.flatMap(head => findSum(numbers.tail, acc + head, target))
}
