package ohnosequences.cosas

import ohnosequences.cosas.fns._

package object klists {

  type KNil[+A] = KNilOf[A]
  def  KNil[A]: KNil[A] = KNilOf[A]
  type ::[+H <: T#Bound, +T <: AnyKList] = KCons[H,T]

  type mapKList[F <: AnyDepFn1] =
    MapKListOf[F, F#In1,F#Out]
  def mapKList[F <: AnyDepFn1](f: F): mapKList[F] = new mapKList[F]

  type toList[L <: AnyKList] = ToList[L]
  def toList[L <: AnyKList]: toList[L] = new ToList[L]

  type findIn[A <: L#Bound, L <: AnyKList] = (A FindIn L)
  def findIn[A <: L#Bound, L <: AnyKList]: findIn[A,L] = new (A FindIn L)
}
