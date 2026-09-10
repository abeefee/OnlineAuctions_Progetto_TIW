package packets;

import java.util.ArrayList;
import java.util.List;

import beans.Auction;
import beans.Bid;
import beans.Item;
import beans.User;

public class PacketAuctionDetails {
	Auction auction; //Non uso packet perchè ho stessi parametri di auction
	List<PacketBid> bids = new ArrayList<>();
	List<PacketItem> items = new ArrayList<>();
	PacketUser buyer;
	
	public PacketAuctionDetails(Auction auction, List<Bid> bids) {
		this.auction = auction;
		for(Bid bid : bids) {
			this.bids.add(new PacketBid(bid));
		}
	}
	
	public PacketAuctionDetails(Auction auction, List<Bid> bids, List<Item> items) {
		this.auction = auction;
		for(Bid bid : bids) {
			this.bids.add(new PacketBid(bid));
		}
		for(Item item : items) {
			this.items.add(new PacketItem(item));
		}
	}
	
	public void setBuyer(User buyer) {
		this.buyer = new PacketUser(buyer);
	}
}
