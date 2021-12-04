package com.ruchij.twentytwenty

import cats.implicits._

object DaySeventeen {
  sealed trait State {
    val label: Char

    override def toString: String = label.toString
  }

  object State {
    case object Active extends State {
      override val label: Char = '#'
    }

    case object Inactive extends State {
      override val label: Char = '.'
    }

    val all: Seq[State] = Seq(Active, Inactive)
  }

  case class Coordinate(x: Int, y: Int, z: Int, w: Int) {
    override def toString: String = s"($x, $y, $z, $w)"
  }

  object Coordinate {
    val Offsets = List(-1, 0, 1)

    def neighbours(coordinate: Coordinate): List[Coordinate] =
      for {
        offsetX <- Offsets
        offsetY <- Offsets
        offsetZ <- Offsets
        offsetW <- Offsets if offsetX != 0 || offsetY != 0 || offsetZ != 0 || offsetW != 0
      } yield Coordinate(coordinate.x + offsetX, coordinate.y + offsetY, coordinate.z + offsetZ, coordinate.w + offsetW)
  }

  def nextState(coordinate: Coordinate, cubes: Map[Coordinate, State]): State = {
    val activeNeighbours =
      Coordinate
        .neighbours(coordinate)
        .flatMap(cubes.get)
        .count(_ == State.Active)

    cubes.getOrElse(coordinate, State.Inactive) match {
      case State.Active =>
        if (activeNeighbours == 2 || activeNeighbours == 3) State.Active else State.Inactive

      case State.Inactive =>
        if (activeNeighbours == 3) State.Active else State.Inactive
    }
  }

  def solve(input: List[String]) =
    partOne(input)

  def partOne(input: List[String]) =
    parse(input)
      .map {
        _.zipWithIndex.foldLeft(Map.empty[Coordinate, State]) {
          case (cube, (line, lineIndex)) =>
            line.zipWithIndex.foldLeft(cube) {
              case (map, (state, index)) =>
                map + (Coordinate(index, lineIndex, 0, 0) -> state)
            }
        }
      }
      .map {
        cubes =>
          Range(0, 6)
            .foldLeft(cubes) {
              case (values, _) => cycle(values)
            }
            .count {
              case (_, state) => state == State.Active
            }
      }

  def cycle(cubes: Map[Coordinate, State]): Map[Coordinate, State] =
    addBoundary(cubes)
      .map {
        case (coordinate, _) => coordinate -> nextState(coordinate, cubes)
      }

  def addBoundary(cubes: Map[Coordinate, State]): Map[Coordinate, State] = {
    val (maxX, minX) = boundary(cubes, _.x, 0)
    val (maxY, minY) = boundary(cubes, _.y, 0)
    val (maxZ, minZ) = boundary(cubes, _.z, 0)
    val (maxW, minW) = boundary(cubes, _.w, 0)

    val coordinates =
      for {
        x <- List.range(minX - 1, maxX + 2)
        y <- List.range(minY - 1, maxY + 2)
        z <- List.range(minZ - 1, maxZ + 2)
        w <- List.range(minW - 1, maxW + 2)
      } yield Coordinate(x, y, z, w)

    coordinates.foldLeft(cubes) {
      case (value, coordinate) =>
        if (value.contains(coordinate)) value else value + (coordinate -> State.Inactive)
    }

  }

  def boundary[A: Ordering](cubes: Map[Coordinate, State], mapper: Coordinate => A, default: A): (A, A) = {
    val values =
      cubes
        .map { case (coordinate, _) => mapper(coordinate) }

    (values.maxOption.getOrElse(default), values.minOption.getOrElse(default))
  }

  def parse(input: List[String]) =
    input.traverse {
      _.toList.traverse(parseCubeState)
    }

  def parseCubeState(input: Char): Either[String, State] =
    State.all
      .find(_.label == input)
      .fold[Either[String, State]](Left(s"Unable to parse '$input' as a ${classOf[State].getSimpleName}"))(Right.apply)

}
