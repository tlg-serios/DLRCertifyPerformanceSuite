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

  var strings = new util.ArrayList[String]

  var getNextString = {
    val string = strings.get(0)
    strings.remove(0)
    string
  }


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
    .exec(http("call epcis")
      .post("/api/acquire/rabbitmq/epcis")
      .body(StringBody("EPCIS_EVENT".replace("EPCIS_EVENT", getNextString)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200))).pause(1)

}