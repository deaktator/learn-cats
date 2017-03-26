package deaktator.cats.free.ex1

/**
  * From Ch 5 of "Functional and Reactive Domain Modeling", Debasish Ghosh.
  * @tparam A
  */
sealed trait AccountRepoA[+A]
case class Query(no: String) extends AccountRepoA[Account]
case class Store(account: Account) extends AccountRepoA[Unit]
case class Delete(no: String) extends AccountRepoA[Unit]

