package oms.formObjects

import oms.Constants

/**
 * defaulted to manufacturer orderer
 */
case class OrderForm(
                  reference: String = "CustomerOrders",
                  flagAction: String = "LICENSEE",
                  product: String = "Cigarettes",
                  itemType: String = "PHYSICAL",
                  orderNumber: String = Constants.orderRef,
                  licensee: String = "125",
                  deliveryTo: String = "LIC",
                  marketCode: String = "AE",
                  deliverySite: String = "417",
                  iPartCodeList: String = "1",
                  productDropdown: String = "1",
                  multiple: String = "1",
                  selectLinkedOrderLineId: String = "0",
                  organisationType: String = "IMPORTER"
                )
