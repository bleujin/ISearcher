package net.ion.nsearcher.rest;

import java.util.concurrent.ExecutionException;

import net.ion.framework.util.IOUtil;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.radon.core.let.PathHandler;

public class MainServer {
	public static void main(String[] args) throws Exception{
		try {

			final Central cen = CentralConfig.newRam().build() ;

			final Radon radon = RadonConfiguration.newBuilder(8182)
					.add("/isearcher", new PathHandler(InfoLet.class, SearchLet.class, IndexLet.class, ListLet.class).prefixURI("isearcher")).createRadon() ;
			radon.getConfig().getServiceContext().putAttribute("CENTRAL", cen) ;
			radon.start().get() ;

			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					try {
						IOUtil.closeQuietly(cen); 
						radon.stop().get() ;
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
