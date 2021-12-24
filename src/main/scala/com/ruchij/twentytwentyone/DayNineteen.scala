package com.ruchij.twentytwentyone

import com.ruchij.twentytwenty.DayTwo.IntValue

import scala.annotation.tailrec
import scala.util.matching.Regex

object DayNineteen {
  case class Coordinate(x: Int, y: Int, z: Int) {
    override def toString: String = s"($x, $y, $z)"
  }

  case class Scanner(id: Int, beacons: List[Coordinate])
  case class DistanceDiff(scannerId: Int, coordinateOne: Coordinate, coordinateTwo: Coordinate, distance: Double)

  val ScannerHeading: Regex = "--- scanner (\\d+) ---".r

  def solve(input: List[String]) = {
    val scanners = parse(input)

    val diffs: Map[Int, List[DistanceDiff]] =
      scanners
        .flatMap(scanner => distanceDiffs(scanner.id, scanner.beacons))
        .groupBy(_.scannerId)

    val matches = permutations[(Int, List[DistanceDiff]), (Int, Int, Int)](
      diffs.toList,
      List.empty, {
        case ((idX, diffsX), (idY, diffsY)) =>
          val distanceMatches =
            (diffsX ++ diffsY)
              .groupBy(_.distance)
              .map { case (_, diffList) => diffList.size }
              .filter(_ > 1)
              .sum

          (idX, idY, distanceMatches)
      }
    )
      .filter { case (_, _, size) => size >= 132 }
      .flatMap {
        case (x, y, _) => List(x -> y, y -> x)
      }
      .groupBy {
        case (x, _) => x
      }
      .map {
        case (x, list) => x -> list.map { case (_, b) => b }
      }
      .toList
      .sortBy { case (x, _) => x }
      .mkString("\n")

    matches
  }

  def alignment(baseScanner: Scanner, otherScanner: Scanner) = {
    val baseDiffs: List[DistanceDiff] = distanceDiffs(baseScanner.id, baseScanner.beacons)
    val otherDiffs: List[DistanceDiff] = distanceDiffs(otherScanner.id, otherScanner.beacons)

    (baseDiffs ++ otherDiffs)
      .groupBy(_.distance)
      .toList
      .collect {
        case (_, x :: y :: Nil) =>
          if (x.scannerId == baseScanner.id) (x, y) else (y, x)
      }

  }

  lazy val triangleNumbers: LazyList[(Int, Int)] =
    (1, 1) #:: triangleNumbers.map { case (value, index) => (value + index + 1) -> (index + 1)  }

  def parse(input: List[String]): List[Scanner] = parse(input, List.empty)

  def parse(input: List[String], scanners: List[Scanner]): List[Scanner] =
    if (input.isEmpty) scanners.reverse
    else {
      val (scannerString, rest) = input.span(_.nonEmpty)

      parse(rest.dropWhile(_.trim.isEmpty), parseScannerString(scannerString) :: scanners)
    }

  def parseScannerString(input: List[String]): Scanner =
    input match {
      case ScannerHeading(IntValue(id)) :: coordinates =>
        Scanner(id, coordinates.map(parseCoordinate))

      case _ => throw new IllegalArgumentException(s"Unable to parse scanner: $input")
    }

  def parseCoordinate(input: String): Coordinate =
    input.split(',').toList match {
      case IntValue(x) :: IntValue(y) :: IntValue(z) :: Nil => Coordinate(x, y, z)
      case _ => throw new IllegalArgumentException(s"Unable to parse coordinate: $input")
    }

  def distance(coordinateOne: Coordinate, coordinateTwo: Coordinate): Double =
    math.sqrt(
      math.pow(coordinateOne.y - coordinateTwo.y, 2) + math.pow(coordinateOne.x - coordinateTwo.x, 2) + math
        .pow(coordinateOne.z - coordinateTwo.z, 2)
    )

  def distanceDiffs(scannerId: Int, coordinate: List[Coordinate]): List[DistanceDiff] =
    permutations(coordinate, List.empty, (x, y) => DistanceDiff(scannerId, x, y, distance(x, y)))

  @tailrec
  def permutations[A, B](input: List[A], result: List[B], f: (A, A) => B): List[B] =
    input match {
      case x :: xs =>
        permutations(xs, xs.map(a => f(x, a)) ::: result, f)

      case _ => result
    }

}
