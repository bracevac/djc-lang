package djc.lang.lib.worker

import djc.lang.typ.Types._
import djc.lang.TypedSyntax._
import djc.lang.TypedSyntaxDerived._
import djc.lang.base.Integer._
import util.Bag
import djc.lang.lib.Fibonacci
import djc.lang.lib.Function

object Task {

  val TTask = ?()
  val TTaskK = TUniv('K, ?(?('K)))

  val mkFibTaskType = TFun(TInteger, TTask)
  val mkFibTask = LocalServerImpl(
    Rule(
      Bag(Pattern('run, 'n -> TInteger, 'k -> ?(TTask))),
      'k!!(Thunk(Fibonacci.fib!!('n, Function.consume(TInteger)))))
  )~>'run

  val mkFibTaskTypeK = TFun(TInteger, TTaskK(TInteger))
  val mkFibTaskK = LocalServerImpl(
    Rule(
      Bag(Pattern('run, 'n -> TInteger, 'k -> ?(TTaskK(TInteger)))),
      'k!!(ServiceRef(
        LocalServerImpl(Rule(
          Bag(Pattern('force, 'k2 -> ?(TInteger))),
          Fibonacci.fib!!('n, 'k2))),
        'force)))
  )~>'run
}