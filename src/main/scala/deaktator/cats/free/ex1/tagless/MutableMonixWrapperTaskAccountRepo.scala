package deaktator.cats.free.ex1.tagless

import cats.Monad
import deaktator.cats.free.ex1.support.{Account, MonixWrapperTask}
import monix.eval.Task

import scala.collection.{mutable => scm}

/**
  * An account repo algebra for [[MonixWrapperTask]].  Notice it's virtually the same
  * as the  MutableTaskAccountRepo.  This is created only to ensure for comprehensions
  * work in the test even though there are no explicit `map` or `flatMap` functions on
  * [[MonixWrapperTask]], like there could be on `Task`.
  *
  * Created by deaktator on 3/26/17.
  */
case class MutableMonixWrapperTaskAccountRepo() extends AccountRepoA[MonixWrapperTask] {
  override implicit def M: Monad[MonixWrapperTask] = MonixWrapperTask.MonixWrapperTaskMonad

  private[this] val table = scm.Map.empty[String, Account]

  def mutableState: Map[String, Account] = table.toMap

  override def query(no: String): MonixWrapperTask[Account] =
    table.get(no)
      .map { a => MonixWrapperTask(Task.pure(a)) }
      .getOrElse { throw new RuntimeException(s"Account no $no not found") }

  override def store(account: Account): MonixWrapperTask[Unit] =
    MonixWrapperTask(Task.pure[Unit](table += account.no -> account))

  override def delete(no: String): MonixWrapperTask[Unit] =
    MonixWrapperTask(Task.pure[Unit](table -= no))
}
