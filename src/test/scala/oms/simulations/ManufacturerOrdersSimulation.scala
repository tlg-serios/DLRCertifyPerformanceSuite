//package oms.simulations
//
//import io.gatling.core.Predef._
//import io.gatling.http.Predef._
//import io.gatling.jdbc.Predef.jdbcFeeder
//import oms.Constants
//import oms.formObjects.OrderForm
//
//class ManufacturerOrdersSimulation extends Simulation {
//
//  val dbConnectionString = "jdbc:sqlserver://SGBBKA6486//APP"
//  val sqlQuery = "SELECT user_uuid FROM users where user_id = '${userId}'"
//  val sqlUserName = "dbUser"
//  val sqlPassword = "dbPassword"
//  val sqlQueryFeeder = jdbcFeeder(dbConnectionString, sqlUserName, sqlPassword, sqlQuery)
//
//  val manufacturerOrder = OrderForm()
//  val manufacturerEdit = manufacturerOrder.copy(
//    orderNumber = s"${Constants.orderRef}edit",
//    iPartCodeList = "3",
//    multiple = "2",
//    productDropdown = "3"
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
//      .get("/fsOrderInput?reference=OrderInput")
//      .check(css("h2:contains('Create Order - Select Order Type')").exists)
//      .check(status.is(200)))
//
//    .exec(http("post order details")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """submitSelectOrderType""")
//      .formParam("""singleLicensee""", """""")
//      .formParam("""licenseeDescription""", """""")
//      .formParam("""constantOrgType""", manufacturerOrder.organisationType)
//      .formParam("""organisationGroup""", manufacturerOrder.deliveryTo)
//      .formParam("""selectedLicensee""", manufacturerOrder.licensee)
//      .formParam("""selectedLicensee""", """""")
//      .formParam("""selectedSite""", manufacturerOrder.deliverySite)
//      .formParam("""selectedMarket""", manufacturerOrder.marketCode)
//      .formParam("""selectedItemType""", manufacturerOrder.itemType)
//      .formParam("""selectedProduct""", manufacturerOrder.product)
//      .formParam("""selectedDeliveryTo""", manufacturerOrder.deliveryTo)
//      .formParam("""selectedDeliverySite""", """""")
//      .formParam("""selectedDeliverySiteValue""", """""")
//      .check(css("h2:contains('Create Order - Products')").exists)
//      .check(status.is(200)))
//
//    .exec(http("confirm submit order")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """confirmSubmitOrder""")
//      .formParam("""organisationGroup""", manufacturerOrder.deliveryTo)
//      .formParam("""selectedSite""", manufacturerOrder.deliverySite)
//      .formParam("""selectedProduct""", manufacturerOrder.product)
//      .formParam("""selectedMarket""", manufacturerOrder.marketCode)
//      .formParam("""selectedLicensee""", manufacturerOrder.licensee)
//      .formParam("""selectedMarket""", manufacturerOrder.marketCode)
//      .formParam("""selectedDeliveryTo""", manufacturerOrder.deliveryTo)
//      .formParam("""selectedDeliverySiteValue""", """""")
//      .formParam("""thisPage""", """""")
//      .formParam("""psize""", """0""")
//      .formParam("""product""", manufacturerOrder.product)
//      .formParam("""itemType""", manufacturerOrder.itemType)
//      .formParam("""organisationID""", manufacturerOrder.licensee)
//      .formParam("""flagAction""", manufacturerOrder.flagAction)
//      .formParam("""orderlinecnt""", """0""")
//      .formParam("""isPLAEnable""", """true""")
//      .formParam("""siteID""", manufacturerOrder.deliverySite)
//      .formParam("""marketCode""", manufacturerOrder.marketCode)
//      .formParam("""orderID""", """""")
//      .formParam("""selectedDeliveryTo""", manufacturerOrder.deliveryTo)
//      .formParam("""selectedDeliverySite""", manufacturerOrder.deliverySite)
//      .formParam("""selectedLinkedOrderLineId""", """""")
//      .formParam("""selectedLinkedOrderLineId0""", manufacturerOrder.selectLinkedOrderLineId)
//      .formParam("""iPartCodeList0""", manufacturerOrder.iPartCodeList)
//      .formParam("""skuIdProduct0""", """""")
//      .formParam("""skuIdProduct0""", """""")
//      .formParam("""productDropdown0""", manufacturerOrder.productDropdown)
//      .formParam("""multiple0""", manufacturerOrder.multiple)
//      .formParam("""orderNumber""", manufacturerOrder.orderNumber)
//      .check(css("h2:contains('Create Order - Order Confirmation')").exists)
//      .check(status.is(200)))
//
//    .exec(http("post confirm submit order")
//      .post("/fsOrderInput")
//      .formParam("""reqaction""", """submitOrder""")
//      .formParam("""reference""", manufacturerOrder.reference)
//      .formParam("""flagAction""", manufacturerOrder.flagAction)
//      .formParam("""product""", manufacturerOrder.product)
//      .formParam("""itemType""", manufacturerOrder.itemType)
//      .formParam("""siteID""", manufacturerOrder.deliverySite)
//      .formParam("""organisationID""", manufacturerOrder.licensee)
//      .formParam("""orderNumber""", manufacturerOrder.orderNumber)
//      .formParam("""calledfrom""", """confirm""")
//      .formParam("""organisationGroup""", manufacturerOrder.deliveryTo)
//      .formParam("""selectedSite""", manufacturerOrder.deliverySite)
//      .formParam("""selectedMarket""", manufacturerOrder.marketCode)
//      .formParam("""selectedProduct""", manufacturerOrder.product)
//      .formParam("""selectedLicensee""", manufacturerOrder.licensee)
//      .formParam("""selectedMarket""", manufacturerOrder.marketCode)
//      .formParam("""selectedDeliveryTo""", manufacturerOrder.deliveryTo)
//      .formParam("""marketCode""", manufacturerOrder.marketCode)
//      .formParam("""selectedDeliverySite""", manufacturerOrder.deliverySite)
//      .formParam("""iPartCodeList0""", manufacturerOrder.iPartCodeList)
//      .formParam("""productDropdown0""", manufacturerOrder.productDropdown)
//      .formParam("""multiple0""", manufacturerOrder.multiple)
//      .formParam("""selectedLinkedOrderLineId0""", manufacturerOrder.selectLinkedOrderLineId)
//      .check(css("h2:contains('Create Order - Order Creation Complete')").exists)
//      .check(status.is(200))
//      // THIS IS HOW TO GRAB RESPONSE FOR DEBUG
//      //      .check(bodyString.saveAs("RESPONSE_DATA"))
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
//      .formParam("""selectedorder""", """27""")
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
//      .formParam("""product""", manufacturerEdit.product)
//      .formParam("""itemType""", manufacturerEdit.itemType)
//      .formParam("""organisationID""", manufacturerEdit.licensee)
//      .formParam("""flagAction""", manufacturerEdit.flagAction)
//      .formParam("""orderlinecnt""", """0""")
//      .formParam("""isPLAEnable""", """true""")
//      .formParam("""siteID""", manufacturerEdit.deliverySite)
//      .formParam("""orderID""", """27""")
//      .formParam("""selectedDeliveryTo""", manufacturerEdit.deliveryTo)
//      .formParam("""selectedDeliverySite""", manufacturerEdit.deliverySite)
//      .formParam("""selectedLinkedOrderLineId""", """""")
//      .formParam("""selectedLinkedOrderLineId0""", manufacturerEdit.selectLinkedOrderLineId)
//      .formParam("""iPartCodeList0""", manufacturerEdit.iPartCodeList)
//      .formParam("""skuIdProduct0""", """""")
//      .formParam("""productDropdown0""", manufacturerEdit.productDropdown)
//      .formParam("""multiple0""", manufacturerEdit.multiple)
//      .formParam("""orderNumber""", manufacturerEdit.orderNumber)
//            .check(bodyString.saveAs("RESPONSE_DATA"))
//      .check(css("h2:contains('Edit Order - Order Confirmation')").exists)
//      .check(status.is(200)))
//
//    .exec(
//      session => {
//        // THIS SECTION IS USED FOR DEBUGGING
//        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
//               println(session("RESPONSE_DATA").as[String]) // prints response
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
//      .formParam("""reference""", manufacturerEdit.reference)
//      .formParam("""flagAction""", manufacturerEdit.flagAction)
//      .formParam("""product""", manufacturerEdit.product)
//      .formParam("""itemType""", manufacturerEdit.itemType)
//      .formParam("""siteID""", manufacturerEdit.deliverySite)
//      .formParam("""organisationID""", manufacturerEdit.licensee)
//      .formParam("""PREFIX""", """""")
//      .formParam("""orderNumber""", manufacturerEdit.orderNumber)
//      .formParam("""calledfrom""", """confirm""")
//      .formParam("""organisationGroup""", """""")
//      .formParam("""selectedSite""", """""")
//      .formParam("""selectedMarket""", """""")
//      .formParam("""selectedProduct""", """""")
//      .formParam("""selectedDeliveryTo""", manufacturerEdit.deliveryTo)
//      .formParam("""selectedDeliverySite""", manufacturerEdit.deliverySite)
//      .formParam("""iPartCodeList0""", manufacturerEdit.iPartCodeList)
//      .formParam("""productDropdown0""", manufacturerEdit.productDropdown)
//      .formParam("""multiple0""", manufacturerEdit.multiple)
//      .formParam("""selectedLinkedOrderLineId0""", manufacturerEdit.selectLinkedOrderLineId)
//      .check(css("h2:contains('Edit Order - Order Amendment Complete')").exists)
//      .check(status.is(200)))
//
//  val approveOrderScenario = scenario("approve order")
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
//    .exec(http("get approve order")
//      .get("/fsOrderApprove?reference=DestroyOrderApproval")
//      .check(css("h2:contains('New Orders - Review and Approve')").exists)
//      .check(status.is(200)))
//
//    .exec(http("approve order")
//      .post( "/fsOrderApprove")
//      .formParam("""reqaction""", """approveOrder""")
//      .formParam("""thisPage""", """/orders/orderApprove.jsp""")
//      .formParam("""orderID""", """45""")
//      .formParam("""showWarehouse""", """false""")
//      .formParam("""isLicOrder""", """true""")
//      .formParam("""fulfillerDefaultValue""", """""")
//      .formParam("""isTaxPaymentFeatureOn""", """false""")
//      .formParam("""fulfiller""", """DLR""")
//      .formParam("""bondedWarehouse""", """""")
//      .check(css("h2:contains('Approve Order - Order Approval Complete')").exists)
//      .check(status.is(200)))
//
//  val rejectOrderScenario = scenario("reject order")
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
//    .exec(http("get reject order")
//      .get("/fsOrderApprove?reference=DestroyOrderApproval")
//      .check(css("h2:contains('New Orders - Review and Approve')").exists)
//      .check(status.is(200)))
//
//    .exec(http("cancel order")
//      .post( "/fsOrderCancel")
//      .formParam("""reqaction""", """selectOrder""")
//      .formParam("""originatingFrom""", """reject""")
//      .formParam("""selectedorder""", """27""")
//      .formParam("""rejectIsTaxPaid""", """""")
//      .formParam("""rejectTaxPaymentDate""", """""")
//      .formParam("""rejectTaxPaymentAdditionalInfo""", """""")
//      .check(css("h2:contains('Reject Order')").exists)
//      .check(status.is(200)))
//
//    .exec(http("confirm cancel order")
//      .post( "/fsOrderCancel")
//      .formParam("""reqaction""", """selectOrder""")
//      .formParam("""originatingFrom""", """reject""")
//      .formParam("""selectedorder""", """27""")
//      .formParam("""rejectIsTaxPaid""", """""")
//      .formParam("""rejectTaxPaymentDate""", """""")
//      .formParam("""rejectTaxPaymentAdditionalInfo""", """""")
//      .formParam("""comments""", """""")
//      .check(css("h2:contains('New Orders - Review and Approve')").exists)
//      .check(status.is(200)))
//    //      // THIS IS HOW TO GRAB RESPONSE FOR DEBUG
//    //      //      .check(bodyString.saveAs("RESPONSE_DATA"))
//    .exec(
//      session => {
//        // THIS SECTION IS USED FOR DEBUGGING
//        //        println(s"AUTH TOKEN = ${session("authToken").as[String]}") // prints auth token
////        println(session("RESPONSE_DATA").as[String]) // prints response
//        session
//      }
//    )
//  setUp(createOrderScenario.inject(atOnceUsers(1)).protocols(httpProtocol)
//    .andThen(editOrderScenario.inject(atOnceUsers(1)).protocols(httpProtocol)))
//      //.andThen(approveOrderScenario.inject(atOnceUsers(1)).protocols(httpProtocol))
//        //.andThen(rejectOrderScenario.inject(atOnceUsers(1)).protocols(httpProtocol)))
//
//
//
//
//}
