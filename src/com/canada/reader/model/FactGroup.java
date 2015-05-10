package com.canada.reader.model;

import java.util.List;

public class FactGroup {	
	private String groupTitle;
	private List<Fact> factList;

	public String getGroupTitle() {
		return groupTitle;
	}
	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}
	public List<Fact> getFactList() {
		return factList;
	}
	public void setFactList(List<Fact> factList) {
		this.factList = factList;
	}
}