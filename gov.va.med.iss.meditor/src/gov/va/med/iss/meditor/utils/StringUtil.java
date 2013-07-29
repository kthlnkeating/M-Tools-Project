package gov.va.med.iss.meditor.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class StringUtil {
	public static String streamToString(InputStream stream) {
		Scanner scanner = new Scanner(stream).useDelimiter("\\A");
		String content = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		return content;
	}
	
	public static InputStream stringToStream(String input) throws UnsupportedEncodingException {
		InputStream stream = new ByteArrayInputStream(input.getBytes("UTF-8"));
		return stream;
	}
}
