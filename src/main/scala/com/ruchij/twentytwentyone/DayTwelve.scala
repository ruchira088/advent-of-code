package com.ruchij.twentytwentyone

object DayTwelve {
  val Destination = "end"
  val Start = "start"

  def solve(input: List[String]) =
    travel(List(List(Start)), Seq.empty, parse(input)).size

  def travel(paths: List[List[String]], success: Seq[Seq[String]], mappings: Map[String, Set[String]]): Seq[Seq[String]] =
    paths match {
      case (path @ Destination :: _) :: rest =>
        travel(rest, success :+ path, mappings)

      case (first @ head :: tail) :: rest =>
        val nextPaths =
          mappings.getOrElse(head, Set.empty).filter(next => next.forall(_.isUpper) || !tail.contains(next)).toList

        travel(rest ++ nextPaths.map(path => path :: first), success, mappings)

      case _ => success.map(_.reverse)
    }

  def parse(input: List[String]) =
    input.flatMap {
      line => line.split('-').toList match {
        case x :: y :: Nil => List(x -> y)
        case _ => List.empty
      }
    }
      .foldLeft(Map.empty[String, Set[String]]) {
        case (mappings, (x, y)) =>
          mappings ++ Map(x -> (mappings.getOrElse(x, Set.empty) + y), y -> (mappings.getOrElse(y, Set.empty) + x))
      }

}
