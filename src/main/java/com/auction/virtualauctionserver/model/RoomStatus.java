package com.auction.virtualauctionserver.model;

public class RoomStatus extends RoomInfo {

	private String roomStatus;
	private String hostName;
	private String skipType;
	private int auctionBreakMinTime;
	
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
	
	public String getSkipType() {
		return skipType;
	}

	public void setSkipType(String skipType) {
		this.skipType = skipType;
	}

	public int getAuctionBreakMinTime() {
		return auctionBreakMinTime;
	}

	public void setAuctionBreakMinTime(int auctionBreakMinTime) {
		this.auctionBreakMinTime = auctionBreakMinTime;
	}
}
