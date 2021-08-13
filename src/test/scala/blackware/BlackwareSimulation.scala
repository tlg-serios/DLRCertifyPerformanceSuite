package blackware

import io.gatling.core.Predef.{pause, _}
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import oms.Constants
import blackware.BlackwareVariableGenerator.{WorkOrder, orderCount, workOrderName}

import java.util
import java.util.regex.{Matcher, Pattern}
import scala.concurrent.duration.DurationInt

class BlackwareSimulation extends Simulation {

  // Used to determine number of update container status calls needed
  var numberOfContainers = 1
  // Used to determine number of work orders are in play
  var numberOfWorkOrders = 0
  // TODO will be used for multiple work order runs
  var currentWorkOrderRef = ""
  // Used to populate update container status lists
  var allocatedWorkOrders: util.ArrayList[Boolean] = new util.ArrayList[Boolean]
  // Used to populate update container status lists
  var jobContainers: util.ArrayList[String] = new util.ArrayList[String]
  // Stores work orders
  val workOrderRefs: util.ArrayList[String] = new util.ArrayList[String]
  // Stores work orders
  var workOrdersToAssign: util.ArrayList[String] = new util.ArrayList[String]
  // Stores work orders
  var workOrdersForContainerCapture: util.ArrayList[String] = new util.ArrayList[String]
  var workOrdersForDebugging: util.ArrayList[String] = new util.ArrayList[String]
  // Stores work orders
  var workOrdersToPrint: util.ArrayList[String] = new util.ArrayList[String]
  // Stores container numbers to be printed
  var containersToPrint: util.ArrayList[String] = new util.ArrayList[String]
  var containersForStock: util.ArrayList[String] = new util.ArrayList[String]
  val workOrders: Option[util.ArrayList[WorkOrder]] = Some(new util.ArrayList[WorkOrder]())

  // Represents a work orders = a work order reference plus list of associated containers
  case class WorkOrder(ref: String, containers: util.ArrayList[String])

  def useWorkOrderRef = {
    println("CALLED USE WORK ORDER REF")
    println(workOrdersForContainerCapture.size())
    val nextIndex = workOrdersForContainerCapture.get(0)
    println(nextIndex)
    workOrdersForContainerCapture.remove(0)
    nextIndex
  }

  def assignWorkOrder = {
    val workOrder = workOrdersToAssign.get(0)
    workOrdersForContainerCapture.add(workOrder)
    workOrdersForDebugging.add(workOrder)
    workOrdersToAssign.remove(0)
    workOrder
  }

  // Returns the next container reference to be printed and then removes it from the list
  def getNextPrintContainer = {
    println("CONTAINER NUMBERS" + containersToPrint)
    val container = containersToPrint.get(0)
    containersToPrint.remove(0)
    container
  }

  // Returns the next container reference to be printed and then removes it from the list
  def getNextReadyContainer = {
    println("CONTAINER NUMBERS" + containersForStock)
    val container = containersForStock.get(0)
    containersForStock.remove(0)
    container
  }

  // Returns the next container reference to be printed and then removes it from the list
  def getOrderContainers = {
    val container = workOrdersForContainerCapture.get(0)
    workOrdersForContainerCapture.remove(0)
    container
  }

  // Pattern for extracting containers from work order calls
  val containerNumberPattern: Pattern = Pattern.compile("ContainerNumber>(.*?)</b")

  // TODO refine
  def getNextWorkOrder: String = {
    orderCount += 1
    val currentWorkOrder = workOrderName + orderCount
    workOrderRefs.add(currentWorkOrder)
    currentWorkOrder
  }

  // TODO perhaps abstract
  def getUUID = java.util.UUID.randomUUID().toString

  // TODO perhaps abstract
  lazy val getDate = java.time.LocalDate.now

  def bundles(i: Int): String = (70000 * i).toString

  def sheets(i: Int): String = (280 * i).toString

  def reels(i: Int): String = (25000 * i).toString

  // Data needed to populate skus
  val skus: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "A1A1", "quantity" -> bundles(4)),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "A1A2", "quantity" -> sheets(4)),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "A1A3", "quantity" -> reels(4)),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "A1B1", "quantity" -> bundles(4)),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "A1B2", "quantity" -> sheets(4)),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "A1B3", "quantity" -> reels(4)),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "A1P0", "quantity" -> bundles(4))).circular

  val allocateUCodeRangeString = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    "<s:Header>\n" +
    "<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<h:RequestID>${uuid}</h:RequestID>\n" +
    "<h:ContractID>1</h:ContractID>\n" +
    "<h:OperatorID>Planning User</h:OperatorID>\n" +
    "<h:MachineID>3</h:MachineID>\n" +
    s"<h:DateTime>${getDate}T00:00.0000000Z</h:DateTime>\n" +
    "<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "<h:Hash i:nil=\"true\"/>\n" +
    "<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "</h:Butterfly2013Header>\n" +
    "</s:Header>\n" +
    "<s:Body>\n" +
    "<AllocateUCodeRange xmlns=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract\">\n" +
    "<Request xmlns:a=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.DataContract\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<a:WorkOrder>\n" +
    "<a:WorkOrderRef>\n" +
    "<a:LicenseeCode>lc</a:LicenseeCode>\n" +
    "<a:PlanningSystemId>1</a:PlanningSystemId>\n" +
    "<a:WorkOrderId>${workOrderRef}</a:WorkOrderId>\n" +
    "</a:WorkOrderRef>\n" +
    "<a:WorkOrderType>Limited</a:WorkOrderType>\n" +
    "<a:ProductionType>Paper</a:ProductionType>\n" +
    "<a:ProductionRunType>Print And Scan</a:ProductionRunType>\n" +
    "<a:SKUCode>${skuCode}</a:SKUCode>\n" +
    "<a:Quantity>${quantity}</a:Quantity>\n" +
    s"<a:DueDate>${getDate}T00:00:00</a:DueDate>\n" +
    "</a:WorkOrder>\n" +
    "<a:ProductionRunOperatorID>Planning User</a:ProductionRunOperatorID>\n" +
    "</Request>\n" +
    "</AllocateUCodeRange>\n" +
    "</s:Body>\n" +
    "</s:Envelope>"

  val getWorkOrderDetailString = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    "<s:Header>\n" +
    "<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<h:RequestID>${uniqueUUID}</h:RequestID>\n" +
    "<h:ContractID>1</h:ContractID>\n" +
    "<h:OperatorID>Planning User</h:OperatorID>\n" +
    "<h:MachineID>3</h:MachineID>\n" +
    "<h:DateTime>" + "2021-07-12" + "T00:00.0000000Z</h:DateTime>\n" +
    "<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "<h:Hash i:nil=\"true\"/>\n" +
    "<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "</h:Butterfly2013Header>\n" +
    "</s:Header>\n" +
    "<s:Body>\n" +
    "<GetWorkOrderDetail xmlns=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract\">\n" +
    "<Request xmlns:a=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.DataContract\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<a:WorkOrderRef>\n" +
    "<a:LicenseeCode>lc</a:LicenseeCode>\n" +
    "<a:PlanningSystemId>1</a:PlanningSystemId>\n" +
    "<a:WorkOrderId>${interpolateMe}</a:WorkOrderId>\n" +
    "</a:WorkOrderRef>\n" +
    "</Request>\n" +
    "</GetWorkOrderDetail>\n" +
    "</s:Body>\n" +
    "</s:Envelope>"

  val allocateStampsString = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    "<s:Header>\n" +
    "<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<h:RequestID>${uniqueUUID}</h:RequestID>\n" +
    "<h:ContractID>1</h:ContractID>\n" +
    "<h:OperatorID>Planning User</h:OperatorID>\n" +
    "<h:MachineID>3</h:MachineID>\n" +
    "<h:DateTime>" + "2021-07-12" + "T00:00.0000000Z</h:DateTime>\n" +
    "<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "<h:Hash i:nil=\"true\"/>\n" +
    "<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "</h:Butterfly2013Header>\n" +
    "</s:Header>\n" +
    "<s:Body>\n" +
    "<WorkOrderStatusChange xmlns=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract\">\n" +
    "<Request xmlns:a=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.DataContract\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<a:WorkOrderRef>\n" +
    "<a:LicenseeCode>lc</a:LicenseeCode>\n" +
    "<a:PlanningSystemId>1</a:PlanningSystemId>\n" +
    "<a:WorkOrderId>${interpolateMe}</a:WorkOrderId>\n" +
    "</a:WorkOrderRef>\n" +
    "<a:ProductionRunOperatorID>Planning User</a:ProductionRunOperatorID>\n" +
    "<a:Status>Assigned</a:Status>\n" +
    "<a:AssignmentController>\n" +
    "<a:ControllerID>3</a:ControllerID>\n" +
    "</a:AssignmentController>\n" +
    "<a:ErrorDetail i:nil=\"true\"></a:ErrorDetail>\n" +
    "</Request>\n" +
    "</WorkOrderStatusChange>\n" +
    "</s:Body>\n" +
    "</s:Envelope>"

  val printedString = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    "<s:Header>\n" +
    "<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<h:RequestID>${uniqueUUID}</h:RequestID>\n" +
    "<h:ContractID>1</h:ContractID>\n" +
    "<h:OperatorID>Planning User</h:OperatorID>\n" +
    "<h:MachineID>3</h:MachineID>\n" +
    "<h:DateTime>" + "2021-07-12" + "T00:00.0000000Z</h:DateTime>\n" +
    "<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "<h:Hash i:nil=\"true\"/>\n" +
    "<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "</h:Butterfly2013Header>\n" +
    "</s:Header>\n" +
    "<s:Body>\n" +
    "<UpdateJobContainerStatus xmlns=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract\">\n" +
    "<Request xmlns:a=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.DataContract\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<a:ContainerNumber>${interpolateMe}</a:ContainerNumber>\n" +
    "<a:JobContainerStatus>Printed</a:JobContainerStatus>\n" +
    "<a:IsManualStatusChange>true</a:IsManualStatusChange>\n" +
    "<a:ProductionRunOperatorID>Planning User</a:ProductionRunOperatorID>\n" +
    "<a:ReasonRequest i:nil=\"true\"></a:ReasonRequest>\n" +
    "<a:VoidedRequest i:nil=\"true\"></a:VoidedRequest>\n" +
    "<a:OverrideStatusDateTime>0001-01-01T00:00:00</a:OverrideStatusDateTime>\n" +
    "</Request>\n" +
    "</UpdateJobContainerStatus>\n" +
    "</s:Body>\n" +
    "</s:Envelope>"

  val readyForStockString = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    "<s:Header>\n" +
    "<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<h:RequestID>${uniqueUUID}</h:RequestID>\n" +
    "<h:ContractID>1</h:ContractID>\n" +
    "<h:OperatorID>Planning User</h:OperatorID>\n" +
    "<h:MachineID>3</h:MachineID>\n" +
    "<h:DateTime>" + "2021-07-12" + "T00:00.0000000Z</h:DateTime>\n" +
    "<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "<h:Hash i:nil=\"true\"/>\n" +
    "<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "</h:Butterfly2013Header>\n" +
    "</s:Header>\n" +
    "<s:Body>\n" +
    "<UpdateJobContainerStatus xmlns=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract\">\n" +
    "<Request xmlns:a=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.DataContract\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "<a:ContainerNumber>${interpolateMe}</a:ContainerNumber>\n" +
    "<a:JobContainerStatus>ReadyForStock</a:JobContainerStatus>\n" +
    "<a:IsManualStatusChange>true</a:IsManualStatusChange>\n" +
    "<a:ProductionRunOperatorID>Planning User</a:ProductionRunOperatorID>\n" +
    "<a:ReasonRequest i:nil=\"true\"></a:ReasonRequest>\n" +
    "<a:VoidedRequest i:nil=\"true\"></a:VoidedRequest>\n" +
    "<a:OverrideStatusDateTime>0001-01-01T00:00:00</a:OverrideStatusDateTime>\n" +
    "</Request>\n" +
    "</UpdateJobContainerStatus>\n" +
    "</s:Body>\n" +
    "</s:Envelope>"

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(Constants.blackwareUrl)
    .inferHtmlResources()
    .acceptHeader("text/html;charset=UTF-8")
    .header("Content-Type", "text/xml; charset=utf-8")

  def allocateUCodeRange: ScenarioBuilder = scenario("allocate u code range").feed(skus).pause(30)
    .exec(http("allocate u code range")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/AllocateUCodeRange""")
      .body(StringBody(allocateUCodeRangeString))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200))).pause(1)
    .exec(
      session => {
        // TODO how to get the work orders to add without the println? Thread.sleep didn't work
        println("BOOLEAN ADDED")
        allocatedWorkOrders.add(true)
        numberOfWorkOrders = allocatedWorkOrders.size()
        println(allocatedWorkOrders)
        session
      }
    )

  def getWorkOrderDetail: ScenarioBuilder = scenario("get work order detail").repeat(session => numberOfWorkOrders, "number of work orders in run") {
    exec(http("get work order detail")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/GetWorkOrderDetail""")
      .body(StringBody(session => getWorkOrderDetailString.replace("${interpolateMe}", workOrderRefs.get(0))
        .replace("${uniqueUUID}", getUUID)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
      .exec(
        session => {
          workOrdersToAssign = workOrderRefs
          session
        })
  }

  def assignStamps: ScenarioBuilder = scenario("assign stamps").repeat(session => numberOfWorkOrders, "number of work orders to be assigned") {
    exec(http("assign stamps")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/WorkOrderStatusChange""")
      .body(StringBody(session => allocateStampsString.replace("${interpolateMe}", assignWorkOrder).replace("${uniqueUUID}", getUUID)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
  }

  def getWorkOrderDetailCaptureContainer: ScenarioBuilder = scenario("capture container numbers").repeat(session => numberOfWorkOrders, "number of work orders to capture containers from") {
    exec(http("get work order detail capture container")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/GetWorkOrderDetail""")
      .body(StringBody(session => getWorkOrderDetailString.replace("${interpolateMe}", getOrderContainers).replace("${uniqueUUID}", getUUID)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
      .exec(session => {
        val container: util.ArrayList[String] = new util.ArrayList[String]
        val x = session("RESPONSE_DATA").as[String]
        val matcher = containerNumberPattern.matcher(x)
        // TODO could possibly make helper method if needed in future API tests
        while (matcher.find()) {
          container.add(matcher.group()
            .replace("ContainerNumber>", "").replace("</b", ""))
        }
        container.forEach(x => {
          jobContainers.add(x)
        })
        //        println("\n\n\n\n\n\n\nWork Order = " + workOrders.getOrElse(new util.ArrayList[WorkOrder]()).get(0).containers.get(0))
        jobContainers.forEach(x => {
          if (x.length > 2 && x.length < 13) { //TODO use better conditional
            containersToPrint.add(x)
            containersForStock.add(x)
          }
        })
        numberOfContainers = containersToPrint.size()
        session
      })
  }

  // session => ... calls are greyed out but are necesarry. Do not remove.
  def setStampsPrinted: ScenarioBuilder = scenario("set stamps printed").repeat(session => numberOfContainers, "containers in job") {
    exec(http("set stamps printed")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/UpdateJobContainerStatus""")
      .body(
        StringBody(session => printedString.replace(
          "${interpolateMe}", getNextPrintContainer
        ).replace("${uniqueUUID}", getUUID)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    //      .exec(
    //        session => {
    //          println("WORK ORDERS ======")
    //          println(session("RESPONSE_DATA").as[String])
    //          // THIS SECTION IS USED FOR DEBUGGING
    //          //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
    //          //        println(session("RESPONSE_DATA").as[String]) // prints response
    //          session
    //        }
    //      )
  }

  // session => ... calls are greyed out but are necesarry. Do not remove.
  def setStampsReadyForStock: ScenarioBuilder = scenario("set stamps ready for stock").repeat(session => numberOfContainers, "containers in job") {
    exec(http("set stamps printed")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/UpdateJobContainerStatus""")
      .body(
        StringBody(session => readyForStockString.replace(
          "${interpolateMe}", getNextReadyContainer
        ).replace("${uniqueUUID}", getUUID)))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
      .exec(
        session => {
          println("WORK ORDERS ======" + workOrdersForDebugging)
          //          println(session("RESPONSE_DATA").as[String])
          // THIS SECTION IS USED FOR DEBUGGING
          //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
          //        println(session("RESPONSE_DATA").as[String]) // prints response
          session
        }
      )
  }

  // Set up with andThen() and nothingFor() to ensure consecutive execution TODO refine timings
  // IMPORTANT: andThen calls must ALL be nested for consecutive execution
  // IMPORTANT: At once users is used here to set the number of SKUs to be created, not users at once
  setUp(allocateUCodeRange.inject(atOnceUsers(7)).protocols(httpProtocol)
    .andThen(getWorkOrderDetail.inject(nothingFor(200.seconds), atOnceUsers(1)).protocols(httpProtocol)
      .andThen(assignStamps.inject(nothingFor(200.seconds), atOnceUsers(1)).protocols(httpProtocol)
        .andThen(getWorkOrderDetailCaptureContainer.inject(nothingFor(400.seconds), atOnceUsers(1)).protocols(httpProtocol)
          .andThen(setStampsPrinted.inject(nothingFor(200.seconds), atOnceUsers(1)).protocols(httpProtocol)
            .andThen(setStampsReadyForStock.inject(nothingFor(200.seconds), atOnceUsers(1)).protocols(httpProtocol)))))))
}
