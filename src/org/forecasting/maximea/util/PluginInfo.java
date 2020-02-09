/**
 * This file is part of iDempiere ERP <http://www.idempiere.org>.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Copyright (c) 2016 Saúl Piña <sauljabin@gmail.com>.
 */

package org.forecasting.maximea.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Singleton class for Bundle information
 */
public final class PluginInfo {

	private static String ATTRIBUTE_BUNDLE_NAME = "Bundle-Name";
	private static String ATTRIBUTE_BUNDLE_ID = "Bundle-SymbolicName";
	private static String ATTRIBUTE_BUNDLE_VERSION = "Bundle-Version";
	private static String ATTRIBUTE_BUNDLE_VENDOR = "Bundle-Vendor";

	private static PluginInfo instance = null;
	private static Manifest manifest = null;

	/**
	 * Private constructor
	 * 
	 * @throws IOException
	 *             If not found manifest file
	 */
	private PluginInfo() throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(JarFile.MANIFEST_NAME);
		manifest = new Manifest(is);
	}

	/**
	 * Create a return a BundleInfo singleton object
	 * 
	 * @return Singleton object
	 * @throws IOException
	 *             If not found manifest file
	 */
	public synchronized static PluginInfo getInstance() throws IOException {
		if (instance == null || manifest == null)
			instance = new PluginInfo();
		return instance;
	}

	/**
	 * Gets the actual Bundle Name
	 * 
	 * @return Bundle Name
	 */
	public String getBundleName() {
		return manifest.getMainAttributes().getValue(ATTRIBUTE_BUNDLE_NAME);
	}

	/**
	 * Gets the actual Bundle Vendor
	 * 
	 * @return Bundle Vendor
	 */
	public String getBundleVendor() {
		return manifest.getMainAttributes().getValue(ATTRIBUTE_BUNDLE_VENDOR);
	}

	/**
	 * Gets the actual Bundle Version
	 * 
	 * @return Bundle Version
	 */
	public String getBundleVersion() {
		return manifest.getMainAttributes().getValue(ATTRIBUTE_BUNDLE_VERSION);
	}

	/**
	 * Gets the actual Bundle ID
	 * 
	 * @return Bundle ID
	 */
	public String getBundleID() {
		try {
			return manifest.getMainAttributes().getValue(ATTRIBUTE_BUNDLE_ID).split(";")[0];
		} catch (Exception e) {
			return "";
		}
	}

}
