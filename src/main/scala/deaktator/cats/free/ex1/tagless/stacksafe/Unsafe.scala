package deaktator.cats.free.ex1.tagless.stacksafe

import cats.{Monad, Semigroup}
import scala.language.higherKinds

/**
  * Created by deak on 4/17/17.
  */
case class Unsafe[F[_]](implicit val M: Monad[F]) extends CompositionA[F] {
  private[stacksafe] def zero[N]: F[Endo[N]] =
    M.pure(identity[N] _)

  private[stacksafe] def increment[N](f: Endo[N], n: N)(implicit sn: Semigroup[N]): F[Endo[N]] =
    M.pure(f compose ((a: N) => sn.combine(a, n)))
}
