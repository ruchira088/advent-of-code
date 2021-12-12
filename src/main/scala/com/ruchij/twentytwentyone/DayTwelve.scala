package com.ruchij.twentytwentyone

object DayTwelve {
  val Destination = "end"
  val Start = "start"

  def solve(input: List[String]) = {
    val paths = new Array[List[String]](1_000_000)

    paths.update(0, List(Start))

    travel(paths, 0, 1, 0, parse(input))
  }

  def travel(paths: Array[List[String]], index: Int, size: Int, successCount: Int, mappings: Map[String, Set[String]]): Int =
    paths.apply(index) match {
      case Destination :: _ =>
        travel(paths, index + 1, size, successCount + 1, mappings)

      case path @ head :: _ =>
        val nextPaths: Set[String] =
          mappings.getOrElse(head, Set.empty)
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

        nextPaths.zipWithIndex.foreach { case (next, i) =>
          paths.update(size + i, next :: path)
        }


        travel(paths, index + 1, size + nextPaths.size, successCount, mappings)

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
