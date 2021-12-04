package com.ruchij.twentytwenty

import cats.implicits._
import com.ruchij.twentytwenty.DayTwentyOne.Line.Group

import scala.util.matching.Regex

object DayTwentyOne {
  case class Ingredient(value: String) extends AnyVal

  case class Allergen(value: String) extends AnyVal

  case class Line(ingredients: Set[Ingredient], allergens: Set[Allergen])

  object Line {
    val Group: Regex = "(.*) \\(contains (.*)\\)".r
  }

  def solve(input: List[String]) =
    input.traverse(parseLine)
      .map { lines =>
        reduce(deduce(lines), changed = true).toList
          .sortBy {
            case (allergen, _) => allergen.value
          }
          .map {
            case (_, value) => value.map(_.value).mkString
          }
          .mkString(",")
      }

  val parseLine: String => Either[String, Line] = {
    case Group(start, end) =>
      Right {
        Line(
          start
            .split(" ")
            .map { word =>
              Ingredient(word.trim)
            }
            .toSet,
          end
            .split(",")
            .map { word =>
              Allergen(word.trim)
            }
            .toSet
        )
      }

    case input => Left(s"""Unable to split $input as "${Group.pattern}" """)
  }

  def deduce(lines: List[Line]): Map[Allergen, Set[Ingredient]] =
    lines.foldLeft(Map.empty[Allergen, Set[Ingredient]]) {
      case (acc, line) =>
        line.allergens.foldLeft(acc) {
          case (mapping, allergen) =>
            mapping + (allergen -> mapping.getOrElse(allergen, line.ingredients).intersect(line.ingredients))
        }
    }

  def reduce(mappings: Map[Allergen, Set[Ingredient]], changed: Boolean): Map[Allergen, Set[Ingredient]] =
    if (changed) {
      val (values, hasChanged) = mappings.foldLeft(mappings -> false) {
        case ((values, hasChanged), (allergen, ingredients)) =>
          if (ingredients.size == 1) {
            val updatedMap =
              values.map {
                case value @ (`allergen`, _) => value
                case (value, otherIngredients) => value -> otherIngredients.diff(ingredients)
              }

            updatedMap -> (hasChanged || updatedMap != values)
          }
          else values -> hasChanged
      }

      reduce(values, hasChanged)
    }
    else mappings

}
