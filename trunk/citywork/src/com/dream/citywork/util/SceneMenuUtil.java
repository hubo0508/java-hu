package com.dream.citywork.util;

public class SceneMenuUtil {

	private final static String URL_PREFIX = "1,绝对路径,地址：,";

	private final static String HTML_PREFIX = "5,,页签内容,";

	public static String[] getURL(String value) {

		String[] str = new String[2];

		String[] urlArr = value.split(URL_PREFIX);
		String[] htmlArr = value.split(HTML_PREFIX);
		if (urlArr.length == 2) {
			str[0] = urlArr[1];
			str[1] = "URL";
		} else if (htmlArr.length == 2) {
			str[0] = htmlArr[1];
			str[1] = "HTML";
		}

		return str;
	}

	public static void main(String[] args) {
		String value = "5,,页签d,<TEXTF";
		String[] urlArr = value.split(URL_PREFIX);
		String[] htmlArr = value.split(HTML_PREFIX);
		if (urlArr.length == 2) {
			System.out.println(urlArr[1]);
		} else if (htmlArr.length == 2) {
			System.out.println(htmlArr[1]);
		}
	}
}
