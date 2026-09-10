package packets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PacketAuction {
	int id_auction;
	int price;
	int starting_bid;
	int bid_increment;
	Date closing_date;
	long millisec;
	List<PacketItem> items = new ArrayList<>();
	
	
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
	
	public int getId_auction() {
		return id_auction;
	}
	public void setId_auction(int id_auction) {
		this.id_auction = id_auction;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public List<PacketItem> getItems() {
		return items;
	}
	public void setItems(List<PacketItem> items) {
		this.items = items;
	}
	public void addItem(PacketItem item) {
		items.add(item);
	}
	public void setMillisec(Date closing_date) {
		this.millisec = closing_date.getTime();
	}
	
}
