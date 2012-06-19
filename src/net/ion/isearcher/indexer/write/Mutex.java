package net.ion.isearcher.indexer.write;

import java.util.concurrent.locks.ReentrantLock;

import net.ion.framework.util.Debug;

import org.apache.lucene.store.LockObtainFailedException;

public class Mutex {

	private Object owner ;
	private boolean isUpdated;
	private ReentrantLock locker = new ReentrantLock();

	public synchronized boolean tryLock(Object owner) {
		if (this.owner != null && this.owner != owner) {
			return false ;
		}
		
		if (this.owner == owner && locker.isLocked()) return true ;
		
		this.owner = owner ;
		boolean result = locker.tryLock();
		return result;
	}

	public synchronized void lock(Object owner) throws LockObtainFailedException {
		if (this.owner != null) throw new LockObtainFailedException("exception.indexer.lock.obtain_failed:owner[" + this.owner + "]");
		this.owner = owner ;
		locker.lock();
		
	}

	public synchronized void unLock(Object owner, boolean modified) throws LockObtainFailedException {
		if (this.owner == null) return ;
		if (this.owner != owner) {
			Debug.line(this.owner, owner) ;
			throw new LockObtainFailedException("exception.indexer.lock.release_failed:not_owner[current:" + this.owner + "]");
		}
		locker.unlock();
		this.owner = null ;
		isUpdated = modified ;
	}

	public synchronized boolean isUpdated() {
		return isUpdated;
	}

	public synchronized void reflectUpdate() {
		isUpdated = false ;
	}

	public synchronized boolean isOwner(IWriter owner) throws LockObtainFailedException {
		if (this.owner == null) {
//			this.owner = owner ;
			throw new LockObtainFailedException("exception.indexer.lock.obtail_failed:no lock acquired"); 
		}
		return this.owner == owner ;
	}
	
	public Object getOwner(){
		return owner ;
	}

}
