package deaktator.cats.free.ex1.tagless

import scala.language.{higherKinds, implicitConversions}

/**
  * Term appearing in Paul Chiusano's blog.
  *
  * Created by deaktator on 3/26/17.
  */
trait Term[Alg[_[_]], +A] {

  /**
    * Given an algebra, construct an `F[A]`
    * @param A type parameterizing the type constructor.
    * @tparam F type constuctor.  Covariant in this implementation.  Not in the blog.
    * @return
    */
  def apply[F[+_]](A: Alg[F]): F[A]
}

object Term {

  /**
    * Implicitly convert a term to an `F[A]` by implicitly applying the algebra to the term.
    * @param term a Term
    * @param A an algebra
    * @tparam Alg type of algebra
    * @tparam F type returned by algebra
    * @tparam A type parameterized by F.
    * @return
    */
  implicit def applyAlgebra[Alg[_[_]], F[+_], A](term: Term[Alg, A])(implicit A: Alg[F]): F[A] = term(A)
}
