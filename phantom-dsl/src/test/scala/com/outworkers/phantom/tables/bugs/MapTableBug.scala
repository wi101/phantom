/*
 * Copyright 2013 - 2017 Outworkers Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.outworkers.phantom.tables.bugs

import com.outworkers.phantom.dsl._
import org.joda.time.Duration

sealed trait Availability

object Availability {
  case object Available extends Availability
  case object Pending extends Availability
  case object UnAvailable extends Availability

  def apply(name: String): Availability = fromString(name)

  def fromString(name: String): Availability = name match {
    case "Available" => Available
    case "Pending" => Pending
    case "UnAvailable" => UnAvailable
    case other => throw new IllegalArgumentException(s"$other is not a valid type of Availability")
  }
}

object CustomPrimitives {

  implicit val availabilityPrimitive: Primitive[Availability] = {
    Primitive.derive[Availability, String](_.toString)(Availability.apply)
  }

  //This one is explicitly needed because the output of macro generating the default map primitive
  //Fails to generate it automatically because it does not have the implicit scope from above.
  //implicit val availabilityTimingMapPrimitive: Primitive[Map[Availability, Duration]] = Primitives.map(availabilityPrimitive, durationPrimitive)

  implicit val durationPrimitive: Primitive[Duration] = {
    Primitive.derive[Duration, String](_.toString)(Duration.parse)
  }
}

import CustomPrimitives._

case class MyEntity(
  key: String,
  timestamp: DateTime,
  durationByState: Map[Availability, Duration]
)

abstract class MapTableBug extends Table[MapTableBug, MyEntity] {

  object key extends StringColumn with PartitionKey
  object timestamp extends DateTimeColumn
  object durationByState extends MapColumn[Availability, Duration]
}
