package com.auction.virtualauctionserver.functions;

import com.auction.virtualauctionserver.model.RoomInfo;

public class AuctionRunning implements Runnable {

	private RoomInfo roomInfo;

	public AuctionRunning(RoomInfo roomInfo) {
		this.roomInfo = roomInfo;
	}

	public void run() {

		try {
			Auction.auction(roomInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
