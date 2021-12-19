package com.ruchij.twentytwentyone

import scala.annotation.tailrec

object DayEighteen {
  sealed trait SnailNumber {}

  object SnailNumber {
    case class Regular(value: Int) extends SnailNumber {
      override def toString: String = value.toString
    }

    case class Pair(left: SnailNumber, right: SnailNumber) extends SnailNumber {
      override def toString: String = s"[$left,$right]"
    }
  }

  case class ParseResult[A, B](value: A, remaining: B)

  def solve(input: List[String]) = {
    val data = "[[[[0,2],4],[7,[[8,4],9]]],[1,1]]"

    val snailNumber = parse(data)

    nested(snailNumber, 4)
  }

  def parse(input: String): SnailNumber =
    if (input.startsWith("[")) parsePair(input, 0, "")
    else SnailNumber.Regular(input.toInt)

  @tailrec
  def parsePair(input: String, bracketOffset: Int, acc: String): SnailNumber.Pair =
    input.toList match {
      case '[' :: rest =>
        parsePair(rest.mkString, bracketOffset + 1, acc + "[")

      case ']' :: rest =>
        parsePair(rest.mkString, bracketOffset - 1, acc + "]")

      case ',' :: rest if bracketOffset == 1 =>
        SnailNumber.Pair(parse(acc.tail), parse(rest.init.mkString))

      case head :: tail =>
        parsePair(tail.mkString, bracketOffset, acc + head)
    }

  def findNestedIndex(input: String, bracketOffset: Int, index: Int): Option[Int] =
    input.headOption match {
      case Some('[') if bracketOffset == 4 => Some(index)
      case Some('[') => findNestedIndex(input.tail, bracketOffset + 1, index + 1)
      case Some(']') => findNestedIndex(input.tail, bracketOffset - 1, index + 1)
      case Some(_) => findNestedIndex(input.tail, bracketOffset, index + 1)
      case None => None
    }

  def nested(snailNumber: SnailNumber, level: Int): Option[SnailNumber.Pair] =
    snailNumber match {
      case SnailNumber.Regular(_) => None

      case pair @ SnailNumber.Pair(_, _) if level == 0 => Some(pair)

      case SnailNumber.Pair(left, right) =>
        nested(left, level - 1).orElse(nested(right, level - 1))
    }

  def explode(input: String) =
    findNestedIndex(input, 0, 0)
      .map {
        index =>

      }

}
