package deaktator.cats.free.ex1.free

import java.util.Date

import cats.free.Free
import deaktator.cats.free.ex1.support.Account
import deaktator.cats.free.ex1.support.common.{Amount, today}

/**
  * Operations.
  *
  * Created by deaktator on 3/25/17.
  */
object AccountRepository {

  // Smart constructors corresponding to types in the GADT.

  def store(account: Account): AccountRepo[Unit] = Free.liftF(Store(account))

  def query(no: String): AccountRepo[Account] = Free.liftF(Query(no))

  def delete(no: String): AccountRepo[Unit] = Free.liftF(Delete(no))

  // Smart constructors based on derived operations.

  def update(no: String, f: Account => Account): AccountRepo[Unit] = {
    // Or alternative, this could be done with for comprehension syntax:
    //    for {
    //      a <- query(no)
    //      _ <- store(f(a))
    //    } yield ()
    query(no).flatMap(a => store(f(a)))
  }

  def updateBalance(no: String, amount: Amount, f: (Account, Amount) => Account): AccountRepo[Unit] =
    query(no).flatMap(a => store(f(a, amount)))

  def open(no: String, name: String, openingDate: Date): AccountRepo[Account] =
    store(Account(no, name, openingDate)).flatMap(_ => query(no))

  def close(no: String): AccountRepo[Account] =
    update(no, close).flatMap(_ => query(no))

  private[this] val close: Account => Account = _.copy(dateOfClosing = Option(today))
}
