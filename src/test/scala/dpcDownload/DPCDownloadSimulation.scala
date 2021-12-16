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
  val orderReference = "DOWN3"
  // Writes codes to csv
  def codeWriter(codes: Array[String]) = {
    val file = new File("C:\\Workspace\\certify-performance-tests\\src\\test\\scala\\dpcDownload\\output.csv")
    val writer = CSVWriter.open(file)
    codes.foreach(code => {
      writer.writeRow(List(code))
    })
    writer.close()
  }
  // Writes download references to csv
  def refWriter(code: String) = {
    val writer = CSVWriter.open("C:\\Workspace\\certify-performance-tests\\src\\test\\scala\\dpcDownload\\downloadRefs.csv", append = true)
    writer.writeRow(List(code))
    writer.close()
  }
  // Stores the protocol parameters for reuse
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Constants.entitlementUrl)
    .inferHtmlResources()
    .acceptHeader("text/html;charset=UTF-8")
    .header("Content-Type", "text/xml; charset=utf-8")
    .header("Authorization", "SXNhZG1pbjpUQHg1dEBtcDUhIVFBVA==")
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
    Map("uuid" -> getUUID, "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA")).circular
  // TODO cast to variables
    val requestCodeGenerationData: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "140000", "manufacturerOrgGLN" -> "7311101.00000.00", "machineGLN" -> "7311101.00001.99MACH1", "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA", "dlRef" -> "001CP"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "140000", "manufacturerOrgGLN" -> "7311101.00000.00", "machineGLN" -> "7311101.00001.99MACH1", "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA", "dlRef" -> "001CR"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "140000", "manufacturerOrgGLN" -> "7311101.00000.00", "machineGLN" -> "7311101.00001.99MACH1", "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA", "dlRef" -> "001CS"),
    Map("uuid" -> getUUID, "manufacturerRequestID" -> getUUID, "quantity" -> "140000", "manufacturerOrgGLN" -> "7311101.00000.00", "machineGLN" -> "7311101.00001.99MACH1", "siteGLN" -> "7311101.00001.99", "productEAN" -> "129", "marketCountryCode" -> "QA", "dlRef" -> "001CT")).circular

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
  val generateCodesScenario: ScenarioBuilder = scenario("Scenario1").feed(requestCodeGenerationData).repeat(400, "foo") {
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
  val downloadCodesScenario: ScenarioBuilder = scenario("Scenario2").repeat(400) {
    feed(Feed.requestCodeGenerationData2)
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
  setUp(generateCodesScenario.inject(atOnceUsers(1)).protocols(httpProtocol))
  // Uncomment to run only the download codes scenario
  setUp(generateCodesScenario.inject(atOnceUsers(1)).protocols(httpProtocol))
  // Uncomment to run one after the other
  setUp(generateCodesScenario.inject(atOnceUsers(1)).protocols(httpProtocol))
    .andThen(downloadCodesScenario.inject(atOnceUsers(1)).protocols(httpProtocol))
  )
}
// TODO CSV feeder? if not add instructions
object Feed {
  val requestCodeGenerationData2: FeederBuilderBase[String]#F = Array(
    Map("dlRef" -> "001CU"),
    Map("dlRef" -> "001CV"),
    Map("dlRef" -> "001CW"),
    Map("dlRef" -> "001CX"),
    Map("dlRef" -> "001CY"),
    Map("dlRef" -> "001D0"),
    Map("dlRef" -> "001D1"),
    Map("dlRef" -> "001D2"),
    Map("dlRef" -> "001D3"),
    Map("dlRef" -> "001D4"),
    Map("dlRef" -> "001D5"),
    Map("dlRef" -> "001D6"),
    Map("dlRef" -> "001D7"),
    Map("dlRef" -> "001D8"),
    Map("dlRef" -> "001D9"),
    Map("dlRef" -> "001DA"),
    Map("dlRef" -> "001DB"),
    Map("dlRef" -> "001DC"),
    Map("dlRef" -> "001DD"),
    Map("dlRef" -> "001DE"),
    Map("dlRef" -> "001DF"),
    Map("dlRef" -> "001DG"),
    Map("dlRef" -> "001DH"),
    Map("dlRef" -> "001DJ"),
    Map("dlRef" -> "001DK"),
    Map("dlRef" -> "001DL"),
    Map("dlRef" -> "001DM"),
    Map("dlRef" -> "001DN"),
    Map("dlRef" -> "001DP"),
    Map("dlRef" -> "001DR"),
    Map("dlRef" -> "001DS"),
    Map("dlRef" -> "001DT"),
    Map("dlRef" -> "001DU"),
    Map("dlRef" -> "001DV"),
    Map("dlRef" -> "001DW"),
    Map("dlRef" -> "001DX"),
    Map("dlRef" -> "001DY"),
    Map("dlRef" -> "001E0"),
    Map("dlRef" -> "001E1"),
    Map("dlRef" -> "001E2"),
    Map("dlRef" -> "001E3"),
    Map("dlRef" -> "001E4"),
    Map("dlRef" -> "001E5"),
    Map("dlRef" -> "001E6"),
    Map("dlRef" -> "001E7"),
    Map("dlRef" -> "001E8"),
    Map("dlRef" -> "001E9"),
    Map("dlRef" -> "001EA"),
    Map("dlRef" -> "001EB"),
    Map("dlRef" -> "001EC"),
    Map("dlRef" -> "001ED"),
    Map("dlRef" -> "001EE"),
    Map("dlRef" -> "001EF"),
    Map("dlRef" -> "001EG"),
    Map("dlRef" -> "001EH"),
    Map("dlRef" -> "001EJ"),
    Map("dlRef" -> "001EK"),
    Map("dlRef" -> "001EL"),
    Map("dlRef" -> "001EM"),
    Map("dlRef" -> "001EN"),
    Map("dlRef" -> "001EP"),
    Map("dlRef" -> "001ER"),
    Map("dlRef" -> "001ES"),
    Map("dlRef" -> "001ET"),
    Map("dlRef" -> "001EU"),
    Map("dlRef" -> "001EV"),
    Map("dlRef" -> "001EW"),
    Map("dlRef" -> "001EX"),
    Map("dlRef" -> "001EY"),
    Map("dlRef" -> "001F0"),
    Map("dlRef" -> "001F1"),
    Map("dlRef" -> "001F2"),
    Map("dlRef" -> "001F3"),
    Map("dlRef" -> "001F4"),
    Map("dlRef" -> "001F5"),
    Map("dlRef" -> "001F6"),
    Map("dlRef" -> "001F7"),
    Map("dlRef" -> "001F8"),
    Map("dlRef" -> "001F9"),
    Map("dlRef" -> "001FA"),
    Map("dlRef" -> "001FB"),
    Map("dlRef" -> "001FC"),
    Map("dlRef" -> "001FD"),
    Map("dlRef" -> "001FE"),
    Map("dlRef" -> "001FF"),
    Map("dlRef" -> "001FG"),
    Map("dlRef" -> "001FH"),
    Map("dlRef" -> "001FJ"),
    Map("dlRef" -> "001FK"),
    Map("dlRef" -> "001FL"),
    Map("dlRef" -> "001FM"),
    Map("dlRef" -> "001FN"),
    Map("dlRef" -> "001FP"),
    Map("dlRef" -> "001FR"),
    Map("dlRef" -> "001FS"),
    Map("dlRef" -> "001FT"),
    Map("dlRef" -> "001FU"),
    Map("dlRef" -> "001FV"),
    Map("dlRef" -> "001FW"),
    Map("dlRef" -> "001FX"),
    Map("dlRef" -> "001FY"),
    Map("dlRef" -> "001G0"),
    Map("dlRef" -> "001G1"),
    Map("dlRef" -> "001G2"),
    Map("dlRef" -> "001G3"),
    Map("dlRef" -> "001G4"),
    Map("dlRef" -> "001G5"),
    Map("dlRef" -> "001G6"),
    Map("dlRef" -> "001G7"),
    Map("dlRef" -> "001G8"),
    Map("dlRef" -> "001G9"),
    Map("dlRef" -> "001GA"),
    Map("dlRef" -> "001GB"),
    Map("dlRef" -> "001GC"),
    Map("dlRef" -> "001GD"),
    Map("dlRef" -> "001GE"),
    Map("dlRef" -> "001GF"),
    Map("dlRef" -> "001GG"),
    Map("dlRef" -> "001GH"),
    Map("dlRef" -> "001GJ"),
    Map("dlRef" -> "001GK"),
    Map("dlRef" -> "001GL"),
    Map("dlRef" -> "001GM"),
    Map("dlRef" -> "001GN"),
    Map("dlRef" -> "001GP"),
    Map("dlRef" -> "001GR"),
    Map("dlRef" -> "001GS"),
    Map("dlRef" -> "001GT"),
    Map("dlRef" -> "001GU"),
    Map("dlRef" -> "001GV"),
    Map("dlRef" -> "001GW"),
    Map("dlRef" -> "001GX"),
    Map("dlRef" -> "001GY"),
    Map("dlRef" -> "001H0"),
    Map("dlRef" -> "001H1"),
    Map("dlRef" -> "001H2"),
    Map("dlRef" -> "001H3"),
    Map("dlRef" -> "001H4"),
    Map("dlRef" -> "001H5"),
    Map("dlRef" -> "001H6"),
    Map("dlRef" -> "001H7"),
    Map("dlRef" -> "001H8"),
    Map("dlRef" -> "001H9"),
    Map("dlRef" -> "001HA"),
    Map("dlRef" -> "001HB"),
    Map("dlRef" -> "001HC"),
    Map("dlRef" -> "001HD"),
    Map("dlRef" -> "001HE"),
    Map("dlRef" -> "001HF"),
    Map("dlRef" -> "001HG"),
    Map("dlRef" -> "001HH"),
    Map("dlRef" -> "001HJ"),
    Map("dlRef" -> "001HK"),
    Map("dlRef" -> "001HL"),
    Map("dlRef" -> "001HM"),
    Map("dlRef" -> "001HN"),
    Map("dlRef" -> "001HP"),
    Map("dlRef" -> "001HR"),
    Map("dlRef" -> "001HS"),
    Map("dlRef" -> "001HT"),
    Map("dlRef" -> "001HU"),
    Map("dlRef" -> "001HV"),
    Map("dlRef" -> "001HW"),
    Map("dlRef" -> "001HX"),
    Map("dlRef" -> "001HY"),
    Map("dlRef" -> "001J0"),
    Map("dlRef" -> "001J1"),
    Map("dlRef" -> "001J2"),
    Map("dlRef" -> "001J3"),
    Map("dlRef" -> "001J4"),
    Map("dlRef" -> "001J5"),
    Map("dlRef" -> "001J6"),
    Map("dlRef" -> "001J7"),
    Map("dlRef" -> "001J8"),
    Map("dlRef" -> "001J9"),
    Map("dlRef" -> "001JA"),
    Map("dlRef" -> "001JB"),
    Map("dlRef" -> "001JC"),
    Map("dlRef" -> "001JD"),
    Map("dlRef" -> "001JE"),
    Map("dlRef" -> "001JF"),
    Map("dlRef" -> "001JG"),
    Map("dlRef" -> "001JH"),
    Map("dlRef" -> "001JJ"),
    Map("dlRef" -> "001JK"),
    Map("dlRef" -> "001JL"),
    Map("dlRef" -> "001JM"),
    Map("dlRef" -> "001JN"),
    Map("dlRef" -> "001JP"),
    Map("dlRef" -> "001JR"),
    Map("dlRef" -> "001JS"),
    Map("dlRef" -> "001JT"),
    Map("dlRef" -> "001JU"),
    Map("dlRef" -> "001JV"),
    Map("dlRef" -> "001JW"),
    Map("dlRef" -> "001JX"),
    Map("dlRef" -> "001JY"),
    Map("dlRef" -> "001K0"),
    Map("dlRef" -> "001K1"),
    Map("dlRef" -> "001K2"),
    Map("dlRef" -> "001K3"),
    Map("dlRef" -> "001K4"),
    Map("dlRef" -> "001K5"),
    Map("dlRef" -> "001K6"),
    Map("dlRef" -> "001K7"),
    Map("dlRef" -> "001K8"),
    Map("dlRef" -> "001K9"),
    Map("dlRef" -> "001KA"),
    Map("dlRef" -> "001KB"),
    Map("dlRef" -> "001KC"),
    Map("dlRef" -> "001KD"),
    Map("dlRef" -> "001KE"),
    Map("dlRef" -> "001KF"),
    Map("dlRef" -> "001KG"),
    Map("dlRef" -> "001KH"),
    Map("dlRef" -> "001KJ"),
    Map("dlRef" -> "001KK"),
    Map("dlRef" -> "001KL"),
    Map("dlRef" -> "001KM"),
    Map("dlRef" -> "001KN"),
    Map("dlRef" -> "001KP"),
    Map("dlRef" -> "001KR"),
    Map("dlRef" -> "001KS"),
    Map("dlRef" -> "001KT"),
    Map("dlRef" -> "001KU"),
    Map("dlRef" -> "001KV"),
    Map("dlRef" -> "001KW"),
    Map("dlRef" -> "001KX"),
    Map("dlRef" -> "001KY"),
    Map("dlRef" -> "001L0"),
    Map("dlRef" -> "001L1"),
    Map("dlRef" -> "001L2"),
    Map("dlRef" -> "001L3"),
    Map("dlRef" -> "001L4"),
    Map("dlRef" -> "001L5"),
    Map("dlRef" -> "001L6"),
    Map("dlRef" -> "001L7"),
    Map("dlRef" -> "001L8"),
    Map("dlRef" -> "001L9"),
    Map("dlRef" -> "001LA"),
    Map("dlRef" -> "001LB"),
    Map("dlRef" -> "001LC"),
    Map("dlRef" -> "001LD"),
    Map("dlRef" -> "001LE"),
    Map("dlRef" -> "001LF"),
    Map("dlRef" -> "001LG"),
    Map("dlRef" -> "001LH"),
    Map("dlRef" -> "001LJ"),
    Map("dlRef" -> "001LK"),
    Map("dlRef" -> "001LL"),
    Map("dlRef" -> "001LM"),
    Map("dlRef" -> "001LN"),
    Map("dlRef" -> "001LP"),
    Map("dlRef" -> "001LR"),
    Map("dlRef" -> "001LS"),
    Map("dlRef" -> "001LT"),
    Map("dlRef" -> "001LU"),
    Map("dlRef" -> "001LV"),
    Map("dlRef" -> "001LW"),
    Map("dlRef" -> "001LX"),
    Map("dlRef" -> "001LY"),
    Map("dlRef" -> "001M0"),
    Map("dlRef" -> "001M1"),
    Map("dlRef" -> "001M2"),
    Map("dlRef" -> "001M3"),
    Map("dlRef" -> "001M4"),
    Map("dlRef" -> "001M5"),
    Map("dlRef" -> "001M6"),
    Map("dlRef" -> "001M7"),
    Map("dlRef" -> "001M8"),
    Map("dlRef" -> "001M9"),
    Map("dlRef" -> "001MA"),
    Map("dlRef" -> "001MB"),
    Map("dlRef" -> "001MC"),
    Map("dlRef" -> "001MD"),
    Map("dlRef" -> "001ME"),
    Map("dlRef" -> "001MF"),
    Map("dlRef" -> "001MG"),
    Map("dlRef" -> "001MH"),
    Map("dlRef" -> "001MJ"),
    Map("dlRef" -> "001MK"),
    Map("dlRef" -> "001ML"),
    Map("dlRef" -> "001MM"),
    Map("dlRef" -> "001MN"),
    Map("dlRef" -> "001MP"),
    Map("dlRef" -> "001MR"),
    Map("dlRef" -> "001MS"),
    Map("dlRef" -> "001MT"),
    Map("dlRef" -> "001MU"),
    Map("dlRef" -> "001MV"),
    Map("dlRef" -> "001MW"),
    Map("dlRef" -> "001MX"),
    Map("dlRef" -> "001MY"),
    Map("dlRef" -> "001N0"),
    Map("dlRef" -> "001N1"),
    Map("dlRef" -> "001N2"),
    Map("dlRef" -> "001N3"),
    Map("dlRef" -> "001N4"),
    Map("dlRef" -> "001N5"),
    Map("dlRef" -> "001N6"),
    Map("dlRef" -> "001N7"),
    Map("dlRef" -> "001N8"),
    Map("dlRef" -> "001N9"),
    Map("dlRef" -> "001NA"),
    Map("dlRef" -> "001NB"),
    Map("dlRef" -> "001NC"),
    Map("dlRef" -> "001ND"),
    Map("dlRef" -> "001NE"),
    Map("dlRef" -> "001NF"),
    Map("dlRef" -> "001NG"),
    Map("dlRef" -> "001NH"),
    Map("dlRef" -> "001NJ"),
    Map("dlRef" -> "001NK"),
    Map("dlRef" -> "001NL"),
    Map("dlRef" -> "001NM"),
    Map("dlRef" -> "001NN"),
    Map("dlRef" -> "001NP"),
    Map("dlRef" -> "001NR"),
    Map("dlRef" -> "001NS"),
    Map("dlRef" -> "001NT"),
    Map("dlRef" -> "001NU"),
    Map("dlRef" -> "001NV"),
    Map("dlRef" -> "001NW"),
    Map("dlRef" -> "001NX"),
    Map("dlRef" -> "001NY"),
    Map("dlRef" -> "001P0"),
    Map("dlRef" -> "001P1"),
    Map("dlRef" -> "001P2"),
    Map("dlRef" -> "001P3"),
    Map("dlRef" -> "001P4"),
    Map("dlRef" -> "001P5"),
    Map("dlRef" -> "001P6"),
    Map("dlRef" -> "001P7"),
    Map("dlRef" -> "001P8"),
    Map("dlRef" -> "001P9"),
    Map("dlRef" -> "001PA"),
    Map("dlRef" -> "001PB"),
    Map("dlRef" -> "001PC"),
    Map("dlRef" -> "001PD"),
    Map("dlRef" -> "001PE"),
    Map("dlRef" -> "001PF"),
    Map("dlRef" -> "001PG"),
    Map("dlRef" -> "001PH"),
    Map("dlRef" -> "001PJ"),
    Map("dlRef" -> "001PK"),
    Map("dlRef" -> "001PL"),
    Map("dlRef" -> "001PM"),
    Map("dlRef" -> "001PN"),
    Map("dlRef" -> "001PP"),
    Map("dlRef" -> "001PR"),
    Map("dlRef" -> "001PS"),
    Map("dlRef" -> "001PT"),
    Map("dlRef" -> "001PU"),
    Map("dlRef" -> "001PV"),
    Map("dlRef" -> "001PW"),
    Map("dlRef" -> "001PX"),
    Map("dlRef" -> "001PY"),
    Map("dlRef" -> "001R0"),
    Map("dlRef" -> "001R1"),
    Map("dlRef" -> "001R2"),
    Map("dlRef" -> "001R3"),
    Map("dlRef" -> "001R4"),
    Map("dlRef" -> "001R5"),
    Map("dlRef" -> "001R6"),
    Map("dlRef" -> "001R7"),
    Map("dlRef" -> "001R8"),
    Map("dlRef" -> "001R9"),
    Map("dlRef" -> "001RA"),
    Map("dlRef" -> "001RB"),
    Map("dlRef" -> "001RC"),
    Map("dlRef" -> "001RD"),
    Map("dlRef" -> "001RE"),
    Map("dlRef" -> "001RF"),
    Map("dlRef" -> "001RG"),
    Map("dlRef" -> "001RH"),
    Map("dlRef" -> "001RJ"),
    Map("dlRef" -> "001RK"),
    Map("dlRef" -> "001RL"),
    Map("dlRef" -> "001RM"),
    Map("dlRef" -> "001RN"),
    Map("dlRef" -> "001RP"),
    Map("dlRef" -> "001RR"),
    Map("dlRef" -> "001RS"),
    Map("dlRef" -> "001RT"),
    Map("dlRef" -> "001RU"),
    Map("dlRef" -> "001RV"),
    Map("dlRef" -> "001RW"),
    Map("dlRef" -> "001RX"),
    Map("dlRef" -> "001RY"),
    Map("dlRef" -> "001S0"),
    Map("dlRef" -> "001S1"),
    Map("dlRef" -> "001S2"),
    Map("dlRef" -> "001S3"),
    Map("dlRef" -> "001S4"),
    Map("dlRef" -> "001S5"),
    Map("dlRef" -> "001S6"),
    Map("dlRef" -> "001S7"),
    Map("dlRef" -> "001S8"),
    Map("dlRef" -> "001S9"),
    Map("dlRef" -> "001SA")).circular
}
