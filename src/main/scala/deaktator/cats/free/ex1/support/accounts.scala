package deaktator.cats.free.ex1.support

// From Ch 3 code of "Functional and Reactive Domain Modeling", Debasish Ghosh.

import java.util.{Calendar, Date}

object common {
  type Amount = BigDecimal

  val today: Date = Calendar.getInstance.getTime
}

import deaktator.cats.free.ex1.support.common._

case class Balance(amount: Amount = 0)

case class Account(no: String,
                   name: String,
                   dateOfOpening: Date = today,
                   dateOfClosing: Option[Date] = None,
                   balance: Balance = Balance())

