package deaktator.cats.free.ex1.tagless

import java.util.Date

import cats.Monad
import deaktator.cats.free.ex1.support.{Account, Balance}
import deaktator.cats.free.ex1.support.common.{Amount, today}

import scala.language.higherKinds

/**
  * The account repo algebra from ch 5, reworked to use avoid creating a GADT as
  * suggested by Paul Chiusano in the Scala version of Oleg (et al)'s finally tagless
  * encoding.
  *
  * Created by deaktator on 3/26/17.
  */
trait AccountRepoA[F[_]] {

  /**
    * A monad used for chaining operations.  Paul Chiusano suggests having
    * the algebra extend Monad, etc.  But, it seems easier to inject an existent
    * monad (or applicative, functor, etc) for F if it's just encoded as an
    * implicit def.
    *
    * @return a monad for F.
    */
  implicit def M: Monad[F]

  // Basic operations on which all others are based.

  /**
    * Given an account number (alpha-numeric), retrieve an account.
    * @param no alpha-numeric account number.
    * @return an account.
    */
  def query(no: String): F[Account]

  /**
    * Store an account
    * @param account an account to store
    * @return Unit
    */
  def store(account: Account): F[Unit]

  /**
    * Delete an account if it exists.
    * @param no alpha-numeric account number.
    * @return Unit
    */
  def delete(no: String): F[Unit]
}

/**
  * Provides operations in the format of Term.  See Paul Chiusano's article.  These operations,
  * given an instance of an Algebra are translated to the final type constructor F.  In this
  * example F has a monad instance but this is not necessarily required for other algebras.  In
  * this algebra, it is so by design in order to allow chaining of operations.
  */
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
      // Or alternative, this could be done with for comprehension syntax:
      //   import cats.syntax.flatMap.toFlatMapOps
      //   import cats.syntax.functor.toFunctorOps
      //   import A.M
      //   for {
      //     a <- A.query(no)
      //     _ <- A.store(f(a))
      //   } yield ()
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
    override def apply[F[+_]](A: AccountRepoA[F]): F[Account] =
      A.M.flatMap(update(no, close)(A))(_ => A.query(no))
  }

  private[this] val close: Account => Account = _.copy(dateOfClosing = Option(today))
  private[this] val updateBal: (Account, Amount) => Account = (a, amt) => a.copy(balance = Balance(amt))
}
