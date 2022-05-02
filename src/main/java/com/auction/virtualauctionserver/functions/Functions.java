package com.auction.virtualauctionserver.functions;

import java.sql.Connection;
import java.sql.SQLException;

import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

public class Functions {

	public static int calculatePrice(int oldPrice) {

		int totalPrice = 0;

		int length = String.valueOf(oldPrice).length();
		if (length == 1) {

			totalPrice = oldPrice + 1;

		} else if (length == 2) {

			totalPrice = oldPrice + 5;

		} else if (length == 3 || length==4) {

			if (oldPrice < 500) {

				if (oldPrice == 150) {
					totalPrice = oldPrice + 10;
				} else {
					totalPrice = oldPrice + 20;
				}

			} else {

				totalPrice = oldPrice + 50;

			}
		}

		return totalPrice;
	}

}
