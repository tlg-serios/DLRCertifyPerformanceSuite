//package oms.simulations
//
//import io.gatling.core.Predef._
//import io.gatling.core.structure.PopulationBuilder
//import io.gatling.http.Predef._
//import oms.Constants
//import oms.formObjects.reports.ReportsObjects
//
//class ReportsSimulation extends Simulation{
//extends
//  val reportsOrder = ReportsObjects()
//  val httpProtocol = http
//    .baseUrl(Constants.baseUrl)
//    .acceptHeader("text/html;charset=UTF-8")
//    .acceptEncodingHeader("gzip, deflate, br")
//    .acceptLanguageHeader("en-US,en;q=0.9")
//
//  val createStampsPerOrderReport = scenario("create stamps per order report")
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
//      .exec(http("get stamp per order report ")
//        .get("/fsHome?reference=L1_Reports")
//        .check(status.is(200)))
//
//      .exec(http("post report details")
//        .post("/spring/reports/stampStatusPerOrder/search")
//        .formParam("""govOrderRefHide""", reportsOrder.defaultRef)
//        .formParam("""govOrderRefHide""", """0""")
//        .formParam("""searchPageFlag""", """0""")
//        .formParam("""selectedLicenseeId""", """""")
//        .formParam("""LicenseeId"""", """125""")
//        .formParam("""siteId""", """417""")
//        .formParam("""siteId""", """0""")
//        .formParam("""productCategory""", reportsOrder.product)
//        .formParam("""fulfiller""", reportsOrder.company)
//        .formParam("""invalidCount""", """0""")
//        .formParam("""governmentRefList""", reportsOrder.defaultRef)
//        .formParam("""customerRefList""", reportsOrder.defaultRef)
//        .formParam("""orderIdList""", """0""")
//        .check(css("h2:contains('Report - Stamp Status Per Order')").exists)
//        .check(status.is(200)))
//
//  val createOutstandingOrderReport = scenario("create outstanding order report")
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
//    .exec(http("get outstanding order report ")
//      .get("/fsOutstandingOrdersReport?reference=outstandingOrders")
//      .check(status.is(200)))
//
//    .exec(http("post report details")
//      .post("/fsOutstandingOrdersReport")
//      .formParam("""reqaction""", reportsOrder.action)
//      .formParam("""selectedProductCategory""", """null""")
//      .formParam("""selectedFulfiller""", reportsOrder.company)
//      .formParam("""selectedBondedWarehouse""", """""")
//      .formParam("""productCategoryList"""", """""")
//      .formParam("""bondedWarehouseList""", """440""")
//      .formParam("""RESULTS_PER_PAGE""", reportsOrder.results)
//      .formParam("""as_sfid""", """AAAAAAXAHyzI8ByF2JWXt5Rfu0EMtUfaXsqoSozvYW2VVwIM1QZNb-P5F3_eVzOXunqjFnU840mjsRIzrEYHkwfju2lpLKdDM0Cgx3jZbuAcY7Yi6W8hoVsXEwNlgM8TyA7wr3c4H0BBf9VhOZOzL1C3XUKGyuLCzpT-of7CjyYGhROEiw==""")
//      .formParam("""as_fid""", """fd7c9c026c410c0c147df40404687ed1b0e245c6""")
//      .check(css("h2:contains('Report - Outstanding Orders')").exists)
//      .check(status.is(200)))
//
//  val createStampStatusReport = scenario("create stamp status report")
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
//    .exec(http("get stamp status report ")
//      .get("/fsStampStatusReport?reference=stampStatusReport")
//      .check(status.is(200)))
//
//    .exec(http("post report details")
//      .post("/fsStampStatusReport")
//      .formParam("""reqaction""", reportsOrder.action)
//      .formParam("""selectedProductCategory""", """""")
//      .formParam("""selectedItemType""", reportsOrder.itemType)
//      .formParam("""govAddress""", """null""")
//      .formParam("""selectedFulfiller""", reportsOrder.company)
//      .formParam("""selectedDep""", """true""")
//      .formParam("""page"""", """stampStatusReport""")
//      .formParam("""RESULTS_PER_PAGE""", reportsOrder.results)
//      .formParam("""REPORT_FORMAT""", """""")
//      .formParam("""productList""", """""")
//      .formParam("""fulfillerList""", reportsOrder.company)
//      .formParam("""receiverOrg""", """0""")
//      .formParam("""itemTypeList""", reportsOrder.itemType)
//      .formParam("""as_sfid""", """AAAAAAXbFiVq-WpLvrF9dFE_CMI2RRwghjUHrk0Ku0sqn4BgZUUvr_AMTpTewhE7BmgjCtcKsWx66SlpGzvnQr1qcc6RX1FCxXcqCidWxM0klicYfBnbuwqlxUpe-0fClAFbkmxSaKA8-spasOxcpscJwOfHoBoOQzydC8QoU2CXvHuawg==""")
//      .formParam("""as_fid""", """45967d67bf9afdad63d65e35af41ba880af0e806""")
//      .check(css("h2:contains('Report - Stamp Status')").exists)
//      .check(status.is(200)))
//
//  val createStampExpiryReport = scenario("create stamp expiry report")
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
//    .exec(http("get stamp expiry report ")
//      .get("/fsHome?reference=L1_Reports")
//      .check(status.is(200)))
//
//    .exec(http("post report details")
//      .post("/spring/reports/stampExpirySummaryReport/viewStampExpirySummaryReportDetails")
//      .formParam("""govRefNumber""", """""")
//      .formParam("""isBackButton""", """""")
//      .formParam("""selectedSiteId""", """""")
//      .formParam("""selectedMfgImpIdOrderFor""", """""")
//      .formParam("""selectedProductTypeIdr""", """""")
//      .formParam("""selectedProductSubTypeID""", """""")
//      .formParam("""selectedGovRefNumber: """", """""")
//      .formParam("""validOrgLicensee""", """1004""")
//      .formParam("""validOrgType""", """""")
//      .formParam("""listOfGovtRefs""", """""")
//      .formParam("""stampExpiryDateErrTxts""", """""")
//      .formParam("""mfgImpId""", """0""")
//      .formParam("""mfgImpIdOrderFor""", """0""")
//      .formParam("""siteId""", """0""")
//      .formParam("""productTypeId""", """0""")
//      .formParam("""fromDate""", """""")
//      .formParam("""toDate""", """""")
//      .formParam("""as_sfid""", """AAAAAAWPbvFqXivFhSH4uvN536JRtenTvhSW6ao6V3su7Q0_iBM5WwkPQtCNU1MsAFBEkAicPrp-bj-hgdAdPfBOqfmhc63FSGeeFlZNvSFMFN00Im5UW6-xixHkBW-t-A69Dq9A3ix3MqwnChye_aAm1kiEE49_0wbE-yuKd5d8iMEw4g==""")
//      .formParam("""as_fid""", """25050f1b79b3dba4e3e141ffa9b8bc6ebc53b11b""")
//      .check(css("h2:contains('Report - Stamp Expiry Summary')").exists)
//      .check(status.is(200)))
//
//  val createUserAuditReport = scenario("create user audit report")
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
//    .exec(http("get user audit report ")
//      .get("/fsUserAuditReport?reference=userAuditReport")
//      .check(status.is(200)))
//
//    .exec(http("post report details")
//      .post("/fsUserAuditReport")
//      .formParam("""reqaction""", reportsOrder.action)
//      .formParam("""RESULTS_PER_PAGE""", reportsOrder.results)
//      .formParam("""REPORT_FORMAT""", """""")
//      .formParam("""selectedDateFrom_display""", """10/08/2021""")
//      .formParam("""selectedDateFrom""", """2021-08-10""")
//      .formParam("""selectedDateTo_display""", """10/08/2021""")
//      .formParam("""selectedDateTo: """", """2021-08-10""")
//      .formParam("""selectedUser""", """0""")
//      .formParam("""validOrgType""", """""")
//      .formParam("""as_sfid""", """AAAAAAWeYCbc0QlsHvugv0UnKnpGzsimz07Ixl1_243Zk8IqqSLkcQO5NlOqS2P-Xj319AgKUhR0apCVZCFyBTn0j0Iw16M3PAk32NGElKWA0NNVSi6cOJ5hFM4hK_nbFNhJG0B1ef0HQjb38bNY9lqQmc4yWlSqpvjFH6cyiNa2QrYyKQ==""")
//      .formParam("""as_fid""", """a575613300ea4e95330af6dfd33f10032d9dd1cf""")
//      .check(css("h2:contains('Report - User Audit')").exists)
//      .check(status.is(200)))
//
//  val createWasteDeclarationReport = scenario("create waste declaration report")
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
//    .exec(http("get waste declaration report ")
//      .get("/fsHome?reference=L1_Reports")
//      .check(status.is(200)))
//
//    .exec(http("post report details")
//      .post("/spring/reports/wasteDeclaration/wasteDeclarationSearch")
//      .formParam("""currentDate""", """10/08/2021""")
//      .formParam("""submissionFrom""", """10/08/2021""")
//      .formParam("""searchPageFlag""", """10/08/2021""")
//      .formParam("""licensee""", """0""")
//      .formParam("""site"""", """0""")
//      .formParam("""product""", """""")
//      .formParam("""SubmitForm""", """Submit""")
//      .formParam("""as_sfid""", """AAAAAAWw8jDU0W7tfx2fVv1aLuSQexXo8UAluavcqiD79HN3innzQ-DqorBpK-WsvIyjXJUll3W_zv1N7npmPz2x_ZUvwU2LCF83xqoBlIvCoFxP8ObB2TtpeJxgZOylaAxnCfaKXp00Gqdok9tHAjcjUI6yxAG_d8MOnGUr-BFmMSWUZw==""")
//      .formParam("""as_fid""", """b96463ffe0f2111a946de8aaa68e807daf04781a""")
//
//
//      .check(css("h2:contains('Report - Waste Declaration')").exists)
//      .check(status.is(200)))
//
//  val createPerRegionDistributionReport = scenario("create per region distribution report")
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
//    .exec(http("get per region distribution report ")
//      .get("/fsPERREGION?reference=PerRegionDistributionReport")
//      .check(status.is(200)))
//
//    .exec(http("post report details")
//      .post("/fsPERREGION")
//      .formParam("""startDate""", """2021-08-11""")
//      .formParam("""endDate""", """2021-08-11""")
//      .formParam("""reqaction""", """GET_REPORT_DSTR""")
//      .formParam("""fromDate_display""", """11/08/2021""")
//      .formParam("""fromDate""", """2021-08-11""")
//      .formParam("""toDate_display""", """11/08/2021""")
//      .formParam("""toDate""", """2021-08-11""")
//      .formParam("""as_sfid""", """AAAAAAXxdybfspzUONi0RXXYGysz0F2a1hoMMG7CjWQx015UnrUpalQKlmSELgrtyt8sv5Xu-6On3u4RlwV4lRpeW0wWP53drrA74FNyimE6Ngt10RUr99T18LBcxLm_hTC-kyn48uOBlCGUYJui51GUNuTqyKhaMNJKzB-GOjHXIk_Z4Q==""")
//      .formParam("""as_fid""", """c5bc962acdf16efd0ddf24a62366a5946412f574""")
//
//      .check(css("h2:contains('Report - Per Region Distribution Percentage')").exists)
//      .check(status.is(200)))
//
//  val createOrderCountByStatusReport = scenario("create order count by status report")
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
//    .exec(http("get per region distribution report ")
//      .get("/fsHome?reference=L1_Reports")
//      .check(status.is(200)))
//
//    .exec(http("post report details")
//      .post("/spring/reports/orderStatusReport/reportOrderStatusDetails")
//      .formParam("""fromDate_display""", """11/08/2021""")
//      .formParam("""selectedfromDate""", """2021-08-11""")
//      .formParam("""toDate_display""", """11/08/2021""")
//      .formParam("""selectedtoDate""", """2021-08-11""")
//      .formParam("""as_sfid""", """AAAAAAVVdMDyhAKcEScvn3OusRiknFkc3YmpAfUQJ-xvF1xqXiXhnTfJnOQ5ZJfn2bvBolJ74hfr4wQ44Spb_tzI1XaGueGstA5DT31ny037kdqs-Av2dseuEovqYRh3OAeRYedoxZWpxnA0TJ5KUKbGAcYwiOz4I1GyHaodTamA9utdZQ==""")
//      .formParam("""as_fid""", """e766c3ee89f2364d77b36d5509885f9c22003832""")
//
//      .check(css("h2:contains('Report - Order Count by Status')").exists)
//      .check(status.is(200)))
//
//
//
//
//  setUp(createStampsPerOrderReport.inject(atOnceUsers(1)).protocols(httpProtocol)
//    .andThen(createOutstandingOrderReport.inject(atOnceUsers(1)).protocols(httpProtocol))
//      .andThen(createStampStatusReport.inject(atOnceUsers(1)).protocols(httpProtocol))
//        .andThen(createStampExpiryReport.inject(atOnceUsers( 1)).protocols(httpProtocol))
//          .andThen(createUserAuditReport.inject(atOnceUsers( 1)).protocols(httpProtocol))
//            .andThen(createWasteDeclarationReport.inject(atOnceUsers( 1)).protocols(httpProtocol))
//              .andThen(createPerRegionDistributionReport.inject(atOnceUsers( 1)).protocols(httpProtocol))
//                .andThen(createOrderCountByStatusReport.inject(atOnceUsers( 1)).protocols(httpProtocol)))
//
//}