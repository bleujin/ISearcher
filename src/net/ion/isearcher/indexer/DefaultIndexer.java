package net.ion.isearcher.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.events.ApplyEvent;
import net.ion.isearcher.events.CollectorEvent;
import net.ion.isearcher.events.ICollectorEvent;
import net.ion.isearcher.events.IIndexEvent;
import net.ion.isearcher.events.IndexEndEvent;
import net.ion.isearcher.events.IndexExceptionEvent;
import net.ion.isearcher.indexer.channel.RelayChannel;
import net.ion.isearcher.indexer.collect.IndexCancelException;
import net.ion.isearcher.indexer.policy.ExceptionPolicy;
import net.ion.isearcher.indexer.policy.IWritePolicy;
import net.ion.isearcher.indexer.policy.MergePolicy;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.util.CloseUtils;

import org.apache.lucene.store.LockObtainFailedException;

public class DefaultIndexer implements Runnable {

	private RelayChannel<ICollectorEvent> channel = null;

	private IWriter iwriter = IWriter.EMPTY_WRITER;
	private IWritePolicy wpolicy;
	private ExceptionPolicy epolicy;

	private List<BeforeIndexHandler> ibefores = new ArrayList<BeforeIndexHandler>();
	private List<AfterIndexHandler> iafters = new ArrayList<AfterIndexHandler>();

	public DefaultIndexer() {
	}

	public void setWritePolicy(IWritePolicy wpolicy) {
		this.wpolicy = wpolicy;
	}

	public void addBeforeHandler(BeforeIndexHandler ibefore) {
		ibefores.add(ibefore);
	}

	public void addAfterHandler(AfterIndexHandler iafter) {
		iafters.add(iafter);
	}

	private void beforeHandle(CollectorEvent event, MyDocument mydoc) {
		for (BeforeIndexHandler before : ibefores) {
			before.handleDoc(event, mydoc);
		}
	}

	private void afterHandle(IIndexEvent ievent) {
		for (AfterIndexHandler after : iafters) {
			after.indexed(ievent);
		}
	}

	public void run() {
		try {
			
			Debug.debug("writePolicy : " + getWritePolicy()) ;
			Debug.debug("exceptionPolicy : " +  getExceptionPolicy()) ;
			Debug.debug("iWriter : " +  getWriter()) ;
			
			while (true) {

				if (getChannel().isEndMessageOccured() ) {
					break;
				}
				if (getExceptionPolicy().isEnd()) {
					getChannel().doEnd(getExceptionPolicy().toString()) ;
					break ;
				}

				try {
					ICollectorEvent event = getChannel().pollMessage();

					if (event.getEventType().isBegin()) {
						getWriter().begin(event.getCollectorName());
						getWritePolicy().begin(getWriter()) ;
					} else if (event.getEventType().isEnd()) {
						break;
					} else if (event.getEventType().isShutDown()) {
						throw new IndexCancelException(event.toString());
					} else if (event.getEventType().isNormal()) {
						MyDocument[] docs = ((CollectorEvent) event).makeDocument();
						for (MyDocument doc : docs) {
							beforeHandle((CollectorEvent) event, doc);
							getWritePolicy().apply(getWriter(), doc);
							afterHandle(new ApplyEvent(doc));
						}
					}
				} catch (LockObtainFailedException ex) {
					ex.printStackTrace() ;
					getChannel().doEnd(ex.getMessage());
					throw ex;
				} catch (IOException ex) {
					ex.printStackTrace() ;
					afterHandle(new IndexExceptionEvent(ex)) ;
					getExceptionPolicy().whenExceptionOccured(getWriter(), ex);
				}
			}
		} catch (IndexCancelException ex) {
			ex.printStackTrace() ;
			afterHandle(new IndexExceptionEvent(ex)) ;
			try {
				getWriter().rollback();
			} catch (IOException ignore) {
				ignore.printStackTrace();
			}
		} catch (LockObtainFailedException ex) {
			ex.printStackTrace();
			afterHandle(new IndexExceptionEvent(ex)) ;
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			try {
				getWritePolicy().end(getWriter()) ;
				getWriter().end();
			} catch (IOException ex) {
				afterHandle(new IndexExceptionEvent(ex)) ;
			}
			afterHandle(new IndexEndEvent()) ;
		}
	}

	public void setExceptionPolicy(ExceptionPolicy epolicy) {
		if (epolicy == null)
			throw new IllegalArgumentException("exception.indexer.exception_policy.not_defined");
		this.epolicy = epolicy;
	}

	private ExceptionPolicy getExceptionPolicy() {
		if (epolicy == null)
			this.epolicy = ExceptionPolicy.ABORT_AFTER_ROLLBACK;
		return epolicy;
	}

	public void setWriter(IWriter iw) {
		this.iwriter = iw;
	}

	private IWritePolicy getWritePolicy() {
		if (this.wpolicy == null)
			this.wpolicy = new MergePolicy();
		return this.wpolicy;
	}

	private IWriter getWriter() {
		return this.iwriter;
	}

	void setChannel(RelayChannel<ICollectorEvent> channel) {
		this.channel = channel;
	}

	private RelayChannel<ICollectorEvent> getChannel() {
		return this.channel;
	}

	public String toString() {
		return "WritePolicy:" + getWritePolicy() + ", ExceptionPolicy:" + getExceptionPolicy();

	}

	public void forceClose(){
		CloseUtils.silentClose(getWriter()) ;
	}
}
