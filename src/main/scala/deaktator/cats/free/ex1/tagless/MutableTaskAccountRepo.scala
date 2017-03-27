package deaktator.cats.free.ex1.tagless

import cats.Monad
import deaktator.cats.free.ex1.support.Account
import monix.eval.Task

import scala.collection.{mutable => scm}
import monix.cats.monixToCatsMonad

/**
  * Use Monix Task directly.  This is the right thing to use in practice.
  *
  * Created by deaktator on 3/26/17.
  */
case class MutableTaskAccountRepo() extends AccountRepoA[Task] {
  override implicit val M: Monad[Task] = monixToCatsMonad[Task]

  private[this] val table = scm.Map.empty[String, Account]

  def mutableState: Map[String, Account] = table.toMap

  override def query(no: String): Task[Account] =
    table.get(no)
      .map { a => Task.pure(a) }
      .getOrElse { throw new RuntimeException(s"Account no $no not found") }

  override def store(account: Account): Task[Unit] =
    Task.pure[Unit](table += account.no -> account)

  override def delete(no: String): Task[Unit] =
    Task.pure[Unit](table -= no)
}
