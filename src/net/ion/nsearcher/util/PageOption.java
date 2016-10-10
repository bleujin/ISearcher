package net.ion.nsearcher.util;

import net.ion.framework.db.Page;
import net.ion.framework.util.StringUtil;

public class PageOption {

	private String linkName = "link";
	private String strDepth = "/";
	private String forwardName = "list";
	private boolean showListNum = true ;
	private String listCount = "list count" ;
	private String countUnit = " unit" ;
	
	public final static PageOption DEFAULT = new PageOptionBuilder().build() ;
	
	public PageOption(String linkName, String strDepth, String forwardName, boolean showListNum, String listCount, String countUnit) {
		this.linkName = linkName ;
		this.strDepth = strDepth ;
		this.forwardName = forwardName ;
		this.showListNum = showListNum ;
		this.listCount = listCount ;
		this.countUnit = countUnit ;
	}
	
	
	public String toHtml(int rowcount, Page page) {
		String goForwardName = StringUtil.isBlank(forwardName) ? "list" : forwardName;
		PageOutPut pageout = new PageOutPut(linkName, rowcount, page);
		if (pageout.isValidPage()) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("<div class='page-box'>");
			
			buffer.append("<ol class='pagelist'>");
			buffer.append(pageout.printPagePrev("<li>", "<img src='" + strDepth + "common/button/list_prev.gif' border='0'>", "<img src='" + strDepth + "common/button/list_prev.gif' border='0'>", "</li>", forwardName));
			buffer.append(pageout.printPageNoList(goForwardName));
			buffer.append("<li></li>");
			buffer.append(pageout.printPageNext("<li>", "<img src='" + strDepth + "common/button/list_next.gif' border='0'>", "<img src='" + strDepth + "common/button/list_next.gif' border='0'>", "</li>", forwardName));
			buffer.append("</ol>");
			
			if(showListNum) {
				buffer.append("<p class='page-num'>" + listCount + " : <input name='listNum' size='5' maxlength='5' onchange='link.changeListNum()' onkeypress='onlyNumber(event)' value='" + page.getListNum() + "'/> "+ countUnit + "</p>");
			}
			
			buffer.append("</div>");
			
			
			return buffer.toString();
		} else {
			return "<script type=\"text/javascript\">link.goListPage(link.getSelfForm().pageNo.value - 1, '" + goForwardName + "' )</script>";
		}
	}
	
	
	
	
	
}
