package deaktator.cats.free.ex1.support

import java.util.Date

/**
  * Created by deak on 3/27/17.
  */
private[cats] object Constants {
  val acctNo: String = "abc123"
  val name: String = "ryan"
  val date: Date = common.today
  val bal: Int = 1000
  val expected: Map[String, Account] = Map(acctNo -> Account(acctNo, name, date, balance = Balance(bal)))
}
