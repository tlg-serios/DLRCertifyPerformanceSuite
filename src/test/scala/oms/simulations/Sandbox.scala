package oms.simulations

import org.junit._
import java.sql.DriverManager
import java.sql.Connection

class Sandbox {
  val url = """jdbc:sqlserver://SGBBKA6486\APP;databaseName=Butterfly.DAS"""
//  val driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
  val sqlQuery = "SELECT user_uuid FROM users where user_id = '${userId}'"
  val username = """DLR\ADM_GreestT"""
  val password = "%Y4puDt==%xe2C5h"
  var connection:Connection = null

  @Test def foo = {
    try {
      // make the connection
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT host, user FROM user")
      while ( resultSet.next() ) {
        val host = resultSet.getString("host")
        val user = resultSet.getString("user")
        println("host, user = " + host + ", " + user)
      }
    } catch {
      case e => e.printStackTrace
    }
    connection.close()
  }
}
