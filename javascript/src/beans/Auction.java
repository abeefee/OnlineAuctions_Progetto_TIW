package beans;

import java.util.Date;

public class Auction {
	int id_user;
	int id_auction;
	int starting_bid;
	int bid_increment;
	Date closing_date;
	String auction_state;
	
	public int getId_user() {
		return id_user;
	}
	public void setId_user(int id_user) {
		this.id_user = id_user;
	}
	public int getId_auction() {
		return id_auction;
	}
	public void setId_auction(int id_auction) {
		this.id_auction = id_auction;
	}
	public int getStarting_bid() {
		return starting_bid;
	}
	public void setStarting_bid(int starting_bid) {
		this.starting_bid = starting_bid;
	}
	public int getBid_increment() {
		return bid_increment;
	}
	public void setBid_increment(int bid_increment) {
		this.bid_increment = bid_increment;
	}
	public Date getClosing_date() {
		return closing_date;
	}
	public void setClosing_date(Date closing_date) {
		this.closing_date = closing_date;
	}
	public String getAuction_state() {
		return auction_state;
	}
	public void setAuction_state(String auction_state) {
		this.auction_state = auction_state;
	}
}
