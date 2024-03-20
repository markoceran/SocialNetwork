package models

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue, Reads, Writes}

object Gender extends Enumeration {
  type Gender = Value
  val Male, Female, Other = Value

  implicit val genderFormat: Format[Gender] = new Format[Gender] {
    def writes(g: Gender): JsValue = JsString(g.toString)

    def reads(json: JsValue): JsResult[Gender] = json match {
      case JsString(value) => JsSuccess(Gender.withName(value))
      case _ => JsError("Invalid gender value")
    }
  }
}

