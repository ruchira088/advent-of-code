package com.ruchij.twentytwentyone

import scala.collection.Factory

object Utils {
  object IntValue {
    def unapply(input: String): Option[Int] = input.toIntOption
  }

  implicit class TraverseOps[Itr[x] <: Iterable[x], +A](iterable: Itr[A]) {
    def traverse[F[_], B](f: A => F[B])(implicit monad: Monad[F], factory: Factory[B, Itr[B]]): F[Itr[B]] =
      iterable
        .foldLeft(monad.pure(factory.newBuilder)) { (acc, current) =>
          acc.flatMap(values => f(current).map(value => values.+=(value)))
        }
        .map(_.result())
  }

  trait Monad[F[_]] {
    def map[A, B](monad: F[A])(f: A => B): F[B] = flatMap(monad)(value => pure(f(value)))

    def flatMap[A, B](monad: F[A])(f: A => F[B]): F[B]

    def pure[A](value: => A): F[A]
  }

  implicit class MonadOps[F[_], A](value: F[A])(implicit monad: Monad[F]) {
    def flatMap[B](f: A => F[B]): F[B] =
      monad.flatMap(value)(f)

    def map[B](f: A => B): F[B] = monad.map(value)(f)
  }

  implicit def eitherMonad[C]: Monad[Either[C, *]] =
    new Monad[Either[C, *]] {
      override def flatMap[A, B](monad: Either[C, A])(f: A => Either[C, B]): Either[C, B] =
        monad.flatMap(f)

      override def pure[A](value: => A): Either[C, A] = Right(value)
    }

  implicit class IndexSeqOps[+A](indexedSeq: IndexedSeq[A]) {
    def getIndex(index: Int): Option[A] =
      if (index >= 0 && indexedSeq.size > index) Some(indexedSeq(index)) else None
  }

}
