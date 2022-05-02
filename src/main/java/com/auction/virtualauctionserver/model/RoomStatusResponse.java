package com.auction.virtualauctionserver.model;

public class RoomStatusResponse extends AuctionParams {

	private String usernameslist;
	private String teamslist;
	private String host;
	
	public String getUsernameslist() {
		return usernameslist;
	}
	public void setUsernameslist(String usernameslist) {
		this.usernameslist = usernameslist;
	}
	public String getTeamslist() {
		return teamslist;
	}
	public void setTeamslist(String teamslist) {
		this.teamslist = teamslist;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}


}
