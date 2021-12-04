package com.ruchij.twentytwenty

import cats.data.NonEmptyList
import cats.implicits._
import com.ruchij.twentytwenty.DayNineteen.Rule.{FixedValue, Pipe, RuleNumbers}
import com.ruchij.twentytwenty.DayTwo.IntValue

import java.text.NumberFormat
import scala.collection.mutable
import scala.util.matching.Regex

object DayNineteen {
  sealed trait Rule

  object Rule {
    case class RuleNumbers(numbers: NonEmptyList[Int]) extends Rule

    object RuleNumbers {
      def unapply(input: String): Option[RuleNumbers] =
        input
          .split(" ")
          .map(_.trim)
          .toList
          .traverse { _.toIntOption }
          .flatMap {
            case Nil => None

            case head :: tail => Some(RuleNumbers(NonEmptyList(head, tail)))
          }
    }

    case class Pipe(left: Rule, right: Rule) extends Rule

    sealed trait FixedValue extends Rule {
      val label: String
    }

    object FixedValue {
      val all: List[FixedValue] = List(ValueA, ValueB)
    }

    case object ValueA extends FixedValue {
      override val label: String = """"a""""

      override def toString: String = "a"
    }

    case object ValueB extends FixedValue {
      override val label: String = """"b""""

      override def toString: String = "b"
    }

    val RuleWithNumber: Regex = "(\\d+): (.*)".r

    def parse(input: String): Either[String, Rule] =
      input
        .split("\\|")
        .toList
        .map(_.trim)
        .traverse {
          case ValueA.label => Right(ValueA)

          case ValueB.label => Right(ValueB)

          case RuleNumbers(rule) => Right(rule)

          case phrase => Left(s"""Unable to parse "$phrase" as rule""")

        }
        .flatMap {
          case Nil => Left("No rules found")

          case rule :: Nil => Right(rule)

          case head :: tail =>
            Right {
              tail.foldLeft(head) { case (acc, rule) => Pipe(acc, rule) }
            }
        }
  }

  def solve(input: List[String]) =
    parseMessages(input.dropWhile(_.nonEmpty).dropWhile(_.isEmpty))
      .map(_.map(_.map(_.toString).mkString))
      .flatMap { messages =>
        ruleZero(input.takeWhile(_.nonEmpty), messages)
          .map { rules =>
            messages.count(rules.contains)
          }
      }

  def parseMessages(input: List[String]): Either[String, List[List[FixedValue]]] =
    input.traverse(parseMessage)

  def parseMessage(input: String): Either[String, List[FixedValue]] =
    input
      .split("")
      .toList
      .traverse { letter =>
        FixedValue.all
          .find(_.toString.equalsIgnoreCase(letter))
          .fold[Either[String, FixedValue]](
            Left(s"""Unable to parse "$letter" as a ${classOf[FixedValue].getSimpleName}""")
          )(Right.apply)
      }

  def ruleZero(input: List[String], messages: List[String]): Either[String, Set[String]] =
    parseRules(input)
      .flatMap { rules =>
        rules
          .get(0)
          .map(origin => evaluate(origin, rules, mutable.Map.empty, messages))
          .fold[Either[String, List[String]]](Left("Rule 0 does NOT exist"))(Right.apply)
          .map {
            values =>
              println(s"Size: ${NumberFormat.getIntegerInstance.format(values.size)}")
              values.toSet
          }
      }

  def parseRules(input: List[String]) =
    input
      .traverse {
        case Rule.RuleWithNumber(IntValue(number), rule) =>
          Rule.parse(rule.trim).map(number -> _)

        case line => Left(s"""Unable to parse "$line"""")
      }
      .map(_.toMap)

  def evaluate(rule: Rule, rules: Map[Int, Rule], cache: mutable.Map[Int, List[String]], messages: List[String]): List[String] =
    rule match {
      case fixedValue: FixedValue => List(fixedValue.toString)

      case Pipe(left, right) =>
        (evaluate(left, rules, cache, messages) ++ evaluate(right, rules, cache, messages))
          .filter(rule => messages.exists(_.contains(rule)))

      case ruleNumbers: RuleNumbers =>
        ruleNumbers.numbers.toList
          .flatMap { number =>
            println(number)
            cache.get(number)
              .orElse {
                rules.get(number).map {
                  rule =>
                    evaluate(rule, rules, cache, messages)
                      .filter(rule => messages.exists(_.contains(rule)))
                }
              }
              .tapEach {
                values => cache.put(number, values)
              }
          }
          .foldLeft(List.empty[String]) {
            case (acc, values) =>
              if (acc.isEmpty) values
              else
                acc
                  .flatMap(accValue => values.map(value => accValue ++ value))
                  .filter(rule => messages.exists(_.contains(rule)))
          }
    }

}
