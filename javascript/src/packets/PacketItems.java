package packets;

import java.util.ArrayList;
import java.util.List;

import beans.Item;

public class PacketItems {
	private List<PacketItem> items = new ArrayList<>();
	
	public PacketItems(List<Item> items) {
		for(Item item : items) {
			this.items.add(new PacketItem(item));
		}
	}
	
	public List<PacketItem> getItems(){
		return items;
	}
}
