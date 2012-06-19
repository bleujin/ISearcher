package net.ion.isearcher.impl;

public class HashBean {

	private String idValue;
	private String bodyValue;

	HashBean(String idValue, String bodyValue) {
		this.idValue = idValue;
		this.bodyValue = bodyValue;
	}

	public boolean isSameId(String thatValue) {
		return (idValue != null) && idValue.equals(thatValue);
	}
	
	public String getBodyValue(){
		return bodyValue ;
	}
	
}
