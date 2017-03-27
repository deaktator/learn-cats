package deaktator.cats.free.ex1.tagless

import deaktator.cats.free.ex1.support.{Account, Balance}
import deaktator.cats.free.ex1.tagless.AccountRepoA._
import deaktator.cats.free.ex1.support.Constants
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by deak on 3/26/17.
  */
class MutableTaskAccountRepoTest extends FlatSpec with Matchers {
  "MutableTaskAccountRepo" should "correctly create and update records" in {
    // Must be defined here (and implicitly) b/c it's used in the for comprehension.
    implicit val acctRepo = MutableTaskAccountRepo()

    val actions = for {
      a <- open(Constants.acctNo, Constants.name, Constants.date)
      _ <- update(a.no, _.copy(balance = Balance(Constants.bal)))
    } yield ()

    check(actions.runAsync, acctRepo.mutableState)
  }

  private def check(cf: CancelableFuture[Unit], state: Map[String, Account]): Unit = {
    Await.result(cf, 1.second)
    assert(state === Constants.expected)
  }
}
