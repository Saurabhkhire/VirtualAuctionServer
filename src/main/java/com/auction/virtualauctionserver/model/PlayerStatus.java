package com.auction.virtualauctionserver.model;

public class PlayerStatus extends PlayerCount {

	private int time;
	private String playerMessage;

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getPlayerMessage() {
		return playerMessage;
	}

	public void setPlayerMessage(String playerMessage) {
		this.playerMessage = playerMessage;
	}

}
