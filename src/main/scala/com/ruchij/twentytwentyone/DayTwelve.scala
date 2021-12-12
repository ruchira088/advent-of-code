package com.ruchij.twentytwentyone

object DayTwelve {
  val Destination = "end"
  val Start = "start"

  def solve(input: List[String]) =
    travel(Vector(List(Start)), 0, parse(input))

  def travel(paths: Vector[List[String]], successCount: Int, mappings: Map[String, Set[String]]): Int =
    paths.headOption match {
      case Some(Destination :: _) =>
        travel(paths.tail, successCount + 1, mappings)

      case Some(path @ head :: _) =>
        val nextPaths: Vector[String] =
          mappings.getOrElse(head, Set.empty)
            .toVector
            .filter {
              next =>
                next.forall(_.isUpper) ||
                  (next != Start && (!path.contains(next) || {
                    path.filter(_.forall(_.isLower))
                      .groupBy(identity)
                      .map { case (path, paths) => path -> paths.size }
                      .forall { case (_, count) => count == 1 }
                  }))
            }


        travel(paths.tail ++ nextPaths.map(next => next +: path), successCount, mappings)

      case _ => successCount
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
