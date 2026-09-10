package packets;

import java.util.ArrayList;
import java.util.List;

import beans.Bid;
import beans.Item;

public class PacketAuctionBids {
	List<PacketItem> items = new ArrayList<>();
	List<PacketBid> bids = new ArrayList<>();
	int minOffer;
	
	public PacketAuctionBids(List<Item> items, List<Bid> bids, int minOffer) {
		for(Item item : items) {
			this.items.add(new PacketItem(item));
		}
		
		for(Bid bid : bids) {
			this.bids.add(new PacketBid(bid));
		}
		
		this.minOffer = minOffer;
	}
}
