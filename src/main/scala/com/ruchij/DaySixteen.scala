package com.ruchij

import com.ruchij.DayTwo.IntValue

import cats.implicits._
import scala.util.matching.Regex

object DaySixteen {

  val NumberRange: Regex = "(.*): (\\d+)-(\\d+) or (\\d+)-(\\d+)".r

  case class DaySixteenInput(fields: List[FieldNumberRange], myTicket: Ticket, nearByTickets: List[Ticket])

  case class FieldNumberRange(fieldName: String, rangeA: Range, rangeB: Range)

  object FieldNumberRange {
    def isMatch(value: Int, fieldNumberRange: FieldNumberRange): Boolean =
      fieldNumberRange.rangeA.contains(value) || fieldNumberRange.rangeB.contains(value)
  }

  case class Ticket(value: Vector[Int]) extends AnyVal

  def solve(input: List[String]) =
    parse(input).map {
      case DaySixteenInput(fields, myTicket, nearByTickets) =>
        val allFieldNumbers =
          fields.foldLeft(Set.empty[Int]) {
            case (numbers, FieldNumberRange(_, rangeA, rangeB)) =>
              numbers ++ rangeA.toSet ++ rangeB.toSet
          }

      val validTickets = (myTicket :: nearByTickets).filter { _.value.forall(allFieldNumbers.contains) }

      deduce(possibleMappings(fields, validTickets), Map.empty)
        .filter {
          case (fieldName, _) => fieldName.startsWith("departure")
        }
        .flatMap {
          case (_, index) => myTicket.value.get(index).map(_.toLong)
        }
        .product
    }

  def deduce(mappings: List[(String, List[Int])], result: Map[String, Int]): Map[String, Int] =
    mappings
      .collectFirst {
        case (name, index :: Nil) => name -> index
      }
      .fold(result) { case (name, index) =>
        deduce(
          mappings.collect {
            case (fieldName, values) if name != fieldName => fieldName -> values.filter(_ != index)
          },
          result + (name -> index)
        )
      }

  def possibleMappings(fieldNumberRanges: List[FieldNumberRange], tickets: List[Ticket]) = {
    val fields: IndexedSeq[(Int, List[Int])] =
      fieldNumberRanges.indices
        .map {
          index => index -> tickets.traverse(_.value.get(index)).getOrElse(List.empty)
        }

    fieldNumberRanges.map {
      fieldNumberRange =>
        fieldNumberRange.fieldName -> fields.filter {
          case (_, values) =>
            values.forall(number => FieldNumberRange.isMatch(number, fieldNumberRange))
        }
        .map {
          case (index, _) => index
        }
        .toList
    }
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
      .map(value => Ticket(value.toVector))

}
