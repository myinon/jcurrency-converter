/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package lib.ico;

import java.text.MessageFormat;

/**
 * The {@code IconDirEntry} class represents the icon directory entry structure
 * of a Windows icon or cursor file. Its format is conveniently described
 * using this C structure:
 * <br><br><em>
 * typedef struct<br>
 * {<br>
 *     BYTE bWidth;<br>
 *     BYTE bHeight;<br>
 *     BYTE bColorCount;<br>
 *     BYTE bReserved;<br>
 *     WORD wPlanes;<br>
 *     WORD wBitCount;<br>
 *     DWORD dwBytesInRes;<br>
 *     DWORD dwImageOffset;<br>
 * } ICONDIRENTRY, *LPICONDIRENTRY;<br>
 * </em><br>
 * Each {@code IconDirEntry} contains a reference to an {@link IconImage}
 * which contains the entry's image information.
 *
 * @author myinon
 * @version 1.0
 * @see IconDir
 * @see IconImage
 */
public final class IconDirEntry {
	private static final String FORMAT = "[bWidth={0}, bHeight={1}, bColorCount={2}, bReserved={3}, wPlanes={4}, wBitCount={5}, dwBytesInRes={6}, dwImageOffset={7}, IconImage={8}]";
	
	private byte      bWidth;        // Width, in pixels, of the image
	private byte      bHeight;       // Height, in pixels, of the image
	private byte      bColorCount;   // Number of colors in image (0 if >= 8bpp)
	private byte      bReserved;     // Reserved (must be 0)
	private short     wPlanes;       // Color Planes
	private short     wBitCount;     // Bits per pixel
	private int       dwBytesInRes;  // How many bytes in this resource?
	private int       dwImageOffset; // Where in the file is this image?
	private volatile IconImage info; // Icon bitmap information
	
	/**
	 * Creates a new {@code IconDirEntry} object.
	 * An {@code IconDirEntry} contains image specific information such as the image's width
	 * and height as well as its bits per pixel.
	 *
	 * @param w   the width of the image.
	 * @param h   the height of the image.
	 * @param c   the number of colors in the image.
	 * @param r   the reserved byte of the entry. This must be equal to 0.
	 * @param p   the number of planes in the image.
	 * @param bc  the bit count or bits per pixel of the image.
	 * @param bir the number of bytes the image takes up.
	 * @param io  the offset of the image data. This offset is relative to the beginning of the file.
	 */
	IconDirEntry(byte w, byte h, byte c, byte r, short p, short bc, int bir, int io) {
		this.bWidth = w;
		this.bHeight = h;
		this.bColorCount = c;
		this.bReserved = r;
		this.wPlanes = p;
		this.wBitCount = bc;
		this.dwBytesInRes = bir;
		this.dwImageOffset = io;
	}
	
	/**
	 * Retrieves the width of the image in pixels.
	 *
	 * @return the width of the image.
	 */
	public byte getWidth() {
		return this.bWidth;
	}
	
	/**
	 * Retrieves the height of the image in pixels.
	 *
	 * @return the height of the image.
	 */
	public byte getHeight() {
		return this.bHeight;
	}
	
	/**
	 * Retrieves the count of all the colors in the image
	 * or 0 if the image's bits per pixel is &#x2265; 8.
	 *
	 * @return the color count or 0 if the bit per pixel &#x2265; 8.
	 */
	public byte getColorCount() {
		return this.bColorCount;
	}
	
	/**
	 * Retrieves the number of planes in the image.
	 * Currently this value is equal to 1 if the the resource is an icon.
	 * <br>
	 * If the resource is a cursor, this value is equal to the horizontal hotspot for the cursor.
	 *
	 * @return the number of planes in the image.
	 */
	public short getPlanes() {
		return this.wPlanes;
	}
	
	/**
	 * Retrieves the image's bits per pixel count.
	 * <br>
	 * If the resource is a cursor, this value is equal to the vertical hotspot for the cursor.
	 *
	 * @return the bits per pixel for the image.
	 */
	public short getBitCount() {
		return this.wBitCount;
	}
	
	/**
	 * Retrieves the size in bytes of the image.
	 *
	 * @return the size in bytes of the image.
	 */
	public int getBytesInResource() {
		return this.dwBytesInRes;
	}
	
	/**
	 * Retrieves the location of the image within the icon file.
	 * This offset is relative to the beginning of the icon file.
	 *
	 * @return the offset of the image from the beginning of the icon file.
	 */
	public int getImageOffset() {
		return this.dwImageOffset;
	}
	
	/**
	 * Retrieves the bitmap information for the image. This value will be {@code null}
	 * if the entry represents a <em>PNG</em> compressed image.
	 *
	 * @return the bitmap information for the image.
	 */
	public IconImage getIconImage() {
		return this.info;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format(FORMAT, this.bWidth, this.bHeight, this.bColorCount,
			this.bReserved, this.wPlanes, this.wBitCount, this.dwBytesInRes, this.dwImageOffset, this.info);
	}
}
