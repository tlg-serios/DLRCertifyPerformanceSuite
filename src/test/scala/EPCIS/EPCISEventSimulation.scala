package EPCIS

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import oms.Constants

import java.math.BigInteger
import java.security.SecureRandom
import java.time.LocalDate
import java.util
import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.io.Source

class EPCISEventSimulation extends Simulation {
  before(seperateDPCS("C:\\Workspace\\certify-performance-tests\\src\\test\\scala\\dpcDownload\\output.csv"))
  // Used for storing sequential event number
  var instanceID: BigInteger = null
  // Generates next instance id
  def getInstanceID: BigInteger = {
    if (instanceID == null) {
      val idPrefix = String.format("%06d", new SecureRandom().nextInt(1000000)) + String.format("%06d", new SecureRandom().nextInt(1000000))
      instanceID = new BigInteger(idPrefix + "00")
    }
    else {
      instanceID = instanceID.add(new BigInteger(String.valueOf(1)))
      return instanceID
    }
    instanceID
  }
  // EPCIS feeder
  val sagaEPCISData: FeederBuilderBase[String]#F = Array(
      Map("date123" -> getTimestamp, "readPoint" -> "urn:epc:id:sgln:7311101.00001.99MACH1", "sourceLocation" -> "urn:epc:id:gdti:7311101.00001.00", "sourceOwner" -> "urn:epc:id:sgln:7311101.00000.00", "destOwner" -> "urn:epc:id:sgln:7311101.00000.00", "destLocation" -> "urn:epc:id:sgln:7311101.00000.00", "bizLocation" -> "urn:epc:id:gdti:7311101.00001.00", "bizTrans" -> "urn:epcglobal:cbv:bt:7311101.00001.00", "epcCommissionPrefix" -> "http://dts.gta.gov.qa/epcis-aggr/obj/")).circular
  // Generic http protocol
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Constants.sagaUrl)
    .inferHtmlResources()
    .acceptHeader("text/html;charset=UTF-8")
    .header("Content-Type", "text/xml; charset=utf-8")
    .header("Authorization", "Basic UUFUVGVzdDo2T2xyYlZqU1px")

  val sagaSimulation: ScenarioBuilder = scenario("Scenario1").feed(sagaEPCISData)
    .exec(http("get entitlement")
      .post("/api/acquire/rabbitmq/epcis")
      .body(StringBody(session => generateCommissionEvent(getDPCs)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    .exec(
        session => {
        session
      }
    )

  def seperateDPCS(fileName: String) = {
    for (line <- Source.fromFile(fileName).getLines) {
      if (!line.substring(0, 13).contains(",")) {
        dpcsFromCSV.add(line.substring(0, 13))
      }
    }
    dpcsFromCSV
  }

  def groupDPCs(dpcs: List[String]) = {
    dpcsFromCSV.asScala.toList.grouped(5).foreach(group => {
      partitionedDPCs.add(group)
    })
    partitionedDPCs.asScala.toList
  }

  def getDPCs: List[String] = {
    if (dpcsFromCSV == null) {
      seperateDPCS("C:\\Workspace\\certify-performance-tests\\src\\test\\scala\\dpcDownload\\output.csv")
    }
    var listToReturn = new util.ArrayList[String]()
    dpcsFromCSV.forEach(dpc => {
      listToReturn.add(dpc)
    })
    listToReturn.asScala.toList
  }
  // Number of times to call
  val numberOfCalls = 10
  // Stores dpc codes for event from CSV
  var dpcsFromCSV: util.ArrayList[String] = new util.ArrayList[String]()
  // Partitioned CSVs
  var partitionedDPCs: util.ArrayList[List[String]] = new util.ArrayList[List[String]]()
  // Generate commission event for saga events
  def generateCommissionEvent(dpcs: List[String]) = {
    var epcList = ""
    var finalString = ""
    dpcs.foreach(dpc => {
      epcList = epcList + "<epc>EPCCOMMISSIONPREFIX" + dpc + "</epc>\n"
    })
      finalString = finalString + commissionString.replace("EPC_LIST", epcList)
        .replace("INSTANCE_ID", getInstanceID.toString)
        .replace("DATE", "2021-10-14" + """T00:00:00.000Z""")
        .replace("EPCCOMMISSIONPREFIX", "http://dts.gta.gov.qa/epcis-aggr/obj/")
        .replace("READPOINT", "urn:epc:id:sgln:7311101.00001.99MACH1")
        .replace("BIZLOCATION", "urn:epc:id:gdti:7311101.00001.00")
      epcList = ""
    finalString
  }
  // Generic EPCIS event string
  val epcisString =
    """<?xml version="1.0" encoding="utf-8"?>
    <epcis:EPCISDocument xmlns:fcc="http://fracturecode.com" schemaVersion="1.2" creationDate="2017-10-27T13:45:03.064+00:00" xmlns:epcis="urn:epcglobal:epcis:xsd:1">
      <EPCISHeader>
        <StandardBusinessDocumentHeader xmlns="http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader">
          <HeaderVersion>1.0</HeaderVersion>
          <Sender>
            <Identifier Authority="SGLN">urn:epc:id:sgln:0614141.00000.0</Identifier>
          </Sender>
          <Receiver>
            <Identifier Authority="SGLN">urn:epc:id:sgln:629600000142..0</Identifier>
          </Receiver>
          <DocumentIdentification>
            <Standard>EPCglobal</Standard>
            <TypeVersion>1.0</TypeVersion>
            <InstanceIdentifier>INSTANCE_ID</InstanceIdentifier>
            <Type>Events</Type>
            <CreationDateAndTime>DATE</CreationDateAndTime>
          </DocumentIdentification>
        </StandardBusinessDocumentHeader>
      </EPCISHeader>
      <EPCISBody>
        <EventList>
          EPCIS_BODY
        </EventList>
      </EPCISBody>
    </epcis:EPCISDocument>"""
  // Commission string for epcis event
  val commissionString = epcisString.replace("EPCIS_BODY",
    """<ObjectEvent>
      <eventTime>DATE</eventTime>
      <eventTimeZoneOffset>+02:00</eventTimeZoneOffset>
      <epcList>
        EPC_LIST
      </epcList>
      <action>ADD</action>
      <bizStep>urn:epcglobal:cbv:bizstep:commissioning</bizStep>
      <disposition>urn:epcglobal:cbv:disp:active</disposition>
      <readPoint>
        <id>READPOINT</id>
      </readPoint>
      <bizLocation>
        <id>BIZLOCATION</id>
      </bizLocation>
    </ObjectEvent>"""
  )
  // Timestamp for event interpolation
  def getTimestamp: String = {
    LocalDate.now.toString
  }
  // Runs saga simulation
  setUp(sagaSimulation.inject(atOnceUsers(1)).protocols(httpProtocol))
}
