package EPCIS

import java.io._
import java.util
import java.util.{ArrayList, Arrays, List}


object CSVHandler {
  @throws[IOException]
  def readFolder(path: String): util.List[util.List[String]] = {
    val records = new util.ArrayList[util.List[String]]
    val dlFolder = new File(path)
    val files = dlFolder.listFiles
    try {
      val br = new BufferedReader(new FileReader(files(0)))
      try {
        var line = null
        while ( {
          (line = br.readLine) != null
        }) {
          val values = line.split(",")
          records.add(util.Arrays.asList(values))
        }
      } finally if (br != null) br.close()
    }
    records
  }

  @throws[IOException]
  def readFile(path: String): util.ArrayList[String] = {
    val records = new util.ArrayList[String]
    val file = new File(path)
    try {
      val br = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "com" + File.separator + "dlrcertify" + File.separator + "saga" + File.separator + "output" + File.separator + file))
      try {
        var line = null
        while ( {
          (line = br.readLine) != null
        }) {
          val values = line.split(",")
          records.addAll(util.Arrays.asList(values))
        }
      } finally if (br != null) br.close()
    }
    records
  }

  def output(outputFile: String, epc: String): Unit = {
    try {
      val writer = new FileWriter(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "com" + File.separator + "dlrcertify" + File.separator + "saga" + File.separator + "output" + File.separator + outputFile)
      writer.write(epc)
      writer.close()
    } catch {
      case e: IOException =>
        System.out.println("An error occurred.")
        e.printStackTrace()
    }
  }

  def output(outputFile: String, epcs: util.List[String]): Unit = {
    try {
      val writer = new FileWriter(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + "com" + File.separator + "dlrcertify" + File.separator + "saga" + File.separator + "output" + File.separator + outputFile)
      var outputString = ""
      epcs.forEach(epc => {
        outputString = outputString + epc + ","
      })
      writer.write(outputString.substring(0, outputString.length - 2))
      writer.close()
    } catch {
      case e: IOException =>
        System.out.println("An error occurred.")
        e.printStackTrace()
    }
  }
}
