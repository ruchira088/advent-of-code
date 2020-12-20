package com.ruchij

import com.ruchij.DayTwo.IntValue

object DayThirteen {
  val solve: List[String] => Either[String, Int] = {
    case IntValue(earliest) :: y :: Nil =>
      parseBusIds(y)
        .map {
          busId => busId -> earliestDepartureForBusId(busId, 0, earliest)
        }
        .minByOption {
          case (_, departure) => departure
        }
        .fold[Either[String, Int]](Left("Bus IDs are empty")) {
          case (busId, departure) => Right((departure - earliest) * busId)
        }

    case input => Left(s"Expected 2 lines of input, but got ${input.length} lines")
  }

  def parseBusIds(line: String): List[Int] =
    line.split(",")
      .toList
      .collect {
        case IntValue(int) => int
      }

  def earliestDepartureForBusId(busId: Int, startTime: Int, earliestDeparture: Int): Int =
    if (startTime >= earliestDeparture) startTime else earliestDepartureForBusId(busId, startTime + busId, earliestDeparture)
}
