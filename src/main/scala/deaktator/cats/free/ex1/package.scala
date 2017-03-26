package deaktator.cats.free

import cats.free.Free

/**
  * Created by deak on 3/25/17.
  */
package object ex1 {
  type AccountRepo[A] = Free[AccountRepoA, A]
}
