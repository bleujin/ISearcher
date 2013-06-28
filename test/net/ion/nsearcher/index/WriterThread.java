package net.ion.nsearcher.index;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.channel.RelayChannel;
import net.ion.nsearcher.index.policy.IWritePolicy;

public class WriterThread extends Thread {

	private IndexSession isession;
	private IWritePolicy policy ;
	private RelayChannel<WriteDocument> channel;

	public WriterThread(String name, RelayChannel<WriteDocument> channel, IWritePolicy policy, IndexSession isession) {
		super(name);
		this.channel = channel;
		this.policy = policy ;
		this.isession = isession;
	}

	public void run() {
		try {
			isession.begin(this.getClass().getName()) ;
			getPolicy().begin(isession) ;
			while (true) {
				if (channel.isEndMessageOccured() && (!channel.hasMessage()))
					break;

				try {
					WriteDocument doc = channel.pollMessage();
					
					getPolicy().apply(isession, doc) ;
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
			}
			isession.commit() ;
		} catch(IOException ex){
			ex.printStackTrace() ;
			isession.rollback() ;
		} finally {
			getPolicy().end(isession) ;
			isession.end();
		}

	}
	
	private IWritePolicy getPolicy(){
		return this.policy ;
	}

	
	
}
