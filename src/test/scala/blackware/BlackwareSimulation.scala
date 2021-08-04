package oms.simulations

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import oms.Constants
import blackware.BlackwareVariableGenerator.{WorkOrder, orderCount, workOrderName}
import io.gatling.core.session.SessionAttribute

import java.util
import java.util.regex.{Matcher, Pattern}
import scala.concurrent.duration.DurationInt

class BlackwareSimulation extends Simulation {
  var container2 = ""
  val jobContainers: util.ArrayList[WorkOrder] = new util.ArrayList[WorkOrder]
  val workOrderRefs: util.ArrayList[String] = new util.ArrayList[String]
  val workOrders: Option[util.ArrayList[WorkOrder]] = Some(new util.ArrayList[WorkOrder]())
  var orderorder: util.ArrayList[WorkOrder] = new util.ArrayList[WorkOrder]()

  def getWorkOrders: util.ArrayList[WorkOrder] = {
    val stringList = new util.ArrayList[String]
    stringList.add("abc")
    stringList.add("abc")
    stringList.add("abc")
    stringList.add("abc")
    stringList.add("abc")
    stringList.add("abc")
    stringList.add("abc")
    stringList.add("abc")
    println(stringList.size())
    val wo = WorkOrder("def", stringList)
    val wos = new util.ArrayList[WorkOrder]
    wos.add(wo)
    val foo = workOrders.getOrElse(wos)
    foo
  }

  case class WorkOrder(ref: String, containers: util.ArrayList[String])

  val containerNumberPattern: Pattern = Pattern.compile("ContainerNumber>(.*?)<")
  val skus: FeederBuilderBase[String]#F = Array(
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "AEA1", "quantity" -> "70000"),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "AEA2", "quantity" -> "280"),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "AEA3", "quantity" -> "25000"),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "AEB1", "quantity" -> "70000"),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "AEB2", "quantity" -> "280"),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "AEB3", "quantity" -> "25000"),
    Map("uuid" -> getUUID, "workOrderRef" -> getNextWorkOrder, "skuCode" -> "AEP0", "quantity" -> "70000")).queue


  def getNextWorkOrder: String = {
    orderCount += 1
    val currentWorkOrder = workOrderName + orderCount
    workOrderRefs.add(currentWorkOrder)
    currentWorkOrder
  }

  def getUUID = java.util.UUID.randomUUID().toString

  lazy val getDate = java.time.LocalDate.now

  val allocateUCodeRangeString = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    "\t<s:Header>\n" +
    "\t\t<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "\t\t\t<h:RequestID>${uuid}</h:RequestID>\n" +
    "\t\t\t<h:ContractID>1</h:ContractID>\n" +
    "\t\t\t<h:OperatorID>Planning User</h:OperatorID>\n" +
    "\t\t\t<h:MachineID>3</h:MachineID>\n" +
    s"\t\t\t<h:DateTime>${getDate}T00:00.0000000Z</h:DateTime>\n" +
    "\t\t\t<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "\t\t\t<h:Hash i:nil=\"true\"/>\n" +
    "\t\t\t<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "\t\t\t<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "\t\t</h:Butterfly2013Header>\n" +
    "\t</s:Header>\n" +
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

  def getWorkOrderDetailString = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    "\t<s:Header>\n" +
    "\t\t<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    s"\t\t\t<h:RequestID>${getUUID}</h:RequestID>\n" +
    "\t\t\t<h:ContractID>1</h:ContractID>\n" +
    "\t\t\t<h:OperatorID>Planning User</h:OperatorID>\n" +
    "\t\t\t<h:MachineID>3</h:MachineID>\n" +
    "\t\t\t<h:DateTime>" + "2021-07-12" + "T00:00.0000000Z</h:DateTime>\n" +
    "\t\t\t<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "\t\t\t<h:Hash i:nil=\"true\"/>\n" +
    "\t\t\t<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "\t\t\t<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "\t\t</h:Butterfly2013Header>\n" +
    "\t</s:Header>\n" +
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
    "\t<s:Header>\n" +
    "\t\t<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    s"\t\t\t<h:RequestID>${getUUID}</h:RequestID>\n" +
    "\t\t\t<h:ContractID>1</h:ContractID>\n" +
    "\t\t\t<h:OperatorID>Planning User</h:OperatorID>\n" +
    "\t\t\t<h:MachineID>3</h:MachineID>\n" +
    "\t\t\t<h:DateTime>" + "2021-07-12" + "T00:00.0000000Z</h:DateTime>\n" +
    "\t\t\t<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "\t\t\t<h:Hash i:nil=\"true\"/>\n" +
    "\t\t\t<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "\t\t\t<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "\t\t</h:Butterfly2013Header>\n" +
    "\t</s:Header>\n" +
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
    "\t<s:Header>\n" +
    "\t\t<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    s"\t\t\t<h:RequestID>${getUUID}</h:RequestID>\n" +
    "\t\t\t<h:ContractID>1</h:ContractID>\n" +
    "\t\t\t<h:OperatorID>Planning User</h:OperatorID>\n" +
    "\t\t\t<h:MachineID>3</h:MachineID>\n" +
    "\t\t\t<h:DateTime>" + "2021-07-12" + "T00:00.0000000Z</h:DateTime>\n" +
    "\t\t\t<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n" +
    "\t\t\t<h:Hash i:nil=\"true\"/>\n" +
    "\t\t\t<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n" +
    "\t\t\t<h:CallingApplicationId i:nil=\"true\"/>\n" +
    "\t\t</h:Butterfly2013Header>\n" +
    "\t</s:Header>\n" +
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
      .check(status.is(200)))
    .exec(
      session => {
        // THIS SECTION IS USED FOR DEBUGGING
        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
        println(workOrderRefs) // prints response
        session
      }
    )

  def getWorkOrderDetail2: ScenarioBuilder = scenario("get work order detail 2").feed(
    Iterator.continually(Map("uuid" -> getUUID))).pause(30)
    .exec(http("get work order detail 2")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/GetWorkOrderDetail""")
      .body(StringBody(getWorkOrderDetailString.replace("${interpolateMe}", workOrderRefs.get(0))))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    .exec(session => {
      val container: util.ArrayList[String] = new util.ArrayList[String]
      println("RAW MATCHER DATA")
      println(session("RESPONSE_DATA").as[String])
      val matcher = containerNumberPattern.matcher(session("RESPONSE_DATA").as[String])
      while (matcher.find()) {
        container.add(matcher.group()
          .replace("ContainerNumber>", "").replace("<", ""))
      }
      workOrders.getOrElse(new util.ArrayList[WorkOrder]()).add(WorkOrder(workOrderRefs.get(0), container))
      //      println(jobContainers)

      println("\n\n\n\n\n\n\nCONTAINER = " + container.get(0))
      println("\n\n\n\n\n\n\nCONTAINER = " + container.get(0))
      println("\n\n\n\n\n\n\nCONTAINER = " + container.get(0))
      println("\n\n\n\n\n\n\nWork Order = " + workOrders.getOrElse(new util.ArrayList[WorkOrder]()).get(0).containers.get(0))

      orderorder = workOrders.getOrElse(new util.ArrayList[WorkOrder]())
      session.set("CONTAINER", workOrders.getOrElse(new util.ArrayList[WorkOrder]()).get(0).containers.get(0))
      container2 = workOrders.getOrElse(new util.ArrayList[WorkOrder]()).get(0).containers.get(0)
      exec(http("set stamps printed")
        .post("/ProductionDataService.svc")
        .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/UpdateJobContainerStatus""")
        .body(try {
          StringBody(
            printedString
              .replace(
                "${interpolateMe}",
                workOrders.getOrElse(new util.ArrayList[WorkOrder]()).get(0).containers.get(0))
          )
        } catch {
          case e: Exception => StringBody(
            printedString
              .replace(
                "${interpolateMe}",
                "workOrders.getOrElse(getWorkOrders).get(0).containers.get(0)"
              )
          )
        })
        .check(bodyString.saveAs("RESPONSE_DATA"))
        .check(status.is(200)))
        .exec(
          session => {
            println("sdfsdfsdfsdf")
            println("sdfsdfsdfsdf")
            println(session("RESPONSE_DATA").as[String])
            println("WORK ORDERS ======")
            try {
              println(workOrders.head.get(0).containers.get(0))
            } catch {
              case e: Exception => println("work orders not init")
            }
            // THIS SECTION IS USED FOR DEBUGGING
            //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
            //        println(session("RESPONSE_DATA").as[String]) // prints response
            session
          }
        )
      session
    })

  val assignStamps: ScenarioBuilder = scenario("assign stamps").feed(Iterator.continually(Map("uuid" -> getUUID))).pause(30)
    .exec(http("assign stamps")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/WorkOrderStatusChange""")
      .body(StringBody(allocateStampsString.replace("${interpolateMe}", workOrderRefs.get(0))))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
  //    .exec(
  //      session => {
  //        // THIS SECTION IS USED FOR DEBUGGING
  //        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
  //                println(session("RESPONSE_DATA").as[String]) // prints response
  //        session
  //      }
  //    )

  def getWorkOrderDetail: ScenarioBuilder = scenario("get work order detail").feed(
    Iterator.continually(Map("uuid" -> getUUID))).pause(30)
    .exec(http("get work order detail")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/GetWorkOrderDetail""")
      .body(StringBody(getWorkOrderDetailString.replace("${interpolateMe}", workOrderRefs.get(0))))
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))

  def setStampsPrinted: ScenarioBuilder = scenario("set stamps printed").exec(_.set("workPlease", container2))
    .exec(http("set stamps printed")
      .post("/ProductionDataService.svc")
      .header("SOAPAction", """http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract/IProductionDataService/UpdateJobContainerStatus""")
      .body(try {
        StringBody(
          printedString
            .replace(
              "${interpolateMe}",
              workOrders.getOrElse(new util.ArrayList[WorkOrder]()).get(0).containers.get(0))
        )
      } catch {
        case e: Exception => StringBody(
          printedString
            .replace(
              "${interpolateMe}",
              "workOrders.getOrElse(getWorkOrders).get(0).containers.get(0)"
            )
        )
      })
      .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(status.is(200)))
    .exec(
      session => {
        println("WORK ORDERS ======")
        try {
          println(workOrders.head.get(0).containers.get(0))
        } catch {
          case e: Exception => println("work orders not init")
        }
        // THIS SECTION IS USED FOR DEBUGGING
        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
        //        println(session("RESPONSE_DATA").as[String]) // prints response
        session
      }
    )

//  val scn = List(getWorkOrderDetail2.inject())

  setUp(
    allocateUCodeRange.inject(atOnceUsers(7)).protocols(httpProtocol)
      .andThen(getWorkOrderDetail.inject(atOnceUsers(1)).protocols(httpProtocol)
        .andThen(assignStamps.inject(atOnceUsers(1)).protocols(httpProtocol)
          .andThen(getWorkOrderDetail2.inject(nothingFor(200.seconds), atOnceUsers(1)).protocols(httpProtocol)
            .andThen(setStampsPrinted.inject(nothingFor(200 seconds), atOnceUsers(1)).protocols(httpProtocol))))))
}
