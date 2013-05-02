package net.ion.nsearcher.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import net.ion.framework.util.Debug;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.channel.RelayChannel;
import net.ion.nsearcher.index.collect.IndexCancelException;
import net.ion.nsearcher.index.event.ApplyEvent;
import net.ion.nsearcher.index.event.CollectorEvent;
import net.ion.nsearcher.index.event.ICollectorEvent;
import net.ion.nsearcher.index.event.IIndexEvent;
import net.ion.nsearcher.index.event.IndexEndEvent;
import net.ion.nsearcher.index.event.IndexExceptionEvent;
import net.ion.nsearcher.index.policy.ExceptionPolicy;
import net.ion.nsearcher.index.policy.IWritePolicy;
import net.ion.nsearcher.index.policy.MergePolicy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.LockObtainFailedException;

public class AsyncIndexer {

	private RelayChannel<ICollectorEvent> channel = null;

	private IWritePolicy wpolicy;
	private ExceptionPolicy epolicy;

	private List<BeforeIndexHandler> ibefores = new ArrayList<BeforeIndexHandler>();
	private List<AfterIndexHandler> iafters = new ArrayList<AfterIndexHandler>();

	private Indexer indexer;
	private Analyzer analyzer;

	public AsyncIndexer(Indexer indexer, Analyzer analyzer) {
		this.indexer = indexer;
		this.analyzer = analyzer;
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

	public Future<Void> index() {
		return indexer.asyncIndex(analyzer, new IndexJob<Void>() {
			public Void handle(IndexSession session) throws IOException {
				try {
					Debug.debug("writePolicy : " + getWritePolicy());
					Debug.debug("exceptionPolicy : " + getExceptionPolicy());
					Debug.debug("iWriter : " + session);

					while (true) {

						if (getChannel().isEndMessageOccured()) {
							break;
						}
						if (getExceptionPolicy().isEnd()) {
							getChannel().doEnd(getExceptionPolicy().toString());
							break;
						}

						try {
							ICollectorEvent event = getChannel().pollMessage();

							if (event.getEventType().isBegin()) {
								getWritePolicy().begin(session);
							} else if (event.getEventType().isEnd()) {
								break;
							} else if (event.getEventType().isShutDown()) {
								throw new IndexCancelException(event.toString());
							} else if (event.getEventType().isNormal()) {
								WriteDocument[] docs = ((CollectorEvent) event).makeDocument();
								for (WriteDocument doc : docs) {
									beforeHandle((CollectorEvent) event, doc);
									getWritePolicy().apply(session, doc);
									afterHandle(new ApplyEvent(doc));
								}
							}
						} catch (LockObtainFailedException ex) {
							ex.printStackTrace();
							getChannel().doEnd(ex.getMessage());
							throw ex;
						} catch (IOException ex) {
							ex.printStackTrace();
							afterHandle(new IndexExceptionEvent(ex));
							getExceptionPolicy().whenExceptionOccured(session, ex);
						}
					}
				} catch (IndexCancelException ex) {
					ex.printStackTrace();
					afterHandle(new IndexExceptionEvent(ex));
					session.cancel() ;
//					try {
//						session.rollback();
//					} catch (IOException ignore) {
//						ignore.printStackTrace();
//					}
				} catch (LockObtainFailedException ex) {
					ex.printStackTrace();
					afterHandle(new IndexExceptionEvent(ex));
				} catch (Throwable ex) {
					ex.printStackTrace();
				} finally {
					getWritePolicy().end(session);
					afterHandle(new IndexEndEvent());
				}
				return null;
			}

		});

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

	private IWritePolicy getWritePolicy() {
		if (this.wpolicy == null)
			this.wpolicy = new MergePolicy();
		return this.wpolicy;
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

}
