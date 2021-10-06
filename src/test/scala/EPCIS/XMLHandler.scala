//package EPCIS
//
//import java.io.BufferedReader
//import java.io.File
//import java.io.FileReader
//import java.util
//import java.util.Map
//
//
//object XMLHandler {
//  @throws[Throwable]
//  def parse(path: String): String = {
//    val reader = new BufferedReader(new FileReader(new File(path)))
//    var line = null
//    val builder = new StringBuilder
//    while ( {
//      (line = reader.readLine) != null
//    }) builder.append(line.trim)
//    builder.toString
//  }
//
//  @throws[Throwable]
//  def interpolate(raw: String, interpolationData: util.Map[String, String]): String = {
//    import scala.collection.JavaConversions._
//    interpolationData.entrySet().forEach(entry => {
//
//    })
//    for (entry <- interpolationData.entrySet) {
//      raw = raw.replace(entry.getKey, entry.getValue)
//    }
//    raw
//  }
//}
