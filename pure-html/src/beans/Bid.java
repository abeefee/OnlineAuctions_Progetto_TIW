package beans;

import java.util.Date;

public class Bid {
	int id_user;
	int id_auction;
	int price;
	Date date;
	
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
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
