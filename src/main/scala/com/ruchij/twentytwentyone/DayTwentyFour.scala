package com.ruchij.twentytwentyone

import com.ruchij.twentytwenty.DayTwo.{CharValue, IntValue}

object DayTwentyFour {
  case class ALU(registers: Map[Char, Long]) {
    def set(register: Char, value: Long): ALU =
      ALU(registers.updated(register, value))

    def get(register: Char): Long = registers.getOrElse(register, 0)

    def isValid: Boolean = registers.getOrElse('z', 0) == 0
  }

  sealed trait Instruction

  object Instruction {
    sealed trait OperationInstruction extends Instruction {
      val registerOne: Char
      val registerTwo: Either[Char, Int]

      def run(registerOneValue: Long, registerTwoValue: Long): Long
    }

    case class Input(register: Char) extends Instruction {
    }

    case class Add(registerOne: Char, registerTwo: Either[Char, Int]) extends OperationInstruction {
      override def run(registerOneValue: Long, registerTwoValue: Long): Long = registerOneValue + registerTwoValue
    }

    case class Multiply(registerOne: Char, registerTwo: Either[Char, Int]) extends OperationInstruction {
      override def run(registerOneValue: Long, registerTwoValue: Long): Long = registerOneValue * registerTwoValue
    }

    case class Division(registerOne: Char, registerTwo: Either[Char, Int]) extends OperationInstruction {
      override def run(registerOneValue: Long, registerTwoValue: Long): Long = registerOneValue / registerTwoValue
    }

    case class Modulus(registerOne: Char, registerTwo: Either[Char, Int]) extends OperationInstruction {
      override def run(registerOneValue: Long, registerTwoValue: Long): Long = registerOneValue % registerTwoValue
    }

    case class Equal(registerOne: Char, registerTwo: Either[Char, Int]) extends OperationInstruction {
      override def run(registerOneValue: Long, registerTwoValue: Long): Long =
        if (registerOneValue == registerTwoValue) 1 else 0
    }

    def opParser(op: String): (Char, Either[Char, Int]) => OperationInstruction =
      op match {
        case "add" => Instruction.Add
        case "mul" => Instruction.Multiply
        case "div" => Instruction.Division
        case "mod" => Instruction.Modulus
        case "eql" => Instruction.Equal
      }

    val Operation = "(\\S{3}) (\\S) (\\S+)".r
    val InputOp = "inp (\\S)".r

    def parse(line: String): Instruction =
      line match {
        case Operation(ops, CharValue(char), IntValue(value)) => opParser(ops)(char, Right(value))
        case Operation(ops, CharValue(char), CharValue(value)) => opParser(ops)(char, Left(value))
        case InputOp(CharValue(char)) => Instruction.Input(char)
        case _ => throw new IllegalArgumentException(s"Unable to parse $line as an instruction")
      }
  }

  lazy val modelNumbers: LazyList[Long] = 99999999999999L #:: modelNumbers.map(_ - 1)

  def parse(input: List[String]): List[Instruction] =
    input.map(Instruction.parse)

  def solve(input: List[String]) = {
    val instructions = parse(input)
    var count: Long = 0

    modelNumbers
      .filter(number => !number.toString.contains("0"))
      .find {
      number =>
        if (count % 100_000 == 0) { println(count, number) }
        count += 1

        run(instructions, ALU(Map.empty), number.toString.split("").map(_.toInt).toList).isValid
    }
  }

  def run(instructions: List[Instruction], alu: ALU, input: List[Int]): ALU =
    instructions.headOption match {
      case None => alu

      case Some(Instruction.Input(register)) =>
        run(instructions.tail, alu.set(register, input.head), input.tail)

      case Some(operationInstruction: Instruction.OperationInstruction) =>
        run(
          instructions.tail,
          alu.set(
            operationInstruction.registerOne,
            operationInstruction
              .run(alu.get(operationInstruction.registerOne), operationInstruction.registerTwo.fold(alu.get, _.toLong))
          ),
          input
        )
    }

}
