package deaktator.cats.free.ex1.tagless

import deaktator.cats.free.ex1.support.{Account, Balance}
import deaktator.cats.free.ex1.tagless.AccountRepoA.{open, update}
import deaktator.cats.free.ex1.free.AccountRepoMutableInterpreterTest
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by deak on 3/26/17.
  */
class MutableMonixWrapperTastAccountRepoTest extends FlatSpec with Matchers {
  "MutableMonixWrapperTastAccountRepo" should "correctly create and update records" in {
    import deaktator.cats.free.ex1.free.AccountRepoMutableInterpreterTest._

    // Must be defined here (and implicitly) b/c it's used in the for comprehension.
    // The Terms on the right of the '<-' in the for comprehension are implicitly
    // converted to F[A] via Term.toF
    implicit val acctRepo = MutableMonixWrapperTaskAccountRepo()

    val actions = for {
      a <- open(acctNo, name, date)
      _ <- update(a.no, _.copy(balance = Balance(bal)))
    } yield ()

    check(actions.task.runAsync, acctRepo.mutableState)
  }

  private def check(cf: CancelableFuture[Unit], state: Map[String, Account]): Unit = {
    Await.result(cf, 1.second)
    assert(state === AccountRepoMutableInterpreterTest.expected)
  }
}
