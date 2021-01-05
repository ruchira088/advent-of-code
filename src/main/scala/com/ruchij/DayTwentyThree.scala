package com.ruchij

import cats.implicits._

object DayTwentyThree {

  case class CupCircle(cups: List[Int], currentCup: Int) {
    val currentCupIndex: Int = cups.indexOf(currentCup)

    def get(index: Int): Int =
      if (index < 0) get(cups.size + index)
      else cups.get(index).getOrElse(get(index - cups.size))

    def label: String =
      (cups.dropWhile(_ != 1) ++ cups.takeWhile(_ != 1)).filter(_ != 1).mkString
  }

  object CupCircle {
    def pickThreeCups(cupCircle: CupCircle): (CupCircle, List[Int]) = {
      val picked =
        List.range(cupCircle.currentCupIndex + 1, cupCircle.currentCupIndex + 4)
          .map(cupCircle.get)

     CupCircle(cupCircle.cups.filter(number => !picked.contains(number)), cupCircle.currentCup) -> picked
    }

    def destinationCup(cupCircle: CupCircle): Int =
      destinationCup(cupCircle.cups, cupCircle.currentCup - 1)

    def destinationCup(cups: List[Int], value: Int): Int =
      if (cups.contains(value)) value
      else if (cups.min > value) destinationCup(cups, cups.max)
      else destinationCup(cups, value - 1)

    def combine(cupCircle: CupCircle, dest: Int, picked: List[Int]) = {
      val (listA, listB) = cupCircle.cups.splitAt(cupCircle.cups.indexOf(dest) + 1)
      val cups = listA ++ picked ++ listB

      CupCircle(cups, (cups ++ cups).apply(cups.indexOf(cupCircle.currentCup) + 1))
    }

    def run(cupCircle: CupCircle): CupCircle = {
      val (newCupCircle, picked) = pickThreeCups(cupCircle)
      val dest = destinationCup(newCupCircle)

      combine(newCupCircle, dest, picked)
    }

    def run(cupCircle: CupCircle, moves: Int): CupCircle =
      if (moves == 0) cupCircle else run(run(cupCircle), moves - 1)
  }

  def solve(input: List[String]) =
    parse("219347865").map {
      cupCircle => CupCircle.run(cupCircle, 100).label
    }

  def parse(input: String) =
    input.split("").toList.traverse(_.toIntOption)
      .collect {
        case values @ head :: _ => CupCircle(values, head)
      }

}
