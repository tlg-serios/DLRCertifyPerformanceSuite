//package oms.simulations
//
//import io.gatling.core.Predef._
//import io.gatling.http.Predef._
//import oms.Constants
//import oms.formObjects.OrderForm
//
//class OrdersSimulation extends Simulation {
//
//  val order = OrderForm()
//
//  val httpProtocol = http
//    .baseUrl(Constants.baseUrl)
//    .acceptHeader("text/html;charset=UTF-8")
//    .acceptEncodingHeader("gzip, deflate, br")
//    .acceptLanguageHeader("en-US,en;q=0.9")
//
//  val createOrderScenario = scenario("create order")
//    .exec(http("get login")
//      .get("/index.jsp?timeout=true")
//      .check(status.is(200)))
//
//    .exec(http("input credentials")
//      .post("/fsLogin")
//      .check(headerRegex("Set-Cookie", "(.*)").saveAs("authToken"))
//      .formParam("""userid""", Constants.username)
//      .formParam("""userpassword""", Constants.password)
//      .formParam("""actionrec""", """""")
//      .formParam("""reqaction""", """""")
//      .formParam("""languageIdSelected""", """1""")
//      .formParam("""isLanguageChanged""", """true""")
//      .formParam("""as_sfid""", """AAAAAAXxaIp5ByeBoCWwjMfTIm-ps6I9mnPaiVUUdM71Rf9SGAltlzH2jM77mWqsD8WuozlyfarM3bt_7mPPwiO_Tz2MK0dvXt2Aiq2X_Za9XunfEQYwj-tFnVj_vO1CzPbK-6oVZ-_LPysuK0Aj8v9NYzSxINR5imOVHJ98hDipRNtHGg==""")
//      .formParam("""as_fid""", """e9148e20ec8c51eddc470a00d3c45d7852781966""")
//      .check(status.is(200)))
//
//    .exec(http("get manage orders")
//      .get("/fsHome?reference=L1_Data_Entry")
//      .check(status.is(200)))
//
//    .exec(http("get customer orders")
//      .get("/fsHome?reference=CustomerOrders")
//      .check(status.is(200)))
//
//    .exec(http("get create order")
//      .get("/fsOrderInput?reference=OrderInput")
//      .check(css("h2:contains('Create Order - Select Order Type')").exists)
//      .check(status.is(200)))
//
//    .exec(http("post order details")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """submitSelectOrderType""")
//      .formParam("""singleLicensee""", """""")
//      .formParam("""licenseeDescription""", """""")
//      .formParam("""constantOrgType""", """IMPORTER""")
//      .formParam("""organisationGroup""", """LIC""")
//      .formParam("""selectedLicensee""", """126""")
//      .formParam("""selectedLicensee""", """""")
//      .formParam("""selectedSite""", """417""")
//      .formParam("""selectedMarket""", """A1""")
//      .formParam("""selectedItemType""", """PHYSICAL""")
//      .formParam("""selectedProduct""", """Cigarettes""")
//      .formParam("""selectedDeliveryTo""", """LIC""")
//      .formParam("""selectedDeliverySite""", """""")
//      .formParam("""selectedDeliverySiteValue""", """""")
//      .check(css("h2:contains('Create Order - Products')").exists)
//      .check(status.is(200)))
//
//    .exec(http("post confirm submit order")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """confirmSubmitOrder""")
//      .formParam("""organisationGroup""", """LIC""")
//      .formParam("""selectedSite""", """417""")
//      .formParam("""selectedProduct""", """Cigarettes""")
//      .formParam("""selectedMarket""", """A1""")
//      .formParam("""selectedLicensee""", """126""")
//      .formParam("""selectedMarket""", """A1""")
//      .formParam("""selectedDeliveryTo""", """LIC""")
//      .formParam("""selectedDeliverySiteValue""", """""")
//      .formParam("""thisPage""", """""")
//      .formParam("""psize""", """0""")
//      .formParam("""product""", """Cigarettes""")
//      .formParam("""itemType""", """PHYSICAL""")
//      .formParam("""organisationID""", """126""")
//      .formParam("""flagAction""", """LICENSEE""")
//      .formParam("""orderlinecnt""", """0""")
//      .formParam("""isPLAEnable""", """true""")
//      .formParam("""siteID""", """417""")
//      .formParam("""marketCode""", """A1""")
//      .formParam("""orderID""", """""")
//      .formParam("""selectedDeliveryTo""", """LIC""")
//      .formParam("""selectedDeliverySite""", """417""")
//      .formParam("""selectedLinkedOrderLineId""", """""")
//      .formParam("""selectedLinkedOrderLineId0""", """0""")
//      .formParam("""iPartCodeList0""", """1""")
//      .formParam("""skuIdProduct0""", """""")
//      .formParam("""skuIdProduct0""", """""")
//      .formParam("""productDropdown0""", """1""")
//      .formParam("""multiple0""", """1""")
//      .formParam("""orderNumber""", Constants.orderRef)
//      .check(css("h2:contains('Create Order - Order Confirmation')").exists)
//      .check(status.is(200)))
//
//    .exec(http("post confirm submit order")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """submitOrder""")
//      .formParam("""reference""", order.reference)
//      .formParam("""flagAction""", order.flagAction)
//      .formParam("""product""", order.product)
//      .formParam("""itemType""", order.itemType)
//      .formParam("""siteID""", order.deliverySite)
//      .formParam("""organisationID""", order.licensee)
//      .formParam("""orderNumber""", Constants.orderRef)
//      .formParam("""calledfrom""", """confirm""")
//      .formParam("""organisationGroup""", order.deliveryTo)
//      .formParam("""selectedSite""", order.deliverySite)
//      .formParam("""selectedMarket""", order.marketCode)
//      .formParam("""selectedProduct""", order.product)
//      .formParam("""selectedLicensee""", order.licensee)
//      .formParam("""selectedMarket""", order.marketCode)
//      .formParam("""selectedDeliveryTo""", order.deliveryTo)
//      .formParam("""marketCode""", order.marketCode)
//      .formParam("""selectedDeliverySite""", order.deliverySite)
//      .formParam("""iPartCodeList0""", order.iPartCodeList)
//      .formParam("""productDropdown0""", order.productDropdown)
//      .formParam("""multiple0""", order.multiple)
//      .formParam("""selectedLinkedOrderLineId0""", order.selectLinkedOrderLineId)
//      .check(css("h2:contains('Create Order - Order Creation Complete')").exists)
//      .check(status.is(200))
//      // THIS IS HOW TO GRAB RESPONSE FOR DEBUG
//      //      .check(bodyString.saveAs("RESPONSE_DATA"))
//    )
//    .exec(
//      session => {
//        // THIS SECTION IS USED FOR DEBUGGING
//        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
//        //        println(session("RESPONSE_DATA").as[String]) // prints response
//        session
//      }
//    )
//  setUp(createOrderScenario.inject(atOnceUsers(10)).protocols(httpProtocol))
//}
