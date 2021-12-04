package com.ruchij.twentytwenty

import scala.language.implicitConversions

object DayThree {

  case class Coordinate(x: Int, y: Int) {
    def +(coordinate: Coordinate): Coordinate = Coordinate(x + coordinate.x, y + coordinate.y)
  }

  implicit def toCoordinate(value: (Int, Int)): Coordinate = {
    val (x, y) = value
    Coordinate(x, y)
  }

  case class Grid(data: List[List[Int]]) {
    def apply(coordinate: Coordinate): Option[Int] =
      if (coordinate.y >= data.length)
        None
      else {
        val yAxis = data.apply(coordinate.y)
        Some(yAxis(coordinate.x % yAxis.length))
      }
  }

  def solve(input: List[String]): Long = {
    val grid = parse(input)
    val destination = grid.data.length - 1

    val origin = Coordinate(0, 0)
    val stepSizes =
      List(Coordinate(1, 1), Coordinate(3, 1), Coordinate(5, 1), Coordinate(7, 1), Coordinate(1, 2))

    stepSizes
      .map {
        stepSize =>
          steps(origin, stepSize, destination)
            .flatMap { coordinate => grid(coordinate) }
            .sum
            .toLong
      }
      .product
  }

  def steps(origin: Coordinate, step: Coordinate, destination: Int): List[Coordinate] =
    if (origin.y == destination) List.empty else origin + step :: steps(origin + step, step, destination)

  val parse: Char => Int = {
    case '#' => 1
    case _ => 0
  }

  def parse(input: List[String]): Grid =
    Grid {
      input.map {
        _.map(parse).toList
      }
    }
}
