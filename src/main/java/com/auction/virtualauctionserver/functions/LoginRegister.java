package com.auction.virtualauctionserver.functions;

import java.sql.Connection;

import com.auction.virtualauctionserver.model.Login;
import com.auction.virtualauctionserver.model.Register;
import com.auction.virtualauctionserver.model.ResponseMessage;
import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.model.RoomStatusResponse;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

public class LoginRegister {

	public static ResponseMessage login(Login login) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				boolean userExist = auctionService.login(con, login);
				if (userExist) {
					responseMessage.setMessage("UserExist");
				} else {
					responseMessage.setMessage("Error");
				}

			} catch (Exception ex) {
				responseMessage.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			responseMessage.setMessage("Error");
		}

		return responseMessage;
	}

	public static ResponseMessage register(Register register) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				auctionService.register(con, register);
				responseMessage.setMessage("Ok");

			} catch (Exception ex) {
				responseMessage.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			responseMessage.setMessage("Error");
		}

		return responseMessage;

	}
}
