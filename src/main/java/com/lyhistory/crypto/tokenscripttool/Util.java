package com.lyhistory.crypto.tokenscripttool;

import sun.misc.BASE64Encoder;

public class Util {
	static char[] carr = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String convertHexToBase64String(String input) {
		byte barr[] = new byte[16];
		int bcnt = 0;
		for (int i = 0; i < 32; i += 2) {
			char c1 = input.charAt(i);
			char c2 = input.charAt(i + 1);
			int i1 = convertCharToInt(c1);
			int i2 = convertCharToInt(c2);

			barr[bcnt] = 0;
			barr[bcnt] |= (byte) ((i1 & 0x0F) << 4);
			barr[bcnt] |= (byte) (i2 & 0x0F);
			bcnt++;
		}

		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(barr);
	}

	private static int convertCharToInt(char c) {
		char clower = Character.toLowerCase(c);
		for (int i = 0; i < carr.length; i++) {
			if (clower == carr[i]) {
				return i;
			}
		}
		return 0;
	}

	public static byte[] convertHexToByteArray(String inHex) {
		int hexlen = inHex.length();
		byte[] result;
		if (hexlen % 2 == 1) {
			hexlen++;
			result = new byte[(hexlen / 2)];
			inHex = "0" + inHex;
		} else {
			result = new byte[(hexlen / 2)];
		}
		int j = 0;
		for (int i = 0; i < hexlen; i += 2) {
			result[j] = convertHexToByte(inHex.substring(i, i + 2));
			j++;
		}
		return result;
	}

	public static byte convertHexToByte(String inHex) {
		return (byte) Integer.parseInt(inHex, 16);
	}

	public static String convertByteArrayToHex(byte[] byteArray) {
		StringBuffer hexStringBuffer = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			hexStringBuffer.append(convertByteToHex(byteArray[i]));
		}
		return hexStringBuffer.toString();
	}

	public static String convertByteToHex(byte num) {
		char[] hexDigits = new char[2];
		hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
		hexDigits[1] = Character.forDigit((num & 0xF), 16);
		return new String(hexDigits);
	}
}
