/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.jcurrency.converter;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides many utility functions for the currency converter panel.
 * 
 * @author myinon
 * @version 1.0
 */
public final class CurrencyUtils {
	public static final Logger LOGGER = Logger.getLogger(CurrencyUtils.class.getPackage().getName());
	
	private static final ResourceBundle.Control UNICODE_CONTROL = new UnicodeResourceBundleControl();
	private static ResourceBundle resources; // Localized string resources
	
	static {
		LOGGER.setLevel(Level.INFO);
		try {
			Path tmp = Paths.get(System.getProperty("java.io.tmpdir"), "jcc_logs");
			if (Files.notExists(tmp, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectory(tmp);
			}
			FileHandler fh = new FileHandler("%t/jcc_logs/jcc%u_%g.log", 1024 * 1024, 3, false);
			fh.setEncoding("utf8");
			LOGGER.addHandler(fh);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		
		try {
			resources = ResourceBundle.getBundle("com/jcurrency/converter/resources/currency", Locale.US, UNICODE_CONTROL);	
		} catch (MissingResourceException e) {
			LOGGER.log(Level.SEVERE, "currency_en_US.properties not found", e);
			throw e;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.toString());
			throw e;
		}
	}
	
	// No instantiations of this class
	private CurrencyUtils() {
		throw new InternalError();
	}
	
	/**
	 * Gets a named resource.
	 * 
	 * @param name The name of the resource to retrieve.
	 * @return The value of the resource or {@code null}.
	 */
	public static String getResourceString(String name) {
		String str;
		try {
			str = resources.getString(name);
		} catch (MissingResourceException e) {
			str = null;
		}
		return str;
	}
	
	/**
	 * Gets the url of a resource in this program.
	 * 
	 * @param key the name of the resource.
	 * @return the url pointing to the resource or {@code null}
	 *         if the resource can't be found.
	 */
	public static URL getResource(String key) {
		return getResource(key, CurrencyUtils.class);
	}
	
	/**
	 * Gets the url of a resource in this program.
	 * 
	 * @param key the name of the resource.
	 * @param clazz the {@code Class} object to use as a base for finding
	 *              the resource.
	 * @return the url pointing to the resource or {@code null}
	 *         if the resource can't be found.
	 */
	public static URL getResource(String key, Class<?> clazz) {
		if (key != null) {
			URL url = clazz.getResource(key);
			return url;
		}
		return null;
	}
	
	/**
	 * Tokenizes the input string into different pieces using on the
	 * specified separator.
	 * 
	 * @param input the input string to split.
	 * @param sep the separator to use to split the input.
	 * @return an array of the different pieces of the split input.
	 */
	public static String[] tokenize(String input, String sep) {
		List<String> l = new ArrayList<>();
		StringTokenizer t = new StringTokenizer((input == null) ? "" : input, sep);
		while (t.hasMoreTokens())
			l.add(t.nextToken());
		String[] cmd = l.toArray(new String[l.size()]);
		return cmd;
	}
}
