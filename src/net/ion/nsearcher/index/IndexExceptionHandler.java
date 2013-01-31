package net.ion.nsearcher.index;

public interface IndexExceptionHandler<T> {
	
	public static IndexExceptionHandler<Void> DEFAULT = new IndexExceptionHandler<Void>() {
		public Void onException(Throwable ex) {
			ex.printStackTrace() ;
			return null ;
		}
	};
	
	public T onException(Throwable ex) ;
}
