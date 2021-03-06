package djc.lang.lib

import util.Bag
import djc.lang.Syntax
import djc.lang.TypedLanguage._
import djc.lang.TypedLanguage.types._
import djc.lang.TypedSyntaxDerived._

import djc.lang.sem._
import djc.lang.base.Integer._
import djc.lang.base.Double._
import djc.lang.AbstractTest


//class TestConcurrentPi1 extends TestConcurrentPi(3, 1, nondeterm_1_subst.Semantics)
//class TestConcurrentPi2 extends TestConcurrentPi(3, 1, nondeterm_2_env.Semantics)
//class TestConcurrentPi3 extends TestConcurrentPi(3, 1, nondeterm_3_routed.SemanticsFactory)
//class TestConcurrentPi4 extends TestConcurrentPi(3, 1, nondeterm_4_grouped.SemanticsFactory)
//class TestConcurrentPi5 extends TestConcurrentPi(3, 1, nondeterm_5_parallel.SemanticsFactory)
class TestConcurrentPi6 extends TestConcurrentPi(2000, 500, concurrent_6_thread.SemanticsFactory)


class TestConcurrentPi[V](max: Int, step: Int, semFactory : ISemanticsFactory[V]) extends AbstractTest(semFactory) {

  testType("formula", ConcurrentPi.formula, ConcurrentPi.formulaType)
  testType("for", ConcurrentPi.forService, ConcurrentPi.forType)
  testType("mkReducer", ConcurrentPi.mkReducer,  ConcurrentPi.mkReducerType)
  testType("piServer", ConcurrentPi.piServer, ConcurrentPi.piServerType)

  val maximalDeviation = 1e-14

  def testPi(n: Int) {
    val piCall = SpawnImg(ConcurrentPi.piServer)~>'pi!!(n, SpawnImg(PRINT_SERVER(TDouble))~>'PRINT)
    testType(s"pi_$n", piCall, Unit)
    val referencePi = ConcurrentPi.concurrentPi(n)
    testInterp(s"pi_$n", piCall, (res: Bag[Syntax.Send]) => res.size == 1 && (res.head match {
      case Syntax.Send(Syntax.ServiceRef(Syntax.Spawn(_, Syntax.Img(`PRINT_SERVER_NO`, Bag())),'PRINT),
             Syntax.BaseCall(DoubleLit(pi), Nil)::Nil)
        => val actualDeviation = Math.abs(pi - referencePi)
           println(s"Computed pi for n=$n: $pi")
           println(s"deviation from sequential aprox: $actualDeviation")
           actualDeviation < maximalDeviation
      case s => println(s"Was $s"); false
    }))
  }

  for (i <- 0 to(max,step))
    testPi(i)
}
