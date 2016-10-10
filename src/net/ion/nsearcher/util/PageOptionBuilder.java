package net.ion.nsearcher.util;

public class PageOptionBuilder {

	private String linkName = "link";
	private String strDepth = "/";
	private String forwardName = "list";
	private boolean showListNum = true ;
	private String listCount = "list count" ;
	private String countUnit = " unit" ;
	
	public String getLinkName() {
		return linkName;
	}
	public String getStrDepth() {
		return strDepth;
	}
	public String getForwardName() {
		return forwardName;
	}
	public boolean isShowListNum() {
		return showListNum;
	}
	public String getListCount() {
		return listCount;
	}
	public String getCountUnit() {
		return countUnit;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public void setStrDepth(String strDepth) {
		this.strDepth = strDepth;
	}
	public void setForwardName(String forwardName) {
		this.forwardName = forwardName;
	}
	public void setShowListNum(boolean showListNum) {
		this.showListNum = showListNum;
	}
	public void setListCount(String listCount) {
		this.listCount = listCount;
	}
	public void setCountUnit(String countUnit) {
		this.countUnit = countUnit;
	}

	public PageOption build(){
		return new PageOption(this.linkName, this.strDepth, this.forwardName, this.showListNum, this.listCount, this.countUnit) ;
	}
	
}
