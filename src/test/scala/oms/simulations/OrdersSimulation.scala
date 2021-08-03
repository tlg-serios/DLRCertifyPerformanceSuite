package oms

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import oms.formObjects.OrderForm

import scala.::
import scala.concurrent.duration._
import scala.util.Random

class OrdersSimulation extends Simulation {

  val order = OrderForm()

  val httpProtocol = http
    .baseUrl(Constants.baseUrl)
    .acceptHeader("text/html;charset=UTF-8")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("en-US,en;q=0.9")

  val createOrderScenario = scenario("create order")
    .exec(http("get login")
      .get("/index.jsp?timeout=true")
      .check(status.is(200)))

    .exec(http("input credentials")
      .post("/fsLogin")
      .check(headerRegex("Set-Cookie", "(.*)").saveAs("authToken"))
      .formParam("""userid""", Constants.username)
      .formParam("""userpassword""", Constants.password)
      .formParam("""actionrec""", """""")
      .formParam("""reqaction""", """""")
      .formParam("""languageIdSelected""", """1""")
      .formParam("""isLanguageChanged""", """true""")
      .formParam("""as_sfid""", """AAAAAAXxaIp5ByeBoCWwjMfTIm-ps6I9mnPaiVUUdM71Rf9SGAltlzH2jM77mWqsD8WuozlyfarM3bt_7mPPwiO_Tz2MK0dvXt2Aiq2X_Za9XunfEQYwj-tFnVj_vO1CzPbK-6oVZ-_LPysuK0Aj8v9NYzSxINR5imOVHJ98hDipRNtHGg==""")
      .formParam("""as_fid""", """e9148e20ec8c51eddc470a00d3c45d7852781966""")
      .check(status.is(200)))

    .exec(http("get manage orders")
      .get("/fsHome?reference=L1_Data_Entry")
      .check(status.is(200)))

    .exec(http("get customer orders")
      .get("/fsHome?reference=CustomerOrders")
      .check(status.is(200)))

    .exec(http("get create order")
      .get("/fsOrderInput?reference=OrderInput")
      .check(css("h2:contains('Create Order - Select Order Type')").exists)
      .check(status.is(200)))

    .exec(http("post order details")
      .post("/fsOrderInput")
      .formParam("""reqaction""", """submitSelectOrderType""")
      .formParam("""singleLicensee""", """""")
      .formParam("""licenseeDescription""", """""")
      .formParam("""constantOrgType""", """IMPORTER""")
      .formParam("""organisationGroup""", """LIC""")
      .formParam("""selectedLicensee""", """126""")
      .formParam("""selectedLicensee""", """""")
      .formParam("""selectedSite""", """417""")
      .formParam("""selectedMarket""", """A1""")
      .formParam("""selectedItemType""", """PHYSICAL""")
      .formParam("""selectedProduct""", """Cigarettes""")
      .formParam("""selectedDeliveryTo""", """LIC""")
      .formParam("""selectedDeliverySite""", """""")
      .formParam("""selectedDeliverySiteValue""", """""")
      .check(css("h2:contains('Create Order - Products')").exists)
      .check(status.is(200)))

    .exec(http("post confirm submit order")
      .post("/fsOrderInput")
      .formParam("""reqaction""", """confirmSubmitOrder""")
      .formParam("""organisationGroup""", """LIC""")
      .formParam("""selectedSite""", """417""")
      .formParam("""selectedProduct""", """Cigarettes""")
      .formParam("""selectedMarket""", """A1""")
      .formParam("""selectedLicensee""", """126""")
      .formParam("""selectedMarket""", """A1""")
      .formParam("""selectedDeliveryTo""", """LIC""")
      .formParam("""selectedDeliverySiteValue""", """""")
      .formParam("""thisPage""", """""")
      .formParam("""psize""", """0""")
      .formParam("""product""", """Cigarettes""")
      .formParam("""itemType""", """PHYSICAL""")
      .formParam("""organisationID""", """126""")
      .formParam("""flagAction""", """LICENSEE""")
      .formParam("""orderlinecnt""", """0""")
      .formParam("""isPLAEnable""", """true""")
      .formParam("""siteID""", """417""")
      .formParam("""marketCode""", """A1""")
      .formParam("""orderID""", """""")
      .formParam("""selectedDeliveryTo""", """LIC""")
      .formParam("""selectedDeliverySite""", """417""")
      .formParam("""selectedLinkedOrderLineId""", """""")
      .formParam("""selectedLinkedOrderLineId0""", """0""")
      .formParam("""iPartCodeList0""", """1""")
      .formParam("""skuIdProduct0""", """""")
      .formParam("""skuIdProduct0""", """""")
      .formParam("""productDropdown0""", """1""")
      .formParam("""multiple0""", """1""")
      .formParam("""orderNumber""", Constants.orderRef)
      .check(css("h2:contains('Create Order - Order Confirmation')").exists)
      .check(status.is(200)))

    .exec(http("post confirm submit order")
      .post("/fsOrderInput")
      .formParam("""reqaction""", """submitOrder""")
      .formParam("""reference""", order.reference)
      .formParam("""flagAction""", order.flagAction)
      .formParam("""product""", order.product)
      .formParam("""itemType""", order.itemType)
      .formParam("""siteID""", order.deliverySite)
      .formParam("""organisationID""", order.licensee)
      .formParam("""orderNumber""", Constants.orderRef)
      .formParam("""calledfrom""", """confirm""")
      .formParam("""organisationGroup""", order.deliveryTo)
      .formParam("""selectedSite""", order.deliverySite)
      .formParam("""selectedMarket""", order.marketCode)
      .formParam("""selectedProduct""", order.product)
      .formParam("""selectedLicensee""", order.licensee)
      .formParam("""selectedMarket""", order.marketCode)
      .formParam("""selectedDeliveryTo""", order.deliveryTo)
      .formParam("""marketCode""", order.marketCode)
      .formParam("""selectedDeliverySite""", order.deliverySite)
      .formParam("""iPartCodeList0""", order.iPartCodeList)
      .formParam("""productDropdown0""", order.productDropdown)
      .formParam("""multiple0""", order.multiple)
      .formParam("""selectedLinkedOrderLineId0""", order.selectLinkedOrderLineId)
      .check(css("h2:contains('Create Order - Order Creation Complete')").exists)
      .check(status.is(200))
      // THIS IS HOW TO GRAB RESPONSE FOR DEBUG
      //      .check(bodyString.saveAs("RESPONSE_DATA"))
    )
    .exec(
      session => {
        // THIS SECTION IS USED FOR DEBUGGING
        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
        //        println(session("RESPONSE_DATA").as[String]) // prints response
        session
      }
    )
  setUp(createOrderScenario.inject(atOnceUsers(1)).protocols(httpProtocol))

  val allocateUCodeRangeString = "%s%s</h:RequestID>\n\t\t\t<h:ContractID>1</h:ContractID>\n\t\t\t<h:OperatorID>Planning User</h:OperatorID>\n\t\t\t<h:MachineID>3</h:MachineID>\n\t\t\t<h:DateTime>%sT00:00.0000000Z</h:DateTime>\n\t\t\t<h:AuthenticationToken>F0C4B20D-694A-48F7-8D6A-E7B8D8D19AC2</h:AuthenticationToken>\n\t\t\t<h:Hash i:nil=\"true\"/>\n\t\t\t<h:ThrowConfigurationErrors>false</h:ThrowConfigurationErrors>\n\t\t\t<h:CallingApplicationId i:nil=\"true\"/>\n\t\t</h:Butterfly2013Header>\n\t</s:Header>\n<s:Body>\n<AllocateUCodeRange xmlns=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.ServiceContract\">\n<Request xmlns:a=\"http://schemas.delarue.com/2013/02/21/Butterfly.UCodeBroker.DataContract\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n<a:WorkOrder>\n<a:WorkOrderRef>\n<a:LicenseeCode>lc</a:LicenseeCode>\n<a:PlanningSystemId>1</a:PlanningSystemId>\n<a:WorkOrderId>%s</a:WorkOrderId>\n</a:WorkOrderRef>\n<a:WorkOrderType>Limited</a:WorkOrderType>\n<a:ProductionType>Paper</a:ProductionType>\n<a:ProductionRunType>Print And Scan</a:ProductionRunType>\n<a:SKUCode>%s</a:SKUCode>\n<a:Quantity>%s</a:Quantity>\n<a:DueDate>%sT00:00:00</a:DueDate>\n</a:WorkOrder>\n<a:ProductionRunOperatorID>Planning User</a:ProductionRunOperatorID>\n</Request>\n</AllocateUCodeRange>\n</s:Body>\n</s:Envelope>".format("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
    "\t<s:Header>\n" +
    "\t\t<h:Butterfly2013Header xmlns:h=\"http://schemas.delarue.com/2013/02/21/Butterfly.2013Header\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "\t\t\t<h:RequestID>", BlackwareVariableGenerator.getNextUUID(), BlackwareVariableGenerator.getTimestamp(), BlackwareVariableGenerator.workOrderRef, skuCode, quantity, BlackwareVariableGenerator.getTimestamp())

}
