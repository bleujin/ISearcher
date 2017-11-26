package net.ion.nsearcher.extend;

public class SimilaryDoc implements Comparable<SimilaryDoc> {
	private int docId;
	private double simValue;

	public SimilaryDoc(int docId, double simValue) {
		this.docId = docId;
		this.simValue = simValue;
	}

	@Override
	public int compareTo(SimilaryDoc o) {
		return new Double((this.simValue - o.simValue) * 1000000).intValue();
	}

	public double simValue() {
		return simValue;
	}

	public int docId() {
		return docId;
	}

	public String toString() {
		return "docId:" + docId + ", similary:" + simValue;
	}

}