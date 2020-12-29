package com.ruchij

import cats.data.NonEmptyList
import cats.implicits._
import com.ruchij.DayNineteen.Rule.{FixedValue, Pipe, RuleNumbers}
import com.ruchij.DayTwo.IntValue

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
    ruleZero(input.takeWhile(_.nonEmpty))
      .flatMap {
        rules =>
          parseMessages(input.dropWhile(_.nonEmpty).dropWhile(_.isEmpty))
            .map { messages =>
              messages.count(rules.contains)
            }
      }

  def parseMessages(input: List[String]): Either[String, List[String]] =
    input.traverse(parseMessage).map(_.map(_.mkString))

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

  def ruleZero(input: List[String]): Either[String, Set[String]] =
    parseRules(input)
      .flatMap { rules =>
        rules
          .get(0)
          .map(origin => evaluate(origin, rules))
          .fold[Either[String, List[List[FixedValue]]]](Left("Rule 0 does NOT exist"))(Right.apply)
          .map(_.map(_.mkString).toSet)
      }

  def parseRules(input: List[String]) =
    input
      .traverse {
        case Rule.RuleWithNumber(IntValue(number), rule) =>
          Rule.parse(rule.trim).map(number -> _)

        case line => Left(s"""Unable to parse "$line"""")
      }
      .map(_.toMap)

  def evaluate(rule: Rule, rules: Map[Int, Rule]): List[List[FixedValue]] =
    rule match {
      case fixedValue: FixedValue => List(List(fixedValue))

      case Pipe(left, right) => evaluate(left, rules) ++ evaluate(right, rules)

      case ruleNumbers: RuleNumbers =>
        ruleNumbers.numbers.toList
          .flatMap(rules.get)
          .map(rule => evaluate(rule, rules))
          .foldLeft(List.empty[List[FixedValue]]) {
            case (acc, values) =>
              if (acc.isEmpty) values else acc.flatMap(accValue => values.map(value => accValue ++ value))
          }
    }

}
