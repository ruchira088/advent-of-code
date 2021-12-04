package com.ruchij.twentytwentyone

object DayThree {

  def solve(input: List[String]) = {
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
}
