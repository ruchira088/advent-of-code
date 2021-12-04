package com.ruchij.twentytwenty

import java.lang.{Long => JavaLong}

import cats.implicits._
import com.ruchij.twentytwenty.DayFourteen.InputValue.{Mask, MemoryAssignment}

import scala.util.matching.Regex

object DayFourteen {
  sealed trait InputValue

  object InputValue {
    val MaskValue: Regex = "mask = (\\S+)".r
    val MemoryAssignmentValue: Regex = "mem\\[(\\d+)\\] = (\\S+)".r

    case class Mask(value: String) extends InputValue

    case class MemoryAssignment(address: Long, value: Long) extends InputValue
  }

  case class MaskSet(mask: Mask, memoryAssignments: List[MemoryAssignment])

  object LongValue {
    def unapply(input: String): Option[Long] =
      input.toLongOption
  }

  def solve(input: List[String]) =
    input.traverse(parse)
      .map(values => group(values, List.empty))
      .map { _.foldLeft(Map.empty[Long, Long]) {
          case (result, maskSet) =>
            result ++ reduce(maskSet)
        }
      }
      .map { _.values.sum }

  def group(input: List[InputValue], values: List[MaskSet]): List[MaskSet] =
    input match {
      case (mask: Mask) :: tail =>
        group(
          tail.dropWhile {
            case MemoryAssignment(_, _) => true
            case _ => false
          },
          values :+
            MaskSet(
              mask,
              tail
                .takeWhile {
                  case MemoryAssignment(_, _) => true
                  case _ => false
                }
                .collect {
                  case memoryAssignment: MemoryAssignment => memoryAssignment
                }
            )
        )

      case _ =>  values
    }

  def result(mask: Mask)(value: Long): Long =
    JavaLong.parseLong(
      toBinary(value).zip(mask.value)
        .map {
          case (char, 'X') => char

          case (_, char) => char
        }
        .mkString
      ,
      2
    )

  def reduce(maskSet: MaskSet): Map[Long, Long] =
    maskSet.memoryAssignments
      .flatMap {
        case MemoryAssignment(address, value) =>
          addresses(maskSet.mask, toBinary(address)).map {
            _ -> value
          }
      }
      .toMap

  val parse: String => Either[String, InputValue] = {
    case InputValue.MaskValue(mask) => Right(Mask(mask))

    case InputValue.MemoryAssignmentValue(LongValue(address), LongValue(value)) =>
      Right(MemoryAssignment(address, value))

    case input => Left(s"""Unable to parse "$input" as InputValue""")
  }

  def toBinary(value: Long): String = {
    val binary = value.toBinaryString

    List.fill(36 - binary.length)(0).mkString + binary
  }

  def addresses(mask: Mask, address: String): List[Long] =
    expand(maskAddress(mask, address).toList)
      .map {
        binary => JavaLong.parseLong(binary, 2)
      }

  def maskAddress(mask: Mask, address: String): String =
    mask.value.zip(address)
      .map {
        case ('0', value) => value

        case (value, _) => value
      }
      .mkString

  val expand: List[Char] => List[String] = {
    case 'X' :: tail => expand('0' :: tail) ++ expand('1' :: tail)

    case value :: Nil => List(value.toString)

    case value :: tail => expand(tail).map(result => s"$value$result")

    case Nil => List.empty
  }
}
