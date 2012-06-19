package net.ion.isearcher.indexer.policy;

import java.io.IOException;

import net.ion.isearcher.indexer.write.IWriter;

public interface ExceptionPolicy {
	ExceptionPolicy ABORT_AFTER_ROLLBACK = new ExceptionPolicy() {

		private boolean isOccured = false;

		public boolean isEnd() {
			return isOccured;
		}

		public void whenExceptionOccured(IWriter writer, IOException e) {
			try {
				e.printStackTrace() ;
				writer.rollback();
			} catch (IOException ignore) {
			}
			this.isOccured = true;
		}

		
		public String toString(){
			return "ABORT_AFTER_ROLLBACK_WHEN_EXCEPTION_OCCURED" ;
		}
	};

	public void whenExceptionOccured(IWriter writer, IOException e);

	public boolean isEnd();
}
