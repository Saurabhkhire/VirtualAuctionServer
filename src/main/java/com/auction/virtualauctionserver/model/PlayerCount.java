package com.auction.virtualauctionserver.model;

public class PlayerCount extends PlayerInfo{

	private int budgetInLakhs;
	private double budgetInCrores;
	private int batsman;
	private int wicketKeepers;
	private int allRounders;
	private int fastBowlers;
	private int spinBowlers;
	private int foreigners;
	private int total;

	public int getBudgetInLakhs() {
		return budgetInLakhs;
	}

	public void setBudgetInLakh(int budget) {
		this.budgetInLakhs = budget;
	}

	public int getBatsman() {
		return batsman;
	}

	public void setBatsman(int batsman) {
		this.batsman = batsman;
	}

	public int getWicketKeepers() {
		return wicketKeepers;
	}

	public void setWicketKeepers(int wicketKeepers) {
		this.wicketKeepers = wicketKeepers;
	}

	public int getAllRounders() {
		return allRounders;
	}

	public void setAllRounders(int allRounders) {
		this.allRounders = allRounders;
	}

	public int getFastBowlers() {
		return fastBowlers;
	}

	public void setFastBowlers(int fastBowlers) {
		this.fastBowlers = fastBowlers;
	}

	public int getSpinBowlers() {
		return spinBowlers;
	}

	public void setSpinBowlers(int spinBowlers) {
		this.spinBowlers = spinBowlers;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getForeigners() {
		return foreigners;
	}

	public void setForeigners(int foreigners) {
		this.foreigners = foreigners;
	}

	public double getBudgetInCrores() {
		return budgetInCrores;
	}

	public void setBudgetInCrores(double budgetInCrores) {
		this.budgetInCrores = budgetInCrores;
	}
}
