package com.ruchij

import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits._
import com.ruchij.twentytwentyone.DayTwentyFive
import fs2.Stream
import fs2.io.file.{Files, Path}
import fs2.text.lines
import fs2.text.utf8.decode

import java.nio.file.Paths

object App extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    solve[IO]
      .flatMap(result => IO.blocking(println(result)))
      .as(ExitCode.Success)

  def solve[F[_]: Sync: Files] =
    for {
      path <- Sync[F].blocking[Path] {
        Path.fromNioPath(Paths.get("./input/2021/day-25.txt"))
      }

      inputData <- input[F](path).compile.toList
      result = DayTwentyFive.solve(inputData)
    } yield result

  def input[F[_]: Sync: Files](inputFile: Path): Stream[F, String] =
    Files[F]
      .readAll(inputFile)
      .through(decode)
      .through(lines)

}
