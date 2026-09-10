package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import beans.Auction;


public class AuctionDAO {
	private Connection conn;

	public AuctionDAO(Connection conn) {
		this.conn = conn;
	}
	
	public List<Auction> getOpenedAuctionsByUserId(int id_user) throws SQLException{
		List<Auction> auctions = new ArrayList<>();
		
		String performedAction = " finding auctions by user id";
		String query = "SELECT * FROM auction_house.auction WHERE id_user = ? AND auction_state = 'opened'";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Auction auction = new Auction();
				auction.setId_user(rs.getInt("id_user"));
				auction.setId_auction(rs.getInt("id_auction"));
				auction.setStarting_bid(rs.getInt("starting_bid"));
				auction.setClosing_date(new Date(rs.getTimestamp("closing_date").getTime()));
				auction.setAuction_state(rs.getString("auction_state"));
				auctions.add(auction);
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
		
		return auctions;
	}
	
	public List<Auction> getClosedAuctionsByUserId(int id_user) throws SQLException{
		List<Auction> auctions = new ArrayList<>();
		
		String performedAction = " finding auctions by user id";
		String query = "SELECT * FROM auction_house.auction WHERE id_user = ? AND auction_state = 'closed'";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Auction auction = new Auction();
				auction.setId_user(rs.getInt("id_user"));
				auction.setId_auction(rs.getInt("id_auction"));
				auction.setStarting_bid(rs.getInt("starting_bid"));
				auction.setClosing_date(new Date(rs.getTimestamp("closing_date").getTime()));
				auction.setAuction_state(rs.getString("auction_state"));
				auctions.add(auction);
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
		
		return auctions;
	}

	public void createAuction(int id_user, int bid_increment, Date closing_date, int[] ids_item) throws SQLException{
		int starting_bid = 0;
		int id_auction = 0;
		
		String performedAction = " creating a new auction";
		String queryItemPrice = "SELECT SUM(price) FROM auction_house.item WHERE id_item IN (";
		String queryAuction = "INSERT INTO auction_house.auction (id_user, starting_bid, bid_increment, closing_date, auction_state) VALUES (?,?,?,?,'opened')";
		String queryIdAuction = "SELECT MAX(id_auction) FROM auction_house.auction";
		String queryUpdateItem = "UPDATE auction_house.item SET id_auction = ?, item_state = 'auctioned' WHERE id_item IN (";
		
		for(int i = 0; i < ids_item.length; i++) {
			if(i > 0) {
				queryItemPrice += ",";
				queryUpdateItem += ",";
			}
			queryItemPrice += "?";
			queryUpdateItem += "?";
		}
		queryItemPrice += ")";
		queryUpdateItem += ")";
		
		PreparedStatement psItemPrice = null;
		PreparedStatement psAuction = null;
		PreparedStatement psIdAuction = null;
		PreparedStatement psUpdateItem = null;
		ResultSet rsItem = null;
		ResultSet rsIdAuction = null;
				
		try {
			conn.setAutoCommit(false);
			
			psItemPrice = conn.prepareStatement(queryItemPrice);
			psItemPrice.setInt(1, id_auction);
			for(int i = 0; i < ids_item.length; i++) {
				psItemPrice.setInt(i + 1, ids_item[i]);
			}
			rsItem = psItemPrice.executeQuery();
			while(rsItem.next()) {
				starting_bid = rsItem.getInt("SUM(price)");
			}
			
			psAuction = conn.prepareStatement(queryAuction);
			psAuction.setInt(1, id_user);
			psAuction.setInt(2, starting_bid);
			psAuction.setInt(3, bid_increment);
			psAuction.setDate(4, new java.sql.Date(closing_date.getTime()));
			psAuction.executeUpdate();
			
			psIdAuction = conn.prepareStatement(queryIdAuction);
			rsIdAuction = psIdAuction.executeQuery();
			
			while(rsIdAuction.next()) {
				id_auction = rsIdAuction.getInt("MAX(id_auction)");
			}
			
			psUpdateItem = conn.prepareStatement(queryUpdateItem);
			psUpdateItem.setInt(1, id_auction);
			for(int i = 0; i < ids_item.length; i++) {
				psUpdateItem.setInt(i+2, ids_item[i]);
			}
			psUpdateItem.executeUpdate();
			
			conn.commit();
			
		} catch(SQLException e) {
			conn.rollback();
			throw new SQLException("Error accessing the DB when " + performedAction);
		} finally {
			conn.setAutoCommit(true);
			try {
				rsItem.close();
				rsIdAuction.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the result set when " + performedAction);
			}
			try {
				psItemPrice.close();
				psAuction.close();
				psIdAuction.close();
				psUpdateItem.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the statement when " + performedAction);
			}
		}
	}

	public Auction getAuctionDetailsByAuctionId(int id_auction) throws SQLException{
		Auction auction = null;
		
		String performedAction = " finding auction by id";
		String query = "SELECT * FROM auction_house.auction WHERE id_auction = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()){
				auction = new Auction();
				auction.setId_user(rs.getInt("id_user"));
				auction.setId_auction(rs.getInt("id_auction"));
				auction.setStarting_bid(rs.getInt("starting_bid"));
				auction.setBid_increment(rs.getInt("bid_increment"));
				auction.setClosing_date(new Date(rs.getTimestamp("closing_date").getTime()));
				auction.setAuction_state(rs.getString("auction_state"));
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
		
		return auction;
	}
	
	public boolean isMyAuction(int id_user, int id_auction) throws SQLException{
		boolean isMine = false;
		
		String performedAction = " checking if auction owner is correct";
		String query = "SELECT * FROM auction_house.auction WHERE id_user = ? AND id_auction = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			ps.setInt(2, id_auction);
			rs = ps.executeQuery();
			
			isMine = rs.next();
			
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
		
		return isMine;
	}

	public void closeAuction(int id_auction) throws SQLException{
		String performedAction = " closing an auction by auction id";
		String queryBid = "SELECT * FROM auction_house.bid WHERE id_auction = ?";
		String queryAuction = "UPDATE auction_house.auction SET auction_state = 'closed' WHERE id_auction = ?";
		String queryItem = "UPDATE auction_house.item SET item_state = ? WHERE id_auction = ?";
		
		PreparedStatement psBid = null;
		PreparedStatement psAuction = null;
		PreparedStatement psItem = null;
		
		ResultSet rs = null;
		
		try {
			conn.setAutoCommit(false);
			
			psBid = conn.prepareStatement(queryBid);
			psBid.setInt(1, id_auction);
			rs = psBid.executeQuery();
			
			psAuction = conn.prepareStatement(queryAuction);
			psAuction.setInt(1, id_auction);
			psAuction.executeUpdate();
			
			psItem = conn.prepareStatement(queryItem);
			if(rs.next()) {
				psItem.setString(1, "sold");
			} else {
				psItem.setString(1, "not_auctioned");
			}			
			psItem.setInt(2, id_auction);
			psItem.executeUpdate();
			
			conn.commit();
		} catch(SQLException e) {
			conn.rollback();
			throw new SQLException("Error accessing the DB when " + performedAction);
		} finally {
			conn.setAutoCommit(true);
			try {
				rs.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the result set when " + performedAction);
			}
			try {
				psAuction.close();
				psItem.close();
			} catch(Exception e) {
				throw new SQLException("Error closing the statement when " + performedAction);
			}
		}
		
	}
	
	public List<Auction> searchByKeyword(String keyword) throws SQLException {
		List<Auction> auctions = new ArrayList<>();
		String performedAction = "finding auction containing keyword";
		String query = "SELECT DISTINCT i.id_auction, a.closing_date FROM auction_house.item AS i JOIN auction_house.auction AS a ON i.id_auction = a.id_auction WHERE a.auction_state = 'opened' AND name like ? OR  description LIKE ? ORDER BY a.closing_date DESC";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setString(1, "%"+keyword+"%");
			ps.setString(2, "%"+keyword+"%");
	
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Auction auction = new Auction();
				auction.setId_auction(rs.getInt("id_auction"));
				auctions.add(auction);
			}
			
		}catch(SQLException e) {
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
		
		return auctions;
	}

	public int getBidIncrementByAuctionId(int id_auction) throws SQLException{
		int bid_increment = 0;
		
		String performedAction = " finding bid increment by auction id";
		String query = "SELECT bid_increment FROM auction_house.auction WHERE id_auction = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				bid_increment = rs.getInt("bid_increment");
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
		
		return bid_increment;
	}
	
	public int getStartingBidByAuctionId(int id_auction) throws SQLException{
		int starting_bid = 0;
		
		String performedAction = " finding starting bid by auction id";
		String query = "SELECT starting_bid FROM auction_house.auction WHERE id_auction = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				starting_bid = rs.getInt("starting_bid");
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
		
		return starting_bid;
	}

	public boolean isClosed(int id_auction) throws SQLException{
		boolean isClosed = false;
		
		String performedAction = " checking if the auction is closed";
		String query = "SELECT * FROM auction_house.auction WHERE id_auction = ? AND auction_state = 'closed'";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				isClosed = true;
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
		
		return isClosed;
	}

	public boolean canBeClosed(int id_auction) throws SQLException{
		boolean canBeClosed = false;
		Date currentDate = new Date();
		
		String performedAction = " checking if the auction can be closed";
		String query = "SELECT closing_date FROM auction_house.auction WHERE id_auction = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Date closingDate = rs.getDate("closing_date");
				System.out.println(closingDate);
				System.out.println(currentDate);
				if(currentDate.after(closingDate)) {
					System.out.println("Dentro if");
					canBeClosed = true;
				}
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
		
		System.out.println(canBeClosed);
		return canBeClosed;
	}
	
	public boolean exists(int id_auction) throws SQLException{
		boolean exists = false;
		
		String performedAction = " checking if the auction exists";
		String query = "SELECT * FROM auction_house.auction WHERE id_auction = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				exists = true;
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
		
		return exists;
	}
	
	public String getStillOpenedAuction(int[] ids_auction) throws SQLException{
		String newIds_auction = null;
		
		String performedAction = " getting the auction still opened";
		String query = "SELECT GROUP_CONCAT(id_auction) FROM auction_house.auction WHERE auction_state = 'opened' AND id_auction IN (";
				
		
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
				newIds_auction = rs.getString("GROUP_CONCAT(id_auction)");
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
		return newIds_auction;
	}
}
