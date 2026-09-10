package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {
	private Connection conn;
	
	public UserDAO(Connection connection) {
		this.conn = connection;
	}
	
	public User findUser(String username, String password) throws SQLException{
		User user = null;
		
		String performedAction = " finding user";
		String query = "SELECT * FROM auction_house.user WHERE username = ? AND password = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				user = new User();
				user.setId_user(rs.getInt("id_user"));
				user.setUsername(rs.getString("username"));
				user.setAddress(rs.getString("address"));
			}

		} catch(SQLException e) {
			throw new SQLException("Error accessing the DB when " + performedAction);
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the result set when " + performedAction);
			}
			try {
				ps.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the statement when " + performedAction);
			}
		}
		
		return user;
	}
	
	public void registerUser(String username, String password, String address) throws SQLException{
		String performedAction = " registering a new user";
		String query = "INSERT INTO auction_house.user (username, password, address) VALUES (?,?,?)";
		
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setString(3, address);
			ps.executeUpdate();
			
		} catch(SQLException e) {
			throw new SQLException("Error accessing the DB when " + performedAction);
		} finally {
			try {
				ps.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the statement when " + performedAction);
			}
		}
	}
	
	public User getUserById(int id) throws SQLException{

		User user = null;
		
		String performedAction = " finding a user by id";
		String query = "SELECT * FROM auction_house.user WHERE id_user = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				user = new User();
				user.setId_user(rs.getInt("id_user"));
				user.setUsername(rs.getString("username"));
				user.setAddress(rs.getString("address"));
			}
		} catch(SQLException e) {
			throw new SQLException("Error accessing the DB when " + performedAction);
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the result set when " + performedAction);
			}
			try {
				ps.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the statement when " + performedAction);
			}
		}
		
		return user;
	}
	
	public User getUserByUsername(String username) throws SQLException {
		User user = null;
		
		String performedAction = " finding a user by username";
		String query = "SELECT * FROM auction_house.user WHERE username = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, username);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				user = new User();
				user.setId_user(rs.getInt("id_user"));
				user.setUsername(rs.getString("username"));
				user.setAddress(rs.getString("address"));
			}
		} catch(SQLException e) {
			throw new SQLException("Error accessing the DB when " + performedAction);
		} finally {
			try {
				rs.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the result set when " + performedAction);
			}
			try {
				ps.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the statement when " + performedAction);
			}
		}
		
		return user;
	}
}
