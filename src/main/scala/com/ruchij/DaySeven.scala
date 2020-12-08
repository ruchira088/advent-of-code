package com.ruchij

import cats.Semigroup
import cats.implicits._
import cats.kernel.Monoid
import com.ruchij.DayTwo.IntValue

import scala.util.matching.Regex

object DaySeven {
  case class BagColor(prefix: String, color: String)

  object BagColor {
    val SubjectBag: Regex = "(\\S+) (\\S+) bags".r

    def unapply(input: String): Option[(String, String)] =
      input.trim match {
        case SubjectBag(prefix, color) => Some((prefix, color))
        case _ => None
      }
  }

  object Bags {
    val CompositionBag: Regex = "(\\d+) (\\S+) (\\S+) bags?.?".r

    def unapplySeq(input: String): Option[Seq[(Int, BagColor)]] =
      input.split(",").toList.map(_.trim)
        .filterNot(_ == "no other bags.")
        .traverse {
          case CompositionBag(IntValue(count), prefix, color) => Some((count, BagColor(prefix, color)))
          case _ => None
        }
  }

  case class BagComposition(subject: BagColor, composition: Map[BagColor, Int])

  val BagDescription: Regex = "(.*) contain (.*)".r

  def solve(input: List[String]) =
    parseInput(input).map {
      data =>
        val color = BagColor("shiny", "gold")

        (allBags(Map(color -> 1), data, Map.empty) - color).foldLeft(0) {
          case (total, (_, count)) => total + count
        }
    }

  def parseInput(input: List[String]): Either[String, Map[BagColor, Map[BagColor, Int]]] =
    input.traverse(parseLine)
      .map {
        _.map { case BagComposition(subject, composition) => subject -> composition }.toMap
      }

  val parseLine: String => Either[String, BagComposition] = {
    case BagDescription(BagColor(prefix, color), Bags(bags @ _*)) =>
      Right(BagComposition(BagColor(prefix, color), bags.map { case (count, color) => color -> count }.toMap))

    case line => Left(line)
  }

  def totalCombos(bagColors: Set[BagColor], combinations: Map[BagColor, Map[BagColor, Int]], acc: Set[BagColor]): Set[BagColor] =
    if (bagColors.isEmpty) acc else
    totalCombos(bagColors.flatMap(color => bagCombinations(color, combinations)), combinations, acc ++ bagColors)

  def bagCombinations(bagColor: BagColor, combinations: Map[BagColor, Map[BagColor, Int]]): Iterable[BagColor] =
    combinations
      .flatMap { case (color, combinations) => combinations.get(bagColor).as(color) }

  def allBags(bagColors: Map[BagColor, Int], combinations: Map[BagColor, Map[BagColor, Int]], acc: Map[BagColor, Int]): Map[BagColor, Int] =
    if (bagColors.isEmpty) acc
    else
      allBags(
        bagColors.foldLeft(Map.empty[BagColor, Int]) {
          case (result, (color, count)) =>
            addMap(result, combinations.getOrElse(color, Map.empty).map { case (k, v) =>  k -> v * count })
        },
        combinations,
        addMap(acc, bagColors)
      )

  def addMap[K, V: Monoid](mapOne: Map[K, V], mapTwo: Map[K, V]): Map[K, V] =
    mapOne.map { case (k, v) => k -> Semigroup[V].combine(v, mapTwo.getOrElse(k, Monoid[V].empty)) } ++
      mapTwo.filter { case (k, _) => !mapOne.contains(k) }
}
