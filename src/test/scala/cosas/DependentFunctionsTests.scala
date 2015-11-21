package ohnosequences.cosas.tests

import ohnosequences.cosas._, fns._
import sampleFunctions._

case object sampleFunctions {

  case object size extends DepFn1[Any,Int] {

    implicit val sizeForInt: App1[this.type,Int,Int]   = this at { x: Int    => x }
    implicit val sizeForStr: App1[this.type,String,Int] = this at { _.length }
    implicit val sizeForChar: App1[this.type,Char,Int]  = this at { x: Char   => 1 }
  }

  case object print extends DepFn1[Any,String] {

    implicit val atInt    : App1[print.type,Int,String]     = print at { n: Int => s"${n}: Int" }
    implicit val atString : App1[print.type,String,String]  = print at { str: String => s"""'${str}': String""" }
  }
}

class DependentFunctionsTests extends org.scalatest.FunSuite {

  test("can apply dependent functions") {

    assert { 2 === size(size("bu")) }
    assert { size(4) === size("four") }
  }

  test("can apply functions as dependent functions") {

    val f: List[String] => Int = { x: List[String] => x.size }
    val df: Fn1[List[String], Int] = f
    assert { df(List("hola", "scalac")) === f(List("hola", "scalac")) }

    val f2: (String,Int) => Int = (str,n) => str.size + n
    val df2: Fn2[String,Int,Int] = f2
    assert { df2("hola",2) === f2("hola",2) }
  }

  test("composition?") {

    assert {
      (size ∘ size ∘ size)(2) === size(size(size(2)))
    }

    assert {
      (print ∘ size)(2) === print(size(2))
    }

    assert {
      (print ∘ print)("abc") === print(print("abc"))
    }
  }
}
