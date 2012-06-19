package net.ion.isearcher.indexer.write;


public class WriteEvent<T> {

	private IWriter writer;
	private int hashCode;
	private T about;

	public WriteEvent(IWriter writer, T about) {
		this.writer = writer;
		this.hashCode = about.hashCode();
		this.about = about;
	}

	public IWriter getWriter() {
		return this.writer;
	}

	public T getAbout() {
		return this.about;
	}
}
