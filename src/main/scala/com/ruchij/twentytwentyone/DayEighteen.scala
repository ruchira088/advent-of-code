package com.ruchij.twentytwentyone

import scala.annotation.tailrec

object DayEighteen {
  sealed trait SnailNumber {
    var parent: Option[SnailNumber.Pair]
  }

  object SnailNumber {
    case class Regular(var value: Int, var parent: Option[SnailNumber.Pair]) extends SnailNumber {
      override def toString: String = value.toString
    }

    case class Pair(var left: SnailNumber, var right: SnailNumber, var parent: Option[SnailNumber.Pair])
        extends SnailNumber {
      override def toString: String = s"[$left,$right]"
    }

    def clone(snailNumber: SnailNumber): SnailNumber =
      snailNumber match {
        case SnailNumber.Regular(value, _) => SnailNumber.Regular(value, None)

        case SnailNumber.Pair(left, right, _) =>
          val leftClone = clone(left)
          val rightClone = clone(right)

          val result = SnailNumber.Pair(leftClone, rightClone, None)

          leftClone.parent = Some(result)
          rightClone.parent = Some(result)

          result
      }
  }

  def solve(input: List[String]) = {
    val snailNumbers = input.map(line => parse(line, None))

    snailNumbers.zipWithIndex
      .map {
        case (x, i0) =>
          snailNumbers
            .zipWithIndex
            .map { case (y, i1) =>
              if (i0 == i1) 0 else {
                val left = SnailNumber.clone(x)
                val right = SnailNumber.clone(y)

                val value = SnailNumber.Pair(left, right, None)

                left.parent = Some(value)
                right.parent = Some(value)

                magnitude(reduce(value))
              }
            }
            .max
      }
      .max
  }

  def reduce(snailNumber: SnailNumber): SnailNumber =
    oneStep(snailNumber) match {
      case None => snailNumber
      case Some(value) => reduce(value)
    }

  def oneStep(snailNumber: SnailNumber): Option[SnailNumber] =
    updatedNested(snailNumber).orElse(updateHighest(snailNumber))

  def updateHighest(snailNumber: SnailNumber): Option[SnailNumber] = {
    val result = findHigh(snailNumber, 9)

    result.foreach {
      case number @ SnailNumber.Regular(value, Some(parent)) =>
        val split =
          SnailNumber.Pair(
            SnailNumber.Regular(math.floor(value.toDouble / 2).toInt, None),
            SnailNumber.Regular(math.ceil(value.toDouble / 2).toInt, None),
            Some(parent)
          )

        split.right.parent = Some(split)
        split.left.parent = Some(split)

        if (parent.left.eq(number)) {
          parent.left = split
        } else {
          parent.right = split
        }
    }

    result.map(_ => snailNumber)
  }

  def updatedNested(snailNumber: SnailNumber): Option[SnailNumber] = {
    val result = findNested(snailNumber, 4)

    result.foreach {
      case number @ SnailNumber.Pair(SnailNumber.Regular(left, _), SnailNumber.Regular(right, _), Some(parent)) =>
        leftNeighbour(number, None)
          .foreach { regular =>
            regular.value = regular.value + left
          }

        rightNeighbour(number, None)
          .foreach { regular =>
            regular.value = regular.value + right
          }

        if (parent.left == number) {
          parent.left = SnailNumber.Regular(0, Some(parent))
        } else {
          parent.right = SnailNumber.Regular(0, Some(parent))
        }

        Some()

      case _ =>
        None
    }

    result.map(_ => snailNumber)
  }

  def rightMost(snailNumber: SnailNumber): Option[SnailNumber.Regular] =
    snailNumber match {
      case regular: SnailNumber.Regular => Some(regular)
      case SnailNumber.Pair(_, right, _) => rightMost(right)
    }

  def leftMost(snailNumber: SnailNumber): Option[SnailNumber.Regular] =
    snailNumber match {
      case regular: SnailNumber.Regular => Some(regular)
      case SnailNumber.Pair(left, _, _) => leftMost(left)
    }

  def leftNeighbour(snailNumber: SnailNumber, last: Option[SnailNumber]): Option[SnailNumber.Regular] =
    snailNumber match {
      case _ if last.isEmpty && snailNumber.parent.nonEmpty =>
        leftNeighbour(snailNumber.parent.get, Some(snailNumber))

      case SnailNumber.Pair(left, _, _) if last.exists(x => !x.eq(left)) =>
        rightMost(left)

      case regular: SnailNumber.Regular => Some(regular)

      case SnailNumber.Pair(_, _, Some(parent)) =>
        leftNeighbour(parent, Some(snailNumber))

      case _ => None
    }

  def rightNeighbour(snailNumber: SnailNumber, last: Option[SnailNumber]): Option[SnailNumber.Regular] =
    snailNumber match {
      case _ if last.isEmpty && snailNumber.parent.nonEmpty =>
        rightNeighbour(snailNumber.parent.get, Some(snailNumber))

      case SnailNumber.Pair(_, right, _) if last.exists(x => !x.eq(right)) =>
        leftMost(right)

      case regular: SnailNumber.Regular => Some(regular)

      case SnailNumber.Pair(_, _, Some(parent)) =>
        rightNeighbour(parent, Some(snailNumber))

      case _ => None
    }

  def parse(input: String, parent: Option[SnailNumber.Pair]): SnailNumber =
    if (input.startsWith("[")) parsePair(input, 0, "", parent)
    else SnailNumber.Regular(input.toInt, parent)

  @tailrec
  def parsePair(input: String, bracketOffset: Int, acc: String, parent: Option[SnailNumber.Pair]): SnailNumber.Pair =
    input.toList match {
      case '[' :: rest =>
        parsePair(rest.mkString, bracketOffset + 1, acc + "[", parent)

      case ']' :: rest =>
        parsePair(rest.mkString, bracketOffset - 1, acc + "]", parent)

      case ',' :: rest if bracketOffset == 1 =>
        val left = parse(acc.tail, None)
        val right = parse(rest.init.mkString, None)

        val number: SnailNumber.Pair =
          SnailNumber.Pair(left, right, parent)

        left.parent = Some(number)
        right.parent = Some(number)

        number

      case head :: tail =>
        parsePair(tail.mkString, bracketOffset, acc + head, parent)
    }

  def findNested(snailNumber: SnailNumber, level: Int): Option[SnailNumber.Pair] =
    snailNumber match {
      case SnailNumber.Regular(_, _) => None

      case pair @ SnailNumber.Pair(_, _, _) if level == 0 => Some(pair)

      case SnailNumber.Pair(left, right, _) =>
        findNested(left, level - 1).orElse(findNested(right, level - 1))
    }

  def findHigh(snailNumber: SnailNumber, max: Int): Option[SnailNumber.Regular] =
    snailNumber match {
      case regular @ SnailNumber.Regular(value, _) if value > max => Some(regular)
      case _: SnailNumber.Regular => None
      case SnailNumber.Pair(left, right, _) =>
        findHigh(left, max).orElse(findHigh(right, max))
    }

  def magnitude(snailNumber: SnailNumber): Int =
    snailNumber match {
      case SnailNumber.Regular(value, _) => value
      case SnailNumber.Pair(left, right, _) =>
        magnitude(left) * 3 + magnitude(right) * 2
    }

}
