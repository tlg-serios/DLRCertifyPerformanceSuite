//package oms.simulations
//
//import io.gatling.core.Predef._
//import io.gatling.http.Predef._
//import oms.Constants
//import oms.formObjects.OrderForm
//
//class ImporterOrdersSimulation extends Simulation {
//
//  val importerOrder = OrderForm().copy(
//    orderNumber = Constants.orderRef,
//    licensee = "123",
//    deliverySite = "415",
//    productDropdown = "0"
//  )
//
//  val importerEdit = importerOrder.copy(
//    orderNumber = s"${Constants.orderRef}edit",
//    iPartCodeList = "6",
//    multiple = "2",
//    productDropdown = "0",
//    licensee = "123"
//  )
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
//      .formParam("""as_sfid""", """AAAAAAW5wqSJNRJZhWsdrXIeeDP2x5dy3lWiMLIvjB5ZyJkMeuEXM4Dkdj6bmv_q4tSovydMFki8Jnv00IK0qn0VNRm6VFsrShUeMK6TgceUyFifPrdI0kCMRU3TnQqt92lFN3sy5egAeQ9tFL0INJsO-2p1F_9pdEeJa93SnqxGzUBHsA==""")
//      .formParam("""as_fid""", """9380625ce296d688ace1d9b5b5be88a72b0f9b01""")
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
//      .get("/fsOrderInput")
//      .check(css("h2:contains('Create Order - Select Order Type')").exists)
//      .check(status.is(200)))
//
//    .exec(http("post order details")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """submitSelectOrderType""")
//      .formParam("""singleLicensee""", """""")
//      .formParam("""licenseeDescription""", """""")
//      .formParam("""constantOrgType""", importerOrder.organisationType)
//      .formParam("""organisationGroup""", importerOrder.deliveryTo)
//      .formParam("""selectedLicensee""", importerOrder.licensee)
//      .formParam("""selectedLicensee""", """""")
//      .formParam("""selectedSite""", importerOrder.deliverySite)
//      .formParam("""selectedMarket""", importerOrder.marketCode)
//      .formParam("""selectedItemType""", importerOrder.itemType)
//      .formParam("""selectedProduct""", importerOrder.product)
//      .formParam("""selectedDeliveryTo""", importerOrder.deliveryTo)
//      .formParam("""selectedDeliverySite""", """""")
//      .formParam("""selectedDeliverySiteValue""", """""")
//      .check(css("h2:contains('Create Order - Products')").exists)
//      .check(bodyString.saveAs("RESPONSE_DATA"))
//      .check(status.is(200)))
//    .exec(
//      session => {
//        println(session("RESPONSE_DATA").as[String])
//        session
//      }
//    )
//
//    .exec(http("confirm submit order")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """confirmSubmitOrder""")
//      .formParam("""organisationGroup""", importerOrder.deliveryTo)
//      .formParam("""selectedSite""", importerOrder.deliverySite)
//      .formParam("""selectedProduct""", importerOrder.product)
//      .formParam("""selectedMarket""", importerOrder.marketCode)
//      .formParam("""selectedLicensee""", importerOrder.licensee)
//      .formParam("""selectedMarket""", importerOrder.marketCode)
//      .formParam("""selectedDeliveryTo""", importerOrder.deliveryTo)
//      .formParam("""selectedDeliverySiteValue""", """""")
//      .formParam("""thisPage""", """""")
//      .formParam("""psize""", """0""")
//      .formParam("""product""", importerOrder.product)
//      .formParam("""itemType""", importerOrder.itemType)
//      .formParam("""organisationID""", importerOrder.licensee)
//      .formParam("""flagAction""", importerOrder.flagAction)
//      .formParam("""orderlinecnt""", """0""")
//      .formParam("""isPLAEnable""", """true""")
//      .formParam("""siteID""", importerOrder.deliverySite)
//      .formParam("""marketCode""", importerOrder.marketCode)
//      .formParam("""orderID""", """""")
//      .formParam("""selectedDeliveryTo""", importerOrder.deliveryTo)
//      .formParam("""selectedDeliverySite""", importerOrder.deliverySite)
//      .formParam("""selectedLinkedOrderLineId""", """""")
//      .formParam("""selectedLinkedOrderLineId0""", importerOrder.selectLinkedOrderLineId)
//      .formParam("""iPartCodeList0""", importerOrder.iPartCodeList)
//      .formParam("""skuIdProduct0""", """""")
//      .formParam("""skuIdProduct0""", """""")
//      .formParam("""productDropdown0""", importerOrder.productDropdown)
//      .formParam("""multiple0""", importerOrder.multiple)
//      .formParam("""element1""", """""")
//      .formParam("""element2_0""", """bundle""")
//      .formParam("""element3_0""", """""")
//      .formParam("""element4_display_0""", """""")
//      .formParam("""element4_0""", """""")
//      .formParam("""element5_0""", """""")
//      .formParam("""orderNumber""", importerOrder.orderNumber)
//      .check(css("h2:contains('Create Order - Order Confirmation')").exists)
//      .check(status.is(200)))
//
//    .exec(http("post confirm submit order")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """submitOrder""")
//      .formParam("""reference""", importerOrder.reference)
//      .formParam("""flagAction""", importerOrder.flagAction)
//      .formParam("""product""", importerOrder.product)
//      .formParam("""itemType""", importerOrder.itemType)
//      .formParam("""siteID""", importerOrder.deliverySite)
//      .formParam("""organisationID""", importerOrder.licensee)
//      .formParam("""PREFIX""", """""")
//      .formParam("""orderNumber""", importerOrder.orderNumber)
//      .formParam("""calledfrom""", """confirm""")
//      .formParam("""organisationGroup""", importerOrder.deliveryTo)
//      .formParam("""selectedSite""", importerOrder.deliverySite)
//      .formParam("""selectedMarket""", importerOrder.marketCode)
//      .formParam("""selectedProduct""", importerOrder.product)
//      .formParam("""selectedLicensee""", importerOrder.licensee)
//      .formParam("""selectedMarket""", importerOrder.marketCode)
//      .formParam("""selectedDeliveryTo""", importerOrder.deliveryTo)
//      .formParam("""marketCode""", importerOrder.marketCode)
//      .formParam("""selectedDeliverySite""", importerOrder.deliverySite)
//      .formParam("""iPartCodeList0""", importerOrder.iPartCodeList)
//      .formParam("""productDropdown0""", importerOrder.productDropdown)
//      .formParam("""multiple0""", importerOrder.multiple)
//      .formParam("""selectedLinkedOrderLineId0""", importerOrder.selectLinkedOrderLineId)
//      .formParam("""element1_0""", """""")
//      .formParam("""element1_0""", """""")
//      .formParam("""element2_0""", """bundle""")
//      .formParam("""element2_0""", """bundle""")
//      .formParam("""element3_0""", """""")
//      .formParam("""element3_0""", """""")
//      .formParam("""element4_0""", """""")
//      .formParam("""element4_0""", """""")
//      .formParam("""element5_0""", """""")
//      .formParam("""element5_0""", """""")
//      .check(css("h2:contains('Create Order - Order Creation Complete')").exists)
//      .check(status.is(200))
//      //       THIS IS HOW TO GRAB RESPONSE FOR DEBUG
//      //            .check(bodyString.saveAs("RESPONSE_DATA"))
//    )
//    .exec(
//      session => {
//        // THIS SECTION IS USED FOR DEBUGGING
//        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
//        //                println(session("RESPONSE_DATA").as[String]) // prints response
//        session
//      }
//    )
//
//  val editOrderScenario = scenario("edit order")
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
//      .formParam("""as_sfid""", """AAAAAAW5wqSJNRJZhWsdrXIeeDP2x5dy3lWiMLIvjB5ZyJkMeuEXM4Dkdj6bmv_q4tSovydMFki8Jnv00IK0qn0VNRm6VFsrShUeMK6TgceUyFifPrdI0kCMRU3TnQqt92lFN3sy5egAeQ9tFL0INJsO-2p1F_9pdEeJa93SnqxGzUBHsA==""")
//      .formParam("""as_fid""", """9380625ce296d688ace1d9b5b5be88a72b0f9b01""")
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
//    .exec(http("get edit order")
//      .get("/fsOrderEdit?reference=OrderEdit")
//      .check(css("h2:contains('Edit Order - Select Order')").exists)
//      .check(status.is(200)))
//
//    .exec(http("select order")
//      .post("/fsOrderEdit?reference=OrderEdit")
//      .formParam("""reqaction""", """selectOrder""")
//      .formParam("""thisPage""", """/orders/orderEditSelect.jsp""")
//      .formParam("""selectedorder""", """45""")
//      .formParam("""orderColumn""", """order_data""")
//      .formParam("""orderBy""", """0""")
//      .check(css("h2:contains('Edit Order - Products')").exists)
//      .check(status.is(200)))
//
//    .exec(http("post edited order")
//      .post("/fsOrderEdit?reference=OrderEdit")
//      .formParam("""reqaction""", """submitOrder""")
//      .formParam("""organisationGroup""", """""")
//      .formParam("""selectedSite""", """""")
//      .formParam("""selectedProduct""", """""")
//      .formParam("""selectedMarket""", """""")
//      .formParam("""thisPage""", """""")
//      .formParam("""psize""", """0""")
//      .formParam("""product""", importerEdit.product)
//      .formParam("""itemType""", importerEdit.itemType)
//      .formParam("""organisationID""", importerEdit.licensee)
//      .formParam("""flagAction""", importerEdit.flagAction)
//      .formParam("""orderlinecnt""", """0""")
//      .formParam("""isPLAEnable""", """false""")
//      .formParam("""siteID""", importerEdit.deliverySite)
//      .formParam("""orderID""", """45""")
//      .formParam("""selectedDeliveryTo""", importerEdit.deliveryTo)
//      .formParam("""selectedDeliverySite""", importerEdit.deliverySite)
//      .formParam("""selectedLinkedOrderLineId""", """""")
//      .formParam("""selectedLinkedOrderLineId0""", importerEdit.selectLinkedOrderLineId)
//      .formParam("""iPartCodeList0""", importerEdit.iPartCodeList)
//      .formParam("""skuIdProduct0""", """""")
//      .formParam("""productDropdown0""", importerEdit.productDropdown)
//      .formParam("""multiple0""", importerEdit.multiple)
//      .formParam("""orderNumber""", importerEdit.orderNumber)
//      .check(css("h2:contains('Edit Order - Order Confirmation')").exists)
//      .check(status.is(200)))
//    .exec(
//      session => {
//        // THIS SECTION IS USED FOR DEBUGGING
//        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
//        println(session("RESPONSE_DATA").as[String]) // prints response
//        session
//      }
//    )
//
//    .exec(http("confirmed edited order")
//      .post("/fsOrderEdit")
//      .formParam("""reqaction""", """submitOrderConfirm""")
//      .formParam("""selectedLinkedOrderLineId""", """""")
//      .formParam("""selectedLinkedOrderLineId""", """""")
//      .formParam("""orderID""", """27""")
//      .formParam("""reference""", importerEdit.reference)
//      .formParam("""flagAction""", importerEdit.flagAction)
//      .formParam("""product""", importerEdit.product)
//      .formParam("""itemType""", importerEdit.itemType)
//      .formParam("""siteID""", importerEdit.deliverySite)
//      .formParam("""organisationID""", importerEdit.licensee)
//      .formParam("""PREFIX""", """""")
//      .formParam("""orderNumber""", importerEdit.orderNumber)
//      .formParam("""calledfrom""", """confirm""")
//      .formParam("""organisationGroup""", """""")
//      .formParam("""selectedSite""", """""")
//      .formParam("""selectedMarket""", """""")
//      .formParam("""selectedProduct""", """""")
//      .formParam("""selectedDeliveryTo""", importerEdit.deliveryTo)
//      .formParam("""selectedDeliverySite""", importerEdit.deliverySite)
//      .formParam("""iPartCodeList0""", importerEdit.iPartCodeList)
//      .formParam("""productDropdown0""", importerEdit.productDropdown)
//      .formParam("""multiple0""", importerEdit.multiple)
//      .formParam("""selectedLinkedOrderLineId0""", importerEdit.selectLinkedOrderLineId)
//      .formParam("""element1_0""", """""")
//      .formParam("""element1_0""", """""")
//      .formParam("""element2_0""", """bundle""")
//      .formParam("""element2_0""", """bundle""")
//      .formParam("""element3_0""", """""")
//      .formParam("""element3_0""", """""")
//      .formParam("""element4_0""", """""")
//      .formParam("""element4_0""", """""")
//      .formParam("""element5_0""", """""")
//      .formParam("""element5_0""", """""")
//      .check(css("h2:contains('Edit Order - Order Amendment Complete')").exists)
//      .check(bodyString.saveAs("RESPONSE_DATA"))
//      .check(status.is(200)))
//    //      // THIS IS HOW TO GRAB RESPONSE FOR DEBUG
//    //      //      .check(bodyString.saveAs("RESPONSE_DATA"))
//    .exec(
//      session => {
//        // THIS SECTION IS USED FOR DEBUGGING
//        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
//        println(session("RESPONSE_DATA").as[String]) // prints response
//        session
//      }
//    )
//  setUp(createOrderScenario.inject(atOnceUsers(1)).protocols(httpProtocol))
//  //    .andThen(editOrderScenario.inject(atOnceUsers(1)).protocols(httpProtocol)))
//}
