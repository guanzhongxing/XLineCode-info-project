/**
 * 
 */
package com.vertonur;

import java.util.Date;

/**
 * @author Vertonur This class is used to keep common code in a place
 */
public class CommonUtil {

	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c >= 0) && (c <= 255))
				sb.append(c);
			else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
					System.out.println(ex);
					b = new byte[0];
				}

				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) {
						k += 256;
					}
					sb.append('%').append(Integer.toHexString(k).toUpperCase());
				}
			}
		}

		return sb.toString();
	}

	public static int strToIntTransitionBuffer(String value) {
		if (value != null && !value.equals(""))
			return new Integer(value);
		else
			return 0;
	}

	public static int daysUntilToday(Date today, Date from) {
		int days = (int) ((today.getTime() - from.getTime()) / (24 * 60 * 60 * 1000));
		return days == 0 ? 1 : days;
	}
}
