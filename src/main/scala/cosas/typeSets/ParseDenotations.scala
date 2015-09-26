package ohnosequences.cosas.typeSets

import ohnosequences.cosas._, types._, fns._

trait ParseDenotationsError
case class KeyNotFound[V](val key: String, val map: Map[String,V]) extends ParseDenotationsError
case class ErrorParsing[PE <: DenotationParserError](val err: PE) extends ParseDenotationsError

class ParseDenotations[V] extends DepFn1[Map[String,V], Either[ParseDenotationsError,AnyTypeSet]]

case object ParseDenotations {

  implicit def empty[V]: App1[parseDenotations[V], Map[String,V], Either[ParseDenotationsError,∅]] =
    App1 { map: Map[String,V] => Right(∅) }

  implicit def nonEmpty[
    V,
    H <: AnyType, TD <: AnyTypeSet,
    HR <: H#Raw,
    PH <: AnyDenotationParser { type Type = H; type Value = V; type D = HR }
  ](implicit
    parseH: PH,
    parseRest: App1[parseDenotations[V], Map[String,V], Either[ParseDenotationsError,TD]]
  )
  : App1[parseDenotations[V], Map[String,V], Either[ParseDenotationsError, (H := HR) :~: TD]] =

  App1 { map: Map[String,V] => {

      map.get(parseH.labelRep).fold[Either[ParseDenotationsError, (H := HR) :~: TD]](
        Left(KeyNotFound(parseH.labelRep, map))
      )(
        v => parseH(parseH.labelRep, v) fold (

          l => Left(ErrorParsing(l)),

          r => parseRest(map).fold[Either[ParseDenotationsError, (H := HR) :~: TD]] (
            err => Left(err),
            td  => Right(r :~: (td: TD))
          )
        )
      )

    }
  }
}
