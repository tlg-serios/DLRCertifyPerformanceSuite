package dpcDownload

import io.gatling.core.Predef.{pause, _}
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import oms.Constants

import java.util
import java.util.regex.{Matcher, Pattern}
import scala.concurrent.duration.DurationInt

class DPCDownloadSimulation extends Simulation {

  var customerReferences = new util.ArrayList[String]

  def getNextReference = {
    val ref = customerReferences.get(0)
    customerReferences.remove(0)
    ref
  }

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Constants.entitlementUrl)
    .inferHtmlResources()
    .acceptHeader("text/html;charset=UTF-8")
    .header("Content-Type", "text/xml; charset=utf-8")
    .header("Authorization", "SXNhZG1pbjpUQHg1dEBtcDUhIQ==")

  def generateEntitlements(entitlements: List[Entitlement]): String = {
    var returnString = ""
    entitlements.foreach(entitlement => {
      returnString = returnString + "<Entitlement>\n" +
        s"<EntitlementID>${entitlement.entitlementID}</EntitlementID>\n" +
        s"<Quantity>${entitlement.quantity}</Quantity>\n" +
        "</Entitlement>\n"
    })
    returnString
  }

  def getEntitlementsString: String = {
    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">
<soapenv:Header>
<soap:HUBHeader>
<soap:RequestId>c7d54a6d-69f2-489c-8056-0949ff698342</soap:RequestId>
<soap:ContractId>1</soap:ContractId>
<soap:OperatorId>1</soap:OperatorId>
<soap:MachineId>1</soap:MachineId>
<soap:DateTime>2019-12-23T12:34:56.789+01:00</soap:DateTime>
<soap:CallingApplicationId>DTP</soap:CallingApplicationId>
<soap:AuthenticationToken>A3D53E5D-F136-4144-B40A-CF7A9A8C3223</soap:AuthenticationToken>
</soap:HUBHeader>
</soapenv:Header>
<soapenv:Body>
<dpc:GetEntitlementsRequest>
<SiteGLN>${siteGLN}</SiteGLN>
<ProductEAN>${productEAN}</ProductEAN>
<MarketCountryCode>${marketCountryCode}</MarketCountryCode>
</dpc:GetEntitlementsRequest>
</soapenv:Body>
</soapenv:Envelope>"""
  }


  def codeGenString = {
    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">
   <soapenv:Header>
      <soap:HUBHeader>
         <soap:RequestId>c7d54a6d-69f2-439c-8056-0949ff698342</soap:RequestId>
           <soap:ContractId>1</soap:ContractId>
         <soap:OperatorId>1</soap:OperatorId>
         <soap:MachineId>1</soap:MachineId>
         <soap:DateTime>2019-12-09T12:34:56.789+01:00</soap:DateTime>
         <soap:CallingApplicationId>DTP</soap:CallingApplicationId>
         <soap:AuthenticationToken>A3D53E5D-F136-4144-B40A-CF7A9A8C3223</soap:AuthenticationToken>
      </soap:HUBHeader>
   </soapenv:Header>
   <soapenv:Body>
      <dpc:CodeGenRequest>
        <ManufacturerRequestID>00004</ManufacturerRequestID>
        <ManufacturerOrgGLN>${manufacturerOrgGLN}</ManufacturerOrgGLN>
        <SiteGLN>${siteGLN}</SiteGLN>
        <ProductEAN>${productEAN}</ProductEAN>
        <MarketCountryCode>${marketCountryCode}</MarketCountryCode>
        <MachineGLN>${machineGLN}</MachineGLN>
         <ISR>NA</ISR>
         <Entitlements>
            <Entitlement>
               <EntitlementID>1</EntitlementID>
               <Quantity>1</Quantity>
            </Entitlement>
         </Entitlements>
      </dpc:CodeGenRequest>
   </soapenv:Body>
</soapenv:Envelope>"""
  }

  def requestCodeGenerationString: String = {
    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">
        <soapenv:Header>
        <soap:HUBHeader>
        <soap:RequestID>${uuid}</h:RequestID>
        <soap:ContractID>1</h:ContractID>
        <soap:OperatorID>Planning User</h:OperatorID>
        <soap:MachineID>3</h:MachineID>""" +
      s"<soap:DateTime>${getDate}T00:00.0000000Z</h:DateTime>" +
      """<soap:CallingApplicationId i:nil="true">
        <soap:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</soap:AuthenticationToken>
        </soap:HUBHeader>
        </soapenv:Header>
        <soapenv:Body>
        <dpc:CodeGenRequest>
        <ManufacturerRequestID>1</ManufacturerRequestID>
        <ManufacturerOrgGLN>${manufacturerOrgGLN}</ManufacturerOrgGLN>
        <SiteGLN>${siteGLN}</SiteGLN>
        <ProductEAN>${productEAN}</ProductEAN>
        <MarketCountryCode>${marketCountryCode}</MarketCountryCode>
        <MachineGLN>${machineGLN}</MachineGLN>
        <ISR>NA</ISR>
        <Entitlements>""" +
      s"${generateEntitlements(List(Entitlement("1", "1")))}" +
      """</Entitlements>
        </dpc:CodeGenRequest>
        </soapenv:Body>
        </soapenv:Envelope>"""
  }



  def downloadString = """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">
   <soapenv:Header>
      <soap:HUBHeader>
         <soap:RequestId>d7d54a6d-69f2-489c-8055-0949af698342</soap:RequestId>
         <soap:ContractId>1</soap:ContractId>
         <soap:OperatorId>1</soap:OperatorId>
         <soap:MachineId>1</soap:MachineId>
         <soap:DateTime>2019-12-09T12:34:56.789+01:00</soap:DateTime>
         <soap:CallingApplicationId>DTP</soap:CallingApplicationId>
         <soap:AuthenticationToken>A3D53E5D-F136-4144-B40A-CF7A9A8C3223</soap:AuthenticationToken>
      </soap:HUBHeader>
   </soapenv:Header>
   <soapenv:Body>
      <dpc:DownloadCodesRequest>
         <DownloadReference>0001U</DownloadReference>
         <Offset>1</Offset>
         <Quantity>3000</Quantity>
      </dpc:DownloadCodesRequest>
   </soapenv:Body>
</soapenv:Envelope>"""

  def getUUID = java.util.UUID.randomUUID().toString

  lazy val getDate = java.time.LocalDate.now

  val getEntitlementData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA"),
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA"),
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA"),
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA"),
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA"),
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA"),
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA"),
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA")).queue

  val requestCodeGenerationData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "50000", "manufacturerOrgGLN" -> "7311101.00000.00", "machineGLN" -> "7311101.00001.99MACH1", "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA")).circular

  case class RequestCodeEntitlementList(entitlements: List[Entitlement])

  case class Entitlement(entitlementID: String, quantity: String)

  def pollString = {
    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">
      |   <soapenv:Header>
      |      <soap:HUBHeader>
      |         <soap:RequestId>c7d54a6d-69f2-489c-8056-0949ff698342</soap:RequestId>
      |         <soap:ContractId>1</soap:ContractId>
      |         <soap:OperatorId>1</soap:OperatorId>
      |         <soap:MachineId>1</soap:MachineId>
      |         <soap:DateTime>2019-12-09T12:34:56.789+01:00</soap:DateTime>
      |         <soap:CallingApplicationId>DTP</soap:CallingApplicationId>
      |         <soap:AuthenticationToken>A3D53E5D-F136-4144-B40A-CF7A9A8C3223</soap:AuthenticationToken>
      |      </soap:HUBHeader>
      |   </soapenv:Header>
      |   <soapenv:Body>
      |      <dpc:GetCodeGenStatusRequest>
      |         <DownloadReference>CUSTOMER_REFERENCE</DownloadReference>
      |      </dpc:GetCodeGenStatusRequest>
      |   </soapenv:Body>
      |</soapenv:Envelope>""".stripMargin
  }

  val scn : ScenarioBuilder = scenario("Scenario1").feed(requestCodeGenerationData)
    .exec(http("get entitlement")
      .post("/DpcDownload/DpcDownloadService.svc")
      .header("SOAPAction", "GetEntitlements")
      .body(StringBody(getEntitlementsString))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    .pause(1)
    .exec(http("request code generation")
      .post("/DpcDownload/DpcDownloadService.svc")
      .header("SOAPAction", "RequestCodeGeneration")
      .body(StringBody(codeGenString))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    .exec(
      session => {
        val referencePattern: Pattern = Pattern.compile("<DownloadReference>(.*?)</DownloadReference>")
        val response = session("RESPONSE_DATA").as[String]
        var reference = ""
        val matcher = referencePattern.matcher(response)
        while (matcher.find()) {
           reference = matcher.group().replace("<DownloadReference>", "")
            .replace("</DownloadReference>", "")
        }
        session.set("CUSTOMER_REFERENCE", reference)
      }
    )
  .doWhile(session => !session("RESPONSE_DATA").as[String].contains("Success")) {
    exec(
      http("code generation polling")
        .post("/DpcDownload/DpcDownloadService.svc")
        .body(StringBody(session => pollString.replace("CUSTOMER_REFERENCE", session("CUSTOMER_REFERENCE").as[String])))
        .check(
          bodyString.saveAs("RESPONSE_DATA")
        ))
  }

//
//  def getCodes = {
//    fileDownloader
//  }

  val scn2 : ScenarioBuilder = scenario("Scenario2").feed(requestCodeGenerationData)
    .exec(http("get entitlement")
      .post("/DpcDownload/DpcDownloadService.svc")
      .header("SOAPAction", "DownloadCodes")
      .body(StringBody(session => downloadString))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
//    .pause(1)
//    .exec(http("request code generation")
//      .post("/DpcDownload/DpcDownloadService.svc")
//      .header("SOAPAction", "RequestCodeGeneration")
//      .body(StringBody(codeGenString))
//      .check(bodyString.saveAs("RESPONSE_DATA"))
//      .check(status.is(200)))
//    .exec(
//      session => {
//        val referencePattern: Pattern = Pattern.compile("<DownloadReference>(.*?)</DownloadReference>")
//        val response = session("RESPONSE_DATA").as[String]
//        var reference = ""
//        val matcher = referencePattern.matcher(response)
//        while (matcher.find()) {
//           reference = matcher.group().replace("<DownloadReference>", "")
//            .replace("</DownloadReference>", "")
//        }
//        session.set("CUSTOMER_REFERENCE", reference)
//      }
//    )
//  .doWhile(session => !session("RESPONSE_DATA").as[String].contains("Success")) {
//    exec(
//      http("code generation polling")
//        .post("/DpcDownload/DpcDownloadService.svc")
//        .body(StringBody(session => downloadString))
//        .check(
//          bodyString.saveAs("RESPONSE_DATA")
//        ))
//  }

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
//    setUp(scn2.inject(atOnceUsers(1)).protocols(httpProtocol))
}