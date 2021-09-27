package EPCIS

import EPCIS.eventModels.EPC
import EPCIS.eventModels.PairingEvent
import EPCIS.eventModels.Shipment
import com.google.common.collect.Lists

import java.util
import java.util.{ArrayList, List, TreeMap}
import java.util.concurrent.CopyOnWriteArrayList


object EventHandler {
  @throws[Throwable]
  def getAggregationXMLStrings(epcs: util.List[String]): Shipment = { // Used for Shipment constructor holds epc strings
    val aggStrings = new util.ArrayList[String]
    // Used for Shipment constructor holds ship event strings
    val epcStrings = new util.ArrayList[String]
    // // Used for Shipment constructor holds ship cont id
    var shippingID = ""
    // Holds epcs to not modify object references externally
    val epcIterableList = new CopyOnWriteArrayList[String](epcs)
    // while there is more than one parent code to be aggregated
    while ( {
      epcIterableList.size > 1
    }) { // get agg events for all dpcs or parent code epcs
      val events = getAggregationEvents(epcIterableList)
      // remove all from epcs to recycle on next iteration
      epcIterableList.forEach(epc => {
        epcStrings.add(epc)
      })
      epcIterableList.removeAll(epcIterableList)
      // loop through events
      import scala.collection.JavaConversions._
      for (event <- events) { // get agg event string for event and add to return string
        aggStrings.add(EPCISEventFactory.prepareAggregationEvent(event))
        // take the parent ID add add to epcs for next run, made into events at top of loop
        epcIterableList.add(event.getParentID)
        shippingID = event.getParentID
      }
    }
    new Shipment(shippingID, epcStrings, aggStrings)
  }``

  @throws[Throwable]
  def getCommissionXMLStrings(epcs: util.List[String]): util.List[String] = { // return list
    val comStrings = new util.ArrayList[String]
    // create list of agg events
    val events = getAggregationEvents(epcs)
    // Loop through events adding them to return list
    import scala.collection.JavaConversions._
    for (event <- events) {
      comStrings.add(EPCISEventFactory.prepareCommissionEvent(event))
    }
    comStrings
  }

  @throws[Throwable]
  def getEUCommissionXMLStrings(epcs: util.List[String]): util.List[String] = {
    val comStrings = new util.ArrayList[String]
    val events = getAggregationEvents(epcs)
    import scala.collection.JavaConversions._
    for (event <- events) {
      comStrings.add(EPCISEventFactory.prepareEUCommissionEvent(event))
    }
    comStrings
  }

  @throws[Throwable]
  def getPairingXMLStrings(pairs: util.TreeMap[String, String]): util.ArrayList[String] = {
    val pairingEvents = new util.ArrayList[String]
    // create list of pairing events
    val events = getPairingEvents(pairs)
    import scala.collection.JavaConversions._
    for (event <- events) {
      pairingEvents.add(EPCISEventFactory.preparePairingEvent(event))
    }
    pairingEvents
  }

  @throws[Throwable]
  def getDestroyXMLStrings(epcs: util.List[String]): util.List[String] = { // List to be returned
    val destStrings = new util.ArrayList[String]
    val events = getAggregationEvents(epcs)
    events.forEach(event => {
      destStrings.add(EPCISEventFactory.prepareDestructionEvent(event))
    })
    destStrings
  }

  @throws[Throwable]
  def getShippingXMLString(containerID: String): String = {
    val shippingEvent = EPCISEventFactory.prepareShippingEvent(containerID)
    shippingEvent
  }

  @throws[Throwable]
  def getAggregationEvents(epcs: util.List[String]): util.List[EPC] = { // List of events to be parsed
    val events = new util.ArrayList[EPC]
    // get mapped codes from epc list
    val mappedCodes = DPCHandler.mapDPCsToContainers(epcs)
    // get parent codes as list from keyset
    val parentCodes = new util.ArrayList[String](mappedCodes.keySet)
    // loop through parent codes and create agg events with lists of mapped codes
    parentCodes.forEach(parentCode => {
      events.add(new EPC(parentCode, mappedCodes.get(parentCode)))
    })
    events
  }

  @throws[Throwable]
  def getPairingEvents(epcs: util.TreeMap[String, String]): util.ArrayList[PairingEvent] = { // List of events to be paired
    val events = new util.ArrayList[PairingEvent]
    // get list of keys (eu codes)
    val keys = new util.ArrayList[String](epcs.keySet)
    // chop eu codes into 5s
    val partitionedEUCodes = Lists.partition(keys, 5)
    // loop through blocks of 5 codes
    import scala.collection.JavaConversions._
    for (list <- partitionedEUCodes) { // create list to hold 5 dpcs
      val uk = new util.ArrayList[String]
      // for each eu code get the corresponding dpc from map and add to list
      import scala.collection.JavaConversions._
      for (string <- list) {
        uk.add(epcs.get(string))
      }
      // creat arraylist object and use to create pairing event object
      val eu = new util.ArrayList[String](list)
      events.add(new PairingEvent(eu, uk))
    }
    events
  }
}
