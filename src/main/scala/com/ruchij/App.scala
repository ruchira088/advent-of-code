package com.ruchij

import cats.effect.{Blocker, ContextShift, ExitCode, IO, IOApp, Sync}
import cats.implicits._
import fs2.Stream
import fs2.io.file.readAll
import fs2.text.{lines, utf8Decode}

import java.nio.file.{Path, Paths}

object App extends IOApp
{
  override def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use { blocker =>
      solve[IO](blocker)
        .flatMap(result => IO.delay(println(result)))
        .as(ExitCode.Success)
    }

  def solve[F[_]: Sync: ContextShift](blocker: Blocker) =
    for {
      path <- Sync[F].delay(Paths.get("/Users/ruchira/Development/advent-of-code/input/day-13.txt"))
      inputData <- input[F](path, blocker).compile.toList
      result = DayThirteen.solve(inputData)
    }
    yield result

  def input[F[_]: Sync: ContextShift](inputFile: Path, blocker: Blocker): Stream[F, String] =
    readAll(inputFile, blocker, 4096)
      .through(utf8Decode)
      .through(lines)

}
