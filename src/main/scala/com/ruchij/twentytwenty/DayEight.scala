package com.ruchij.twentytwenty

import cats.implicits._
import com.ruchij.twentytwenty.DayEight.InstructionType.{Accumulator, Jump, Nop}
import com.ruchij.twentytwenty.DayTwo.IntValue

object DayEight {
  sealed trait InstructionType {
    val label: String
  }

  object InstructionType {
    def unapply(input: String): Option[InstructionType] =
      all.find(_.label.equalsIgnoreCase(input.trim))

    case object Accumulator extends InstructionType {
      override val label: String = "acc"
    }

    case object Nop extends InstructionType {
      override val label: String = "nop"
    }

    case object Jump extends InstructionType {
      override val label: String = "jmp"
    }

    val all: Seq[InstructionType] = Seq(Accumulator, Nop, Jump)
  }

  case class Instruction(instructionType: InstructionType, value: Int)

  object Instruction {
    def unapply(line: String): Option[(InstructionType, Int)] =
      line.trim.split(" ").toList match {
        case InstructionType(instructionType) :: IntValue(intValue) :: _ =>
          Some((instructionType, intValue))

        case _ => None
      }
  }

  def solve(input: List[String]) =
    parse(input).map {
      instructions =>
        flip(instructions)
          .map { flipped =>
            execute(0, flipped, 0, List.empty)
          }
          .collect {
            case Right(value) => value
          }
    }

  def parse(input: List[String]): Either[String, Map[Int, Instruction]] =
    input
      .traverse {
        case Instruction(instructionType, intValue) => Right(Instruction(instructionType, intValue))
        case line => Left(s"Unable to parse $line as Instruction")
      }
      .map {
        _.zipWithIndex.map { case (instruction, index) => index -> instruction }.toMap
      }

  def execute(index: Int, instructions: Map[Int, Instruction], acc: Int, executed: List[Int]): Either[String, Int] =
    if (executed.contains(index)) Left(s"Infinite loop: acc=$acc index=$index")
    else instructions.get(index)
      .map {
        case Instruction(Accumulator, value) => (index + 1, acc + value)
        case Instruction(Nop, _) => (index + 1, acc)
        case Instruction(Jump, value) => (index + value, acc)
      }
      .fold[Either[String, Int]](Right(acc)) {
        case (updatedIndex, updatedAcc) => execute(updatedIndex, instructions, updatedAcc, executed :+ index)
      }

  def flip(instructions: Map[Int, Instruction]): List[Map[Int, Instruction]] =
    flip(Nop, Jump, instructions) ++ flip(Jump, Nop, instructions)

  def flip(from: InstructionType, to: InstructionType, instructions: Map[Int, Instruction]): List[Map[Int, Instruction]] =
    instructions.collect { case (index, Instruction(`from`, _)) => index }
      .map {
        index =>
          instructions ++
            instructions.get(index)
              .collect { case Instruction(_, value) => Instruction(`to`, value) }
              .fold(Map.empty[Int, Instruction]) { instruction => Map(index -> instruction)}
      }
      .toList
}
