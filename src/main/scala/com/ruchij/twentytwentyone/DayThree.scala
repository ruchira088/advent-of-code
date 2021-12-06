package com.ruchij.twentytwentyone

object DayThree {

  def solvePartOne(input: List[String]) = {
    val head = input.headOption.getOrElse(throw new Exception("Input is an empty list"))

    val number =
      input
        .foldLeft(Seq.fill(head.length)(0)) {
          (acc, line) => acc.zip(line).map {
            case (count, char) =>
              if (char == '1') count + 1 else count - 1
          }
        }
        .map {
          digit => math.max(math.min(digit, 1), 0)
        }

    fromBinaryToDecimal(number) * fromBinaryToDecimal { number.map(digit => if (digit == 1) 0 else 1) }
  }

  def fromBinaryToDecimal(value: Seq[Int]): Int = {
    val (_, result) = value.foldRight((1, 0)) { case (digit, (multiplier, result)) => (multiplier * 2, (result + digit * multiplier)) }

    result
  }

  def solve(input: List[String]) = {
    val oxygen =
      solvePartTwo(input, (zeros, ones) => ones >= zeros, Seq.empty).map(fromCharToBinary)

    val co2 = solvePartTwo(input, (zeros, ones) => ones < zeros, Seq.empty).map(fromCharToBinary)

    fromBinaryToDecimal(oxygen) * fromBinaryToDecimal(co2)
  }

  def fromCharToBinary(char: Char): Int = if (char == '1') 1 else 0

  def solvePartTwo(input: List[String], selectorOne: (Int, Int) => Boolean, result: Seq[Char]): Seq[Char] = {
    val groups: Map[Char, Int] =
      input.flatMap(_.headOption)
        .groupBy(identity)
        .map { case (charValue, values) => charValue -> values.length }

    val ones = groups.getOrElse('1', 0)
    val zeros = groups.getOrElse('0', 0)

    val select = if (selectorOne(zeros, ones)) '1' else '0'

    val remaining = input.collect { case string if string.startsWith(select.toString) => string.tail }

    val updated = result :+ select

    if (remaining.length == 1) updated ++ remaining.flatMap(_.toList)
    else solvePartTwo(remaining, selectorOne, updated)
  }
}
