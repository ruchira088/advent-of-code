package com.ruchij

import com.ruchij.DayTwo.IntValue

import cats.implicits._
import scala.util.matching.Regex

object DaySixteen {

  val NumberRange: Regex = "(.*): (\\d+)-(\\d+) or (\\d+)-(\\d+)".r

  case class DaySixteenInput(fields: List[FieldNumberRange], myTicket: Ticket, nearByTickets: List[Ticket])

  case class FieldNumberRange(fieldName: String, rangeA: Range, rangeB: Range)

  case class Ticket(value: List[Int]) extends AnyVal

  def solve(input: List[String]) =
    parse(input).map {
      case DaySixteenInput(fields, _, nearByTickets) =>
        val allFieldNumbers =
          fields.foldLeft(Set.empty[Int]) {
            case (numbers, FieldNumberRange(_, rangeA, rangeB)) =>
              numbers ++ rangeA.toSet ++ rangeB.toSet
          }

      nearByTickets.flatMap(_.value)
        .filter(number => !allFieldNumbers.contains(number))
        .sum
    }

  def parse(input: List[String]): Either[String, DaySixteenInput] =
    for {
      fields <- input.takeWhile(_.nonEmpty).traverse(parseRange)

      myTicket <- input.dropWhile(_.nonEmpty).get(2).fold[Either[String, Ticket]](Left("Invalid input"))(parseTicket)

      nearByTickets <- input.dropWhile(_.nonEmpty).tail.dropWhile(_.nonEmpty).drop(2).traverse(parseTicket)
    }
    yield DaySixteenInput(fields, myTicket, nearByTickets)

  val parseRange: String => Either[String, FieldNumberRange] = {
    case NumberRange(fieldName, IntValue(startA), IntValue(endA), IntValue(startB), IntValue(endB)) =>
        Right {
          FieldNumberRange(fieldName, Range.inclusive(startA, endA), Range.inclusive(startB, endB))
        }

    case input => Left(s"""Unable to parse "$input" as a ${classOf[FieldNumberRange].getSimpleName}""")
  }

  def parseTicket(input: String): Either[String, Ticket] =
    input.split(",").toList
      .traverse { word =>
        word.toIntOption.fold[Either[String, Int]](Left(s"""Unable to parse "$word" as an Int"""))(Right.apply)
      }
      .map(Ticket.apply)

}
