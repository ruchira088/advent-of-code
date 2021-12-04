package com.ruchij.twentytwenty

import com.ruchij.twentytwenty.DayTwo.IntValue

import scala.util.matching.Regex

object DayFour {
  val KeyValue: Regex = "(\\S+):(\\S+)".r

  val HeightCm: Regex = "(\\d+)cm".r

  val HeightInch: Regex = "(\\d+)in".r

  val HairColor: Regex = "#([0-9a-f]{6})".r

  val MandatoryFields = Set("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

  def solve(input: List[String]) = {
    val groups = group(input, List.empty)

    groups.count { line =>
      val fields = parse(line)

      MandatoryFields.forall(fields.contains)
    }
  }

  def parse(fields: List[String]): List[String] =
    fields
      .flatMap(_.split(" "))
      .collect {
        case KeyValue(key, value) if validate(key, value) => key
      }


  def validate(key: String, value: String): Boolean =
    key match {
      case "byr" => value.length == 4 && value.toIntOption.exists(year => year >= 1920 && year <= 2002)

      case "iyr" => value.length == 4 && value.toIntOption.exists(year => year >= 2010 && year <= 2020)

      case "eyr" => value.length == 4 && value.toIntOption.exists(year => year >= 2020 && year <= 2030)

      case "hgt" =>
        value match {
          case HeightCm(IntValue(cm)) => cm >= 150 && cm <= 193

          case HeightInch(IntValue(inch)) => inch >= 59 && inch <= 76

          case _ => false
        }

      case "hcl" => HairColor.matches(value)

      case "ecl" => List("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(value)

      case "pid" => value.length == 9 && value.forall(_.isDigit)

      case _ => false
    }

  def group(input: List[String], interim: List[String]): List[List[String]] =
    input match {
      case head :: tail if head.trim.isEmpty => interim :: group(tail, List.empty)
      case head :: tail => group(tail, interim :+ head)
      case _ => List(interim)
    }
}
