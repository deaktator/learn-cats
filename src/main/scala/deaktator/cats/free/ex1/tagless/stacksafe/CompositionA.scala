package deaktator.cats.free.ex1.tagless.stacksafe

import cats.{Monad, Semigroup}
import scala.language.higherKinds

/**
  * Created by deak on 4/17/17.
  */
trait CompositionA[F[_]] {
  implicit def M: Monad[F]

  private[stacksafe] def zero[N]: F[Endo[N]]

  private[stacksafe] def increment[N](f: Endo[N], n: N)(implicit sn: Semigroup[N]): F[Endo[N]]
}
