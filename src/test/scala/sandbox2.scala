class sandbox2 {

  object entitlements {
    def generateEntitlements(): String = {
      """EntitlementID>Qatar2021</EntitlementID>""" +
        """<Quantity>1</Quantity>""" +
        """EntitlementID>Qatar2022</EntitlementID>""" +
        """<Quantity>2</Quantity>""" +
        """EntitlementID>Qatar2023</EntitlementID>""" +
        """<Quantity>3</Quantity>"""
    }
    printf(generateEntitlements())
  }
}
