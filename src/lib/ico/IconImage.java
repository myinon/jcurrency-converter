/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package lib.ico;

import java.lang.ref.SoftReference;
import java.text.MessageFormat;

/**
 * The {@code IconImage} class represents the image data for an entry
 * in a Windows icon or cursor file. It contains header information for the
 * bitmap, its color table as well as the color pixels and monochrome pixels.
 * Its format is conveniently described using this C structure:
 * <br><br><em>
 * typedef struct<br>
 * {<br>
 *     BITMAPINFOHEADER icHeader;<br>
 *     RGBQuad icColors[1];<br>
 *     BYTE icXOR[1];<br>
 *     BYTE icAND[1];<br>
 * } ICONIMAGE, *LPICONIMAGE;<br>
 * </em>
 *
 * @author myinon
 * @version 1.0
 * @see BitmapInfoHeader
 * @see RGBQuad
 */
public final class IconImage {
	private static final String FORMAT = "[icHeader={0}, icColorsCount={1}, icXORCount={2}, icANDCount={3}]";
	
	private volatile BitmapInfoHeader icHeader;
	private volatile SoftReference<RGBQuad[]> icColors;
	private volatile SoftReference<byte[]> icXOR;
	private volatile SoftReference<byte[]> icAND;
	
	/**
	 * Creates a new {@code IconImage} object.
	 * An {@code IconImage} contains information about an entry's image specific information such as a
	 * width and height as well as the bytes for the image itself.
	 *
	 * @param bih the header information for the image. This contains the width and height of the image.
	 * @param rgb the color table for the image. This contains the colors used in the image.
	 * @param xor the image pixel data.
	 * @param and the image mask pixel data. This is used to given the image some transparency.
	 */
	IconImage(BitmapInfoHeader bih, RGBQuad[] rgb, byte[] xor, byte[] and) {
		this.icHeader = bih;
		this.icColors = new SoftReference<>(rgb);
		this.icXOR = new SoftReference<>(xor);
		this.icAND = new SoftReference<>(and);
	}
	
	/**
	 * Retrieves the bitmap header information which includes the width and height of the image.
	 *
	 * @return the header information of the image.
	 */
	public BitmapInfoHeader getHeader() {
		return this.icHeader;
	}
	
	/**
	 * Retrieves the image's color table. This value can be {@code null} if the bits per pixel
	 * of the image is &gt; 8.<br><br>
	 *
	 * <strong>Note:</strong>The color table is wrapped in a {@code SoftReference} so that it may be
	 * easily reclaimed by the garbage collector if it is not referenced.
	 *
	 * @return the image's color table or {@code null}.
	 */
	public RGBQuad[] getColorTable() {
		return this.icColors.get();
	}
	
	/**
	 * Retrieves the image's <em>xor</em> bitmap as a byte array. This array represents all of the color
	 * pixels in the image. This value can be {@code null}.<br><br>
	 *
	 * <strong>Note:</strong>The <em>xor</em> bitmap is wrapped in a {@code SoftReference} so that it may be
	 * easily reclaimed by the garbage collector if it is not referenced.
	 *
	 * @return the image's <em>xor</em> (color) bitmap or {@code null}.
	 */
	public byte[] getXORBitmap() {
		return this.icXOR.get();
	}
	
	/**
	 * Retrieves the image's <em>and</em> bitmap as a byte array. This array is used to help provide
	 * transparency information for image's with a bits per pixel that are &#x2260; to 32.
	 * This value can be {@code null}.<br><br>
	 *
	 * <strong>Note:</strong>The <em>and</em> bitmap is wrapped in a {@code SoftReference} so that it may be
	 * easily reclaimed by the garbage collector if it is not referenced.
	 *
	 * @return the image's <em>and</em> (mask) bitmap or {@code null}.
	 */
	public byte[] getANDBitmap() {
		return this.icAND.get();
	}
	
	/**
	 * Clears the references to the image's color table and <em>xor</em> and <em>and</em> bitmaps.
	 * The references will be reclaimed by the garbage collector if they are not being used and
	 * the corresponding {@code get} methods will now return {@code null}.
	 */
	public void clearReferences() {
		this.icColors.clear();
		this.icXOR.clear();
		this.icAND.clear();
	}
	
	@Override
	public String toString() {
		return MessageFormat.format(FORMAT, this.icHeader,
			(this.icColors.get() == null ? 0 : this.icColors.get().length),
			(this.icXOR.get() == null ? 0 : this.icXOR.get().length),
			(this.icAND.get() == null ? 0 : this.icAND.get().length));
	}
}
