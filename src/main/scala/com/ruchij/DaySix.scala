package com.ruchij

object DaySix {
  def solve(input: List[String]) =
    group(input, None, List.empty)
      .map(_.size)
      .sum

  def group(input: List[String], interim: Option[Set[Char]], result: List[Set[Char]]): List[Set[Char]] =
    input match {
      case head :: tail if head.trim.isEmpty => group(tail, None, result :+ interim.getOrElse(Set.empty))

      case head :: tail => group(tail, Some(interim.fold(head.toSet)(_.intersect(head.toSet))), result)

      case Nil => result :+ interim.getOrElse(Set.empty)
    }
}
