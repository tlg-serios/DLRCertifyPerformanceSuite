package oms.formObjects

case class OrderForm(
                  reference: String = "CustomerOrders",
                  flagAction: String = "LICENSEE",
                  product: String = "Cigarettes",
                  itemType: String = "PHYSICAL",
                  orderNumber: String = "foo",
                  licensee: String = "126",
                  deliveryTo: String = "LIC",
                  marketCode: String = "A1",
                  deliverySite: String = "417",
                  iPartCodeList: String = "1",
                  productDropdown: String = "1",
                  multiple: String = "1",
                  selectLinkedOrderLineId: String = "0"
                )
