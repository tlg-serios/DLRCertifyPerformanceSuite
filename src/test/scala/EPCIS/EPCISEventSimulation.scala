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

import java.util

class EPCISEventSimulation extends Simulation {

  @Test def generate() = {
    println(generateCommissionEvent(List("one", "two", "three")))
  }

  val numberOfCalls = 10

  var separatedDPCs = new util.TreeMap[String, util.ArrayList[String]]()
  var liveDPCs: util.ArrayList[String] = new util.ArrayList[String]()
  var wasteDPCs: util.ArrayList[String]  = new util.ArrayList[String]()

  var strings = new util.ArrayList[String]

  def generateCommissionEvent(dpcs: List[String]) = {
    var epcList = ""
    dpcs.foreach(dpc => {
      epcList = epcList +
      "<epc>${epcCommissionPrefix}" + dpc + "</epc>\n"
    })
    commissionString.replace("EPC_LIST", epcList)
  }


  val epcisString = """<?xml version="1.0" encoding="utf-8"?>
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
            <CreationDateAndTime>${date}</CreationDateAndTime>
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
      <eventTime>${date}</eventTime>
      <eventTimeZoneOffset>+02:00</eventTimeZoneOffset>
      <epcList>
        EPC_LIST
      </epcList>
      <action>ADD</action>
      <bizStep>urn:epcglobal:cbv:bizstep:commissioning</bizStep>
      <disposition>urn:epcglobal:cbv:disp:active</disposition>
      <readPoint>
        <id>${readPoint}</id>
      </readPoint>
      <bizLocation>
        <id>${bizLocation}</id>
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

  var getNextString = {
    val string = strings.get(0)
    strings.remove(0)
    string
  }

  val sagaEPCISData : FeederBuilderBase[String]#F = Array(
    Map("date" -> "FIND ME", "readPoint" -> "FIND ME", "sourceLocation" -> "FIND ME", "sourceOwner" -> "FIND ME", "destOwner" -> "FIND ME", "destLocation" -> "FIND ME", "bizLocation" -> "FIND ME", "bizTrans" -> "A1", "epcCommissionPrefix" -> "FIND ME", "epcAggregationPrefix" -> "FIND ME")).circular


  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Constants.sagaUrl)
    .inferHtmlResources()
    .acceptHeader("text/html;charset=UTF-8")
    .header("Content-Type", "text/xml; charset=utf-8")
    .authorizationHeader("username")

  var calls = new util.ArrayList[String]

  def createStrings(): List[String] = {

    List("")
  }
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

  def callEpcis: ScenarioBuilder = scenario("callEPCIS")
    exec(http("call epcis")
      .post("/api/acquire/rabbitmq/epcis")
      .body(StringBody("EPCIS_EVENT".replace("EPCIS_EVENT", getNextString)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200))).pause(1)
}
