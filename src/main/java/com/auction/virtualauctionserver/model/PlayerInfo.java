package com.auction.virtualauctionserver.model;

public class PlayerInfo extends AuctionInfo {

	private int playerId;
	private String playerName;
	private String playerCountry;
	private String playerRole;
	private String battingStyle;
	private String bowlingStyle;
	private String battingPosition;
	private int totalPriceinLakhs;

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerCountry() {
		return playerCountry;
	}

	public void setPlayerCountry(String playerCountry) {
		this.playerCountry = playerCountry;
	}

	public String getPlayerRole() {
		return playerRole;
	}

	public void setPlayerRole(String playerRole) {
		this.playerRole = playerRole;
	}

	public String getBattingStyle() {
		return battingStyle;
	}

	public void setBattingStyle(String battingStyle) {
		this.battingStyle = battingStyle;
	}

	public String getBowlingStyle() {
		return bowlingStyle;
	}

	public void setBowlingStyle(String bowlingStyle) {
		this.bowlingStyle = bowlingStyle;
	}

	public String getBattingPosition() {
		return battingPosition;
	}

	public void setBattingPosition(String battingPosition) {
		this.battingPosition = battingPosition;
	}

	public int getTotalPriceinLakhs() {
		return totalPriceinLakhs;
	}

	public void setTotalPriceinLakhs(int totalPriceinLakhs) {
		this.totalPriceinLakhs = totalPriceinLakhs;
	}

}
