package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Item;

public class ItemDAO {
	private Connection conn;

	public ItemDAO(Connection conn) {
		this.conn = conn;
	}
	
	public Item getItemById(int id_item) throws SQLException{
		Item item = null;
		
		String performedAction = " finding item by id";
		String query = "SELECT * FROM auction_house.item WHERE id_item = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_item);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				item = new Item();
				item.setId_item(rs.getInt("id_item"));
				item.setId_user(rs.getInt("id_user"));
				item.setItem_state(rs.getString("item_state"));
				if(item.getItem_state().equals("auctioned")) {
					item.setId_auction(rs.getInt("id_auction"));
				}
				item.setName(rs.getString("name"));
				item.setPrice(rs.getInt("price"));
				item.setDescription(rs.getString("description"));
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
		
		return item;
	}

	public boolean allItemMine(int id_user, int[] ids_item) throws SQLException{
		int rowCounter = 0;
		
		String performedAction = " checking if all item owner is correct";
		String query = "SELECT * FROM auction_house.item WHERE id_user = ? AND id_item IN (";
		
		for(int i = 0; i < ids_item.length; i++) {
			if(i > 0) {
				query += ",";
			}
			query += "?";
		}
		query += ")";
		
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			for(int i = 0; i < ids_item.length; i++) {
				ps.setInt(i + 2, ids_item[i]);
			}
			rs = ps.executeQuery();
			
			while(rs.next()) {
				rowCounter++;
			}
			
		} catch(SQLException e) {
			throw new SQLException("Error accessing the DB when " + performedAction);
		} 
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
		
		return rowCounter == ids_item.length;
	}
	
	public void publishItem(int id_user, int price, String name, String description, String img_name) throws SQLException{
		String performedAction = " adding a new item";
		String query = "INSERT INTO auction_house.item (id_user, price, name, description, img_name, item_state) VALUES (?,?,?,?,?,'not_auctioned')";
		
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			ps.setInt(2, price);
			ps.setString(3, name);
			ps.setString(4, description);
			ps.setString(5, img_name);
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
	
	public List<Item> getNotAuctionedItemById(int id_user) throws SQLException {
		List<Item> items = new ArrayList<>();
		
		String performedAction = " finding auctionable items by user id";
		String query = "SELECT * FROM auction_house.item WHERE id_user = ? AND item_state = 'not_auctioned'";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Item item = new Item();
				item.setId_user(rs.getInt("id_user"));
				item.setId_item(rs.getInt("id_item"));
				item.setId_auction(rs.getInt("id_auction"));
				item.setPrice(rs.getInt("price"));
				item.setName(rs.getString("name"));
				item.setDescription(rs.getString("description"));
				item.setImg_name(rs.getString("img_name"));
				items.add(item);
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
		
		return items;
	}

	public List<Item> getWonItemsByAuctionIds(int[] ids_auction) throws SQLException{
		List<Item> items = new ArrayList<>();
		
		String performedAction = " finding items won by user id";
		String query = "SELECT * FROM auction_house.item WHERE id_auction IN (";
		
		for(int i = 0; i < ids_auction.length; i++) {
			if(i > 0) {
				query += ",";
			}
			query += "?";
		}
		query += ")";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
				
		try {
			ps = conn.prepareStatement(query);
			for(int i = 0; i < ids_auction.length; i++) {
				ps.setInt(i + 1, ids_auction[i]);
			}
			
			rs = ps.executeQuery();
			while(rs.next()) {
				Item item = new Item();
				item.setId_auction(rs.getInt("id_auction"));
				item.setId_item(rs.getInt("id_item"));
				item.setId_user(rs.getInt("id_user"));
				item.setName(rs.getString("name"));
				item.setDescription(rs.getString("description"));
				item.setImg_name(rs.getString("img_name"));
				item.setPrice(rs.getInt("price"));
				items.add(item);
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
		
		return items;
	}
	
	public List<Item> getItemsByAuctionId(int auction_id) throws SQLException {
		List<Item> items = new ArrayList<>();
		
		String performedAction = " finding items by auction id";
		String query = "SELECT id_user,id_item,price,name,description,img_name FROM auction_house.item WHERE id_auction = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, auction_id);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Item item = new Item();
				item.setId_user(rs.getInt("id_user"));
				item.setId_item(rs.getInt("id_item"));
				item.setPrice(rs.getInt("price"));
				item.setName(rs.getString("name"));
				item.setDescription(rs.getString("description"));
				item.setImg_name(rs.getString("img_name"));
				items.add(item);
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
		
		return items;
	}

	public List<Item> getLiveAuctionedItemsByUserId(int id_user) throws SQLException {
		List<Item> items = new ArrayList<>();
		
		String performedAction = " finding auctioned items by user id";
		String query = "SELECT id_auction, id_item, name FROM auction_house.item WHERE id_user = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Item item = new Item();
				item.setId_auction(rs.getInt("id_auction"));
				item.setId_item(rs.getInt("id_item"));
				item.setName(rs.getString("name"));
				items.add(item);
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
		
		return items;
	}
}
