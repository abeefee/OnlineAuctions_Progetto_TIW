package packets;

import java.util.ArrayList;
import java.util.List;

import beans.Auction;
import beans.Bid;
import beans.Item;

public class PacketAuctions {
	private List<PacketAuction> auctions = new ArrayList<>();

	public PacketAuctions(List<Auction> closedAuctions, String type) {
		for(Auction closedAuction : closedAuctions) {
			PacketAuction auction = new PacketAuction();
			
			if(type.equals("keyword")) {
				auction.setId_auction(closedAuction.getId_auction());
			} else if(type.equals("closed")) {
				auction.setId_auction(closedAuction.getId_auction());
				auction.setStarting_bid(closedAuction.getStarting_bid());
				auction.setBid_increment(closedAuction.getBid_increment());
				auction.setClosing_date(closedAuction.getClosing_date());
			} else if(type.equals("opened")) {
				auction.setId_auction(closedAuction.getId_auction());
				auction.setStarting_bid(closedAuction.getStarting_bid());
				auction.setBid_increment(closedAuction.getBid_increment());
				auction.setClosing_date(closedAuction.getClosing_date());
				auction.setMillisec(closedAuction.getClosing_date());
			}
			
			auctions.add(auction);
		}
	}
	
	public PacketAuctions(List<Bid> awardedAuctions, List<Item> items) {
		for(Bid awardedAuction : awardedAuctions) {
			PacketAuction auction = new PacketAuction();
			auction.setId_auction(awardedAuction.getId_auction());
			auction.setPrice(awardedAuction.getPrice());
			
			for(Item item : items) {
				if(awardedAuction.getId_auction() == item.getId_auction()) {
					auction.addItem(new PacketItem(item));
				}
			}
			
			auctions.add(auction);
		}
	}

	public List<PacketAuction> getAuctions() {
		return auctions;
	}
	
}
