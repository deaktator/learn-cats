package deaktator.cats.free.ex1

import java.util.Date

import cats.free.Free
import deaktator.cats.free.ex1.common.Amount
import common.today

/**
  * Created by deak on 3/25/17.
  */
object AccountRepository {

  def store(account: Account): AccountRepo[Unit] = Free.liftF(Store(account))

  def query(no: String): AccountRepo[Account] = Free.liftF(Query(no))

  def delete(no: String): AccountRepo[Unit] = Free.liftF(Delete(no))

  def update(no: String, f: Account => Account): AccountRepo[Unit] =
    for {
      a <- query(no)
      _ <- store(f(a))
    } yield ()

  def updateBalance(no: String, amount: Amount, f: (Account, Amount) => Account): AccountRepo[Unit] =
    for {
      a <- query(no)
      _ <- store(f(a, amount))
    } yield ()

  def open(no: String, name: String, openingDate: Date): AccountRepo[Account] =
    for {
      _ <- store(Account(no, name, openingDate))
      a <- query(no)
    } yield a

  def close(no: String): AccountRepo[Account] =
    for {
      _ <- update(no, close)
      a <- query(no)
    } yield a

  private[this] val close: Account => Account = _.copy(dateOfClosing = Option(today))
}
