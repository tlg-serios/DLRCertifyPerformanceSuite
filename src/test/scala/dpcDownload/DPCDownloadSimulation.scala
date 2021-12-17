package dpcDownload

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import oms.Constants
import com.github.tototoshi.csv._

import java.io.File
import java.util.regex.Pattern

// Simulates download of codes
class DPCDownloadSimulation extends Simulation {
  // Stores downloaded codes for reuse
  var downloadedCodes = new Array[String](1)
  // Reassigned for sequential downloads
  var currentDownloadReference: String = ""
  // TODO To be set
  val orderReference = Constants.dpcOrderRef

  // Writes codes to csv
  def codeWriter(codes: Array[String]) = {
    val file = new File(Constants.codesCSV)
    val writer = CSVWriter.open(file)
    codes.foreach(code => {
      writer.writeRow(List(code))
    })
    writer.close()
  }

  // Writes download references to csv
  def refWriter(code: String) = {
    val writer = CSVWriter.open(Constants.downloadRefsCSV, append = true)
    writer.writeRow(List(code))
    writer.close()
  }

  // Stores the protocol parameters for reuse
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Constants.entitlementUrl)
    .inferHtmlResources()
    .acceptHeader("text/html;charset=UTF-8")
    .header("Content-Type", "text/xml; charset=utf-8")
    .header("Authorization", Constants.authString)

  // Creates xml block relating to entitlements
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

  // String for getEntitlement request
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
<ProductEAN>ALL</ProductEAN>
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
        <ManufacturerRequestID>FORT1</ManufacturerRequestID>
        <ManufacturerOrgGLN>${manufacturerOrgGLN}</ManufacturerOrgGLN>
        <SiteGLN>${siteGLN}</SiteGLN>
        <ProductEAN>${productEAN}</ProductEAN>
        <MarketCountryCode>${marketCountryCode}</MarketCountryCode>
        <MachineGLN>${machineGLN}</MachineGLN>
         <ISR>NA</ISR>
         <Entitlements>
            <Entitlement>
               <EntitlementID>34</EntitlementID>
               <Quantity>100000</Quantity>
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
        <ManufacturerRequestID>23</ManufacturerRequestID>
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

  // Extract the downloaded codes for later use
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

  // Set current download reference
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
         <soap:RequestId>e766e66a-80fd-4c38-b6f1-f3e60bd28d46</soap:RequestId>
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
         <Quantity>100000</Quantity>
      </dpc:DownloadCodesRequest>
   </soapenv:Body>
</soapenv:Envelope>"""

  // Generate UUID for interpolation
  def getUUID = java.util.UUID.randomUUID().toString

  // Generate date for interpolation
  lazy val getDate = java.time.LocalDate.now
  // TODO cast to variables
  val getEntitlementData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "siteGLN" -> Constants.siteGLN, "productEAN" -> Constants.productEAN, "marketCountryCode" -> Constants.marketCountryCode)).circular
  // TODO cast to variables
  val requestCodeGenerationData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> Constants.quantity, "manufacturerOrgGLN" -> Constants.manufacturerOrgGLN, "machineGLN" -> Constants.machineGLN, "siteGLN" -> Constants.siteGLN, "productEAN" -> Constants.productEAN, "marketCountryCode" -> Constants.marketCountryCode),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> Constants.quantity, "manufacturerOrgGLN" -> Constants.manufacturerOrgGLN, "machineGLN" -> Constants.machineGLN, "siteGLN" -> Constants.siteGLN, "productEAN" -> Constants.productEAN, "marketCountryCode" -> Constants.marketCountryCode),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> Constants.quantity, "manufacturerOrgGLN" -> Constants.manufacturerOrgGLN, "machineGLN" -> Constants.machineGLN, "siteGLN" -> Constants.siteGLN, "productEAN" -> Constants.productEAN, "marketCountryCode" -> Constants.marketCountryCode),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> Constants.quantity, "manufacturerOrgGLN" -> Constants.manufacturerOrgGLN, "machineGLN" -> Constants.machineGLN, "siteGLN" -> Constants.siteGLN, "productEAN" -> Constants.productEAN, "marketCountryCode" -> Constants.marketCountryCode)).circular

  // case class RequestCodeEntitlementList(entitlements: List[Entitlement])
  // Used to generate entitlement block
  case class Entitlement(entitlementID: String, quantity: String)

  // String to poll code generation status endpoint
  def pollString = {
    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/SOAPHeader" xmlns:dpc="http://UCodeBrokerHub.Schemas.Canonical/2013/02/21/DpcDownload">
<soapenv:Header>
<soap:HUBHeader>
<soap:RequestId>c7d54a6d-69f2-489c-8056-0949ff698342</soap:RequestId>
<soap:ContractId>1</soap:ContractId>
<soap:OperatorId>1</soap:OperatorId>
<soap:MachineId>1</soap:MachineId>
<soap:DateTime>2019-12-09T12:34:56.789+01:00</soap:DateTime>
<soap:CallingApplicationId>DTP</soap:CallingApplicationId>
<soap:AuthenticationToken>A3D53E5D-F136-4144-B40A-CF7A9A8C3223</soap:AuthenticationToken>
</soap:HUBHeader>
</soapenv:Header>
<soapenv:Body>
<dpc:GetCodeGenStatusRequest>
<DownloadReference>CUSTOMER_REFERENCE</DownloadReference>
</dpc:GetCodeGenStatusRequest>
</soapenv:Body>
</soapenv:Envelope>"""
  }

  // Scenario for code generation
  val generateCodesScenario: ScenarioBuilder = scenario("Scenario1").feed(requestCodeGenerationData).repeat(1, "foo") {
    exec(http("get entitlements")
      .post("/DpcDownload/DpcDownloadService.svc")
      .header("SOAPAction", "GetEntitlements")
      .body(StringBody(getEntitlementsString))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    exec(http("request code generation")
      .post("/DpcDownload/DpcDownloadService.svc")
      .header("SOAPAction", "RequestCodeGeneration")
      .body(StringBody(codeGenString))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
      // TODO IMPORTANT! Uncomment when generating large values of codes for DL scenario
      //      .pause(1)
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
          println(reference)
          session.set("CUSTOMER_REFERENCE", reference)
          currentDownloadReference = reference
          refWriter(reference)
          session
        }
      )
      .pause(1)
      .doWhile(session => session("RESPONSE_DATA").as[String].contains("Pending")) {
        exec(
          http("code generation polling")
            .post("/DpcDownload/DpcDownloadService.svc")
            .body(StringBody(session => pollString.replace("CUSTOMER_REFERENCE", currentDownloadReference)))
            .check(
              bodyString.saveAs("RESPONSE_DATA")
            ))
      }
  }

  // Scenario for download codes tests
  val downloadCodesScenario: ScenarioBuilder = scenario("Scenario2").repeat(1) {
    feed(csv(Constants.downloadRefsCSV))
      .exec(http("download codes")
        .post("/DpcDownload/DpcDownloadService.svc")
        .header("SOAPAction", "DownloadCodes")
        .body(StringBody(downloadString))
        .body(StringBody(session => downloadString.replace("DOWNLOAD_REFERENCE", session("dlRef").as[String])))
        .check(bodyString.saveAs("RESPONSE_DATA"))
        .check(status.is(200)))
      .exec(session => {
        downloadedCodes = extractDownloadedCodes(session("RESPONSE_DATA").as[String])
        codeWriter(downloadedCodes)
        session
      })
  }
  // Uncomment to run only the generate codes scenario
//  setUp(generateCodesScenario.inject(atOnceUsers(1)).protocols(httpProtocol))
  // Uncomment to run only the download codes scenario
  // After run, downloadRefs must be manually cleared with 'dlRef' left at line 1
//  setUp(downloadCodesScenario.inject(atOnceUsers(1)).protocols(httpProtocol))
  setUp(generateCodesScenario.inject(atOnceUsers(1)).protocols(httpProtocol)
    .andThen(downloadCodesScenario.inject(atOnceUsers(1)).protocols(httpProtocol)))
}
