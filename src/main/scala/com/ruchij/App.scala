package com.ruchij

import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits._
import com.ruchij.twentytwenty.DayTwentyFive
import fs2.Stream
import fs2.io.file.{Files, Path}
import fs2.text.lines
import fs2.text.utf8.decode

import java.nio.file.Paths

object App extends IOApp
{
  override def run(args: List[String]): IO[ExitCode] =
    solve[IO]
      .flatMap(result => IO.blocking(println(result)))
      .as(ExitCode.Success)

  def solve[F[_]: Sync: Files]: F[Option[Long]] =
    for {
      path <- Sync[F].blocking {
        Path.fromNioPath(Paths.get("/Users/ruchira/Development/advent-of-code/input/day-25.txt"))
      }
      inputData <- input[F](path).compile.toList
      result = DayTwentyFive.solve(inputData)
    }
    yield result

  def input[F[_]: Sync: Files](inputFile: Path): Stream[F, String] =
    Files[F].readAll(inputFile)
      .through(decode)
      .through(lines)

}
