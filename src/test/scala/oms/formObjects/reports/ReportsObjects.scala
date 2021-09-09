package oms.formObjects.reports

import oms.Constants

/**
 * defaulted to manufacturer orderer
 */
case class ReportsObjects(
                           company: String = "DLR",
                           defaultRef: String = "0|ALL",
                           product: String = "Cigarettes",
                           itemType: String = "PHYSICAL",
                           action: String = "GET_REPORT",
                           results: String = "9999",


                           orderNumber: String = Constants.orderRef,
                           licensee: String = "125",
                           deliveryTo: String = "LIC",
                           marketCode: String = "AE",
                           deliverySite: String = "417",
                           iPartCodeList: String = "1",
                           productDropdown: String = "1",
                           multiple: String = "1",
                           selectLinkedOrderLineId: String = "0",
                           organisationType: String = "IMPORTER",
                         )
