package net.ion.nsearcher.index;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.channel.RelayChannel;
import net.ion.nsearcher.index.policy.IWritePolicy;

public class WriterThread extends Thread {

	private IndexSession writer;
	private IWritePolicy policy ;
	private RelayChannel<WriteDocument> channel;

	public WriterThread(String name, RelayChannel<WriteDocument> channel, IWritePolicy policy, IndexSession iw) {
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
					WriteDocument doc = channel.pollMessage();
					
					getPolicy().apply(getWriter(), doc) ;
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
			}
		} catch(IOException ex){
			ex.printStackTrace() ;
		} finally {
			getPolicy().end(writer) ;
			try {
				getWriter().end();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	private IWritePolicy getPolicy(){
		return this.policy ;
	}

	private IndexSession getWriter(){
		return this.writer ;
	}
	
	
}
