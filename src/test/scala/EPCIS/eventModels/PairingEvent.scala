package EPCIS.eventModels

import java.util
import java.util.ArrayList


class PairingEvent(var eu: util.ArrayList[String], var dpc: util.ArrayList[String]) {
  def getEU: util.ArrayList[String] = this.eu

  def getDPC: util.ArrayList[String] = this.dpc
}
