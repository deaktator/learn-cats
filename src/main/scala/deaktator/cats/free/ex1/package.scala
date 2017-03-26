package deaktator.cats.free

import cats.free.Free

/**
  * An adaptation of the `Free` code in chapter 5 of
  * [[https://www.manning.com/books/functional-and-reactive-domain-modeling Functional and Reactive Domain Modeling]]
  * by Debasish Ghosh (ISBN 9781617292248).
  *
  * This code uses [[http://typelevel.org/cats/ Cats]] and [[https://monix.io Monix]] instead of
  * [[https://github.com/scalaz/scalaz Scalaz]].
  *
  * Code is moved around and doesn't necessarily have the exact same names.
  *
  * Created by deaktator on 3/25/17.
  */
package object ex1 {
  type AccountRepo[A] = Free[AccountRepoA, A]
}
