package ohnosequences.cosas

object properties {

  // deps
  import types._

  trait AnyProperty extends AnyType

  class Property[V](val label: String) extends AnyProperty { type Raw = V }

  case object AnyProperty {

    type ofType[T] = AnyProperty { type Raw = T }

    implicit def propertyOps[P <: AnyProperty](p: P): PropertyOps[P] = PropertyOps(p)
  }

  case class PropertyOps[P <: AnyProperty](val p: P) extends AnyVal {

    def apply(v: P#Raw): ValueOf[P] = valueOf(p)(v)
  }

}
