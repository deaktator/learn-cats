package deaktator.cats.free.ex1.tagless

import java.util.Date

import cats.Monad
import deaktator.cats.free.ex1.{Account, Balance}
import deaktator.cats.free.ex1.common.{Amount, today}
import monix.eval.Task
import monix.cats.monixToCatsMonad
import scala.collection.{mutable => scm}
import scala.language.{higherKinds, implicitConversions}

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

trait Term[Alg[_[_]], +A] {
  def apply[F[+_]](A: Alg[F]): F[A]
}

object Term {

  /**
    * Implicitly convert a term to an `F[A]` by implicitly applying the algebra to the term.
    * @param term a Term
    * @param A an algebra
    * @tparam Alg type of algebra
    * @tparam F type returned by algebra
    * @tparam A type parameterized by F.
    * @return
    */
  implicit def toF[Alg[_[_]], F[+_], A](term: Term[Alg, A])(implicit A: Alg[F]): F[A] = term(A)
}

case class MutableTaskAccountRepo() extends AccountRepoA[Task] {
  override implicit val M: Monad[Task] = monixToCatsMonad[Task]

  private[this] val table = scm.Map.empty[String, Account]

  def getTable: Map[String, Account] = table.toMap

  override def query(no: String): Task[Account] =
    table.get(no)
      .map { a => Task.now(a) }
      .getOrElse { throw new RuntimeException(s"Account no $no not found") }

  override def store(account: Account): Task[Unit] =
    Task.now[Unit](table += account.no -> account)

  override def delete(no: String): Task[Unit] =
    Task.now[Unit](table -= no)
}
