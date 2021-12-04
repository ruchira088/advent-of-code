package com.ruchij.twentytwenty

import cats.{Applicative, ApplicativeError}
import cats.implicits._

import scala.util.Try

object DayOne {
  def solve[F[_]: ApplicativeError[*[_], Throwable]](input: List[String]): F[Option[Int]] =
    input.traverse { num =>
      Try(num.toInt).toEither
        .fold[F[Int]](
          throwable => ApplicativeError[F, Throwable].raiseError(throwable),
          Applicative[F].pure
        )
    }
      .map(solveTwo(2020))

  def solveOne(target: Int): List[Int] => Option[Int] = {
    case head :: tail if tail.contains(target - head) => Some(head * (target - head))

    case _ :: tail => solveOne(target)(tail)

    case _ => None
  }

  def solveTwo(target: Int): List[Int] => Option[Int] = {
    case head :: tail =>
      solveOne(target - head)(tail).map(_ * head).orElse(solveTwo(target)(tail))

    case _ => None
  }

}
