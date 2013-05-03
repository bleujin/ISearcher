package net.ion.nsearcher.search;

import net.ion.framework.util.Closure;
import net.ion.nsearcher.common.MyDocument;
import net.ion.nsearcher.common.ReadDocument;

public interface EachDocHandler extends Closure<ReadDocument>{

	public void execute(ReadDocument doc) ;
}
