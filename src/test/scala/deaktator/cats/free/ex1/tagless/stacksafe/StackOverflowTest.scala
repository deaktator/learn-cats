package deaktator.cats.free.ex1.tagless.stacksafe

import cats.instances.int.catsKernelStdGroupForInt
import cats.instances.option.catsStdInstancesForOption
import cats.syntax.flatMap.toFlatMapOps
import cats.{Id, Group, Monad}
import deaktator.cats.free.ex1.tagless.stacksafe.CompositionOps._
import org.scalatest.{FlatSpec, Matchers}

import scala.language.higherKinds

/**
  * Created by deak on 4/17/17.
  */
class StackOverflowTest extends FlatSpec with Matchers {
  "Finally tagless" should "throw a StackOverflowError with unsafe function composition with Option monad" in {
    a [StackOverflowError] should be thrownBy {
      test(Unsafe[Option])
    }
  }

  it should "throw a StackOverflowError with unsafe function composition with Id monad" in {
    a [StackOverflowError] should be thrownBy {
      test(Unsafe[Id])
    }
  }

  it should "complete successfully with stack-safe function composition with Option monad" in {
    test(Safe[Option])
  }

  it should "complete successfully with stack-safe function composition with Id monad" in {
    test(Safe[Id])
  }

  private[this] def test[M[_]](comp: CompositionA[M])(implicit M: Monad[M]):Unit = {
    val bigEnoughToBlowStack = (1 until 100001).iterator
    val g = oscillate(bigEnoughToBlowStack, comp)
    val h = () => M.map(g)(f => f(0))
    val valOrBlownStack = h()
    valOrBlownStack should be (M.pure(-50000))
  }

  private[this] def oscillate[M[_]: Monad, X: Group](xs: Iterator[X], algebra: CompositionA[M]): M[Endo[X]] = {
    xs.zipWithIndex.foldLeft(zero[X](algebra)){ case (ff, (x, i)) =>
      ff.flatMap { f =>
        val op =
          if (i % 2 == 0)
            increment(f, x)
          else decrement(f, x)
        op(algebra)
      }
    }
  }
}
