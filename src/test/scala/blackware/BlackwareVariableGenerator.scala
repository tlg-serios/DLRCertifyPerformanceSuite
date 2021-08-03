package blackware

import java.time.LocalDate
import java.util.UUID
;

object BlackwareVariableGenerator {
  var orderCount: Int = 0
  var workOrderName: String = "BW-" + getRandomString + "-"
  var workOrderRef: String = ""


  case class WorkOrder(ref: String, containers: java.util.ArrayList[String])

  def getNextUUID: String = {
    UUID.randomUUID.toString
  }

  def getTimestamp: String = {
    LocalDate.now.toString
  }

  def getRandomString: String = {
    val ALPHA_NUMERIC_STRING: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    var count: Int = 5
    val builder: StringBuilder = new StringBuilder
    while ( {
      {
        count -= 1; count + 1
      } != 0
    }) {
      val character: Int = (Math.random * ALPHA_NUMERIC_STRING.length).toInt
      builder.append(ALPHA_NUMERIC_STRING.charAt(character))
    }
    builder.toString
  }
}
