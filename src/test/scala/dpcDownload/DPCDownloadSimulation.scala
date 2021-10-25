package dpcDownload

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import oms.Constants
import com.github.tototoshi.csv._

import java.io.File
import java.util
import java.util.regex.Pattern

class DPCDownloadSimulation extends Simulation {
  var downloadedCodes = new Array[String](1)
  var currentDownloadReference: String = "00017"
  val orderReference = "SOFT3"

  def writer(codes: Array[String]) = {
    val file = new File("C:\\Workspace\\certify-performance-tests\\src\\test\\scala\\dpcDownload\\output.csv")
    val writer = CSVWriter.open(file)
    codes.foreach(code => {
      writer.writeRow(List(code))
    })
    writer.close()
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
        <ManufacturerRequestID>SOFT3</ManufacturerRequestID>
        <ManufacturerOrgGLN>${manufacturerOrgGLN}</ManufacturerOrgGLN>
        <SiteGLN>${siteGLN}</SiteGLN>
        <ProductEAN>${productEAN}</ProductEAN>
        <MarketCountryCode>${marketCountryCode}</MarketCountryCode>
        <MachineGLN>${machineGLN}</MachineGLN>
         <ISR>NA</ISR>
         <Entitlements>
            <Entitlement>
               <EntitlementID>7</EntitlementID>
               <Quantity>100</Quantity>
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
        <ManufacturerRequestID>7</ManufacturerRequestID>
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

  def extractDownloadedCodes(response: String) = {
    val pattern: Pattern = Pattern.compile("<Codes>(.*?)</Codes>")
    var codeArray = ""
    val matcher = pattern.matcher(response)
    while (matcher.find()) {
      codeArray = matcher.group().replace("<Codes>", "")
        .replace("</Codes>", "")
    }
    codeArray.substring(2, codeArray.length - 2).split("""","""")
  }

  def setDownloadReference(response: String) = {
    val pattern: Pattern = Pattern.compile("<DownloadReference>(.*?)</DownloadReference>")
    var ref = ""
    val matcher = pattern.matcher(response)
    while (matcher.find()) {
      ref = matcher.group().replace("<Codes>", "")
        .replace("</Codes>", "")
    }
    currentDownloadReference = ref
  }

  def downloadString =
    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">
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
         <DownloadReference>DOWNLOAD_REFERENCE</DownloadReference>
         <Offset>0</Offset>
         <Quantity>50</Quantity>
      </dpc:DownloadCodesRequest>
   </soapenv:Body>
</soapenv:Envelope>"""

  def getUUID = java.util.UUID.randomUUID().toString

  lazy val getDate = java.time.LocalDate.now

  val getEntitlementData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA")).circular

  val requestCodeGenerationData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "10000", "manufacturerOrgGLN" -> "7311101.00000.00", "machineGLN" -> "7311101.00001.99MACH1", "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA")).circular

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

  val scn: ScenarioBuilder = scenario("Scenario1").feed(requestCodeGenerationData)
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
        println("REF")
        println("REF")
        println("REF")
        println("REF")
        println("REF")
        println("REF")
        println("REF")
        println("REF")
        println("REF")
        println("REF")
        println("REF")
        println(reference)
        session.set("CUSTOMER_REFERENCE", reference)
        currentDownloadReference = reference
        session
      }
    )
    .doWhile(session => session("RESPONSE_DATA").as[String].contains("Pending")) {
      exec(
        http("code generation polling")
          .post("/DpcDownload/DpcDownloadService.svc")
          .body(StringBody(session => pollString.replace("CUSTOMER_REFERENCE", currentDownloadReference)))
          .check(
            bodyString.saveAs("RESPONSE_DATA")
          ))
        .pause(10)
    }

  val scn2: ScenarioBuilder = scenario("Scenario2").feed(requestCodeGenerationData)
    .exec(http("dl code")
      .post("/DpcDownload/DpcDownloadService.svc")
      .header("SOAPAction", "DownloadCodes")
      .body(StringBody(session => downloadString.replace("DOWNLOAD_REFERENCE", currentDownloadReference)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    .exec(session => {
      downloadedCodes = extractDownloadedCodes(session("RESPONSE_DATA").as[String])
      downloadedCodes.foreach(x => {
        println(x)
      })
      writer(downloadedCodes)
      session
    })

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol)
    .andThen(scn2.inject(atOnceUsers(1)).protocols(httpProtocol)))
}