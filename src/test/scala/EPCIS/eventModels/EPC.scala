package EPCIS.eventModels

import EPCIS.EPCISEventFactory

import java.util
import java.util.List


class EPC(var parentID: String, var epcs: util.List[String]) {
  private[eventModels] val prefix = null

  def getParentID: String = parentID

  def getPrefix: String = prefix

  def getEPCS: util.List[String] = epcs

  def getCommissionEPCList: String = EPCISEventFactory.generateCommissionEPCList(epcs)

  def getEUCommissionEPCList: String = EPCISEventFactory.generateEUCommissionEPCList(epcs)

  def getAggregationEPCList: String = EPCISEventFactory.generateAggregationEPCList(epcs)
}
