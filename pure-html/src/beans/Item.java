package beans;

public class Item {
	int id_user;
	int id_item;
	int id_auction;
	int price;
	String name;
	String description;
	String img_name;
	String item_state;
	
	public int getId_user() {
		return id_user;
	}
	public void setId_user(int id_user) {
		this.id_user = id_user;
	}
	public int getId_item() {
		return id_item;
	}
	public void setId_item(int id_item) {
		this.id_item = id_item;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImg_name() {
		return img_name;
	}
	public void setImg_name(String img_name) {
		this.img_name = img_name;
	}
	public String getItem_state() {
		return item_state;
	}
	public void setItem_state(String item_state) {
		this.item_state = item_state;
	}
}
