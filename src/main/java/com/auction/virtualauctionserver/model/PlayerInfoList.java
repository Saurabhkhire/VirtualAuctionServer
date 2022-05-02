package com.auction.virtualauctionserver.model;

import java.util.ArrayList;

public class PlayerInfoList extends PlayerCount {

	private ArrayList<String> playerNameList;
	private ArrayList<String> playerCountryList;
	private ArrayList<String> playerRoleList;
	private ArrayList<String> battingStyleList;
	private ArrayList<String> bowlingStyleList;
	private ArrayList<String> battingPositionList;
	private ArrayList<String> priceinLakhsList;
	private ArrayList<String> priceinCroresList;

	public ArrayList<String> getPlayerNameList() {
		return playerNameList;
	}

	public void setPlayerNameList(ArrayList<String> playerNameList) {
		this.playerNameList = playerNameList;
	}

	public ArrayList<String> getPlayerCountryList() {
		return playerCountryList;
	}

	public void setPlayerCountryList(ArrayList<String> playerCountryList) {
		this.playerCountryList = playerCountryList;
	}

	public ArrayList<String> getPlayerRoleList() {
		return playerRoleList;
	}

	public void setPlayerRoleList(ArrayList<String> playerRoleList) {
		this.playerRoleList = playerRoleList;
	}

	public ArrayList<String> getBattingStyleList() {
		return battingStyleList;
	}

	public void setBattingStyleList(ArrayList<String> battingStyleList) {
		this.battingStyleList = battingStyleList;
	}

	public ArrayList<String> getBowlingStyleList() {
		return bowlingStyleList;
	}

	public void setBowlingStyleList(ArrayList<String> bowlingStyleList) {
		this.bowlingStyleList = bowlingStyleList;
	}

	public ArrayList<String> getBattingPositionList() {
		return battingPositionList;
	}

	public void setBattingPositionList(ArrayList<String> battingPositionList) {
		this.battingPositionList = battingPositionList;
	}

	public ArrayList<String> getPriceinLakhsList() {
		return priceinLakhsList;
	}

	public void setPriceinLakhsList(ArrayList<String> priceinLakhsList) {
		this.priceinLakhsList = priceinLakhsList;
	}

	public ArrayList<String> getPriceinCroresList() {
		return priceinCroresList;
	}

	public void setPriceinCroresList(ArrayList<String> priceinCroresList) {
		this.priceinCroresList = priceinCroresList;
	}

}
