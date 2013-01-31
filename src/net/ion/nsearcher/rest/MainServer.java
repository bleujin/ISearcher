package net.ion.nsearcher.rest;

import net.ion.radon.core.Aradon;
import net.ion.radon.core.config.Configuration;
import net.ion.radon.core.config.ConfigurationBuilder;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;

public class MainServer {
	public static void main(String[] args) throws Exception{
		try {

//			Component component = new Component();
//			component.getServers().add(Protocol.HTTP, 8182);
//			component.getClients().add(Protocol.FILE);
//			
//			Application isearcher = new ISearcherApplication(component.getContext().createChildContext());
//			component.getDefaultHost().attach("/isearcher", isearcher);
//
//			component.start();

			String FileLocation = "FILE_LOCATION";

			Configuration conf = ConfigurationBuilder.newBuilder().aradon()
			.addAttribute(FileLocation, "D:/temp/index/")
			.sections().restSection("isearcher")
				.path("info").handler(InfoLet.class).addUrlPattern("/{indexname}/info.{format}")
				.path("search").handler(SearchLet.class).addUrlPattern("/{indexname}/search.{format}")
				.path("index").handler(IndexLet.class).addUrlPattern("/{indexname}/index.{format}")
				.path("list").handler(ListLet.class).addUrlPattern("/{indexname}/list.{format}")
			.build();
			Aradon.create(conf).startServer(8182) ;
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

class ISearcherApplication extends Application {
	public final static String FileLocation = "FILE_LOCATION";

	public ISearcherApplication(Context context) {
		super(context);
	}

	public Restlet createRoot() {
		Configuration conf = ConfigurationBuilder.newBuilder().aradon()
			.addAttribute(FileLocation, "D:/temp/index/")
			.sections().restSection("isearcher")
				.path("info").handler(InfoLet.class).addUrlPattern("/{indexname}/info")
				.path("search").handler(SearchLet.class).addUrlPattern("/{indexname/search}")
				.path("list").handler(ListLet.class).addUrlPattern("/{indexname}/list.{format}")
			.build();
		return Aradon.create(conf) ;
	}
}
