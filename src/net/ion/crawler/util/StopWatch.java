package net.ion.crawler.util;

public final class StopWatch {

    private long total;

    private long start;

    public void start() {
        start = System.currentTimeMillis();
    }

    public long stop() {
        long diff = System.currentTimeMillis() - start;
        total += diff;
        return diff;
    }

    public void reset() {
        total = 0;
    }

    public long getTime() {
        return total;
    }

}
