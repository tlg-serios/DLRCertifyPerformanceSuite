import org.junit.Test

import java.time.LocalDate
import java.time.format.DateTimeFormatter
//class DateTimeSpec {
//  @Test def date(): Unit = {
//
//    val date = LocalDate.now().minusMonths(22)
//    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
//    val text = date.format(formatter)
//    val parsedDate = LocalDate.parse(text, formatter)
//    println(text)
//  }
//
//}

//"""<Entitlement>""" +
//  """<EntitlementID>${entitlementID}</EntitlementID>""" +
//  """<Quantity>${quantity}</Quantity>""" +
//  """</Entitlement>"""

// Pass a list of a case class called Entitlement
// Entitlement class has 2 strings as variables - entitlementID and quantity
// For every entitlement object in the list, the method must return the string provided with the ${} bits replaced with data
// The block of code should be repeated for as many times as there are entitlement objects in the list//

object entitlements {
  def generateEntitlements(): String = {
      """EntitlementID>Qatar2021</EntitlementID>""" +
      """<Quantity>1</Quantity>""" +
      """EntitlementID>Qatar2022</EntitlementID>""" +
      """<Quantity>2</Quantity>""" +
      """EntitlementID>Qatar2023</EntitlementID>""" +
      """<Quantity>3</Quantity>"""
  }
  printf(generateEntitlements())
}