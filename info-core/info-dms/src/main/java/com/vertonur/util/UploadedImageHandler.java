/**
 * 
 */
package com.vertonur.util;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.vertonur.PropertyFile;
import com.vertonur.common.GIFManipulator;
import com.vertonur.common.ImageManipulator;
import com.vertonur.constants.Constants;

/**
 * @author Vertonur
 * 
 */
public class UploadedImageHandler {

	public static String upload(String extension, InputStream imgInputStream,
			String fileName, int width, int height) {

		int type = -1;// -1 represents unknown;
		if (extension.equalsIgnoreCase("png"))
			type = 1;// 1 presnet a png file
		try {
			if (!extension.equalsIgnoreCase("gif")) {
				BufferedImage bi = null;
				bi = ImageManipulator.resizeImage(ImageIO.read(imgInputStream),
						type, width, height);
				ImageManipulator.saveImage(bi, extension, fileName);
			} else {
				GIFManipulator.saveGIFImageToFile(fileName, imgInputStream);
			}

			return Constants.IMAGE_UPLOAD_SUCCESS;
		} catch (FileNotFoundException e) {
			return PropertyFile.getValue(
					Constants.APPLICATIONRESOURCE_ZH_PROPERTY_FILE,
					"error.utils.UploadedImageHandler.fileNotFound");
		} catch (IOException e) {
			return PropertyFile.getValue(
					Constants.APPLICATIONRESOURCE_ZH_PROPERTY_FILE,
					"error.utils.UploadedImageHandler.imageUploadedFail");
		}
	}
}
