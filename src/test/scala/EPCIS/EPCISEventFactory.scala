//package EPCIS
//
////import EPCIS.XMLHandler
//import EPCIS.eventModels.EPC
//import EPCIS.eventModels.PairingEvent
//
//import java.io.File
//import java.math.BigInteger
//import java.security.SecureRandom
//import java.sql.Timestamp
//import java.text.SimpleDateFormat
//import java.util
//import java.util.{ArrayList, Date, HashMap, List}
//
//
//object EPCISEventFactory {
//  private val eventsPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "com" + File.separator + "dlrcertify" + File.separator + "saga" + File.separator + "eventSchemata" + File.separator
//  var instanceID: BigInteger = null
//  var houseWayBillNumber: BigInteger = null
//  var buyerOrderNumber: BigInteger = null
//  var invoiceNumber: BigInteger = null
//  var eventID: String = null
//  var eventCounter = 0
//  var eventInterpolationMap: util.HashMap[String, String] = new util.HashMap[String, String]() {}
//
//  @throws[Throwable]
//  def prepareCommissionEvent(event: EPC): String = {
//    var template = XMLHandler.parse(eventsPath + "epcisEvent.xml")
//    val eventTemplate = XMLHandler.parse(eventsPath + "comissioningEvent.xml")
//    template = template.replace("${instanceID}", getInstanceID.toString)
//    template = template.replace("${epcisBody}", eventTemplate)
//    template = template.replace("${epcList}", event.getCommissionEPCList)
//    System.out.print("COMMISSION EVENT: \nParent ID:" + event.getParentID + "\n EPCS:" + event.getEPCS + "\n" + XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap) + "\n\n")
//    XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap)
//  }
//
//  @throws[Throwable]
//  def prepareEUCommissionEvent(event: EPC): String = {
//    var template = XMLHandler.parse(eventsPath + "epcisEvent.xml")
//    val eventTemplate = XMLHandler.parse(eventsPath + "comissioningEvent.xml")
//    template = template.replace("${instanceID}", getInstanceID.toString)
//    template = template.replace("${epcisBody}", eventTemplate)
//    template = template.replace("${epcList}", event.getEUCommissionEPCList)
//    System.out.print("EU COMMISSION EVENT: \nParent ID:" + event.getParentID + "\n EPCS:" + event.getEPCS + "\n" + XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap) + "\n\n")
//    XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap)
//  }
//
//  @throws[Throwable]
//  def prepareAggregationEvent(event: EPC): String = {
//    var template = XMLHandler.parse(eventsPath + "epcisEvent.xml")
//    val eventTemplate = XMLHandler.parse(eventsPath + "aggregationEvent.xml")
//    template = template.replace("${instanceID}", getInstanceID.toString)
//    template = template.replace("${epcisBody}", eventTemplate)
//    template = template.replace("${parentID}", event.getParentID)
//    template = template.replace("${epcList}", event.getAggregationEPCList)
//    System.out.print("AGGREGATION EVENT: \nParent ID:" + event.getParentID + "\n EPCS:" + event.getEPCS + "\n" + XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap) + "\n\n")
//    XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap)
//  }
//
//  @throws[Throwable]
//  def prepareDestructionEvent(event: EPC): String = {
//    var template = XMLHandler.parse(eventsPath + "epcisEvent.xml")
//    val eventTemplate = XMLHandler.parse(eventsPath + "destroyingEvent.xml")
//    template = template.replace("${instanceID}", getInstanceID.toString)
//    template = template.replace("${epcisBody}", eventTemplate)
//    template = template.replace("${epcList}", event.getCommissionEPCList)
//    System.out.print("DESTRUCTION EVENT: \nParent ID:" + event.getParentID + "\n EPCS:" + event.getEPCS + "\n" + XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap) + "\n\n")
//    XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap)
//  }
//
//  @throws[Throwable]
//  def prepareShippingEvent(containerID: String): String = {
//    var template = XMLHandler.parse(eventsPath + "epcisMasterEvent.xml")
//    val eventTemplate = XMLHandler.parse(eventsPath + "shippingEvent.xml")
//    template = template.replace("${instanceID}", getInstanceID.toString)
//    template = template.replace("${houseWayBillNumber}", getHouseWayBillNumber.toString)
//    template = template.replace("${buyerOrderNumber}", getBuyerOrderNumber.toString)
//    template = template.replace("${invoiceNumber}", getInvoiceNumber.toString)
//    template = template.replace("${epcisBody}", eventTemplate)
//    template = template.replace("${epcList}", EPCISEventFactory.generateAggregationEPCList(new util.ArrayList[String]() {}))
//    System.out.print("SHIPPING EVENT: \nContainer ID:" + containerID + "\n" + XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap) + "\n\n")
//    XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap)
//  }
//
//  @throws[Throwable]
//  def preparePairingEvent(event: PairingEvent): String = {
//    var template = XMLHandler.parse(eventsPath + "epcisEvent.xml")
//    val eventTemplate = XMLHandler.parse(eventsPath + "pairingEvent.xml")
//    template = template.replace("${instanceID}", getInstanceID.toString)
//    template = template.replace("${epcisBody}", eventTemplate)
//    template = template.replace("${epcList}", EPCISEventFactory.generateAggregationEPCList(event.getEU))
//    template = template.replace("${pairList}", EPCISEventFactory.generatePairingList(event))
//    System.out.print("PAIRING EVENT: \nPairing Event:" + event.getDPC + "\n" + XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap) + "\n\n")
//    XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap)
//  }
//
//  @throws[Throwable]
//  def prepareDisaggregationEvent: String = {
//    var template = XMLHandler.parse(eventsPath + "epcisEvent.xml")
//    val eventTemplate = XMLHandler.parse(eventsPath + "disaggregationEvent.xml")
//    template = template.replace("${instanceID}", getInstanceID.toString)
//    template = template.replace("${epcisBody}", eventTemplate)
//    XMLHandler.interpolate(template, EPCISEventFactory.eventInterpolationMap)
//  }
//
//  def setInterpolationString(key: String, value: String): Unit = {
//    eventInterpolationMap.put(key, value)
//  }
//
//  def generateCommissionEPCList(epcs: util.List[String]): String = {
//    val list = new StringBuilder
//    epcs.forEach( epc => {
//      list.append("<epc>${epcCommissionPrefix}").append(epc).append("</epc>\n")
//    }
//    )
//    list.toString
//  }
//
//  def generateEUCommissionEPCList(epcs: util.List[String]): String = {
//    var list = new StringBuilder
//    epcs.forEach(epc => {
//      list.append("<epc>${euPrefix}").append(epc).append("</epc>\n")
//    })
//    list.toString
//  }
//
//  def generateAggregationEPCList(epcs: util.List[String]): String = {
//    val list = new StringBuilder
//    epcs.forEach( epc => {
//      list.append("<epc>${epcAggregationPrefix}").append(epc).append("</epc>\n")
//    })
//    list.toString
//  }
//
//  def generatePairingList(event: PairingEvent): String = {
//    val eu = event.getEU
//    val dpcs = event.getDPC
//    var list = new StringBuilder
//    eu.forEach(euCode => {
//      val dpc = dpcs.get(eu.indexOf(euCode))
//      list.append("              <item>\n                  <epc>${euPrefix}").append(euCode).append("</epc>\n<code>${epcEUPrefix}").append(dpc).append("</code>\n              </item>")
//    })
//    list.toString
//  }
//
//  def getInstanceID: BigInteger = {
//    if (instanceID == null) {
//      val idPrefix = String.format("%06d", new SecureRandom().nextInt(1000000)) + String.format("%06d", new SecureRandom().nextInt(1000000))
//      instanceID = new BigInteger(idPrefix + "00")
//    }
//    else {
//      instanceID = instanceID.add(new BigInteger(String.valueOf(1)))
//      return instanceID
//    }
//    instanceID
//  }
//
//  def getHouseWayBillNumber: BigInteger = {
//    if (houseWayBillNumber == null) {
//      val idPrefix = String.format("%04d", new SecureRandom().nextInt(1000000))
//      houseWayBillNumber = new BigInteger(idPrefix + "00")
//    }
//    else {
//      houseWayBillNumber = houseWayBillNumber.add(new BigInteger(String.valueOf(1)))
//      return houseWayBillNumber
//    }
//    houseWayBillNumber
//  }
//
//  def getBuyerOrderNumber: BigInteger = {
//    if (buyerOrderNumber == null) {
//      val idPrefix = String.format("%04d", new SecureRandom().nextInt(1000000))
//      buyerOrderNumber = new BigInteger(idPrefix + "00")
//    }
//    else {
//      buyerOrderNumber = buyerOrderNumber.add(new BigInteger(String.valueOf(1)))
//      return buyerOrderNumber
//    }
//    buyerOrderNumber
//  }
//
//  def getInvoiceNumber: BigInteger = {
//    if (invoiceNumber == null) {
//      val idPrefix = String.format("%04d", new SecureRandom().nextInt(1000000))
//      invoiceNumber = new BigInteger(idPrefix + "00")
//    }
//    else {
//      invoiceNumber = invoiceNumber.add(new BigInteger(String.valueOf(1)))
//      return invoiceNumber
//    }
//    invoiceNumber
//  }
//
//  def getEventID: String = {
//    val suffix = eventCounter
//    eventCounter += 1
//    if (eventID == null) eventID = String.format("%06d", new SecureRandom().nextInt(1000000)) + String.format("%06d", new SecureRandom().nextInt(1000000))
//    eventID + "-" + suffix
//  }
//}
