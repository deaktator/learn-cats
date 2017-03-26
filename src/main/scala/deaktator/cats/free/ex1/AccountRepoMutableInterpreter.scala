package deaktator.cats.free.ex1

import scala.collection.{mutable => scm}
import cats.~>
import monix.eval.Task
import monix.cats._

/**
  * Created by deak on 3/25/17.
  */
case class AccountRepoMutableInterpreter() extends (AccountRepo ~> Task) {
  private[this] val table: scm.Map[String, Account] = scm.Map.empty[String, Account]

  private[this] val step = new (AccountRepoA ~> Task) {
    override def apply[A](action: AccountRepoA[A]): Task[A] = action match {
      case Query(no) =>
        table.get(no)
          .map { a => Task.now(a).asInstanceOf[Task[A]] }
          .getOrElse { throw new RuntimeException(s"Account no $no not found") }
      case Store(account) => Task.now[Unit](table += account.no -> account).asInstanceOf[Task[A]]
      case Delete(no) => Task.now[Unit](table -= no).asInstanceOf[Task[A]]
    }
  }

  def getTable: Map[String, Account] = table.toMap

  override def apply[A](action: AccountRepo[A]): Task[A] = action.foldMap(step)
}
