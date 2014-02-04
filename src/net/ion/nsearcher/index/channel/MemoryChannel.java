package net.ion.nsearcher.index.channel;



public class MemoryChannel<T> extends AbstractRelayChannel<T> {
	private static final int MAX_MESSAGE = 10;
	private final T[] store;

	private int tail;
	private int head;
	private int count;

	public MemoryChannel(int maxDocument) {
		this.store = (T[]) new Object[maxDocument];
		this.head = 0;
		this.tail = 0;
		this.count = 0;
	}

	public MemoryChannel() {
		this(MAX_MESSAGE);
	}

	public synchronized void addMessage(T message) {
		while (count >= store.length) {
			if (isEndMessageOccured()) {
				return; // poller signal.. channering listener failed..
			}
			try {
				//Debug.warn(count, store.length, count >= store.length) ;
				wait();
			} catch (InterruptedException ex) {
			}
		}
		store[tail] = message;
		tail = (tail + 1) % store.length;
		count++;

		notifyAll();
	}

	public synchronized void doEnd(String message) {
		super.doEnd(message);
		notifyAll();
	}

	public synchronized T pollMessage() {
		while (count <= 0) {
			if (isEndMessageOccured())
				break; // adder signal..
			try {
				//Debug.warn(count, store.length, count <= 0) ;
				wait();
			} catch (InterruptedException ex) {
			}
		}

		T message = store[head];
		head = (head + 1) % store.length;

		count--;
		notifyAll();
		return message;
	}

	public int getCount() {
		return count;
	}

	public boolean hasMessage() {
		return getCount() > 0;
	}

}