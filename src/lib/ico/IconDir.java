/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package lib.ico;

import java.text.MessageFormat;

/**
 * The {@code IconDir} class represents the icon directory structure
 * of a Windows icon or cursor file. Its format is conveniently described
 * using this C structure:
 * <br><br><em>
 * typedef struct<br>
 * {<br>
 *     WORD idReserved;<br>
 *     WORD idType;<br>
 *     WORD idCount;<br>
 *     ICONDIRENTRY idEntries[1];<br>
 * } ICONDIR, *LPICONDIR;<br>
 * </em><br>
 * Each ICONDIRENTRY is represented by an {@link lib.ico.IconDirEntry} object.
 *
 * @author myinon
 * @version 1.0
 * @see IconDirEntry
 * @see IconType
 */
public final class IconDir {
	private static final String FORMAT = "[idReserved={0}, idType={1}, idCount={2}]";
	
	private short          idReserved; // Reserved (must be 0)
	private short          idType;     // Resource Type (1 for icons)
	private short          idCount;    // How many images?
	private IconDirEntry[] idEntries;  // An entry for each image (idCount of 'em)
	
	/**
	 * Creates a new {@code IconDir} object.
	 * An {@code IconDir} contains information about the total number of images in
	 * the icon file and provides access to image specific information such as a
	 * width and height.
	 *
	 * @param r the reserved word of the icon file. It must be equal to 0.
	 * @param t the type word of the icon file. If the icon file represents an icon, this will be equal to 1.
	 * @param c the image count within the icon file.
	 */
	IconDir(short r, short t, short c) {
		this.idReserved = r;
		this.idType = t;
		this.idCount = c;
	}
	
	/**
	 * Retrieves the type of the icon file: either an ico or a cursor.
	 *
	 * @return the type of the icon file.
	 */
	public IconType getType() {
		switch (this.idType) {
			case 1: return IconType.ICON;
			case 2: return IconType.CURSOR;
			default: return IconType.UNKNOWN;
		}
	}
	
	/**
	 * Retrieves the type of the icon file: either an ico or a cursor.
	 * This is the integer representation of the type.
	 *
	 * @return the type of the icon file.
	 */
	public short getTypeAsInteger() {
		return this.idType;
	}
	
	/**
	 * Retrieves the number of images contained in the icon file.
	 *
	 * @return the number of images in the icon file.
	 */
	public short getCount() {
		return this.idCount;
	}
	
	/**
	 * Retrieves a copy of the {@code IconDirEntry} entries in the icon file.
	 * These entries contain meta data for each of the images in the file.
	 *
	 * @return a copy of the directory entries.
	 * @see IconDirEntry
	 */
	public IconDirEntry[] getEntries() {
		if (this.idEntries == null)
			return (IconDirEntry[]) null;
		return java.util.Arrays.copyOf(this.idEntries, this.idCount);
	}
	
	@Override
	public String toString() {
		return MessageFormat.format(FORMAT, this.idReserved, this.getType(), this.idCount);
	}
}
