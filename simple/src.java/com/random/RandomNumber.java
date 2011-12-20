package com.random;

/**
 * <p>随即产生七位数</p>
 * @User: HUBO
 * @Date Dec 20, 2011 
 * @Time 2:19:50 PM 
 * 
 * <p>Write a detailed description</p>
 */
public class RandomNumber {

	public static String[] assemblyDoubleDigit(int[] random) {

		String[] strArr = new String[random.length];
		for (int i = 0; i < random.length; i++) {
			String singleStr = random[i] + "";
			String single = singleStr.length() == 1 ? "0" + singleStr
					: singleStr;
			strArr[i] = single;
		}

		return strArr;
	}

	public static int[] getSevenDigit(int number, int startNumber,
			int endNumber, int lastNumber) {

		int[] num = new int[number];
		for (int j = 0; j < number - 1; j++) {
			int randomnum = genRandom(startNumber, endNumber);
			if (theOnly(num, randomnum)) {
				num[j] = randomnum;
			} else {
				num[j] = uniqueValue(num, startNumber, endNumber);
			}
		}
		orderAscend(num);
		num[number - 1] = genRandom(startNumber, lastNumber);

		return num;
	}

	private static int[] orderAscend(int[] num) {

		int len = num.length - 1;
		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				if (num[i] > num[j]) {
					int temp = num[i];
					num[i] = num[j];
					num[j] = temp;
				}
			}
		}

		return num;
	}

	private static int genRandom(int start, int end) {
		if (start > end || start < 0 || end < 0) {
			return -1;
		}
		return (int) (Math.random() * (end - start + 1)) + start;
	}

	private static boolean theOnly(int[] num, int number) {
		boolean flag = true;
		for (int i = 0; i < num.length; i++) {
			if (number == num[i]) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	private static int uniqueValue(int[] num, int start, int end) {

		int newValue = 0;
		while (true) {
			boolean flag = true;
			newValue = genRandom(start, end);
			for (int i = 0; i < num.length; i++) {
				if (newValue == num[i]) {
					flag = false;
					break;
				}
			}
			if (flag) {
				break;
			}
		}
		return newValue;
	}

}
