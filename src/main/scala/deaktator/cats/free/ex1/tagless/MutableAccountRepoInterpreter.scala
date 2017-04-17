package deaktator.cats.free.ex1.tagless

import cats.Monad
import deaktator.cats.free.ex1.support.Account

import scala.language.higherKinds
import scala.collection.{mutable => scm}

/**
  * Created by deak on 4/17/17.
  */
case class MutableAccountRepoInterpreter[M[_]](implicit val M: Monad[M]) extends AccountRepoA[M] {

  private[this] val table = scm.Map.empty[String, Account]

  def mutableState: Map[String, Account] = table.toMap

  override def query(no: String): M[Account] =
    table.get(no)
      .map { a => M.pure(a) }
      .getOrElse { throw new RuntimeException(s"Account no $no not found") }

  override def store(account: Account): M[Unit] =
    M.pure[Unit](table += account.no -> account)

  override def delete(no: String): M[Unit] =
    M.pure[Unit](table -= no)
}
