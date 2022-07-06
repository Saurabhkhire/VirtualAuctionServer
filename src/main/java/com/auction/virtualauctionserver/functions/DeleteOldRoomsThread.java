package com.auction.virtualauctionserver.functions;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

public class DeleteOldRoomsThread implements Runnable {

	private String roomId;

	public DeleteOldRoomsThread(String roomId) {
		this.roomId = roomId;
	}

	public void run() {

		try {
			Auction.deleteOldRooms();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}