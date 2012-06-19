package net.ion.isearcher.rest;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class MainServer {
	public static void main(String[] args) {
		try {
			Component component = new Component();
			component.getServers().add(Protocol.HTTP, 8182);
			component.getClients().add(Protocol.FILE);
			
			Application isearcher = new ISearcherApplication(component.getContext().createChildContext());
			component.getDefaultHost().attach("/isearcher", isearcher);

			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
