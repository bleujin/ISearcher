package net.ion.isearcher.indexer.policy ;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.ion.isearcher.impl.HashBean;

public interface Data {
    public abstract Map<String, HashBean> getHashData() throws ExecutionException;
}
