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
  }

  var calls = new util.ArrayList[String]

  def createStrings(): List[String] = {
    // create x event calls, foreach add to dpc list
    List("")
  }

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
}