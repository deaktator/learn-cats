package deaktator.cats.free.ex1

import java.util.Date

import deaktator.cats.free.ex1.AccountRepository._
import deaktator.cats.free.ex1.common.today
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by deak on 3/25/17.
  */
class AccountRepoMutableInterpreterTest extends FlatSpec with Matchers {
  import AccountRepoMutableInterpreterTest._

  "AccountRepoMutableInterpreter" should "correctly create and update records" in {
    val actions = for {
      a <- open(acctNo, name, date)
      _ <- update(a.no, _.copy(balance = Balance(bal)))
    } yield ()

    // Can be defined later because it's not used in the for comprehension.
    val interpreter = new AccountRepoMutableInterpreter
    check(interpreter(actions).runAsync, interpreter.getTable)
  }

  private def check(cf: CancelableFuture[Unit], state: Map[String, Account]): Unit = {
    Await.result(cf, 1.second)
    assert(state === expected)
  }
}

object AccountRepoMutableInterpreterTest {
  val acctNo: String = "abc123"
  val name: String = "ryan"
  val date: Date = today
  val bal: Int = 1000
  val expected: Map[String, Account] = Map(acctNo -> Account(acctNo, name, date, balance = Balance(bal)))
}
