package deaktator.cats.free.ex1.support

import cats.Monad
import monix.cats.monixToCatsMonad
import monix.eval.Task

/**
  * Created by deak on 3/26/17.
  */
case class MonixWrapperTask[+A](task: Task[A]) {
  // Intentionally do not include map / flatMap.
  // This will

//  def map[B](f: A => B): MonixWrapperTask[B] =
//    MonixWrapperTask.MonixWrapperTaskMonad.map(this)(f)
//  def flatMap[B](f: A => MonixWrapperTask[B]): MonixWrapperTask[B] =
//    MonixWrapperTask.MonixWrapperTaskMonad.flatMap(this)(f)
}

object MonixWrapperTask {

  /**
    * Monad for MonixWrapperTask that delegates to the Monix Task Monad.  This is
    * included in MonixWrapperTask's implicit scope to be used with
    *
    * {{{
    * import cats.syntax.flatMap.toFlatMapOps
    * import cats.syntax.functor.toFunctorOps
    * }}}
    *
    * in order to enable for comprehensions.
    */
  implicit object MonixWrapperTaskMonad extends Monad[MonixWrapperTask] {
    private[this] def taskMonad: Monad[Task] = monixToCatsMonad[Task]

    override def pure[A](x: A): MonixWrapperTask[A] = MonixWrapperTask(taskMonad.pure(x))

    override def flatMap[A, B](fa: MonixWrapperTask[A])(f: A => MonixWrapperTask[B]): MonixWrapperTask[B] =
      MonixWrapperTask(taskMonad.flatMap(fa.task)(f.andThen(_.task)))

    override def tailRecM[A, B](a: A)(f: (A) => MonixWrapperTask[Either[A, B]]): MonixWrapperTask[B] =
      MonixWrapperTask(taskMonad.tailRecM(a)(f.andThen(_.task)))
  }
}
