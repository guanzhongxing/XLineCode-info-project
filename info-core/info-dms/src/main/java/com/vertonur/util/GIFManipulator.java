package com.vertonur.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GIFManipulator {

	public static void saveGIFImageToFile(String fileName, InputStream iS) {
		File file = new File(fileName);
		try {
			BufferedInputStream bIS = new BufferedInputStream(iS);
			FileOutputStream fOS = new FileOutputStream(file);
			BufferedOutputStream bOS = new BufferedOutputStream(fOS);
			byte[] bufferedBytes = new byte[4096];
			int size = 0;
			while ((size = bIS.read(bufferedBytes, 0, bufferedBytes.length)) != -1)
				bOS.write(bufferedBytes, 0, size);
			bIS.close();
			bOS.flush();
			fOS.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out
					.println("FileNotFoundException not handled in j2eebbs.GIFManipulator.saveGIFImageToFile");
		} catch (IOException e) {
			System.out
					.println("IOexception not handled in j2eebbs.GIFManipulator.saveGIFImageToFile");
		}
	}
}
