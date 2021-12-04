package com.ruchij.twentytwenty

import cats.implicits._
import com.ruchij.twentytwenty.DayFourteen.LongValue

object DayEighteen {
  sealed trait Expression

  object Expression {
    sealed trait Operator extends Expression {
      def run(numberA: Number, numberB: Number): Number
    }

    case class Number(value: Long) extends Expression {
      override def toString: String = value.toString
    }

    object Number {
      val Zero: Number = Number(0)
    }

    case object Start extends Expression {
      override def toString: String = "{"
    }

    case object End extends Expression {
      override def toString: String = "}"
    }

    case object Multiplication extends Operator {
      override def toString: String = "*"

      override def run(numberA: Number, numberB: Number): Number =
        Number(numberA.value * numberB.value)
    }

    case object Addition extends Operator {
      override def toString: String = "+"

      override def run(numberA: Number, numberB: Number): Number =
        Number(numberA.value + numberB.value)
    }

    def parse(input: String): Either[String, List[Expression]] =
      input.split("")
        .toList
        .filter(_.trim.nonEmpty)
        .traverse[Either[String, *], Expression] {
          case "(" => Right(Start)

          case ")" => Right(End)

          case "*" => Right(Multiplication)

          case "+" => Right(Addition)

          case LongValue(long) => Right(Number(long))

          case word => Left(s"""Unable to parse "$word" as an ${classOf[Expression].getSimpleName}""")
        }

    def reduce(input: List[Expression], value: Option[Number], operator: Option[Operator]): Number =
      (input, value, operator) match {
        case ((number: Number) :: tail, None, None) => reduce(tail, Some(number), None)

        case (Multiplication :: tail, Some(number), _) => Multiplication.run(number, reduce(tail, None, None))

        case (Addition :: tail, _, _) => reduce(tail, value, Some(Addition))

        case ((number: Number) :: tail, Some(numberValue), Some(operatorValue)) =>
          reduce(tail, Some(operatorValue.run(number, numberValue)), None)

        case (Start :: _, None, None) =>
          val (remaining, bracketExpression) = bracket(input, 0, List.empty)

          reduce(remaining, Some(reduce(bracketExpression, None, None)), operator)

        case (Start :: _, Some(numberValue), Some(operatorValue)) =>
          val (remaining, bracketExpression) = bracket(input, 0, List.empty)

          reduce(remaining, Some(operatorValue.run(numberValue, reduce(bracketExpression, None, None))), None)

        case (Nil, Some(value), None) => value
      }

    def bracket(input: List[Expression], count: Int, result: List[Expression]): (List[Expression], List[Expression]) =
      input match {
        case Start :: tail if count == 0 => bracket(tail, count + 1, result)

        case Start :: tail => bracket(tail, count + 1, Start :: result)

        case End :: tail if count == 1 => (tail, result.reverse)

        case End :: tail => bracket(tail, count - 1, End :: result)

        case head :: tail => bracket(tail, count, head :: result)
      }

  }


  def solve(input: List[String]) = {
    input.traverse(Expression.parse)
      .map {
        lines => lines.map(line => Expression.reduce(line, None, None)).map(_.value).sum
      }
  }

}
