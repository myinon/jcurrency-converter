/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package lib.ico;

import java.text.MessageFormat;

/**
 * The {@code RGBQuad} class represents a color in a bitmap's color table.
 * Its format is conveniently described using this C structure:
 * <br><br><em>
 * typedef struct tagRGBQUAD<br>
 * {<br>
 *     BYTE rgbBlue;<br>
 *     BYTE rgbGreen;<br>
 *     BYTE rgbRed;<br>
 *     BYTE rgbReserved;<br>
 * } RGBQuad;<br>
 * </em>
 *
 * @author myinon
 * @version 1.0
 * @see IconDirEntry
 * @see IconImage
 * @see BitmapInfoHeader
 */
public final class RGBQuad {
	private static final String FORMAT = "[blue={0}, green={1}, red={2}, reserved={3}]";
	
	private byte blue;
	private byte green;
	private byte red;
	private byte reserved;
	
	/**
	 * Creates a new {@code RGBQuad} object.
	 * An {@code RGBQuad} object contains color information such as the amount
	 * of red, green, and blue that is in a color.
	 *
	 * @param b the blue component as a byte.
	 * @param g the green component as a byte.
	 * @param r the red component as a byte.
	 * @param s a reserved byte.
	 */
	RGBQuad(byte b, byte g, byte r, byte s) {
		this.blue = b;
		this.green = g;
		this.red = r;
		this.reserved = s;
	}
	
	/**
	 * Retrieves the blue color component as a byte.
	 *
	 * @return the blue color component.
	 */
	public byte getBlue() {
		return this.blue;
	}
	
	/**
	 * Retrieves the green color component as a byte.
	 *
	 * @return the green color component.
	 */
	public byte getGreen() {
		return this.green;
	}
	
	/**
	 * Retrieves the red color component as a byte.
	 *
	 * @return the red color component.
	 */
	public byte getRed() {
		return this.red;
	}
	
	/**
	 * Retrieves the color as a 24-bit value.
	 *
	 * @return the color in 24-bit format.
	 */
	public int getColor() {
		return (((this.red << 16) & 0x00FF0000) | ((this.green << 8) & 0x0000FF00)
			| (this.blue & 0x000000FF));
	}
	
	@Override
	public String toString() {
		return MessageFormat.format(FORMAT, this.blue, this.green, this.red, this.reserved);
	}
}
