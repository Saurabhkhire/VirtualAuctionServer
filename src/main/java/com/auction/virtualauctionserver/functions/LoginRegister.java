package com.auction.virtualauctionserver.functions;

import java.sql.Connection;
import com.auction.virtualauctionserver.model.Login;
import com.auction.virtualauctionserver.model.OtpInfo;
import com.auction.virtualauctionserver.model.Register;
import com.auction.virtualauctionserver.model.ResponseMessage;
import com.auction.virtualauctionserver.model.UpdateDetails;
import com.auction.virtualauctionserver.model.Username;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

public class LoginRegister {

	public static Register login(Login login) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		Register register = new Register();

		try {

			con = auctionService.getConnection(false);

			try {

				String loginMessage = auctionService.login(con, login);
				register = auctionService.getUserDetails(con, login.getUsername());
				register.setMessage(loginMessage);

				con.commit();
				// Thread.sleep(7000);

			} catch (Exception exception) {
				register.setMessage(Constants.COMMON_ERROR_MESSAGE);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			register.setMessage(Constants.COMMON_ERROR_MESSAGE);
		}

		return register;
	}

	public static ResponseMessage register(Register register) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				boolean userExist = auctionService.userExistsCheck(con, register.getUsername(), Constants.I_USERNAME);
				boolean phoneNumberExist = auctionService.userExistsCheck(con, register.getEmailId(),
						Constants.I_EMAIL_ID);
				
				
				if (register.getEmailId().equals("") || register.getEmailId().equals("")) {
					phoneNumberExist = false;
				}

				if (userExist) {
					responseMessage.setMessage(Constants.USERNAME_EXIST_MESSAGE);
				} else if (!userExist && phoneNumberExist) {
					responseMessage.setMessage(Constants.EMAIL_ID_EXIST_MESSAGE);
				} else {
					auctionService.register(con, register);
					responseMessage.setMessage(Constants.USER_REGISTERED);
				}

				con.commit();

			} catch (Exception exception) {
				exception.printStackTrace();
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
		}

		return responseMessage;

	}

	public static ResponseMessage updateUserDetails(UpdateDetails updateDetails) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				boolean userExist = auctionService.userExistsCheck(con, updateDetails.getUsername(),
						Constants.I_USERNAME);
				boolean phoneNumberExist = auctionService.userExistsCheck(con, updateDetails.getEmailId(),
						Constants.I_EMAIL_ID);

				if (userExist && !updateDetails.getUsername().equals(updateDetails.getOldUsername())) {
					responseMessage.setMessage(Constants.USERNAME_EXIST_MESSAGE);
				} else if (!userExist && phoneNumberExist
						&& !updateDetails.getEmailId().equals(updateDetails.getOldEmailId())) {
					responseMessage.setMessage(Constants.EMAIL_ID_EXIST_MESSAGE);
				} else {
					auctionService.updateUserDetails(con, updateDetails);
					responseMessage.setMessage(Constants.USER_DETAILS_UPDATED);
				}

				con.commit();

			} catch (Exception exception) {
				exception.printStackTrace();
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
		}

		return responseMessage;
	}

	public static ResponseMessage deleteUser(Username username) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				auctionService.deleteUser(con, username.getUsername());
				// responseMessage.setMessage(Constants.USER_DELETED);
				responseMessage.setMessage(Constants.OK_MESSAGE);

				con.commit();

			} catch (Exception exception) {
				exception.printStackTrace();
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
		}

		return responseMessage;
	}

	public static Username getUserByPhoneNumber(Register register) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		Username username = new Username();

		try {

			con = auctionService.getConnection(false);

			try {

				boolean emailIdExist = auctionService.userExistsCheck(con, register.getEmailId(), Constants.I_EMAIL_ID);

				if (!emailIdExist) {
					username.setMessage(Constants.EMAIL_ID_INCORRECT_MESSAGE);
				} else {
					String userName = auctionService.getUserNameByPhoneNumber(con, register.getEmailId());
					username.setUsername(userName);
					username.setMessage(Constants.OK_MESSAGE);
				}

				con.commit();

			} catch (Exception exception) {
				username.setMessage(Constants.COMMON_ERROR_MESSAGE);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			username.setMessage(Constants.COMMON_ERROR_MESSAGE);
		}

		return username;

	}

	public static ResponseMessage generateOtp(OtpInfo otpInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				String check = "";

				if (!otpInfo.getOtpOperationType().equals(Constants.I_RESET_PASSWORD)) {

					boolean userExist = auctionService.userExistsCheck(con, otpInfo.getUsername(),
							Constants.I_USERNAME);
					boolean phoneNumberExist = auctionService.userExistsCheck(con, otpInfo.getEmailId(),
							Constants.I_EMAIL_ID);

					if (userExist) {
						if (otpInfo.getOtpOperationType().equals(Constants.I_UPDATE_DETAILS)) {
							if (!otpInfo.getUsername().equals(otpInfo.getOldUsername())) {
								check = Constants.USERNAME_EXIST_MESSAGE;
							}
						} else {
							check = Constants.USERNAME_EXIST_MESSAGE;
						}
					} else if (phoneNumberExist) {
						if (otpInfo.getOtpOperationType().equals(Constants.I_UPDATE_DETAILS)) {
							if (!otpInfo.getEmailId().equals(otpInfo.getOldEmailId())) {
								check = Constants.EMAIL_ID_EXIST_MESSAGE;
							}
						} else {
							check = Constants.EMAIL_ID_EXIST_MESSAGE;
						}
					} else {
						check = Constants.OK_MESSAGE;
					}

				} else {

					check = auctionService.checkUserAndPhoneNumber(con, otpInfo);

				}

				if (check.equals(Constants.OK_MESSAGE)) {

					int otp = Integer.parseInt(Functions.getRandomNumberString());

					Functions.sendOtpEmail(otpInfo.getEmailId(), otp);
					
					if (otpInfo.getOtpOperationType().equals(Constants.I_REGISTER)) {
						
						auctionService.insertTempOtp(con, otpInfo.getUsername(), otp);
						
					} else {

					auctionService.updateOtp(con, otpInfo.getUsername(), otp);
					
					}

					responseMessage.setMessage(Constants.OK_MESSAGE);
				} else {
					responseMessage.setMessage(check);
				}

				con.commit();

			} catch (Exception exception) {
				exception.printStackTrace();
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
		}

		return responseMessage;

	}

	public static ResponseMessage verifyOtp(OtpInfo otpInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				boolean otpExist = auctionService.otpExistsCheck(con, otpInfo.getUsername(), otpInfo.getOtp(), otpInfo.getOtpOperationType());

				if (!otpExist) {
					responseMessage.setMessage(Constants.WRONG_OTP_MESSAGE);
				} else {
					responseMessage.setMessage(Constants.OK_MESSAGE);
				}
				
				auctionService.deleteTempOtp(con, otpInfo.getUsername());

				con.commit();

			} catch (Exception exception) {
				exception.printStackTrace();
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
		}

		return responseMessage;

	}

	public static ResponseMessage resetPassword(Login login) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				auctionService.updatePassword(con, login.getUsername(), login.getPassword());

				responseMessage.setMessage(Constants.PASSWORD_RESET);

				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
		}

		return responseMessage;

	}
}
