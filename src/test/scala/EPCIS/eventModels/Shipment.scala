package EPCIS.eventModels

import java.util
import java.util.List


class Shipment(var containerID: String, var epcs: util.List[String], var aggregationEventStrings: util.List[String]) {
  def getContainerID: String = containerID

  def getAggregationEventStrings: util.List[String] = aggregationEventStrings

  def getEPCs: util.List[String] = epcs
}
