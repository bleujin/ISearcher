package net.ion.isearcher.searcher.processor;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ion.isearcher.indexer.channel.AbstractRelayChannel;
import net.ion.isearcher.indexer.channel.RelayChannel;

/**
 * @author bleujin
 */
public class LimitedChannel<E> extends AbstractRelayChannel<E> implements RelayChannel<E> {

	private List<E> store;
	private final int maxSize;

	public LimitedChannel(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("MaxSize must be greater then zero");
		}
		this.store = Collections.synchronizedList(new LinkedList<E>());
		this.maxSize = maxSize;
	}

	public boolean add(E e) {
		if (this.store.size() >= this.maxSize) {
			this.store.remove(0);
		}
		this.store.add(e);
		return true;
	}
	
	public boolean offer(E e) {
		if (this.store.size() >= this.maxSize) {
			return false;
		}
		this.store.add(0, e);
		return true;
	}

	public E remove() {
		E first = this.store.remove(0);
		return first;
	}

	public E poll() {
		if (this.store.size() > 0) {
			return this.store.remove(0);
		} else {
			return null;
		}
	}

	public E element() {
		return this.store.get(0);
	}

	public E peek() {
		return this.store.get(0);
	}

	public int size() {
		return this.store.size();
	}

	public int capacity() {
		return this.maxSize;
	}

	public boolean isEmpty() {
		return this.store.isEmpty();
	}

	public boolean contains(Object o) {
		return this.store.contains(o);
	}

	public Iterator<E> iterator() {
		return this.store.iterator();
	}

	public Object[] toArray() {
		return this.store.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return this.store.toArray(a);
	}

	public boolean remove(Object o) {
		return this.store.remove(o);
	}

	
	
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException("Operation not supported");
	}

	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException("Operation not supported");
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Operation not supported");
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Operation not supported");
	}

	public void clear() {
		this.store.clear();
	}


	
	
	
	// thread safe method... 
	// if you want thread safe channel
	// only use this method..
	public synchronized void addMessage(E message) {
		while(maxSize <= size()){
			try {
				wait() ;
			} catch(InterruptedException ex){
			}
		}
		add(message) ;
		notifyAll();
	}

	public synchronized boolean hasMessage() {
		return ! isEmpty();
	}
	
	public synchronized E peekMessage(){
		while(isEmpty()){
			try {
				wait() ;
			} catch (InterruptedException e) {
			}
		}
		E message = peek() ;
		notifyAll() ;
		return message ;
	}

	
	
	public synchronized E pollMessage() {
		while (isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ex) {
			}
		}

		E message =  poll();
		if (message == null) throw new IllegalAccessError("exception.search.no_message") ;
		notifyAll() ;
		return message ;
	}

}
