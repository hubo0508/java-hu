package com.random;

public class RandomNumberTest {
	public static void main(String[] args) {
		RandomNumberTest.makeSevenRandomNumber(3);
	}

	public static void makeSevenRandomNumber(int dept) {
		for (int j = 0; j < dept; j++) {
			int[] sevenDigit = RandomNumber.getSevenDigit(7, 1, 33, 16);
			String[] sevenDigitDoubleDigit = RandomNumber
					.assemblyDoubleDigit(sevenDigit);
			for (int i = 0; i < sevenDigitDoubleDigit.length; i++) {
				System.out.print(sevenDigitDoubleDigit[i] + " ");
			}
			System.out.println();
		}
	}
}
