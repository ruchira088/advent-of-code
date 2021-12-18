package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.DaySixteen.LengthCount.{SubPacketCount, TotalLengthCount}

object DaySixteen {
  case class ParserResult[+A, +B](value: A, remaining: B) {
    def map[C](f: A => C): ParserResult[C, B] = ParserResult(f(value), remaining)
  }

  sealed trait PacketType {
    val id: Int
  }

  object PacketType {
    case object LiteralValue extends PacketType {
      override val id: Int = 4
    }

    case class Operator(id: Int) extends PacketType
  }

  sealed trait LengthType {
    val id: Int
  }

  object LengthType {
    case object TotalLength extends LengthType {
      override val id: Int = 0
    }

    case object SubPacketCount extends LengthType {
      override val id: Int = 1
    }
  }

  sealed trait LengthCount {
    val value: Int
  }

  object LengthCount {
    case class TotalLengthCount(value: Int) extends LengthCount
    case class SubPacketCount(value: Int) extends LengthCount
  }

  sealed trait PacketValue {
    val version: Int
    val packetType: PacketType
  }

  object PacketValue {
    def versionSum(packetValue: PacketValue): Int =
      packetValue match {
        case LiteralPacketValue(_, version) => version
        case OperatorPacketValue(values, _, version, _) => version + values.map(versionSum).sum
      }

    case class LiteralPacketValue(value: Long, version: Int) extends PacketValue {
      override val packetType: PacketType = PacketType.LiteralValue
    }

    case class OperatorPacketValue(
      values: List[PacketValue],
      packetType: PacketType.Operator,
      version: Int,
      lengthCount: LengthCount
    ) extends PacketValue

    def evaluate(packetValue: PacketValue): Long =
      packetValue match {
        case LiteralPacketValue(value, _) => value

        case OperatorPacketValue(values, PacketType.Operator(id), _, _) =>
          val mappedValues: List[Long] = values.map(evaluate)

          id match {
            case 0 => mappedValues.sum
            case 1 => mappedValues.product
            case 2 => mappedValues.min
            case 3 => mappedValues.max

            case _ => (id, mappedValues) match {
              case (5, x :: y :: Nil) => if (x > y) 1 else 0
              case (6, x :: y :: Nil) => if (x < y) 1 else 0
              case (7, x :: y :: Nil) => if (x == y) 1 else 0
              case _ => throw new IllegalArgumentException(s"Unexpected condition for evaluation: $mappedValues")
            }
          }
      }
  }

  val Hex: Map[Char, String] =
    Map(
      '0' -> "0000",
      '1' -> "0001",
      '2' -> "0010",
      '3' -> "0011",
      '4' -> "0100",
      '5' -> "0101",
      '6' -> "0110",
      '7' -> "0111",
      '8' -> "1000",
      '9' -> "1001",
      'A' -> "1010",
      'B' -> "1011",
      'C' -> "1100",
      'D' -> "1101",
      'E' -> "1110",
      'F' -> "1111"
    )

  def solve(input: List[String]) =
    parsePacket(fromHexToBinary(input.head)).map(PacketValue.evaluate)

  def fromHexToBinary(hex: String): String = hex.toUpperCase.map(Hex.apply).mkString

  def binaryToDecimal(input: String): Long =
    input
      .map {
        char => if (char == '1') 1 else 0
      }
      .reverse
      .zipWithIndex
      .map {
        case (binary, power) => binary.toLong * math.pow(2, power).toLong
      }
      .sum

  def parseBinaryNumber(input: String, length: Int): ParserResult[Long, String] =
    ParserResult(binaryToDecimal(input.take(length)), input.drop(length))

  def parseVersionNumber(input: String): ParserResult[Int, String] =
    parseBinaryNumber(input, 3).map(_.toInt)

  def parsePacketType(input: String): ParserResult[PacketType, String] =
    parseBinaryNumber(input, 3)
      .map(number => if (number == PacketType.LiteralValue.id) PacketType.LiteralValue else PacketType.Operator(number.toInt))

  def parseLengthType(input: String): ParserResult[LengthType, String] =
    parseBinaryNumber(input, 1)
      .map {
        result =>
          if (result == LengthType.TotalLength.id) LengthType.TotalLength else LengthType.SubPacketCount
      }

  def parseLiteralValue(input: String, result: String): ParserResult[Long, String] = {
    val group = input.take(5)

    if (group.startsWith("1"))
      parseLiteralValue(input.drop(5), result + group.drop(1))
    else
      ParserResult(binaryToDecimal(result + group.drop(1)), input.drop(5))
  }

  def parseTotalLength(input: String): ParserResult[(List[PacketValue], TotalLengthCount), String] = {
    val bitCountResult = parseBinaryNumber(input, 15).map(_.toInt)
    val packetBits = bitCountResult.remaining.take(bitCountResult.value)
    val remaining = bitCountResult.remaining.drop(bitCountResult.value)

    ParserResult((parsePackets(packetBits).value, TotalLengthCount(bitCountResult.value)), remaining)
  }

  def parsePacketCount(input: String): ParserResult[(List[PacketValue], SubPacketCount), String] = {
    val packetCountResult = parseBinaryNumber(input, 11).map(_.toInt)

    Range(0, packetCountResult.value)
      .foldLeft(ParserResult(List.empty[PacketValue], packetCountResult.remaining)) {
        (parserResult, _) =>
          parsePacket(parserResult.remaining).map(result => parserResult.value :+ result)
      }
      .map {
        packets => (packets, SubPacketCount(packetCountResult.value))
      }
  }

  def parsePackets(input: String): ParserResult[List[PacketValue], String] =
    if (input.length < 11) ParserResult(List.empty, input)
    else {
      val parserResult = parsePacket(input)

      parsePackets(parserResult.remaining).map(results => parserResult.value :: results)
    }

  def parsePacket(input: String): ParserResult[PacketValue, String] = {
    val versionResult = parseVersionNumber(input)
    val packetTypeResult = parsePacketType(versionResult.remaining)

    packetTypeResult.value match {
      case PacketType.LiteralValue =>
        parseLiteralValue(packetTypeResult.remaining, "")
          .map(value => PacketValue.LiteralPacketValue(value, versionResult.value))

      case operator: PacketType.Operator =>
        val lengthTypeResult = parseLengthType(packetTypeResult.remaining)

        val operatorResult: ParserResult[(List[PacketValue], LengthCount), String] =
          if (lengthTypeResult.value == LengthType.TotalLength) parseTotalLength(lengthTypeResult.remaining)
          else parsePacketCount(lengthTypeResult.remaining)

        operatorResult.map { case (packetValues, lengthCount) => PacketValue.OperatorPacketValue(packetValues, operator, versionResult.value, lengthCount)}
    }
  }

}
