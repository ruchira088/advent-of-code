package com.ruchij.twentytwentyone

object DaySixteen {
  case class ParserResult[A, B](value: A, remaining: B) {
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

    case class LiteralPacketValue(value: Int, version: Int) extends PacketValue {
      override val packetType: PacketType = PacketType.LiteralValue
    }

    case class OperatorPacketValue(
      values: Vector[PacketValue],
      packetType: PacketType.Operator,
      version: Int,
      lengthType: LengthType
    ) extends PacketValue
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
    parsePackets(fromHexToBinary(input.head)).map(packets => packets.map(PacketValue.versionSum).sum)

  def fromHexToBinary(hex: String): String = hex.map(Hex.apply).mkString

  def binaryToDecimal(input: String): Int =
    input
      .map {
        char => if (char == '1') 1 else 0
      }
      .reverse
      .zipWithIndex
      .map {
        case (binary, power) => binary * math.pow(2, power).toInt
      }
      .sum

  def parseBinaryNumber(input: String, length: Int): ParserResult[Int, String] =
    ParserResult(binaryToDecimal(input.take(length)), input.drop(length))

  def parseVersionNumber(input: String): ParserResult[Int, String] =
    parseBinaryNumber(input, 3)

  def parsePacketType(input: String): ParserResult[PacketType, String] =
    parseBinaryNumber(input, 3)
      .map(number => if (number == PacketType.LiteralValue.id) PacketType.LiteralValue else PacketType.Operator(number))

  def parseLengthType(input: String): ParserResult[LengthType, String] =
    parseBinaryNumber(input, 1)
      .map(result => if (result == LengthType.TotalLength.id) LengthType.TotalLength else LengthType.SubPacketCount)

  def parseLiteralValue(input: String, result: String): ParserResult[Int, String] = {
    val group = input.take(5)

    if (group.startsWith("1"))
      parseLiteralValue(input.drop(5), result + group.drop(1))
    else
      ParserResult(binaryToDecimal(result + group.drop(1)), input.drop(5))
  }

  def parseTotalLength(input: String): ParserResult[Vector[PacketValue], String] = {
    val bitCountResult = parseBinaryNumber(input, 15)
    val packetBits = bitCountResult.remaining.take(bitCountResult.value)
    val remaining = bitCountResult.remaining.drop(bitCountResult.value)

    ParserResult(parsePackets(packetBits).value, remaining)
  }

  def parsePacketCount(input: String): ParserResult[Vector[PacketValue], String] = {
    val packetCountResult = parseBinaryNumber(input, 11)

    Range(0, packetCountResult.value).foldLeft(ParserResult(Vector.empty[PacketValue], input.drop(11))) {
      (parserResult, _) =>
        parsePackets(parserResult.remaining).map(result => parserResult.value ++ result)
    }
  }

  def parsePackets(input: String): ParserResult[Vector[PacketValue], String] = parsePackets(input, Vector.empty)

  def parsePackets(input: String, result: Vector[PacketValue]): ParserResult[Vector[PacketValue], String] =
    if (input.length < 11) ParserResult(result, input)
    else {
      val versionResult = parseVersionNumber(input)
      val packetTypeResult = parsePacketType(versionResult.remaining)

      packetTypeResult.value match {
        case PacketType.LiteralValue =>
          val literalValueResult = parseLiteralValue(packetTypeResult.remaining, "")
          parsePackets(
            literalValueResult.remaining,
            result :+ PacketValue.LiteralPacketValue(literalValueResult.value, versionResult.value)
          )

        case operator: PacketType.Operator =>
          val lengthTypeResult = parseLengthType(packetTypeResult.remaining)

          val operatorResult =
            if (lengthTypeResult.value == LengthType.TotalLength) parseTotalLength(lengthTypeResult.remaining)
            else parsePacketCount(lengthTypeResult.remaining)

          parsePackets(
            operatorResult.remaining,
            result :+ PacketValue
              .OperatorPacketValue(operatorResult.value, operator, versionResult.value, lengthTypeResult.value)
          )
      }
    }

}
