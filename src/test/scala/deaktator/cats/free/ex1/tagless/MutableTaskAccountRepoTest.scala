package deaktator.cats.free.ex1.tagless

import deaktator.cats.free.ex1.{Account, AccountRepoMutableInterpreterTest, Balance}
import deaktator.cats.free.ex1.tagless.AccountRepoA._
import monix.eval.Task
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.higherKinds

/**
  * Created by deak on 3/26/17.
  */
class MutableTaskAccountRepoTest extends FlatSpec with Matchers {
  "MutableTaskAccountRepo" should "correctly create and update records" in {
    import deaktator.cats.free.ex1.AccountRepoMutableInterpreterTest._

    // Must be defined here (and implicitly) b/c it's used in the for comprehension.
    implicit val acctRepo = MutableTaskAccountRepo()

    val actions: Task[Unit] = for {
      a <- open(acctNo, name, date)
      _ <- update(a.no, _.copy(balance = Balance(bal)))
    } yield ()

    check(actions.runAsync, acctRepo.getTable)
  }

  private def check(cf: CancelableFuture[Unit], state: Map[String, Account]): Unit = {
    Await.result(cf, 1.second)
    assert(state === AccountRepoMutableInterpreterTest.expected)
  }
}
