package net.ion.isearcher.rest;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class ISearcherApplication extends Application {
	public final static String FileLocation = "FILE_LOCATION";

	public ISearcherApplication(Context context) {
		super(context);
	}

	public Restlet createRoot() {

		Context context = getContext();

		Router router = new Router(context);
		init(context);

		router.attach("/{dir}/search.{format}", SearchRest.class);
		router.attach("/{dir}/info", InfoRest.class);
		router.attach("/{dir}/list.{format}", IndexRest.class);
		// router.attach("/index/{dir}/list.{format}", CategoryCRUDRest.class);
		return router;
	}

	private void init(Context context) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(FileLocation, "D:/WORKGROUP/ICSS5/icss/repository/index/");

		context.setAttributes(attributes);
	}
}
