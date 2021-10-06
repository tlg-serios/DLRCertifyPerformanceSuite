//package EPCIS
//
//import com.dlrcertify.apiUtils.CSVHandler
//import com.google.common.collect.Lists
//import org.apache.commons.io.FileUtils
//
//import java.io.File
//import java.io.IOException
//import java.util
//import java.util.{ArrayList, List, Random, TreeMap}
//import java.util.stream.Collectors
//import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
//import scala.reflect.internal.util.FileUtils
//
//
//object DPCHandler {
//  var activeCodes: util.List[String] = null
//  var dpcs: util.List[String] = null
//  var dpcLocation: String = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "downloads"
//  var downloadRef: String = null
//
//  def setDownloadRef(ref: String): Unit = {
//    downloadRef = ref
//  }
//
//  @throws[IOException]
//  def refreshDPCList: util.List[String] = {
//    val dpcs = CSVHandler.readFolder(DPCHandler.dpcLocation).stream.flatMap(util.List.stream).collect(Collectors.toList)
//    // remove headers
//    dpcs.subList(10, dpcs.size)
//  }
//
//  @throws[IOException]
//  def clearDownloadedDPCs(): Unit = {
//    FileUtils.cleanDirectory(new File(dpcLocation))
//  }
//
//  def getNextDPC: String = {
//    val nextDPC = dpcs.get(0)
//    dpcs.remove(0)
//    nextDPC
//  }
//
//  def getDPCsByNumber(number: Int): util.List[String] = {
//    var numberCounter = number
//    val dpcs = new util.ArrayList[String]
//    while ( {
//      numberCounter > 0
//    }) {
//      dpcs.add(getNextDPC)
//      numberCounter = numberCounter - 1
//    }
//    activeCodes = dpcs
//    dpcs
//  }
//
//  def getActiveCodes: util.List[String] = activeCodes
//
//  def seperateWasteDPCs(dpcs: util.List[String]): util.TreeMap[String, util.ArrayList[String]] = { // List to hold lists of strings
//    val separatedDPCs = new util.TreeMap[String, util.ArrayList[String]]
//    // seperate waste dpcs
//    val wasteDPCs = new util.ArrayList[String]
//    wasteDPCs.addAll(dpcs.subList(0, 10))
//    // seperate live dpcs
//    val liveDPCs = new util.ArrayList[String]
//    liveDPCs.addAll(dpcs.subList(10, 1010))
//    // add waste first
//    separatedDPCs.put("wasteDPCs", wasteDPCs)
//    // add live 2nd
//    separatedDPCs.put("liveDPCs", liveDPCs)
//    System.out.print("LIVE DPCS LIST: \n" + liveDPCs.toString + "\n")
//    System.out.print("WASTE DPCS LIST: \n" + wasteDPCs.toString + "\n")
//    separatedDPCs
//  }
//
//  def generateEUCodePrefix: String = {
//    val leftLimit = 97 // letter 'a'
//    val rightLimit = 122 // letter 'z'
//    val random = new Random
//    val generatedString = random.ints(leftLimit, rightLimit + 1).limit(6).collect(StringBuilder.`new`, StringBuilder.appendCodePoint, StringBuilder.append).toString
//    "EU" + generatedString.toUpperCase
//  }
//
//  def pairDPCs(dpcs: util.List[String]): util.TreeMap[String, String] = { // Tree map to return paired codes
//    val pairedCodes = new util.TreeMap[String, String]
//    // Get this run's EU prefix
//    val prefix = generateEUCodePrefix
//    // Count to be used as EU Code suffix
//    var count = 0
//    // Loop through the DPCs
//    for (dpc <- dpcs) { // concat count to prefix
//      val euPrefix = prefix + String.format("%06d", count)
//      // increment count
//      count += 1
//      // Add EU prefix as key, DPC as value
//      pairedCodes.put(euPrefix, dpc)
//    }
//    pairedCodes
//  }
//
//  def mapDPCsToContainers(dpcs: util.List[String]): util.TreeMap[String, util.List[String]] = { // Partition codes into 5s
//    val partitionedList: util.List[Iterable[String]] = dpcs.sliding(5).toList
//    // map to hold parent string/child list<string>
//    val mappedCodes = new util.TreeMap[String, util.List[String]]
//    // Loop through list of partitioned lists
//
//    partitionedList.forEach(list => {
//      val container = EPCISEventFactory.getEventID
//      // List to store codes
//      val codes = new util.ArrayList[String]
//      // add lists to codes list
//      codes.addAll(list)
//      // Add event id and codes to map
//      mappedCodes.put(container, codes)
//    })
//
//    // return the map
//    mappedCodes
//  }
//}
