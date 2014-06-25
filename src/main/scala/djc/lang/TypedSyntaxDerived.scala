package djc.lang

import util.Bag
import djc.lang.TypedSyntax._
import djc.lang.typ.Types._
import djc.lang.typ.Checker._

object TypedSyntaxDerived {

  implicit def makeParSend(s: Send) = Par(s)

  // undelimited CPS function type:
  //   t * (u -> Unit) -> Unit
  def TFun(t: Type, u: Type) = TSvc(t, TSvc(u))
  def TFun(t1: Type, t2: Type, u: Type) = TSvc(t1, t2, TSvc(u))

  def Let(x: Symbol, xt: Type, s: Exp, p: Exp): Exp = {
    val srv = LocalServer(Rule(Bag(Pattern('def, x -> xt)), p))
    Send(srv~>'def, s)
  }

  def Let(x: Symbol, s: Exp, p: Exp, gamma: Context, boundTv: Set[Symbol]): Exp = {
    val xt = typeCheck(gamma, boundTv, s)
    Let(x, xt, s, p)
  }

  def Letk(x: Symbol, xt: Type, s: Exp, p: Exp): Exp = {
    val cont = SpawnLocal(ServerImpl(Rule('k?(x -> xt), p)))~>'k
    s match {
      case Send(svc, args) => Send(svc, args :+ cont)
      case e => Send(e, cont)
    }
  }


  def Service(p: Pattern, e: Exp) = ServiceRef(Server(Rule(Bag(p), e)), p.name)
  def LocalService(p: Pattern, e: Exp) = ServiceRef(LocalServer(Rule(Bag(p), e)), p.name)

  def Lambda(x: Symbol, t: (Type,Type), e: Exp): Exp = Lambda(x, t._1, e, t._2)
  def Lambda(x: Symbol, xt: Type, e: Exp, resT: Type): Exp =
      SpawnLocal(ServerImpl(
        Rule(
          'app?(x -> xt, 'k-> ?(resT)),
          'k!!(e))))~>'app
  def Lambda(xs: List[(Symbol,Type)], e: Exp, resT: Type): Exp = {
    val args = (xs :+ ('k -> ?(resT))).toSeq
    SpawnLocal(ServerImpl(
      Rule(
        'app ?(args:_*),
        'k !! (e)))) ~> 'app
  }

  val TThunk = TSrvRep('force -> ?())
  def Thunk(e: Exp) =
    ServerImpl(Rule(Bag(Pattern('force)), e))

  def Ifc(c: Exp, t: Exp, e: Exp) =
    Send(
      ServiceRef(
        SpawnLocal(
          BaseCall(djc.lang.base.Bool.If,
            c,
            Thunk(t),
            Thunk(e)
          )),
        'force))


//  def SendSeq(e1: Send, e2: Send) =
//  LocalServerImpl(Rule(
//      'k1?('v1 -> Unit),
//      e2
//    ))~>'k1!!(Par(e1))



}