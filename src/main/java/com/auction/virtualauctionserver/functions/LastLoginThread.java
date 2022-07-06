package com.auction.virtualauctionserver.functions;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

public class LastLoginThread implements Runnable {

	private String roomId;

	public LastLoginThread(String roomId) {
		this.roomId = roomId;
	}

	public void run() {

		try {
			Auction.lastLogin(roomId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}