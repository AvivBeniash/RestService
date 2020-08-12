package com.example.restservice;

public class BeerBrand {

	private long id;
	private String beerName;
	private String alcohol;

	public BeerBrand(long id, String name, String alcohol) {
		this.id = id;
		this.beerName = name;
		this.alcohol = alcohol;
	}

	public long getId() {
		return id;
	}

	public String getBeerName() {
		return beerName;
	}

	public String getAlcohol() {
		return alcohol;
	}

	@Override
	public String toString() {
		return String.format(
				"BeerBrand[id=%d, beerName='%s', alcohol=%s]",
				id, beerName, alcohol);
	}
}