package deaktator.cats.free.ex1.tagless

import cats.syntax.flatMap.toFlatMapOps
import cats.syntax.functor.toFunctorOps
import deaktator.cats.free.ex1.support.{Account, Balance, Constants}
import deaktator.cats.free.ex1.tagless.AccountRepoA.{open, update}
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
    // Must be defined here b/c it's used in the for comprehension.  The Terms
    // on the right of the '<-' in the for comprehension are converted to F[A]
    // via Term.apply.  In this case, F[A] = MonixWrapperTask[A].
    implicit val acctRepo = MutableMonixWrapperTaskAccountRepo()

    // To support for comprehensions, uses:
    //    cats.syntax.flatMap.toFlatMapOps and
    //    cats.syntax.functor.toFunctorOps
    //
    // How do we get rid of `(acctRepo)` without explicitly including
    // `map` and `flatMap` in `MonixWrapperTask`?  Can we somehow use
    // the monad in the companion class?  Seems Term.applyAlgebra can't
    // be found.
    val actions = for {
      a <- open(Constants.acctNo, Constants.name, Constants.date)(acctRepo)
      _ <- update(a.no, _.copy(balance = Balance(Constants.bal)))(acctRepo)
    } yield ()

    check(actions.task.runAsync, acctRepo.mutableState)
  }

  private def check(cf: CancelableFuture[Unit], state: Map[String, Account]): Unit = {
    Await.result(cf, 1.second)
    assert(state === Constants.expected)
  }
}
