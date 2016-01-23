/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package lib.ico;

/**
 * The {@code IconType} enumeration represents the type of the icon file.
 * The currently supported file types are icon or cursor.
 *
 * @author myinon
 * @version 1.0
 * @see IconDir
 */
public enum IconType {
	/**
	 * Represents an unknown resource file type.
	 */
	UNKNOWN((short) 0),
	
	/**
	 * Represents an icon resource file type.
	 */
	ICON((short) 1),
	
	/**
	 * Represents a cursor resource file type.
	 */
	CURSOR((short) 2);
	
	private short type;
	
	/**
	 * Creates a new {@code IconType} enumeration value.
	 *
	 * @param t the integer representation of the type.
	 */
	IconType(short t) {
		this.type = t;
	}
	
	/**
	 * Retrieves the integer representation of the enumeration value.
	 *
	 * @return the integer representation of the type.
	 */
	public short getType() {
		return this.type;
	}
}
