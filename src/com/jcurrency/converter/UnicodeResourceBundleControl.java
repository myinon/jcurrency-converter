/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.jcurrency.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Allows a resource file encoded in UTF-8 to be read
 * into a {@code ResourceBundle}.
 * 
 * @author myinon
 * @version 1.0
 */
public class UnicodeResourceBundleControl extends ResourceBundle.Control {
	@Override
	public List<String> getFormats(String baseName) {
		if (baseName == null)
			throw new NullPointerException();
		return Arrays.asList("properties");
	}
	
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format,
	ClassLoader loader, boolean reload)
	throws IllegalAccessException, InstantiationException, IOException {
		if (baseName == null || locale == null || format == null || loader == null)
			throw new NullPointerException();
		
		ResourceBundle bundle = null;
		if (format.equals("properties")) {
			String bundleName = this.toBundleName(baseName, locale);
			String resourceName = this.toResourceName(bundleName, format);
			
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						// Disable caches to get fresh data for reloading.
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			
			if (stream != null) {
				try (BufferedReader br = new BufferedReader(
				new InputStreamReader(stream, StandardCharsets.UTF_8))) {
					bundle = new PropertyResourceBundle(br);
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return bundle;
	}
}
