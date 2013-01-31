package net.ion.nsearcher.search;

import net.ion.framework.util.Closure;
import net.ion.nsearcher.common.MyDocument;

public interface EachDocHandler extends Closure<MyDocument>{

	public void execute(MyDocument doc) ;
}
