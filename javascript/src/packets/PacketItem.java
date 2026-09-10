package packets;

import beans.Item;

public class PacketItem {
	int id_item;
	String name;
	String description;
	int price;
	String img_name;
	
	public PacketItem(Item item) {
		this.id_item = item.getId_item();
		this.name = item.getName();
		this.description = item.getDescription();
		this.price = item.getPrice();
		this.img_name = item.getImg_name();
	}
}
