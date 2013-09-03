package net.ion.nsearcher.common;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.common.FieldIndexingStrategy.FieldType;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.util.BytesRef;

@Deprecated
public class IndexField implements IndexableField {

	private static final long serialVersionUID = -7320846412631501001L;
	private Field inner;
	private List<IndexableField> more = ListUtil.newList() ;
	public final static IndexField BLANK = new IndexField(null) ;

	IndexField(Field inner) {
		this.inner = inner;
	}

	public IndexField(FieldType fieldType, String name, String value, Field.Store store, Field.Index index) {
		this(new Field(fieldType == FieldType.Manual ? name : name.toLowerCase(), value, store, index));
	}

	static IndexField load(Field field){
		return new IndexField(field) ;
	}
	
	static Field field(FieldType fieldType, String name, String value, Field.Store store, Field.Index index){
		return new Field(fieldType == FieldType.Manual ? name : name.toLowerCase(), value, store, index) ;
	}
	
	
	public void addMoreField(Field field) {
		more.add(field);
	}


	public String toString() {
		return getRealField().toString();
	}

	public String stringValue() {
		return getRealField().stringValue();
	}

	public TokenStream tokenStream(Analyzer analyzer) throws IOException{
		return inner.tokenStream(analyzer) ;
	}

	public Field getRealField() {
		return inner;
	}

	public IndexableField[] getMoreField() {
		if (more == null || more.size() == 0)
			return new IndexableField[0];
		return more.toArray(new IndexableField[0]);
	}


	public IndexField addTo(Document doc) {
		if (inner == null) return this ;
		doc.add(inner);
		for (IndexableField more : this.getMoreField()) {
			doc.add(more);
		}

		return this ;
	}

	public IndexOptions getIndexOptions() {
		return inner.fieldType().indexOptions() ;
	}

	public void setIndexOptions(IndexOptions option) {
		inner.fieldType().setIndexOptions(option) ;
	}


	public int getBinaryLength() {
		return getRealField().binaryValue().length;
	}

	public int getBinaryOffset() {
		return getRealField().binaryValue().offset;
	}

	public byte[] getBinaryValue() {
		return getRealField().binaryValue().bytes;
	}
	public float boost() {
		return getRealField().boost();
	}


	public boolean isIndexed() {
		return getRealField().fieldType().indexed();
	}


	public boolean isStored() {
		return getRealField().fieldType().stored();
	}

	public boolean isTokenized() {
		return getRealField().fieldType().tokenized();
	}

	public String name() {
		return getRealField().name();
	}

	public Reader readerValue() {
		return getRealField().readerValue();
	}

	public void setBoost(float f) {
		getRealField().setBoost(f);
	}


	public IndexableFieldType fieldType() {
		return getRealField().fieldType();
	}

	public Number numericValue() {
		return getRealField().numericValue();
	}

	public BytesRef binaryValue() {
		return getRealField().binaryValue(); 
	}

	
	
	

}
