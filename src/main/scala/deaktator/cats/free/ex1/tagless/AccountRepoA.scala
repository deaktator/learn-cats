package deaktator.cats.free.ex1.tagless

import java.util.Date

import cats.Monad
import deaktator.cats.free.ex1.{Account, Balance}
import deaktator.cats.free.ex1.common.{Amount, today}

import scala.language.higherKinds

/**
  * Created by deak on 3/26/17.
  */
trait AccountRepoA[F[_]] {
  implicit def M: Monad[F]

  def query(no: String): F[Account]
  def store(account: Account): F[Unit]
  def delete(no: String): F[Unit]
}

object AccountRepoA {
  def query(no: String): Term[AccountRepoA, Account] = new Term[AccountRepoA, Account] {
    override def apply[F[+_]](A: AccountRepoA[F]): F[Account] =
      A.query(no)
  }

  def store(account: Account): Term[AccountRepoA, Unit] = new Term[AccountRepoA, Unit] {
    override def apply[F[+_]](A: AccountRepoA[F]): F[Unit] =
      A.store(account)
  }

  def delete(no: String): Term[AccountRepoA, Unit] = new Term[AccountRepoA, Unit] {
    override def apply[F[+_]](A: AccountRepoA[F]): F[Unit] =
      A.delete(no)
  }

  def update(no: String, f: Account => Account): Term[AccountRepoA, Unit] = new Term[AccountRepoA, Unit] {
    override def apply[F[+_]](A: AccountRepoA[F]): F[Unit] =
      A.M.flatMap(A.query(no))(a => A.store(f(a)))
  }

  def updateBalance(no: String, amount: Amount): Term[AccountRepoA, Unit] = new Term[AccountRepoA, Unit] {
    override def apply[F[+_]](A: AccountRepoA[F]): F[Unit] =
      update(no, a => updateBal(a, amount))(A)
  }

  def open(no: String, name: String, openingDate: Date): Term[AccountRepoA, Account] = new Term[AccountRepoA, Account] {
    override def apply[F[+_]](A: AccountRepoA[F]): F[Account] =
      A.M.flatMap(A.store(Account(no, name, openingDate)))(_ => A.query(no))
  }

  def close(no: String): Term[AccountRepoA, Account] = new Term[AccountRepoA, Account] {
    override def apply[F[+ _]](A: AccountRepoA[F]): F[Account] =
      A.M.flatMap(update(no, close)(A))(_ => A.query(no))
  }

  private[this] val close: Account => Account = _.copy(dateOfClosing = Option(today))
  private[this] val updateBal: (Account, Amount) => Account = (a, amt) => a.copy(balance = Balance(amt))
}
