package deaktator.cats.free.ex1

// From Ch 3 code of "Functional and Reactive Domain Modeling", Debasish Ghosh.

import java.util.{ Date, Calendar }

object common {
  type Amount = BigDecimal

  val today: Date = Calendar.getInstance.getTime
}

import common._

case class Balance(amount: Amount = 0)

case class Account(no: String,
                   name: String,
                   dateOfOpening: Date = today,
                   dateOfClosing: Option[Date] = None,
                   balance: Balance = Balance())

