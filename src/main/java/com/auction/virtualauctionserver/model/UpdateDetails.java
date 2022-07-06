package com.auction.virtualauctionserver.model;

public class UpdateDetails extends Register {

	private String oldUsername;
	private String oldPassword;
	private String oldEmailId;
	
	public String getOldUsername() {
		return oldUsername;
	}
	public void setOldUsername(String oldUsername) {
		this.oldUsername = oldUsername;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getOldEmailId() {
		return oldEmailId;
	}
	public void setOldEmailId(String EmailId) {
		this.oldEmailId = EmailId;
	}

}
