package net.ion.crawler.util;

import java.net.URL;
import java.net.URLEncoder;

import net.ion.framework.parse.gson.JsonObject;

public class Detect extends GoogleAPI {

	/**
	 * Constants.
	 */
	private static String URL = "http://ajax.googleapis.com/ajax/services/language/detect?v=1.0&q=";

	/**
	 * Detects the language of a supplied String.
	 * 
	 * @param text
	 *            The String to detect the language of.
	 * @return A DetectResult object containing the language, confidence and reliability.
	 * @throws Exception
	 *             on error.
	 */
	public static DetectResult execute(final String text) throws Exception {
		validateReferrer();

		final URL url = new URL(URL + URLEncoder.encode(text, ENCODING));

		final JsonObject json = retrieveJSON(url);

		return new DetectResult(Language.fromString(json.asJsonObject("responseData").asString("language")), json.asJsonObject("responseData").asBoolean(
				"isReliable"), json.asJsonObject("responseData").asDouble("confidence"));
	}
}
