package deaktator.cats.free.ex1.tagless.stacksafe

import scala.language.implicitConversions

/**
  * Created by deak on 4/17/17.
  */
case class ListF1[-A, +B] private(fns: List[Any => Any]) extends (A => B) {
  def apply(a: A): B =
    fns.foldLeft(a: Any)((x, f) => f(x)).asInstanceOf[B]
  override def compose[C](g: C => A): C => B =
    ListF1[C, B](g.asInstanceOf[Any => Any] :: fns)
}

object ListF1 {
  implicit def apply[A, B](f: A => B): ListF1[A, B] = {
    f match {
      case g@ListF1(_) => g
      case g => new ListF1[A, B](g.asInstanceOf[Any => Any] :: Nil)
    }
  }
}
