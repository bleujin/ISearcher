package net.ion.nsearcher.util;

import net.ion.framework.db.Page;
import net.ion.framework.util.StringUtil;

public class PageOutPut {
	private final String linkName;
	private final Page page;
	private final int rowCount;

	public PageOutPut(String linkName, int rowCount, Page page) {
		this.linkName = linkName;
		this.page = page;
		this.rowCount = rowCount;
	}

	public String printPagePrev(String head, String body, String repbody, String tail, String forwardName) {
		String result = "";

		result += head;
		if (page.getCurrentScreen() > 1) {
			result += "<a href=\"javascript:" + linkName + "." + "goPreScreen(" + page.getPreScreenEndPageNo() + (StringUtil.isBlank(forwardName) ? "" : ", '" + forwardName + "'") + ")\">" + body + "</a>";
		} else {
			result += repbody;
		}
		result += tail;

		return result;
	}

	public String printPageNext(String head, String body, String repbody, String tail, String forwardName) {
		String result = "";

		result += head;
		// if(listNum * ((screen + 1 ) * ScreenCount) < rowCount) {
		//if(listNum * ScreenCount < rowCount) {
		if (page.getScreenCount() * page.getListNum() < rowCount ) {
			result += " <a href=\"javascript:" + linkName + "." + "goNextScreen(" + page.getNextScreenStartPageNo() + (StringUtil.isBlank(forwardName) ? "" : ", '" + forwardName + "'") + ")\">" + body + "</a>";
		} else {
			result += repbody;
		}
		result += tail;

		return result;
	}

	public String printPageNoList() {
		return printPageNoList(null);
	}

	public String printPageNoList(String forwardName) {
		String _forwardName = "";
		if(StringUtil.isNotBlank(forwardName)) {
			_forwardName = ", '" + forwardName + "'";
		}

		StringBuffer rtnValue = new StringBuffer("");
/*
		if (page.getPageNo() > 1)
			rtnValue.append("<a id=\"n_prev_page\" href=\"javascript:link.goListPage(" + (page.getPageNo() - 1) + _forwardName + ")\"></a>");
		if (page.getPageNo() < page.getMaxPageNo(rowCount))
			rtnValue.append("<a id=\"n_next_page\" href=\"javascript:link.goListPage(" + (page.getPageNo() + 1) + _forwardName + ")\"></a>");
*/
		for (int startPage = page.getMinPageNoOnScreen(), endPage = Math.min(page.getMaxPageNo(rowCount), page.getMaxPageNoOnScreen()); startPage <= endPage; startPage++) {
			if (startPage == page.getPageNo())
				rtnValue.append(" <li class='num'><a class='current'>" + startPage + "</a></li>");
//				rtnValue.append(" <a style='padding: 0 3px 0 3px;' class='current'>" + startPage + "</a>");
			else
				rtnValue.append(" <li class='num'><a href=\"javascript:" + linkName + "." + "goListPage(" + startPage + _forwardName + ")\">" + startPage + "</a></li>");
//				rtnValue.append(" <a style='padding: 0 3px 0 3px;' href=\"javascript:" + linkName + "." + "goListPage(" + startPage + _forwardName + ")\">" + startPage + "</a>");
		}

		return rtnValue.toString();
	}

	public boolean isValidPage() {
		if (page.getPageNo() > 1 && rowCount == 0) {
			return false;
		}
		return true;
	}


}
