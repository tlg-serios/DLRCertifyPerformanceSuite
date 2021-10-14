package EPCIS

//import EPCIS.CSVHandler
//import EPCIS.{DPCHandler, EventHandler}

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import oms.Constants
import org.junit.Test

import java.io.File
import java.math.BigInteger
import java.security.SecureRandom
import java.time.LocalDate
import java.util
import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.io.Source

class EPCISEventSimulation extends Simulation {

  var instanceID: BigInteger = null

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


  val sagaEPCISData: FeederBuilderBase[String]#F = Array(
    Map("date123" -> getTimestamp, "readPoint" -> "urn:epc:id:sgln:7311101.00001.99MACH1", "sourceLocation" -> "urn:epc:id:gdti:7311101.00001.00", "sourceOwner" -> "urn:epc:id:sgln:7311101.00000.00", "destOwner" -> "urn:epc:id:sgln:7311101.00000.00", "destLocation" -> "urn:epc:id:sgln:7311101.00000.00", "bizLocation" -> "urn:epc:id:gdti:7311101.00001.00", "bizTrans" -> "urn:epcglobal:cbv:bt:7311101.00001.00", "epcCommissionPrefix" -> "http://dts.gta.gov.qa/epcis-aggr/obj/")).circular


  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Constants.sagaUrl)
    .inferHtmlResources()
    .acceptHeader("text/html;charset=UTF-8")
    .header("Content-Type", "text/xml; charset=utf-8")
    .header("Authorization", "Basic YWRtaW46U3RhZmZhbnN0b3JwIQ==")

  def callEpcis: ScenarioBuilder = scenario("callEPCIS").feed(sagaEPCISData)

  exec(http("call epcis")
    .post("/api/acquire/rabbitmq/epcis")
    .body(StringBody("commissionString"))
    //    .body(StringBody(session => generateCommissionEvent(getDPCs(5).asScala.toList)))
    .check(bodyString.saveAs("RESPONSE_DATA"))
    .check(status.is(200))).pause(1)


  val scnT: ScenarioBuilder = scenario("Scenario1").feed(sagaEPCISData)
    .exec(http("get entitlement")
      .post("/api/acquire/rabbitmq/epcis")
      .body(StringBody(session => generateCommissionEvent(getDPCs(5).asScala.toList)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    .exec(
      session => {
        println(session("RESPONSE_DATA").as[String])
        session
      }
    )

  def callEpcis2: ScenarioBuilder = scenario("callEPCIS").feed(sagaEPCISData)

  exec(http("call epcis")
    .post("/api/acquire/rabbitmq/epcis")
    .body(StringBody("commissionString")))

  //  @Test def generate() = {
  //    seperateDPCS("C:" + File.separator + "Workspace" + File.separator +
  //      "certify-performance-tests" + File.separator + "src" + File.separator + "test" + File.separator + "scala" + File.separator +"EPCIS" + File.separator + "A1Not applicable_0001C_2021_10_06_11_39_AM.csv")
  //    println(generateCommissionEvent(getDPCs(5).asScala.toList))
  //    println(generateCommissionEvent(getDPCs(5).asScala.toList))
  //    println(generateCommissionEvent(getDPCs(5).asScala.toList))
  //  }
  //
  //  @Test def generateDPCList() = {
  //    seperateDPCS("C:" + File.separator + "Workspace" + File.separator +
  //      "certify-performance-tests" + File.separator + "src" + File.separator + "test" + File.separator + "scala" + File.separator +"EPCIS" + File.separator + "A1Not applicable_0001C_2021_10_06_11_39_AM.csv")
  //    println(getDPCs(5))
  //    println(getDPCs(4))
  //    println(getDPCs(8))
  //    println(getDPCs(7))
  //  }

  before(seperateDPCS("C:" + File.separator + "Workspace" + File.separator +
    "certify-performance-tests" + File.separator + "src" + File.separator + "test" + File.separator + "scala" + File.separator + "EPCIS" + File.separator + "A1Not applicable_0001C_2021_10_06_11_39_AM.csv"))

  def seperateDPCS(fileName: String) = {
    for (line <- Source.fromFile(fileName).getLines) {
      if (!line.substring(0, 13).contains(",")) {
        dpcsFromCSV.add(line.substring(0, 13))
      }
    }
  }

  def groupDPCs(dpcs: List[String]) = {
    dpcsFromCSV.asScala.toList.grouped(5).foreach(group => {
      partitionedDPCs.add(group)
    })
  }

  def getDPCs(number: Int) = {
    if (dpcsFromCSV == null) {
      seperateDPCS("C:\\Workspace\\certify-performance-tests\\src\\test\\scala\\EPCIS\\A1Not applicable_0001C_2021_10_06_11_39_AM.csv")
    }
    var num = number
    var listToReturn = new util.ArrayList[String]()
    while (num > 0) {
      listToReturn.add(dpcsFromCSV.get(0))
      dpcsFromCSV.remove(0)
      num = num - 1
    }
    listToReturn
  }

  val numberOfCalls = 10

  var dpcsFromCSV: util.ArrayList[String] = new util.ArrayList[String]()
  var partitionedDPCs: util.ArrayList[List[String]] = new util.ArrayList[List[String]]()

  //  var separatedDPCs = new util.TreeMap[String, util.ArrayList[String]]()
  //  var liveDPCs: util.ArrayList[String] = new util.ArrayList[String]()
  //  var wasteDPCs: util.ArrayList[String]  = new util.ArrayList[String]()

  //  var strings = new util.ArrayList[String]

  def generateCommissionEvent(dpcs: List[String]) = {
    //    seperateDPCS("C:\\Workspace\\certify-performance-tests\\src\\test\\scala\\EPCIS\\A1Not applicable_0001C_2021_10_06_11_39_AM.csv")
    var epcList = ""
    dpcs.foreach(dpc => {
      epcList = epcList +
        "<epc>EPCCOMMISSIONPREFIX" + dpc + "</epc>\n"
    })
    commissionString.replace("EPC_LIST", epcList)
      .replace("INSTANCE_ID", getInstanceID.toString)
      .replace("DATE", "2021-10-14" + """T00:00:00.000Z""")
      .replace("EPCCOMMISSIONPREFIX", "http://dts.gta.gov.qa/epcis-aggr/obj/")
      .replace("READPOINT", "urn:epc:id:sgln:7311101.00001.99MACH1")
      .replace("BIZLOCATION", "urn:epc:id:gdti:7311101.00001.00")
  }

  def createCommissionStrings(groupedDPCs: List[List[String]]) = {
    val commissionStrings = new util.ArrayList[String]()
    groupedDPCs.foreach(group => {
      commissionStrings.add(generateCommissionEvent(group))
    })
    commissionStrings.asScala.toList
  }

  def generateAggregationEvent(dpcs: List[String]) = {
    var epcList = ""
    dpcs.foreach(dpc => {
      epcList = epcList +
        "<epc>${epcAggregationPrefix}" + dpc + "</epc>\n"
    })
    aggregationString.replace("EPC_LIST", epcList).replace("INSTANCE_ID", getInstanceID.toString)
  }

  def generateShippingEvent(dpcs: List[String]) = {
    var epcList = ""
    dpcs.foreach(dpc => {
      epcList = epcList +
        "<epc>${epcCommissionPrefix}" + dpc + "</epc>\n"
    })
    shippingString.replace("EPC_LIST", epcList).replace("INSTANCE_ID", getInstanceID.toString)
  }


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

  val aggregationString = epcisString.replace("EPCIS_BODY",
    """<AggregationEvent>
      <eventTime>${date}</eventTime>
      <eventTimeZoneOffset>+02:00</eventTimeZoneOffset>
      <parentID>${epcAggregationPrefix}PARENT_ID</parentID>
      <childEPCs>
        EPC_LIST
      </childEPCs>
      <action>ADD</action>
      <bizStep>urn:epcglobal:cbv:bizstep:packing</bizStep>
      <disposition>urn:epcglobal:cbv:disp:in_progress</disposition>
      <bizLocation>
        <id>${bizLocation}</id>
      </bizLocation>
    </AggregationEvent>"""
  )

  val shippingString = epcisString.replace("EPCIS_BODY",
    """<ObjectEvent>
      <eventTime>${date}</eventTime>
      <eventTimeZoneOffset>+02:00</eventTimeZoneOffset>
      <epcList>
        EPC_LIST
      </epcList>
      <action>OBSERVE</action>
      <bizStep>urn:epcglobal:cbv:bizstep:shipping</bizStep>
      <disposition>urn:epcglobal:cbv:disp:in_transit</disposition>
      <bizTransactionList>
        <bizTransaction type="urn:epcglobal:cbv:btt:desadv">${bizTrans}</bizTransaction>
      </bizTransactionList>
      <extension>
        <sourceList>
          <source type="urn:epcglobal:cbv:sdt:location">${sourceLocation}</source>
          <source type="urn:epcglobal:cbv:sdt:owner">${sourceOwner}</source>
        </sourceList>
        <destinationList>
          <destination type="urn:epcglobal:cbv:sdt:location">${destLocation}</destination>
          <destination type="urn:epcglobal:cbv:sdt:owner">${destOwner}</destination>
        </destinationList>
      </extension>
    </ObjectEvent>"""
  )

  //  var getNextString = {
  //    val string = strings.get(0)
  //    strings.remove(0)
  //    string
  //  }

  def getTimestamp: String = {
    LocalDate.now.toString
  }

  //  var calls = new util.ArrayList[String]

  //  def createStrings(): List[String] = {
  //
  //    List("")
  //  }
  //
  //      api.call(EventHandler.getDestroyXMLStrings(wasteDPCs))
  //
  //  CSVHandler.output("destroyedCodes.csv", separatedDPCs.get("wasteDPCs"))
  //    // create comission events live dpcs and send to saga
  //    api.call(EventHandler.getCommissionXMLStrings(liveDPCs))
  //    CSVHandler.output("commissionedCodes.csv", separatedDPCs.get("liveDPCs"))
  //    // create shipment object with live dpcs
  //    val shipment = EventHandler.getAggregationXMLStrings(liveDPCs)
  //    // send aggregation events for this shipment to saga
  //    api.call(shipment.getAggregationEventStrings)
  //    CSVHandler.output("aggregatedCodes.csv", shipment.getEPCs)
  //    // create shipping event with container id and send to saga
  //    api.call(EventHandler.getShippingXMLString(shipment.getContainerID))
  //    CSVHandler.output("shippedCodes.csv", shipment.getContainerID)
  //  // set data feeder as ArrayList 'refs', then

  setUp(scnT.inject(atOnceUsers(1)).protocols(httpProtocol))
}
