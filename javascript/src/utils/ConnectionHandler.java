package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

public class ConnectionHandler {
	public static Connection getConnection(ServletContext context) throws UnavailableException{
		Connection conn = null;
		
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			
			Class.forName(driver);
			
			conn = DriverManager.getConnection(url, user, password);
			
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
		return conn;
	}
	
	public static void closeConnection(Connection conn) throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}
}
