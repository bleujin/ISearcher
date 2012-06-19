package net.ion.isearcher.indexer;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.indexer.channel.RelayChannel;
import net.ion.isearcher.indexer.policy.IWritePolicy;
import net.ion.isearcher.indexer.write.IWriter;

import org.apache.lucene.store.LockObtainFailedException;

public class WriterThread extends Thread {

	private IWriter writer;
	private IWritePolicy policy ;
	private RelayChannel<MyDocument> channel;

	public WriterThread(String name, RelayChannel<MyDocument> channel, IWritePolicy policy, IWriter iw) {
		super(name);
		this.channel = channel;
		this.policy = policy ;
		this.writer = iw;
	}

	public void run() {
		try {
			getWriter().begin(this.getClass().getName()) ;
			getPolicy().begin(writer) ;
			while (true) {
				if (channel.isEndMessageOccured() && (!channel.hasMessage()))
					break;

				try {
					MyDocument doc = channel.pollMessage();
					
					getPolicy().apply(getWriter(), doc) ;
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
			}
		} catch(IOException ex){
			ex.printStackTrace() ;
		} finally {
			try {
				getPolicy().end(writer) ;
				getWriter().end();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	private IWritePolicy getPolicy(){
		return this.policy ;
	}

	private IWriter getWriter(){
		return this.writer ;
	}
	
	
}
