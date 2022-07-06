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
	private double totalPriceInCrores;
	private String playerImage1Uri;
	private String playerImage2Uri;

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

	public String getPlayerImage1Uri() {
		return playerImage1Uri;
	}

	public void setPlayerImage1Uri(String string) {
		this.playerImage1Uri = string;
	}

	public String getPlayerImage2Uri() {
		return playerImage2Uri;
	}

	public void setPlayerImage2Uri(String playerImage2Uri) {
		this.playerImage2Uri = playerImage2Uri;
	}

	public double getTotalPriceInCrores() {
		return totalPriceInCrores;
	}

	public void setTotalPriceInCrores(double totalPriceInCrores) {
		this.totalPriceInCrores = totalPriceInCrores;
	}

}
