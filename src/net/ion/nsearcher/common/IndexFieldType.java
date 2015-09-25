package net.ion.nsearcher.common;

import java.util.Set;

import net.ion.framework.util.SetUtil;
import net.ion.nsearcher.common.MyField.MyFieldType;

public interface IndexFieldType {

	public final IndexFieldType DEFAULT = new IndexFieldType() {

		private Set<String> numericField = SetUtil.newSyncSet();

		public void decideField(MyField field) {
			if (field.myFieldtype() == MyFieldType.Number || field.myFieldtype() == MyFieldType.Double) {
				numericField.add(field.name());
			}
		}

		public boolean isNumericField(String field) {
			return numericField.contains(field);
		}
	};

	public void decideField(MyField field);

	public boolean isNumericField(String field);
	
}
