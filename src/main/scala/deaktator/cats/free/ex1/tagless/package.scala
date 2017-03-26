package deaktator.cats.free.ex1

/**
  * An exploration of the [[http://okmij.org/ftp/tagless-final/ finally tagless]] implementation explained
  * on [[https://twitter.com/pchiusano Paul Chiusano]]'s blog article:
  * [[https://pchiusano.github.io/2014-05-20/scala-gadts.html Alternatives to GADTs in Scala]].
  *
  * This is also an adaptation of chapter 5 of
  * [[https://www.manning.com/books/functional-and-reactive-domain-modeling Functional and Reactive Domain Modeling]]
  * by Debasish Ghosh (ISBN 9781617292248).
  *
  * The desire is to illustrate how to achieve many of the same benefits of `Free` without having
  * to provide an explicit GADT that is lifted into the Free monad.
  */
package object tagless {}
