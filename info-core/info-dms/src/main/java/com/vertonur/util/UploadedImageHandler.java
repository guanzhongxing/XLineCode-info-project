/**
 * 
 */
package com.vertonur.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * @author Vertonur
 * 
 */
public class UploadedImageHandler {

	public static void upload(String extension, InputStream imgInputStream,
			String fileName, int width, int height) throws IOException {

		int type = -1;// -1 represents unknown;
		if (extension.equalsIgnoreCase("png"))
			type = 1;// 1 presnet a png file
		if (!extension.equalsIgnoreCase("gif")) {
			BufferedImage bi = null;
			bi = ImageManipulator.resizeImage(ImageIO.read(imgInputStream),
					type, width, height);
			ImageManipulator.saveImage(bi, extension, fileName);
		} else {
			GIFManipulator.saveGIFImageToFile(fileName, imgInputStream);
		}
	}
}
