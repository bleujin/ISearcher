package net.ion.nsearcher.index.policy;

import java.io.IOException;

import net.ion.nsearcher.index.IndexSession;

public interface ExceptionPolicy {
	ExceptionPolicy ABORT_AFTER_ROLLBACK = new ExceptionPolicy() {

		private boolean isOccured = false;

		public boolean isEnd() {
			return isOccured;
		}

		public void whenExceptionOccured(IndexSession writer, IOException e) {
			e.printStackTrace() ;
			writer.rollback();
			this.isOccured = true;
		}

		
		public String toString(){
			return "ABORT_AFTER_ROLLBACK_WHEN_EXCEPTION_OCCURED" ;
		}
	};

	public void whenExceptionOccured(IndexSession writer, IOException e);

	public boolean isEnd();
}
