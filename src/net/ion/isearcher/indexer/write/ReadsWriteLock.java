package net.ion.isearcher.indexer.write;

public final class ReadsWriteLock {
	private int readingReader = 0;
	private int waitingWriter = 0;
	private int writingWriter = 0;
	private boolean preferWriter = true;

	private Thread owner = null;

	public synchronized void readLock() throws InterruptedException {
		while (writingWriter > 0 || (preferWriter && waitingWriter > 0)) {
			wait();
		}
		readingReader++;
	}

	public synchronized void readUnLock() {
		readingReader--;
		preferWriter = true;
		notifyAll();
	}

	public synchronized void writeLock() throws InterruptedException {
		waitingWriter++;
		Thread me = Thread.currentThread();
		try {
			while ((readingReader > 0 || writingWriter > 0) && owner != me) {
				wait();
			}
		} finally {
			waitingWriter--;
			owner = me;
		}
		writingWriter++;
	}

	public synchronized void writeUnLock() {
		Thread me = Thread.currentThread();
		if (owner != me)
			return;

		owner = null;
		writingWriter--;
		preferWriter = false;
		notifyAll();
	}

}