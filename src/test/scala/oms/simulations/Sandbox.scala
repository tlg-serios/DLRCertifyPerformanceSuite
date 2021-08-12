package oms.simulations

import org.junit.Test

import java.sql.{Connection, DriverManager, SQLException}

class Sandbox {

  val DB_URL = "jdbc:mysql://localhost/TUTORIALSPOINT"
  val USER = "guest"
  val PASS = "guest123"
  val QUERY = "SELECT id, first, last, age FROM Employees"

  def db = {
      // connect to the database named "mysql" on the localhost
      val driver = "com.mysql.jdbc.Driver"
      val url = "jdbc:sqlserver://SGBBKA6486//APP"
      val username = "adm/greestt"
      val password = "%Y4puDt==%xe2C5h"

      // there's probably a better way to do this
      var connection:Connection = null

      try {
        // make the connection
        Class.forName(driver)
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

  @Test def dbTest = {
    db
  }
}
