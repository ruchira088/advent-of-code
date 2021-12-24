package com.ruchij.twentytwentyone

object DayTwentyOne {
  case class Game(playerOneScore: Int, playerOnePosition: Int, playerTwoScore: Int, playerTwoPosition: Int, isPlayerOneTurn: Boolean, similar: Long)

  val numbers: LazyList[Int] = 0 #:: numbers.map(_ + 1)

  val permutations: List[Int] =
    for {
      x <- List(1, 2, 3)
      y <- List(1, 2, 3)
      z <- List(1, 2, 3)
    }
    yield x + y + z

  def solve(input: List[String]) = {
    val playerOne = 4
    val playerTwo = 10

    play(List(Game(0, playerOne, 0, playerTwo, true, 1)), 0, 0)
  }

  def play(games: List[Game], playerOneWins: Long, playerTwoWins: Long): Long = {
    println(playerOneWins, playerTwoWins)

    games match {
      case Nil => math.max(playerOneWins, playerTwoWins)

      case game :: tail =>
        if (game.playerOneScore >= 21) play(tail, playerOneWins + game.similar, playerTwoWins)
        else if (game.playerTwoScore >= 21) play(tail, playerOneWins, playerTwoWins + game.similar)
        else if (game.isPlayerOneTurn) {
          val newGames =
            permutations
              .map(dice => (game.playerOnePosition + dice) % 10)
              .map(position => if (position == 0) 10 else position)
              .groupBy(identity)
              .map {
                case (score, list) => Game(game.playerOneScore + score, score, game.playerTwoScore, game.playerTwoPosition, false, list.size * game.similar)
              }
              .toList

          play(newGames ::: tail, playerOneWins, playerTwoWins)
        } else {
          val newGames =
            permutations
              .map(dice => (game.playerTwoPosition + dice) % 10)
              .map(position => if (position == 0) 10 else position)
              .groupBy(identity)
              .map {
                case (score, list) => Game(game.playerOneScore, game.playerOnePosition, game.playerTwoScore + score, score, true, list.size * game.similar)
              }
              .toList

          play(newGames ::: tail, playerOneWins, playerTwoWins)
        }
    }
  }

  def game(playerOneScore: Int, playerOnePosition: Int, playerTwoScore: Int, playerTwoPosition: Int, rolls: Long, isPlayerOneTurn: Boolean, numbers: LazyList[Int]): Long = {
    if (playerOneScore >= 1000)
      rolls * playerTwoScore
    else if (playerTwoScore >= 1000)
      rolls * playerOneScore
    else numbers match {
      case x #:: y #:: z #:: rest =>
        if (isPlayerOneTurn) {
          val position = (playerOnePosition + x + y + z) % 10
          val newPosition = if (position == 0) 10 else position

          game(playerOneScore + newPosition, newPosition, playerTwoScore, playerTwoPosition, rolls + 3, false, rest)
        } else {
          val position = (playerTwoPosition + x + y + z) % 10
          val newPosition = if (position == 0) 10 else position

          game(playerOneScore, playerOnePosition, playerTwoScore + newPosition, newPosition, rolls + 3, true, rest)
        }

      case _ => throw new IllegalStateException("Unexpected condition")
    }
  }

}
