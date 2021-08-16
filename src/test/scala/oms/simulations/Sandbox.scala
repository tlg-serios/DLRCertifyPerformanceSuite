package oms.simulations

import org.junit._
import java.sql.DriverManager
import java.sql.Connection

class Sandbox {
  val url = """jdbc:sqlserver://SGBBKA6486\APP;databaseName=Butterfly.DAS"""
//  val driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
  val sqlQuery = "SELECT TOP 1 order_id, customer_ref FROM dbo.orders where order_status = 'SUBMITTED_FOR_APPROVAL' " +
    "ORDER BY NEWID()"
  val username = """perftests"""
  val password = "Butterfly2013"
  var connection:Connection = null

  @Test def foo = {
    try {
      // make the connection
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery(sqlQuery)
      while ( resultSet.next() ) {
        val id = resultSet.getString("customer_ref")
        val name = resultSet.getString("order_id")
        println(id + " and " + name)
      }
    } catch {
      case e => e.printStackTrace
    }
    connection.close()
  }
}