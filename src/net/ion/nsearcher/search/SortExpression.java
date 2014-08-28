package net.ion.nsearcher.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

public class SortExpression {

	private static String[] KEYWORD_FIELD = new String[]{"_doc", "_score"};
	private static String[] ORDER = new String[]{"asc", "desc"};
	private static Map<String, Type> ORDER_ENCODING = MapUtil.<Type>chainKeyMap()
		.put("_string", SortField.Type.STRING)
		.put("_doc", SortField.Type.DOC)
		.put("_score", SortField.Type.SCORE)
		.put("_number", SortField.Type.DOUBLE)
		.toMap() ;
	
	public static SortField[] parse(String _str) {
		if (StringUtil.isBlank(_str)) return new SortField[]{SortField.FIELD_SCORE} ;
		
		String str = _str.toLowerCase() ;
		String[] fields = StringUtil.split(str, ",") ;
		
		List<SortField> result = new ArrayList<SortField>() ; 
		for(String field : fields){
			if (StringUtil.isBlank(field)) continue ;
			
			String[] sps = StringUtil.split(field) ;

			String fieldName = sps[0] ; // mandatory
			Type sortFieldType = SortField.Type.STRING ;
			boolean isRerverse = false ;
			
			if (ArrayUtils.contains(KEYWORD_FIELD, fieldName) && sps.length == 1) {
				result.add( ("_doc".equals(fieldName)) ? SortField.FIELD_DOC : SortField.FIELD_SCORE ) ;
			} else {
				if (sps.length == 2) {
					if (ORDER_ENCODING.containsKey(sps[1])) {
						sortFieldType = getSortFieldType(sps[1])  ;
					}
					if (ArrayUtils.contains(ORDER, sps[1])) {
						isRerverse = "desc".equals(sps[1]) ;
					}
				}
				if (sps.length == 3){
					sortFieldType =  getSortFieldType(sps[1]);
					isRerverse = "desc".equals(sps[2]) ;
				}
				result.add(new SortField(fieldName, sortFieldType, isRerverse)) ;
			}
		}
		
		return (SortField[])result.toArray(new SortField[0]) ;
	}

	private static SortField.Type getSortFieldType(String sp) {
		return (ORDER_ENCODING.containsKey(sp)) ?  ORDER_ENCODING.get(sp) : SortField.Type.STRING;
	}

	public static SearchRequest applySort(SearchRequest sreq, String exprString) {
		
		
		
		return sreq;
	}
	
	
	
	
}
