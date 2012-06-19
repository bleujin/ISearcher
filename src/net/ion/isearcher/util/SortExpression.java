package net.ion.isearcher.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.StringUtil;
import net.ion.isearcher.common.MyField;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.SortField;

public class SortExpression {

	private static String[] KEYWORD_FIELD = new String[]{"_doc", "_score"};
	private static String[] ORDER = new String[]{"asc", "desc"};
	private static Map<String, Integer> ORDER_ENCODING = new HashMap<String, Integer>() ;
	static {
		ORDER_ENCODING.put("_string", SortField.STRING) ;
		ORDER_ENCODING.put("_doc", SortField.DOC) ;
		ORDER_ENCODING.put("_score", SortField.SCORE) ;
		ORDER_ENCODING.put("_number", SortField.DOUBLE) ;
	}
	
	public static SortField[] parse(String _str) {
		if (StringUtil.isBlank(_str)) return new SortField[]{SortField.FIELD_SCORE} ;
		
		String str = _str.toLowerCase() ;
		String[] fields = StringUtil.split(str, ",") ;
		
		List<SortField> result = new ArrayList<SortField>() ; 
		for(String field : fields){
			if (StringUtil.isBlank(field)) continue ;
			
			String[] sps = StringUtil.split(field) ;

			String fieldName = sps[0] ; // mandatory
			int order_encoding = SortField.STRING ;
			boolean isRerverse = false ;
			
			if (ArrayUtils.contains(KEYWORD_FIELD, fieldName) && sps.length == 1) {
				result.add( ("_doc".equals(fieldName)) ? SortField.FIELD_DOC : SortField.FIELD_SCORE ) ;
			} else {
				if (sps.length == 2) {
					if (ORDER_ENCODING.containsKey(sps[1])) {
						Integer enc = ORDER_ENCODING.get(sps[1]) ;
						order_encoding = (enc == null) ? SortField.STRING : enc.intValue() ;
					}
					if (ArrayUtils.contains(ORDER, sps[1])) {
						isRerverse = "desc".equals(sps[1]) ;
					}
				}
				if (sps.length == 3){
					Integer enc = ORDER_ENCODING.get(sps[1]) ;
					order_encoding = (enc == null) ? SortField.STRING : enc.intValue() ;
					isRerverse = "desc".equals(sps[2]) ;
				}
				result.add(new SortField(MyField.makeSortFieldName(fieldName), order_encoding, isRerverse)) ;
			}
		}
		
		return (SortField[])result.toArray(new SortField[0]) ;
	}
}
