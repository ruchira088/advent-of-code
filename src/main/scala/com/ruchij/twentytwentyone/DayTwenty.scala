package com.ruchij.twentytwentyone

import java.time.Instant

object DayTwenty {
  case class Coordinate(x: Int, y: Int)
  case class Input(algorithm: IndexedSeq[Boolean], image: Map[Coordinate, Boolean])

  val BorderSize = 10

  def printImage(image: Map[Coordinate, Boolean]): String = {
    val dimen = dimensions(image)

    Range.inclusive(dimen.y, 0, -1)
      .map {
        y => Range.inclusive(0, dimen.x).map(x => if (image.getOrElse(Coordinate(x, y), false)) "#" else ".").mkString
      }
      .mkString("\n")
  }


  object Coordinate {
    def group(coordinate: Coordinate): Seq[Coordinate] =
      for {
        diffY <- Seq(1, 0, -1)
        diffX <- Seq(-1, 0, 1)
      } yield Coordinate(coordinate.x + diffX, coordinate.y + diffY)
  }

  def solve(input: List[String]) = {
    val data = parse(input)

    val output =
      Range(0, 25).foldLeft(data) {
        case (acc, index) =>
          val startTime = Instant.now()
          println(s"Started: $index")

          val input = run(run(acc))

          val endTime = Instant.now()
          println(s"Completed: $index, duration: ${endTime.toEpochMilli - startTime.toEpochMilli}ms")

          Input(input.algorithm, trimBorder(input.image))
      }

    println {
      printImage(output.image)
    }

    output.image.count { case (_, isLight) => isLight }
  }

  def trimBorder(image: Map[Coordinate, Boolean]): Map[Coordinate, Boolean] = {
    val dimen = dimensions(image)
    val delta = 2 * BorderSize - 2

    image
      .filter {
        case (coordinate, _) =>
          coordinate.x >= delta &&
            coordinate.y >= delta &&
            coordinate.x <= (dimen.x - delta) &&
            coordinate.y <= (dimen.y - delta)
      }
      .map {
        case (coordinate, isLight) => Coordinate(coordinate.x - delta, coordinate.y - delta) -> isLight
      }
  }

  def run(input: Input): Input = {
    val imageWithBorders =
      Range(0, BorderSize).foldLeft(input.image) { case (image, _) => addBorders(image) }

    val updatedImage = imageWithBorders
      .map {
        case (coordinate, _) =>
          val index =
            toDecimal {
              Coordinate
                .group(coordinate)
                .map(coord => imageWithBorders.getOrElse(coord, false))
            }

          coordinate -> input.algorithm(index)
      }

    Input(input.algorithm, updatedImage)
  }

  def parse(input: List[String]): Input = {
    val algorithm =
      input
        .takeWhile(_.nonEmpty)
        .toIndexedSeq
        .flatMap { _.toList.map(_ == '#') }

    val image =
      input
        .dropWhile(_.nonEmpty)
        .dropWhile(_.isEmpty)
        .reverse
        .zipWithIndex
        .flatMap {
          case (line, y) =>
            line.toList.zipWithIndex.map {
              case (char, x) => Coordinate(x, y) -> (char == '#')
            }
        }
        .toMap

    Input(algorithm, image)
  }

  def dimensions(image: Map[Coordinate, Boolean]): Coordinate =
    image.foldLeft(Coordinate(0, 0)) {
      case (coord, (current, _)) =>
        Coordinate(math.max(coord.x, current.x), math.max(coord.y, current.y))
    }

  def addBorders(image: Map[Coordinate, Boolean]): Map[Coordinate, Boolean] = {
    val updated: Map[Coordinate, Boolean] = image.map { case (Coordinate(x, y), isLight) => Coordinate(x + 1, y + 1) -> isLight }

    val dimen: Coordinate = dimensions(updated)

    val borderCoordinates = Range
      .inclusive(0, dimen.x + 1)
      .flatMap { x =>
        List(Coordinate(x, 0), Coordinate(x, dimen.y + 1))
      } ++
      Range
        .inclusive(0, dimen.y + 1)
        .flatMap { y =>
          List(Coordinate(0, y), Coordinate(dimen.x + 1, y))
        }

    borderCoordinates.map { coordinate =>
      coordinate -> false
    }.toMap ++ updated
  }

  def toDecimal(binary: Seq[Boolean]): Int =
    binary.reverse.zipWithIndex.map {
      case (isOne, power) => if (isOne) math.pow(2, power).toInt else 0
    }.sum

}
