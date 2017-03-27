package deaktator.cats.free.ex1.free

import deaktator.cats.free.ex1.free.AccountRepository._
import deaktator.cats.free.ex1.support.Constants
import deaktator.cats.free.ex1.support.{Account, Balance}
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by deak on 3/25/17.
  */
class AccountRepoMutableInterpreterTest extends FlatSpec with Matchers {

  "AccountRepoMutableInterpreter" should "correctly create and update records" in {
    val actions = for {
      a <- open(Constants.acctNo, Constants.name, Constants.date)
      _ <- update(a.no, _.copy(balance = Balance(Constants.bal)))
    } yield ()

    // Can be defined later because it's not used in the for comprehension.
    val interpreter = new AccountRepoMutableInterpreter
    check(interpreter(actions).runAsync, interpreter.mutableState)
  }

  private def check(cf: CancelableFuture[Unit], state: Map[String, Account]): Unit = {
    Await.result(cf, 1.second)
    assert(state === Constants.expected)
  }
}
