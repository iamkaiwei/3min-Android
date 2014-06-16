package com.threemin.model;

public class FilterModel {
	
	private int filterID;
	private String maxPrice;
	private String minPrice;
	public int getFilterID() {
		return filterID;
	}
	public void setFilterID(int filterID) {
		this.filterID = filterID;
	}
	public String getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}
	public String getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}
	
}
