package packets;

import java.util.Date;

import beans.Bid;

public class PacketBid {
	int id_user;
	int price;
	Date date;
	
	public PacketBid(Bid bid) {
		this.id_user = bid.getId_user();
		this.price = bid.getPrice();
		this.date = bid.getDate();
	}
}
