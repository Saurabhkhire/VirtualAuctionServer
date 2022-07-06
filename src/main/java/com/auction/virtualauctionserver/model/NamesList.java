package com.auction.virtualauctionserver.model;

import java.util.ArrayList;

public class NamesList extends RoomInfo {

	private ArrayList<String> namesList;
	private ArrayList<String> priceList;

	public ArrayList<String> getNamesList() {
		return namesList;
	}

	public void setNamesList(ArrayList<String> namesList) {
		this.namesList = namesList;
	}

	public ArrayList<String> getPriceList() {
		return priceList;
	}

	public void setPriceList(ArrayList<String> priceList) {
		this.priceList = priceList;
	}

}
