package com.ruchij.twentytwentyone

object DayTwentyOne {
  val numbers: LazyList[Int] = 0 #:: numbers.map(_ + 1)

  def solve(input: List[String]) = {
    val playerOne = 4
    val playerTwo = 10

    game(0, playerOne, 0, playerTwo, 0, true, numbers.map(number => (number % 100) + 1))
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
