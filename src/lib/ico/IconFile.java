/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package lib.ico;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

/**
 * The {@code IconFile} class provides the ability to read the image information
 * contained in tradition Windows icon and cursor files as well as the newer <em>PNG</em>
 * compressed icons found in Windows Vista or higher operating systems.
 * <br><br>
 * An {@code IconFile} object can be created from a {@link String} or {@link File}
 * that represents an icon file on the local computer. A {@link URL} can be used to specify
 * an icon file on a remote computer.
 * <br><br>
 * Once an {@code IconFile} object is created the user can query the information found in the file
 * using the {@link #getIconDirectory()} method and can also get a list of all the images in the
 * file using the {@link #getImages()} method.
 * <br><br>
 * <strong>NOTE:</strong> the list of images may contain {@code null} values if an image failed to
 * load.
 *
 * @author myinon
 * @version 1.0
 * @see IconDir
 */
public final class IconFile {
	private static final int PNG_MAGIC_1 = 0x474e5089;
	private static final int PNG_MAGIC_2 = 0x0a1a0a0d;
	
	private IconDir directory;
	private List<Image> imgs;
	
	/**
	 * Creates an {@code IconFile} object. This object represents either a Windows Icon File or Cursor.
	 *
	 * @param file a {@code String} path to the icon or cursor file.
	 * @throws IOException if an error occurs while reading the file.
	 */
	public IconFile(String file) throws IOException {
		try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file))) {
			this.loadIcon(bin);
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Creates an {@code IconFile} object. This object represents either a Windows Icon File or Cursor.
	 *
	 * @param file a {@code File} object representing the icon or cursor file.
	 * @throws IOException if an error occurs while reading the file.
	 */
	public IconFile(File file) throws IOException {
		try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file))) {
			this.loadIcon(bin);
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Creates an {@code IconFile} object. This object represents either a Windows Icon File or Cursor.
	 *
	 * @param url a {@code URL} object representing a network path to the icon or cursor file.
	 * @throws IOException if an error occurs while reading the file.
	 */
	public IconFile(URL url) throws IOException {
		try (BufferedInputStream bin = new BufferedInputStream(url.openStream())) {
			this.loadIcon(bin);
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Creates an {@code IconFile} object. This object represents either a Windows Icon File or Cursor.
	 *
	 * @param in an {@code InputStream} object representing a stream of bytes of the icon or cursor file.
	 * @throws IOException if an error occurs while reading the file.
	 */
	public IconFile(InputStream in) throws IOException {
		this.loadIcon(new BufferedInputStream(in));
	}
	
	/**
	 * Creates an {@code IconFile} object. This object represents either a Windows Icon File or Cursor.
	 *
	 * @param in a {@code ReadableByteChannel} object representing a stream of bytes of the icon or cursor file.
	 * @throws IOException if an error occurs while reading the file.
	 */
	public IconFile(ReadableByteChannel in) throws IOException {
		this.loadIcon(new BufferedInputStream(Channels.newInputStream(in)));
	}
	
	/**
	 * Retrieves the {@code IconDir} of the icon or cursor file.
	 * An {@code IconDir} contains information about the type of the file
	 * and also how many images it contains.
	 *
	 * @return the {@code IconDir} of the file.
	 */
	public IconDir getIconDirectory() {
		return this.directory;
	}
	
	/**
	 * Retrieves the image for the icon at the specified index.
	 *
	 * @param index the index for the icon whose image should be retrieved.
	 * @return an {@code Image} object representing the icon.
	 */
	public Image getImage(int index) {
		return this.imgs.get(index);
	}
	
	/**
	 * Retrieves an unmodifiable list of all the images in the icon file.
	 *
	 * @return a list of all the images in the file.
	 */
	public List<Image> getImages() {
		return Collections.unmodifiableList(this.imgs);
	}
	
	/*
	 * Loads the icon data from a stream.
	 */
	private void loadIcon(InputStream in) throws IOException {
		long byte_count = 0L;
		// First read the directory info
		short idReserved = this.readShortLE(in);
		if (idReserved != 0) throw new IOException("Reserved word is not equal to 0."); // This must be zero
		short idType = this.readShortLE(in);
		if ((idType != 1) && (idType != 2)) throw new IOException("Only icon or cursor resource types are supported.");
		short idCount = this.readShortLE(in);
		byte_count += 6;
		
		this.directory = new IconDir(idReserved, idType, idCount);
		List<IconDirEntry> entries = new ArrayList<>(idCount);
		
		// Read in all of the directory entries
		for (short i = 0; i < idCount; i++) {
			byte[] b = new byte[4];
			in.read(b);
			short wPlanes = this.readShortLE(in);
			short wBitCount = this.readShortLE(in);
			int dwBytesInRes = this.readIntLE(in);
			int dwImageOffset = this.readIntLE(in);
			byte_count += 16;
			
			if (b[3] != 0) continue; // Reserved byte must be equal to zero
			entries.add(new IconDirEntry(b[0], b[1], b[2], b[3],
				wPlanes, wBitCount, dwBytesInRes, dwImageOffset));
		}
		
		this.setField(this.directory.getClass(), "idEntries",
			this.directory, entries.toArray(new IconDirEntry[entries.size()]));
		
		this.imgs = new ArrayList<>(idCount);
		List<Short> randomAccess = new LinkedList<>();
		for (short i = 0; i < idCount; i++) {
			this.imgs.add(null);
			randomAccess.add(i);
		}
		
		for (short i = 0; i < idCount; i++) {
			int entryIdx = 0;
			Iterator<Short> itr = randomAccess.iterator();
			while (itr.hasNext()) {
				entryIdx = itr.next();
				IconDirEntry ide = entries.get(entryIdx);
				if (ide.getImageOffset() == (int) byte_count) {
					itr.remove();
					break;
				}
			}
			int biSize = this.readIntLE(in);
			
			if (biSize == 40) { // BITMAPINFOHEADER
				int biWidth = this.readIntLE(in);
				int biHeight = this.readIntLE(in);
				short biPlanes = this.readShortLE(in);
				short biBitCount = this.readShortLE(in);
				int biCompression = this.readIntLE(in);
				int biSizeImage = this.readIntLE(in);
				int biX = this.readIntLE(in);
				int biY = this.readIntLE(in);
				int biClrU = this.readIntLE(in);
				int biClrI = this.readIntLE(in);
				byte_count += 40;
				
				BitmapInfoHeader header = new BitmapInfoHeader(biSize, biWidth, biHeight,
					biPlanes, biBitCount, biCompression, biSizeImage, biX, biY, biClrU, biClrI);
				
				IconDirEntry entry = entries.get(entryIdx);
				long bColorCount = entry.getColorCount();
				
				// bColorCount is determined from planes count and bit count
				// Leave bColorCount as 0 if planes = 1 and bit count is 16, 24, or 32
				if (bColorCount == 0L) {
					if (biPlanes == 1) {
						if (biBitCount == 1) {
							bColorCount = 2L;
						} else if (biBitCount == 4) {
							bColorCount = 16L;
						} else if (biBitCount == 8) {
							bColorCount = 256L;
						} else /*if (biBitCount != 32)*/ {
							//bColorCount = (long) Math.pow(2, biBitCount);
							bColorCount = 0;
						}
					} else {
						bColorCount = (long) Math.pow(2, biBitCount * biPlanes);
					}
				}
				
				BufferedImage img = new BufferedImage(biWidth, biHeight / 2, BufferedImage.TYPE_INT_ARGB);
				RGBQuad[] rgbq = null;
				if (bColorCount != 0) {
					rgbq = new RGBQuad[(int) bColorCount];
					
					// Read in the color table
					for (long j = 0; j < bColorCount; j++) {
						byte b = (byte) (in.read() & 0xFF);
						byte g = (byte) (in.read() & 0xFF);
						byte r = (byte) (in.read() & 0xFF);
						byte s = (byte) (in.read() & 0xFF);
						byte_count += 4;
						if (j < rgbq.length) {
							rgbq[(int) j] = new RGBQuad(b, g, r, s);
						}
					}
				}
				
				int[] masks = {128, 64, 32, 16, 8, 4, 2, 1};
				int scanline_xor = ((((biWidth * biBitCount) + 31) / 32) * 4);
				int scanline_and = (((biWidth + 31) / 32) * 4);
				byte[] xor = new byte[scanline_xor * img.getHeight()];
				byte[] and = new byte[scanline_and * img.getHeight()];
				
				if (bColorCount == 2L) {
					in.read(xor);
					in.read(and);
					byte_count += (xor.length + and.length);
					
					for (int y = 0; y < img.getHeight(); y++) {
						int h = img.getHeight() - y - 1;
						for (int x = 0; x < biWidth; x++) {
							int index = -1;
							int d = (y * scanline_xor) + (x / 8);
							if ((xor[d] & masks[(x % 8)]) == 0) { // Bit is cleared
								index = 0;
							} else {
								index = 1;
							}
							
							RGBQuad q = rgbq[index];
							int rgb = (((q.getRed() << 16) & 0x00FF0000) | ((q.getGreen() << 8) & 0x0000FF00)
								| (q.getBlue() & 0x000000FF));
							
							int m = (and[d] & masks[x % 8]);
							if (m == 0) {
								rgb |= 0xFF000000;
							}
							img.setRGB(x, h, rgb);
						}
					}
				} else if (bColorCount == 16L) {
					in.read(xor);
					in.read(and);
					byte_count += (xor.length + and.length);
					
					for (int y = 0; y < img.getHeight(); y++) {
						int h = img.getHeight() - y - 1;
						for (int x = 0; x < biWidth; x++) {
							int index = -1;
							if ((x & 1) == 0) { // Even column
								index = ((xor[(y * scanline_xor) + (x / 2)] & 0xFF) >> 4);
							} else {
								index = (xor[(y * scanline_xor) + (x / 2)] & 0x0F);
							}
							
							RGBQuad q = rgbq[index];
							int rgb = (((q.getRed() << 16) & 0x00FF0000) | ((q.getGreen() << 8) & 0x0000FF00)
								| (q.getBlue() & 0x000000FF));
							
							int m = (and[(y * scanline_and) + (x / 8)] & masks[x % 8]);
							if (m == 0) {
								rgb |= 0xFF000000;
							}
							img.setRGB(x, h, rgb);
						}
					}
				} else if (bColorCount == 256L) {
					in.read(xor);
					in.read(and);
					byte_count += (xor.length + and.length);
					
					for (int y = 0; y < img.getHeight(); y++) {
						int h = img.getHeight() - y - 1;
						for (int x = 0; x < biWidth; x++) {
							int index = (xor[(y * scanline_xor) + x] & 0xFF);
							
							RGBQuad q = rgbq[index];
							int rgb = (((q.getRed() << 16) & 0x00FF0000) | ((q.getGreen() << 8) & 0x0000FF00)
								| (q.getBlue() & 0x000000FF));
							
							int m = (and[(y * scanline_and) + (x / 8)] & masks[x % 8]);
							if (m == 0) {
								rgb |= 0xFF000000;
							}
							img.setRGB(x, h, rgb);
						}
					}
				} else if (bColorCount == 0L) {
					in.read(xor);
					in.read(and);
					byte_count += (xor.length + and.length);
					
					if (biBitCount == 16) {
						for (int y = 0, j = 0; y < img.getHeight(); y++) {
							int h = img.getHeight() - y - 1;
							for (int x = 0; x < biWidth; x++, j += 2) {
								short word = (short) (((xor[j + 1] << 8) & 0xFF00) | (xor[j] & 0x00FF));
								byte b = (byte) (word & 0x1F);
								byte g = (byte) ((word >> 5) & 0x1F);
								byte r = (byte) ((word >> 10) & 0x1F);
								int c = (((r << 16) & 0x00FF0000)
									| ((g << 8) & 0x0000FF00) | (b & 0x000000FF));
								int m = (and[(y * scanline_and) + (x / 8)] & masks[x % 8]);
								if (m == 0) {
									c |= 0xFF000000;
								}
								img.setRGB(x, h, c);
							}
						}
					} else if (biBitCount == 24) {
						for (int y = 0, j = 0; y < img.getHeight(); y++) {
							int h = img.getHeight() - y - 1;
							for (int x = 0; x < biWidth; x++, j += 3) {
								int c = (((xor[j + 2] << 16) & 0x00FF0000)
									| ((xor[j + 1] << 8) & 0x0000FF00) | (xor[j] & 0x000000FF));
								int m = (and[(y * scanline_and) + (x / 8)] & masks[x % 8]);
								if (m == 0) {
									c |= 0xFF000000;
								}
								img.setRGB(x, h, c);
							}
						}
					} else {
						for (int y = img.getHeight() - 1, j = 0; y >= 0; y--) {
							for (int x = 0; x < biWidth; x++, j += 4) {
								int c = (((xor[j + 3] << 24) & 0xFF000000) | ((xor[j + 2] << 16) & 0x00FF0000)
									| ((xor[j + 1] << 8) & 0x0000FF00) | (xor[j] & 0x000000FF));
								img.setRGB(x, y, c);
							}
						}
					}
				}
				imgs.set(entryIdx, img);
				
				IconImage ii = new IconImage(header, rgbq, xor, and);
				this.setField(entry.getClass(), "info", entry, ii);
			} else if (biSize == PNG_MAGIC_1) {
				int png = this.readIntLE(in);
				if (png == PNG_MAGIC_2) {
					try {
						IconDirEntry entry = entries.get(entryIdx);
						byte[] bitmap = new byte[entry.getBytesInResource()];
						this.decomposeIntLE(bitmap, biSize, 0);
						this.decomposeIntLE(bitmap, png, 4);
						in.read(bitmap, 8, entry.getBytesInResource() - 8);
						byte_count += entry.getBytesInResource();
						try {
							BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bitmap));
							imgs.set(entryIdx, bi);
						} catch (IIOException e) {
							//imgs.add(null);
						}
					} catch (Exception e) {
						//imgs.add(null);
					}
				}
			}
		}
	}
	
	/*
	 * Reads a word (2 bytes) from the stream in little-endian order.
	 */
	private short readShortLE(InputStream in) throws IOException {
		int b1 = in.read();
		int b2 = in.read();
		return (short) ((b2 << 8) | b1);
	}
	
	/*
	 * Reads a dword (double word: 4 bytes) from the stream in little-endian order.
	 */
	private int readIntLE(InputStream in) throws IOException {
		int s1 = this.readShortLE(in);
		int s2 = this.readShortLE(in);
		return (((s2 & 0xFFFF) << 16) | (s1 & 0xFFFF));
	}
	
	/*
	 * Decomposes an integer into its 4 byte components and stores them into the array starting at the
	 * specified offset.
	 */
	private void decomposeIntLE(byte[] buf, int num, int offs) {
		buf[offs] = (byte) (num & 0x000000FF);
		buf[offs + 1] = (byte) ((num & 0x0000FF00) >> 8);
		buf[offs + 2] = (byte) ((num & 0x00FF0000) >> 16);
		buf[offs + 3] = (byte) ((num & 0xFF000000) >> 24);
	}
	
	/*
	 * Uses reflection to dynamically set a field in a given class.
	 */
	private <T> void setField(Class<T> clazz, String field, Object obj, Object data) {
		try {
			Field idField = clazz.getDeclaredField(field);
			idField.setAccessible(true);
			idField.set(obj, data);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
