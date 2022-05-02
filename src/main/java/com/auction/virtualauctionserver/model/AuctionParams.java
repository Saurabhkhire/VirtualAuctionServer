package com.auction.virtualauctionserver.model;

public class AuctionParams extends Team {

	private int maxForeigners;
	private int minTotal;
	private int maxTotal;
	private int maxBudget;
	
	public int getMaxForeigners() {
		return maxForeigners;
	}
	public void setMaxForeigners(int maxForeigners) {
		this.maxForeigners = maxForeigners;
	}
	public int getMinTotal() {
		return minTotal;
	}
	public void setMinTotal(int minTotal) {
		this.minTotal = minTotal;
	}
	public int getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	public int getMaxBudget() {
		return maxBudget;
	}
	public void setMaxBudget(int maxBudget) {
		this.maxBudget = maxBudget;
	}


}
