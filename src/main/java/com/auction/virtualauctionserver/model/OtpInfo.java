package com.auction.virtualauctionserver.model;

public class OtpInfo extends UpdateDetails {

	private int otp;
	private String otpOperationType;

	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public String getOtpOperationType() {
		return otpOperationType;
	}

	public void setOtpOperationType(String otpOperationType) {
		this.otpOperationType = otpOperationType;
	}



}
