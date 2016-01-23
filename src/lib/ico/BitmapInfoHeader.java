/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package lib.ico;

import java.text.MessageFormat;

/**
 * The {@code BitmapInfoHeader} class represents the bitmap information header structure
 * of a Windows bitmap object. This structure contains information such as width and height
 * as well as bits per pixel of the immediately following bitmap arrays of the image in order
 * to back up the information in the {@code IconDirEntry}.
 * The {@code BitmapInfoHeader} format is conveniently described using this C structure:
 * <br><br><em>
 * typedef struct tagBITMAPINFOHEADER<br>
 * {<br>
 *     DWORD biSize;<br>
 *     DWORD biWidth;<br>
 *     DWORD biHeight;<br>
 *     WORD biPlanes;<br>
 *     WORD biBitCount;<br>
 *     DWORD biCompression;<br>
 *     DWORD biSizeImage;<br>
 *     DWORD biXPelsPerMeter;<br>
 *     DWORD biYPelsPerMeter;<br>
 *     DWORD biClrUsed;<br>
 *     DWORD biClrImportant;<br>
 * } BITMAPINFOHEADER, *PBITMAPINFOHEADER;<br>
 * </em>
 *
 * @author myinon
 * @version 1.0
 * @see IconDirEntry
 * @see IconImage
 */
public class BitmapInfoHeader {
	private static final String FORMAT = "[biSize={0}, biWidth={1}, biHeight={2}, biPlanes={3}, biBitCount={4}, biCompression={5}, biSizeImage={6}, biXPelsPerMeter={7}, biYPelsPerMeter={8}, biClrUsed={9}, biClrImportant={10}]";
	
	private int   biSize;          // Size of object in bytes
	private int   biWidth;         // Width in pixels
	private int   biHeight;        // Height in pixels
	private short biPlanes;        // Planes
	private short biBitCount;      // Bits per pixel
	private int   biCompression;   // Type of compression for bitmap
	private int   biSizeImage;     // Size in bytes of bitmap
	private int   biXPelsPerMeter; // Horizontal resolution in pixels per meter
	private int   biYPelsPerMeter; // Vertical resolution in pixels per meter
	private int   biClrUsed;       // Number of color indices used
	private int   biClrImportant;  // Number of color indices that are required
	
	/**
	 * Creates a new {@code BitmapInfoHeader} object.
	 * A {@code BitmapInfoHeader} contains information about the bitmap data for
	 * each image in the icon file.
	 *
	 * @param sz the size of the {@code BitmapInfoHeader} in bytes.
	 * @param w  the width of the bitmap.
	 * @param h  the height of the bitmap.
	 * @param p  the number of planes in the image.
	 * @param bc the number of bits per pixel in the image.
	 * @param c  the compression type of the bitmap.
	 * @param si the size of the image bitmap in bytes.
	 * @param x  the horizontal resolution of the bitmap in pixels per meter.
	 * @param y  the vertical resolution of the bitmap in pixels per meter.
	 * @param u  the number of color indices used in the bitmap color table.
	 * @param ci the number of color indices that are required for the bitmap.
	 */
	BitmapInfoHeader(int sz, int w, int h, short p, short bc, int c, int si, int x, int y, int u, int ci) {
		this.biSize = sz;
		this.biWidth = w;
		this.biHeight = h;
		this.biPlanes = p;
		this.biBitCount = bc;
		this.biCompression = c;
		this.biSizeImage = si;
		this.biXPelsPerMeter = x;
		this.biYPelsPerMeter = y;
		this.biClrUsed = u;
		this.biClrImportant = ci;
	}
	
	/**
	 * Retrieves the number of bytes contained in {@code BitmapInfoHeader}.
	 *
	 * @return the size in bytes of {@code BitmapInfoHeader}.
	 */
	public int getSize() {
		return this.biSize;
	}
	
	/**
	 * Retrieves the width in pixels of the bitmap.
	 *
	 * @return the width of the bitmap in pixels.
	 */
	public int getWidth() {
		return this.biWidth;
	}
	
	/**
	 * Retrieves the height in pixels of the bitmap.
	 *
	 * @return the height of the bitmap in pixels.
	 */
	public int getHeight() {
		return this.biHeight;
	}
	
	/**
	 * Retrieves the number of planes in the bitmap. This must be 1.
	 *
	 * @return the number of planes in the bitmap.
	 */
	public short getPlanes() {
		return this.biPlanes;
	}
	
	/**
	 * Retrieves the number of bits per pixel of the bitmap.
	 * If the number is:
	 * <br>
	 * <table border=1>
	 * <tr><th>Value</th><th>Meaning</th></tr>
	 * <tr><td>1</td><td>The bitmap is monochrome and contains only two colors.</td></tr>
	 * <tr><td>4</td><td>The bitmap contains at the most 16 colors.</td></tr>
	 * <tr><td>8</td><td>The bitmap contains at the most 256 colors.</td></tr>
	 * <tr><td>16</td><td>The bitmap contains at the most 2^16 colors.</td></tr>
	 * <tr><td>24</td><td>The bitmap contains at the most 2^24 colors.</td></tr>
	 * <tr><td>32</td><td>The bitmap contains at the most 2^32 colors.</td></tr>
	 * </table>
	 *
	 * @return the number of bits per pixel of the bitmap.
	 */
	public short getBitCount() {
		return this.biBitCount;
	}
	
	/**
	 * Retrieves the type of the compression of the bitmap.
	 * This value should be 0.
	 *
	 * @return the compression type of the bitmap.
	 */
	public int getCompression() {
		return this.biCompression;
	}
	
	/**
	 * Retrieves the size of the bitmap in bytes.
	 *
	 * @return the size of the bitmap.
	 */
	public int getSizeImage() {
		return this.biSizeImage;
	}
	
	/**
	 * Retrieves the horizontal resolution of the bitmap in pixels per meter.
	 * This value should be 0.
	 *
	 * @return the horizontal resolution of the bitmap.
	 */
	public int getXPelsPerMeter() {
		return this.biXPelsPerMeter;
	}
	
	/**
	 * Retrieves the vertical resolution of the bitmap in pixels per meter.
	 * This value should be 0.
	 *
	 * @return the vertical resolution of the bitmap.
	 */
	public int getYPelsPerMeter() {
		return this.biYPelsPerMeter;
	}
	
	/**
	 * Retrieves the number of color indices actually used by the bitmap.
	 * This value should be 0.
	 *
	 * @return the number of color indices used by the bitmap.
	 */
	public int getColorUsed() {
		return this.biClrUsed;
	}
	
	/**
	 * Retrieves the number of color indices that are required to display the bitmap.
	 * This value should be 0.
	 *
	 * @return the number of color indices that are required for the bitmap.
	 */
	public int getColorImportant() {
		return this.biClrImportant;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format(FORMAT, this.biSize, this.biWidth, this.biHeight, this.biPlanes,
			this.biBitCount, this.biCompression, this.biSizeImage, this.biXPelsPerMeter, this.biYPelsPerMeter,
			this.biClrUsed, this.biClrImportant);
	}
}
