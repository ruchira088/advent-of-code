package com.ruchij.twentytwenty

import cats.implicits._
import com.ruchij.twentytwenty.DayTwo.IntValue

import scala.annotation.tailrec
import scala.util.matching.Regex

object DayTwentyTwo {

  case class Player(id: Int, cards: List[Int])

  object Player {
    val PlayerId: Regex = "Player (\\d+):".r

    def score(player: Player): Long =
      List
        .range(1, player.cards.size + 1)
        .zip(player.cards.reverse)
        .map { case (multiplier, cardNumber) => (multiplier * cardNumber).toLong }
        .sum
  }

  case class Game(playerOne: Player, playerTwo: Player)

  object Game {
    def playSingleRound(game: Game, playedGames: Set[Game]): Either[Player, Game] =
      (game.playerOne.cards, game.playerTwo.cards) match {
        case (x :: xs, y :: _) if playedGames.contains(game) =>
          Left(Player(game.playerOne.id, xs :+ x :+ y))

        case (x :: xs, y :: ys) if x <= xs.size && y <= ys.size =>
          val winner = playUntilWinner(Game(Player(game.playerOne.id, xs.take(x)), Player(game.playerTwo.id, ys.take(y))), Set.empty)

          if (winner.id == game.playerOne.id)
            Right(Game(Player(game.playerOne.id, xs :+ x :+ y), Player(game.playerTwo.id, ys)))
          else
            Right(Game(Player(game.playerOne.id, xs), Player(game.playerTwo.id, ys :+ y :+ x)))

        case (x :: xs, y :: ys) =>
          Right {
            if (x > y) Game(Player(game.playerOne.id, xs :+ x :+ y), Player(game.playerTwo.id, ys))
            else Game(Player(game.playerOne.id, xs), Player(game.playerTwo.id, ys :+ y :+ x))
          }

        case (Nil, _) => Left(game.playerTwo)

        case (_, Nil) => Left(game.playerOne)
      }

    @tailrec
    def playUntilWinner(game: Game, playedGames: Set[Game]): Player =
      playSingleRound(game, playedGames) match {
        case Left(player) => player
        case Right(newGame) => playUntilWinner(newGame, playedGames + game)
      }
  }

  def solve(input: List[String]) =
    split(input, _.trim.isEmpty)
      .traverse(parsePlayer)
      .flatMap {
        case one :: two :: Nil => Right(Game(one, two))

        case players => Left(s"Expected 2 players, but found ${players.size} players")
      }
      .map(game => Game.playUntilWinner(game, Set.empty))
      .map(Player.score)

  def split(input: List[String], condition: String => Boolean): List[List[String]] =
    input.foldLeft(List.empty[List[String]]) {
      case (groups, line) =>
        groups.lastOption.fold(if (condition(line)) Nil else List(List(line))) { group =>
          if (condition(line)) if (group.isEmpty) groups else groups :+ List.empty
          else groups.init :+ (group :+ line)
        }
    }

  val parsePlayer: List[String] => Either[String, Player] = {
    case Player.PlayerId(IntValue(id)) :: cardNumbers =>
      cardNumbers
        .traverse { number =>
          number.toIntOption.fold[Either[String, Int]](Left(s"""Unable to parse "$number" as a number"""))(Right.apply)
        }
        .map(cardNumbers => Player(id, cardNumbers))

    case input => Left(s"""Unable to parse $input as a ${classOf[Player].getSimpleName}""")
  }
}
