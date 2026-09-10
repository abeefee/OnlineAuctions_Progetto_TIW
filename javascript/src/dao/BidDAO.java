package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beans.Bid;

public class BidDAO {
	private Connection conn;

	public BidDAO(Connection conn) {
		this.conn = conn;
	}
	
	public List<Bid> getBidsByAuctionId(int id_auction) throws SQLException{
		List<Bid> bids = new ArrayList<>();
		
		String performedAction = " finding bids by auction id";
		String query = "SELECT * FROM auction_house.bid WHERE id_auction = ? ORDER BY timestamp DESC";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Bid bid = new Bid();
				bid.setId_user(rs.getInt("id_user"));
				bid.setId_auction(rs.getInt("id_auction"));
				bid.setDate(new Date(rs.getTimestamp("timestamp").getTime()));
				bid.setPrice(rs.getInt("price"));
				bids.add(bid);
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
		
		return bids;
	}
	
	public void registerBid(int id_user, int id_auction, int price) throws SQLException{
		String performedAction = " creating a new bid";
		String query = "INSERT INTO auction_house.bid (id_user, id_auction, price) VALUES (?,?,?)";
		
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			ps.setInt(2, id_auction);
			ps.setInt(3, price);
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
	
	public int getBuyerIdByAuctionId(int id_auction) throws SQLException {
		int id_user = 0;
		
		String performedAction = " creating a new bid";
		String query = "SELECT id_user FROM auction_house.bid WHERE id_auction = ? AND price = (SELECT MAX(price) FROM auction_house.bid WHERE id_auction = ?)";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			ps.setInt(2, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				id_user = rs.getInt("id_user");
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
		
		return id_user;
	}

	public List<Bid> getAwardedAuctionsByUserId(int id_user) throws SQLException{
		List<Bid> bids = new ArrayList<>();
		
		String performedAction = " finding awarded auctions by user id";
		String query = "SELECT b.id_auction, b.price FROM auction_house.bid AS b JOIN auction_house.auction AS a ON b.id_auction = a.id_auction WHERE a.auction_state = 'closed' AND price IN (SELECT MAX(price) FROM auction_house.bid GROUP BY id_auction) AND b.id_user = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_user);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Bid bid = new Bid();
				bid.setId_auction(rs.getInt("id_auction"));
				bid.setPrice(rs.getInt("price"));
				bids.add(bid);
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
		
		return bids;
	}
	
	public int getMaxBidByAuctionId(int id_auction) throws SQLException {
		int maxBid = 0;
		
		String performedAction = " finding max offer of an auction";
		String query = "SELECT MAX(price) FROM auction_house.bid WHERE id_auction = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, id_auction);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				maxBid = rs.getInt("MAX(price)");
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
		
		return maxBid;
	}
}
