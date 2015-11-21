package ohnosequences.cosas.klists

import ohnosequences.cosas._, fns._

class find[A] extends DepFn1[AnyKList,A]

case object find extends FoundInTail {

  implicit def foundInHead[H <: A, T <: AnyKList, A <: T#Bound]
  : AnyApp1At[find[A], H :: T] { type Y = A } =
    App1 { s: H :: T => s.head }
}

trait FoundInTail {

  implicit def foundInTail[
    H <: T#Bound,
    T <: AnyKList { type Bound >: A },
    A
  ](implicit
    find: AnyApp1At[find[A], T] { type Y = A }
  )
  : AnyApp1At[find[A], H :: T] { type Y = A } =
    App1 { s: H :: T => find(s.tail) }
}

class FindS[A] extends DepFn1[AnyKList, A]

case object FindS extends FindSInTail {

  implicit def foundInHead[E, H <: E, T <: AnyKList { type Bound >: E }]
  : AnyApp1At[FindS[E], H :: T] { type Y = H } =
    App1 { (s: H :: T) => s.head }
}

trait FindSInTail {

  implicit def foundInTail[
    X, E >: X, H <: T#Bound,
    T  <: AnyKList { type Bound >: E }
  ](implicit
      l: AnyApp1At[FindS[E], T] { type Y = X }
  )
  : AnyApp1At[FindS[E], H :: T] { type Y = X } =
    App1 { (s: H :: T) =>  l(s.tail) }
}
