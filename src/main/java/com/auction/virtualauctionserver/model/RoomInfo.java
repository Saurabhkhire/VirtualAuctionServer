package com.auction.virtualauctionserver.model;

public class RoomInfo extends Username {

	private String roomId;
	private String roomPassword;
	private String visibility;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomPassword() {
		return roomPassword;
	}

	public void setRoomPassword(String roomPassword) {
		this.roomPassword = roomPassword;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}


}
