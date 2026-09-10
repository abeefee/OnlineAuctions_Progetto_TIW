package packets;

import java.util.ArrayList;
import java.util.List;

import beans.Bid;

public class PacketBids {
	List<PacketBid> bids = new ArrayList<>();
	
	public PacketBids(List<Bid> bids) {
		for(Bid bid : bids) {
			this.bids.add(new PacketBid(bid));
		}
	}
	
	public List<PacketBid> getBids(){
		return bids;
	}
}
