package com.ruchij.twentytwenty

import cats.implicits._

object DayTen {
  def solve(input: List[String]) =
    parse(input)
      .map(sort[Int])
      .flatMap { numbers =>
        numbers.lastOption.fold[Either[String, Int]](Left("Empty list"))(Right.apply).map {
          last => 0 :: (numbers :+ last + 3)
        }
      }
      .map {
        list =>
          split(list, List.empty, Set.empty).toList
            .map(values => arrangements(values, 0))
            .product
      }

  def parse(input: List[String]): Either[String, List[Int]] =
    input.traverse { line =>
      line.toIntOption.fold[Either[String, Int]](Left(s"""Unable to parse "$line" as an Int""")) { Right.apply }
    }

  def sort[A: Ordering](values: List[A]): List[A] =
    if (values.length <= 1)
      values
    else {
      val (listA, listB) = values.splitAt(values.length / 2)

      mergeOrderedList(sort(listA), sort(listB), List.empty)
    }

  def mergeOrderedList[A](listOne: List[A], listTwo: List[A], result: List[A])(implicit ordering: Ordering[A]): List[A] =
    (listOne, listTwo) match {
      case (xs, Nil) => result ++ xs

      case (Nil, ys) => result ++ ys

      case (x :: xs, ys @ y :: _) if ordering.lt(x, y) => mergeOrderedList(xs, ys, result :+ x)

      case (xs, y :: ys) => mergeOrderedList(xs, ys, result :+ y)
    }

  def diff[A](values: List[A], result: List[A])(implicit numeric: Numeric[A]): List[A] =
    values match {
      case x :: y :: zs => diff(y :: zs, result :+ numeric.minus(y, x))
      case _ => result
    }

  def arrangements(values: List[Int], count: Long): Long =
    values match {
      case Nil => count + 1

      case a :: b :: c :: tail if c - a <= 3 =>
        arrangements(b :: c :: tail, count) + arrangements(a :: c :: tail, 0)

      case a :: b :: c :: d :: tail if d - a <= 3 =>
        arrangements(b :: c :: d :: tail, count) + arrangements(a :: d :: tail, 0)

      case _ :: tail =>
        arrangements(tail, count)
    }

  def split(list: List[Int], interim: List[Int], result: Set[List[Int]]): Set[List[Int]] =
    list match {
      case Nil => result + interim

      case x :: y :: zs if y - 3 == x => split(zs, List(y), result + (interim :+ x))

      case x :: xs => split(xs, interim :+ x, result)
    }

}
