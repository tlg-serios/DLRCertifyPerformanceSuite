package EPCIS

import EPCIS.CSVHandler
import EPCIS.{DPCHandler, EventHandler}
import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import oms.Constants

import java.util

class EPCISEventSimulation extends Simulation {

  var separatedDPCs = new util.TreeMap[String, util.ArrayList[String]]()
  var liveDPCs: util.ArrayList[String]
  var wasteDPCs: util.ArrayList[String]

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Constants.sagaUrl)
    .inferHtmlResources()
    .acceptHeader("text/html;charset=UTF-8")
    .header("Content-Type", "text/xml; charset=utf-8")
    .authorizationHeader("username")

  def processEpcis = {
    separatedDPCs = DPCHandler.seperateWasteDPCs(DPCHandler.dpcs)
    liveDPCs = separatedDPCs.get("liveDPCs")
    wasteDPCs = separatedDPCs.get("wasteDPCs")
    System.out.print("LIVE DPCS LIST: \n" + liveDPCs.toString + "\n")
    System.out.print("WASTE DPCS LIST: \n" + wasteDPCs.toString + "\n")
    // create destroy events waste dpcs and send to saga
    api.call(EventHandler.getDestroyXMLStrings(wasteDPCs))
    CSVHandler.output("destroyedCodes.csv", separatedDPCs.get("wasteDPCs"))
    // create comission events live dpcs and send to saga
    api.call(EventHandler.getCommissionXMLStrings(liveDPCs))
    CSVHandler.output("commissionedCodes.csv", separatedDPCs.get("liveDPCs"))
    // create shipment object with live dpcs
    val shipment = EventHandler.getAggregationXMLStrings(liveDPCs)
    // send aggregation events for this shipment to saga
    api.call(shipment.getAggregationEventStrings)
    CSVHandler.output("aggregatedCodes.csv", shipment.getEPCs)
    // create shipping event with container id and send to saga
    api.call(EventHandler.getShippingXMLString(shipment.getContainerID))
    CSVHandler.output("shippedCodes.csv", shipment.getContainerID)
  }

  def generateEntitlements(entitlements: List[Entitlement]): String = {
    var returnString = ""
    entitlements.foreach(entitlement => {
      returnString = returnString + "<Entitlement>\n" +
        s"<EntitlementID>${entitlement.entitlementID}</EntitlementID>\n" +
        s"<Quantity>${entitlement.quantity}</Quantity>\n" +
        "</Entitlement>\n"
    })
    println(returnString)
    returnString
  }

  val getEntitlementString: String =
    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">""" +
      """<soapenv:Header>""" +
      """<soap:HUBHeader>""" +
      """<soap:RequestID>${uuid}</h:RequestID>""" +
      """<soap:ContractID>1</h:ContractID>""" +
      """<soap:OperatorID>Planning User</h:OperatorID>""" +
      """<soap:MachineID>3</h:MachineID>""" +
      s"""<soap:DateTime>${getDate}T00:00.0000000Z</h:DateTime>""" +
      """<soap:CallingApplicationId i:nil="true">""" +
      """<soap:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</soap:AuthenticationToken>""" +
      """</soap:HUBHeader>""" +
      """</soapenv:Header>""" +
      """<soapenv:Body>""" +
      """<dpc:GetEntitlementsRequest>""" +
      """<SiteGLN>${siteGLN}</SiteGLN>""" +
      """<ProductEAN>${productEAN}</ProductEAN>""" +
      """<MarketCountryCode>${marketCountryCode}</MarketCountryCode>""" +
      """</dpc:GetEntitlementsRequest>""" +
      """</soapenv:Body>""" +
      """</soapenv:Envelope>"""

//  val requestCodeGenerationString: String =
//    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">"""
//  """<soapenv:Header>""" +
//    """<soap:HUBHeader>""" +
//    """<soap:RequestID>${uuid}</h:RequestID>""" +
//    """<soap:ContractID>1</h:ContractID>""" +
//    """<soap:OperatorID>Planning User</h:OperatorID>""" +
//    """<soap:MachineID>3</h:MachineID>""" +
//    s"""<soap:DateTime>${getDate}T00:00.0000000Z</h:DateTime>""" +
//    """<soap:CallingApplicationId i:nil="true">""" +
//    """<soap:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</soap:AuthenticationToken>""" +
//    """</soap:HUBHeader>""" +
//    """</soapenv:Header>""" +
//    """<soapenv:Body>""" +
//    """<dpc:CodeGenRequest>""" +
//    """<ManufacturerRequestID>${manufacturerRequestID}</ManufacturerRequestID>""" +
//    """<ManufacturerOrgGLN>${manufacturerOrgGLN}</ManufacturerOrgGLN>""" +
//    """<SiteGLN>${siteGLN}</SiteGLN>""" +
//    """<ProductEAN>${productEAN}</ProductEAN>""" +
//    """<MarketCountryCode>${marketCountryCode}</MarketCountryCode>""" +
//    """<MachineGLN>${machineGLN}</MachineGLN>""" +
//    """<ISR>NA</ISR>""" +
//    """<Entitlements>""" +
//  // TODO KEV HOMEWORK
//  """${entitlementsList}"""
//
//  """</Entitlements>""" +
//    """</dpc:CodeGenRequest>""" +
//    """</soapenv:Body>""" +
//    """</soapenv:Envelope>"""
//
//  val getCodeGenerationStatusString: String =
//    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">""" +
//      """<soapenv:Header>""" +
//      """<soap:HUBHeader>""" +
//      """<soap:RequestID>${uuid}</h:RequestID>""" +
//      """<soap:ContractID>1</h:ContractID>""" +
//      """<soap:OperatorID>Planning User</h:OperatorID>""" +
//      """<soap:MachineID>3</h:MachineID>""" +
//      s"""<soap:DateTime>${getDate}T00:00.0000000Z</h:DateTime>""" +
//      """<soap:CallingApplicationId i:nil="true">""" +
//      """<soap:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</soap:AuthenticationToken>""" +
//      """</soap:HUBHeader>""" +
//      """</soapenv:Header>""" +
//      """<soapenv:Body>""" +
//      """<dpc:GetCodeGenStatusRequest>""" +
//      """<DownloadReference>${downloadReference}</DownloadReference>""" +
//      """</dpc:GetCodeGenStatusRequest>""" +
//      """</soapenv:Body>""" +
//      """</soapenv:Envelope>"""

  //  val downloadCodesString: String =
  //  """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">""" +
  //    """<soapenv:Header>""" +
  //    """<soap:HUBHeader>""" +
  //      """<soap:RequestID>${uuid}</h:RequestID>""" +
  //      """<soap:ContractID>1</h:ContractID>""" +
  //      """<soap:OperatorID>Planning User</h:OperatorID>""" +
  //      """<soap:MachineID>3</h:MachineID>""" +
  //      s"""<soap:DateTime>${getDate}T00:00.0000000Z</h:DateTime>""" +
  //      """<soap:CallingApplicationId i:nil="true">""" +
  //      """<soap:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</soap:AuthenticationToken>""" +
  //    """</soap:HUBHeader>""" +
  //        """</soapenv:Header>""" +
  //          """<soapenv:Body>""" +
  //              """<dpc:DownloadCodesRequest>""" +
  //                """<DownloadReference>${downloadReference}</DownloadReference>""" +
  //                """<Offset>${offset}</Offset>""" +
  //                """<Quantity>${quantity}</Quantity>""" +
  //              """</dpc:DownloadCodesRequest>""" +
  //            """</soapenv:Body>""" +
  //          """</soapenv:Envelope>"""
  //
  //  val downloadCompleteString: String = """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">""" +
  //    """<soapenv:Header>""" +
  //    """<soap:HUBHeader>""" +
  //      """<soap:RequestID>${uuid}</h:RequestID>""" +
  //      """<soap:ContractID>1</h:ContractID>""" +
  //      """<soap:OperatorID>Planning User</h:OperatorID>""" +
  //      """<soap:MachineID>3</h:MachineID>""" +
  //      s"""<soap:DateTime>${getDate}T00:00.0000000Z</h:DateTime>""" +
  //      """<soap:CallingApplicationId i:nil="true">""" +
  //      """<soap:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</soap:AuthenticationToken>""" +
  //    """</soap:HUBHeader>""" +
  //    """</soapenv:Header>""" +
  //    """<soapenv:Body>""" +
  //      """<dpc:DownloadCompleteRequest>""" +
  //        """<DownloadReference>${downloadReference}</DownloadReference>""" +
  //      """</dpc:DownloadCompleteRequest>""" +
  //    """"</soapenv:Body>"""" +
  //  """</soapenv:Envelope>"""

  def getUUID = java.util.UUID.randomUUID().toString

  lazy val getDate = java.time.LocalDate.now

  val getEntitlementData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "siteGLN" -> "7121300.00020.00", "productEAN" -> "98765432207 - Regression Product D0", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "siteGLN" -> "7121300.00020.00", "productEAN" -> "98765432207 - Regression Product D0", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "siteGLN" -> "7121300.00020.00", "productEAN" -> "98765432207 - Regression Product D0", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "siteGLN" -> "7121300.00020.00", "productEAN" -> "98765432207 - Regression Product D0", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "siteGLN" -> "7121300.00020.00", "productEAN" -> "98765432207 - Regression Product D0", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "siteGLN" -> "7121300.00020.00", "productEAN" -> "98765432207 - Regression Product D0", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "siteGLN" -> "7121300.00020.00", "productEAN" -> "98765432207 - Regression Product D0", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "siteGLN" -> "7121300.00020.00", "productEAN" -> "98765432207 - Regression Product D0", "marketCountryCode" -> "A1")).queue

  val requestCodeGenerationData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "FIND ME", "manufacturerOrgGLN" -> "FIND ME", "machineGLN" -> "FIND ME", "siteGLN" -> "FIND ME", "productEAN" -> "FIND ME", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "FIND ME", "manufacturerOrgGLN" -> "FIND ME", "machineGLN" -> "FIND ME", "siteGLN" -> "FIND ME", "productEAN" -> "FIND ME", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "FIND ME", "manufacturerOrgGLN" -> "FIND ME", "machineGLN" -> "FIND ME", "siteGLN" -> "FIND ME", "productEAN" -> "FIND ME", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "FIND ME", "manufacturerOrgGLN" -> "FIND ME", "machineGLN" -> "FIND ME", "siteGLN" -> "FIND ME", "productEAN" -> "FIND ME", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "FIND ME", "manufacturerOrgGLN" -> "FIND ME", "machineGLN" -> "FIND ME", "siteGLN" -> "FIND ME", "productEAN" -> "FIND ME", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "FIND ME", "manufacturerOrgGLN" -> "FIND ME", "machineGLN" -> "FIND ME", "siteGLN" -> "FIND ME", "productEAN" -> "FIND ME", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "FIND ME", "manufacturerOrgGLN" -> "FIND ME", "machineGLN" -> "FIND ME", "siteGLN" -> "FIND ME", "productEAN" -> "FIND ME", "marketCountryCode" -> "A1"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "FIND ME", "manufacturerOrgGLN" -> "FIND ME", "machineGLN" -> "FIND ME", "siteGLN" -> "FIND ME", "productEAN" -> "FIND ME", "marketCountryCode" -> "A1")).queue

  //  var entitlementIds: util.Map[String, List[Entitlement]] = Map("" -> List(Entitlement("", "")))

  case class RequestCodeEntitlementList(entitlements: List[Entitlement])

  case class Entitlement(entitlementID: String, quantity: String)

  var entitlementsList: util.ArrayList[RequestCodeEntitlementList] = new util.ArrayList[RequestCodeEntitlementList]()

  def getEntitlement: ScenarioBuilder = scenario("get entitlement").feed(getEntitlementData)
    .exec(http("get entitlement")
      .post("/DpcDownload/DpcDownloadService.svc")
      .body(StringBody(getEntitlementString))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200))).pause(1)
    .exec(
      session => {
        println(session("RESPONSE_DATA").as[String])

        // set entitlement list
        session
      }
    )

  var downloadReferences: util.ArrayList[String] = new util.ArrayList[String]()

  def getNextDownloadReference = {
    val ref = downloadReferences.get(0)
    downloadReferences.remove(0)
    ref
  }

//  def requestCodeGeneration: ScenarioBuilder = scenario("request code generation").feed(requestCodeGenerationData)
//    .exec(http("request code generation")
//      .post("/dpcdownloadservice.svc")
//      .body(StringBody(requestCodeGenerationString))
//      .check(bodyString.saveAs("RESPONSE_DATA"))
//      .check(status.is(200))).pause(1)
//    .exec(
//      session => {
//        // set download references
//        session
//      }
//    )
//
//  def pollCodeGenerationStatus = {
//    session => {
//      val downloadReference = getNextDownloadReference
//      doWhile(session => !session("RESPONSE_DATA").as[String].contains("Success")) {
//        exec(
//          polling
//            .every(1 seconds)
//            .exec(
//              http("code generation polling")
//                .post("/dpcdownloadservice.svc")
//                .body(StringBody(requestCodeGenerationString.replace("${downloadReference}", downloadReference)))
//                .check(
//                  bodyString.saveAs("RESPONSE_DATA")
//                ))
//        )
//      }
//    }
//  }
//
//  val getCodeGenerationStatus: ScenarioBuilder = scenario("get code generation status").exec(pollCodeGenerationStatus)

  setUp(getEntitlement.inject(atOnceUsers(1)).protocols(httpProtocol))

//  setUp(getEntitlementString.inject(atOnceUsers(7)).protocols(httpProtocol)
//    .andThen(requestCodeGenerationString.inject(nothingFor(200.seconds), atOnceUsers(1)).protocols(httpProtocol)
//      .andThen(getCodeGenerationStatusString.inject(nothingFor(200.seconds), atOnceUsers(1)).protocols(httpProtocol))))
}