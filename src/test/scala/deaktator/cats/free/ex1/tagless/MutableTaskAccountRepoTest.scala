package deaktator.cats.free.ex1.tagless

import deaktator.cats.free.ex1.support.{Account, Balance}
import deaktator.cats.free.ex1.tagless.AccountRepoA._
import deaktator.cats.free.ex1.support.Constants
import monix.cats.monixToCatsMonad
import monix.eval.Task
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
    // The Terms on the right of the '<-' in the for comprehension are implicitly
    // converted to F[A] via Term.applyAlgebra.  In this case, F[A] = Task[A].
    implicit val acctRepo = MutableAccountRepoInterpreter[Task]

    // Notice that we don't need to supply a type for actions.
    // But Intellij doesn't like it!
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
