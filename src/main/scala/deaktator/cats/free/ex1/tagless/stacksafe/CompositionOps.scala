package deaktator.cats.free.ex1.tagless.stacksafe

import cats.{Group, Semigroup}
import deaktator.cats.free.ex1.tagless.Term

import scala.language.higherKinds

/**
  * Created by deak on 4/17/17.
  */
object CompositionOps {
  def zero[N]: Term[CompositionA, Endo[N]] = new Term[CompositionA, Endo[N]] {
    override def apply[F[_]](A: CompositionA[F]): F[Endo[N]] = A.zero[N]
  }

  def increment[N](f: Endo[N], n: N)(implicit sn: Semigroup[N]): Term[CompositionA, Endo[N]] = new Term[CompositionA, Endo[N]] {
    override def apply[F[_]](A: CompositionA[F]): F[Endo[N]] = A.increment(f, n)
  }

  // Derived

  def decrement[N](f: Endo[N], n: N)(implicit gn: Group[N]): Term[CompositionA, Endo[N]] = new Term[CompositionA, Endo[N]] {
    override def apply[F[_]](A: CompositionA[F]): F[Endo[N]] = A.increment(f, gn inverse n)
  }
}
