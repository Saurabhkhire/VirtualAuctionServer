package com.auction.virtualauctionserver.model;

public class RoomStatus extends RoomInfo {

	private String roomStatus;
	private String hostName;
	
	public String getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(String roomStatus) {
		this.roomStatus = roomStatus;
	}
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
}
