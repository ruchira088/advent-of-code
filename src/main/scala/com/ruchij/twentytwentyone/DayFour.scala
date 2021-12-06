package com.ruchij.twentytwentyone

import com.ruchij.twentytwentyone.Utils.IntValue

object DayFour {
  class BingoTile(val number: Int, var marked: Boolean) {
    override def toString: String = s"$number"
  }

  case class BingoBoard(board: Seq[Seq[BingoTile]]) {
    override def toString: String =
      board.map(_.mkString(" ")).mkString("\n")

    def mark(value: Int): Option[BingoTile] = {
      val maybeTitle = board.flatten.find(tile => tile.number == value)

      maybeTitle.foreach {
        tile => tile.marked = true
      }

      maybeTitle
    }
  }

  object BingoBoard {
    def isWinner(bingoBoard: BingoBoard): Boolean = {
      bingoBoard.board.exists { row => row.forall(tile => tile.marked) } ||
        bingoBoard.board.indices.exists {
          index =>
            bingoBoard.board.forall { row => row(index).marked }
        }
    }

    def unmarkedSum(bingoBoard: BingoBoard): Int =
      bingoBoard.board.flatten
        .filter(tile => !tile.marked)
        .map(_.number)
        .sum
  }

  case class Game(numbers: List[Int], boards: Seq[BingoBoard]) {
    override def toString: String =
      s"""
         |${numbers.mkString(", ")}
         |
         |${boards.mkString("\n\n")}
         |""".stripMargin
  }

  def solve(input: List[String]) =
    parse(input).flatMap {
      game => play(game)
    }

  def play(game: Game): Either[String, Int] =
    game.numbers match {
      case head :: tail =>

        winners(head, game.boards) match {
          case board :: Nil if game.boards.size == 1 =>
            Right(BingoBoard.unmarkedSum(board) * head)

          case winningBoards =>
            play(Game(tail, game.boards.filter(value => !winningBoards.exists(board => board.eq(value)))))
        }

      case _ => Left("No winner")
    }

  def winners(number: Int, boards: Seq[BingoBoard]) = {
    boards.foreach { board => board.mark(number) }

    boards.filter(BingoBoard.isWinner).toList
  }

  def parse(input: List[String]) =
    input match {
      case head :: tail =>
        val numbers = head.split(',').map(_.trim).flatMap(_.toIntOption).toList
        val boards = parseBoards(tail)

        Right { Game(numbers, boards) }

      case _ => Left("Empty input list")
    }

  def parseBoards(input: List[String]) =
    input.filter(_.trim.nonEmpty)
      .grouped(5)
      .toList
      .map {
        boardRows =>
          BingoBoard {
            boardRows.map {
              _.split(' ')
                .collect { case IntValue(number) => number }
                .map {
                  value => new BingoTile(value, false)
                }
                .toList
            }
          }
      }

}
