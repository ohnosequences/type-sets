package ohnosequences.cosas.tests

import ohnosequences.cosas._, fns._

object sampleFunctions {

  case object size extends DepFn1[Any,Int] {

    implicit val sizeForStr   = this at { x: String => x.length }
    implicit val sizeForChar  = this at { x: Char   => 1 }
    implicit val sizeForInt   = this at { x: Int    => x }
  }

  case object print extends DepFn1[Any,String] {

    implicit val atInt = this at { n: Int => s"${n}: Int" }
    implicit val atString = this at { str: String => s"""'${str}': String""" }
  }

  import typeSets._

  // alternative mapping over typeSets
  case object MapToHList extends DepFn2[AnyDepFn1, AnyTypeSet, shapeless.HList] {

    import shapeless._

    implicit def empty[F <: AnyDepFn1] = this at { (f: F, e: ∅) => HNil }

    implicit def cons[
      F <: AnyDepFn1,
      H <: F#In1, T <: AnyTypeSet,
      OutH, OutT <: HList
    ](implicit
      evF: App1[F,H] { type Out = OutH },
      evThis: App2[this.type,F,T] { type Out = OutT }
    )
    : App2[this.type, F,H :~: T] { type Out = OutH :: OutT } =
      this at { (f: F, ht: H :~: T) => f(ht.head) :: this(f,ht.tail) }
  }

  // union
  import typeSets._
  trait union_5 extends DepFn2[AnyTypeSet, AnyTypeSet, AnyTypeSet] {

    // use this, bound it at the end
    type Union <: this.type
    val union: Union

    implicit def bothHeads[SH, ST <: AnyTypeSet, QH, QT <: AnyTypeSet, O <: AnyTypeSet](implicit
      sh: SH ∉ (QH :~: QT),
      qh: QH ∉ (SH :~: ST),
      rest: App2[Union, ST, QT] { type Out = O }
    ) =
      union at { (s: SH :~: ST, q: QH :~: QT) => s.head :~: q.head :~: union(s.tail, q.tail) }
  }
  trait union_4 extends union_5 {

    implicit def qHead[S <: AnyTypeSet, QH, QT <: AnyTypeSet, O <: AnyTypeSet](implicit
      qh: QH ∈ S,
      rest: App2[Union, S, QT] { type Out = O }
    ) =
      union at { (s: S, q: QH :~: QT) => union(s, q.tail) }
  }
  trait union_3 extends union_4 {

    implicit def sHead[SH, ST <: AnyTypeSet, Q <: AnyTypeSet, O <: AnyTypeSet](implicit
      sh: SH ∈ Q,
      rest: App2[Union,ST,Q] { type Out = O }
    ) =
      union at { (s: SH :~: ST, q: Q) => union(s.tail,q) }
  }
  trait union_2 extends union_3 {

    implicit def qInS[S <: AnyTypeSet.SubsetOf[Q], Q <: AnyTypeSet] =
      union at { (q:Q, s: S) => q }
  }
  case object union extends union_2 {

    type Union = this.type
    val union = this

    implicit def sInQ[S <: AnyTypeSet.SubsetOf[Q], Q <: AnyTypeSet] =
      union at { (s:S, q:Q) => q }
  }
}

class DependentFunctionsTests extends org.scalatest.FunSuite {

  import sampleFunctions._
  import shapeless.{union => ppf, _ }
  import typeSets._

  test("can apply dependent functions") {

    assert { 2 === size(size(2)) }
    assert { size(4) === size("four") }

    val a = "ohoho" :~: 'c' :~: ∅
    val b = "lala" :~: 'a' :~: 2 :~: ∅
    val c = "lololo" :~: true :~: ∅

    assert { 4 :: 1 :: 2 :: HNil === MapToHList(size,b) }

    val ab = union(a,b)
    val ba = union(b,a)
    val bc = union(b,c)
    val cb = union(c,b)
    val abc = union(union(a,b),c)

  }

  test("can compose and apply dependent functions") {

    object sp extends Composition(print,size)
    object psp extends Composition(sp,print)

    assert { "1: Int" === Composition(size,print)(1) }
    assert { Composition(size,print)(1) === Composition(size,print)(1) }

    val uh = sp(2)
    val uhoh = psp(2)

    val zzz = Composition(Composition(print,size),print)
    // does not work!
    // zzz(2)
  }
}
