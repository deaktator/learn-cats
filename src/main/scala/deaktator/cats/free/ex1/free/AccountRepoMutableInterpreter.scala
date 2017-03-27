package deaktator.cats.free.ex1.free

import cats.~>
import deaktator.cats.free.ex1.support.Account
import monix.eval.Task
import monix.cats.monixToCatsMonad

import scala.collection.{mutable => scm}

/**
  * Interpreter for the free monad.
  *
  * Use Monix `Task` as the output type of the natural transformation.
  *
  * Created by deaktator on 3/25/17.
  */
case class AccountRepoMutableInterpreter() extends (AccountRepo ~> Task) {
  private[this] val table: scm.Map[String, Account] = scm.Map.empty[String, Account]

  // Notice the casting below.  This doesn't occur in the finally tagless approach.
  // It seems as though this may be an unfortunate necessity.
  private[this] val step = new (AccountRepoA ~> Task) {
    override def apply[A](action: AccountRepoA[A]): Task[A] = action match {
      case Query(no) =>
        table.get(no)
          .map { a => Task.pure(a).asInstanceOf[Task[A]] }
          .getOrElse { throw new RuntimeException(s"Account no $no not found") }
      case Store(account) => Task.pure[Unit](table += account.no -> account).asInstanceOf[Task[A]]
      case Delete(no) => Task.pure[Unit](table -= no).asInstanceOf[Task[A]]
    }
  }

  def mutableState: Map[String, Account] = table.toMap

  override def apply[A](action: AccountRepo[A]): Task[A] = action.foldMap(step)
}
