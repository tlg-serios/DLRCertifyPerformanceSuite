package oms

import java.io.File

object Constants {
  // OMS & Blackware constants
  val baseUrl = "https://demo.dlrcertify.com" + "/auto2" // make variable
  val blackwareUrl = "http://sgbbka6495:9021" // make variable
  val entitlementUrl = "https://dts.qatar.dlrcertify.com/hubqa/" // make variable
  val sagaUrl = "https://dts.qatar.dlrcertify.com:8443/" // make variable
  val username = "ISAdmin"
  val password = "T@x5t@mp5!!"
  val orderRef = "perf124"
  // DPC Constants
  val dpcOrderRef = "DOWN3"
  val authString = "SXNhZG1pbjpUQHg1dEBtcDUhIVFBVA=="
  val quantity = "140000"
  val siteGLN = "7311101.00001.99"
  val manufacturerOrgGLN = "7311101.00000.00"
  val machineGLN = "7311101.00001.99MACH1"
  val marketCountryCode = "QA"
  val productEAN = "129"
  val dpcCSVRoot = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "scala" + File.separator + "dpcDownload"
  val codesCSV = dpcCSVRoot + "codes.csv"
  val downloadRefsCSV = dpcCSVRoot + "downloadRefs.csv"
}
