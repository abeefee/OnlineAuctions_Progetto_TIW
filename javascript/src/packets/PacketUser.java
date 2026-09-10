package packets;

import beans.User;

public class PacketUser {

	private String username;
	private int id_user;
	private String address;
	
	public PacketUser(User user) {
		this.username = user.getUsername();
		this.address = user.getAddress();
		this.id_user = user.getId_user();
	}
	
	public PacketUser(String username, int id_user) {
		this.username = username;
		this.id_user = id_user;
	}

	public String getUsername() {
		return username;
	}

	public int getId_user() {
		return id_user;
	}	
}