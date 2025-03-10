package ch.fhnw.algd1.converters.utf8;

/*
 * Created on 05.09.2014
 */
/**
 * @author
 */
public class UTF8Converter {
	public static byte[] codePointToUTF(int x) {
		// kann auch weggelassen werden
		if (x < 0 || x > 0b1_1111_1111_1111_1111_1111)
			return null;

		// 7 Bit -> 1 Byte, 11 Bit -> 2 Byte, 16 Bit -> 3 Byte, 21 Bit -> 4 Byte
		int size = (x >>> 7 == 0) ? 1 : (x >>> 11 == 0) ? 2 : (x >>> 16 == 0) ? 3 : 4;
		byte[] bytes = new byte[size];

		if (size == 1) {
			bytes[0] = (byte) x;
		} else if (size == 2) {
			bytes[0] = (byte) (0b11000000 | (x >>> 6));
			bytes[1] = (byte) (0b10000000 | (x & 0b0011_1111));
		} else if (size == 3) {
			bytes[0] = (byte) (0b11100000 | (x >>> 12));
			bytes[1] = (byte) (0b10000000 | ((x >>> 6) & 0b0011_1111));
			bytes[2] = (byte) (0b10000000 | (x & 0b0011_1111));
		} else { // size == 4
			bytes[0] = (byte) (0b11110000 | (x >>> 18));
			bytes[1] = (byte) (0b10000000 | ((x >>> 12) & 0b0011_1111));
			bytes[2] = (byte) (0b10000000 | ((x >>> 6) & 0b0011_1111));
			bytes[3] = (byte) (0b10000000 | (x & 0b0011_1111));
		}

		return bytes;
	}

	public static int UTFtoCodePoint(byte[] bytes) {
		if (bytes.length > 4 || bytes.length < 1 || !isValidUTF8(bytes))
			return 0;

		if (bytes.length == 1)
			return bytes[0] & 0b0111_1111;
		else if (bytes.length == 2)
			return ((bytes[0] & 0b0001_1111) << 6) +
					(bytes[1] & 0b0011_1111);
		else if (bytes.length == 3)
			return ((bytes[0] & 0b0000_1111) << 12) +
					((bytes[1] & 0b0011_1111) << 6) +
					(bytes[2] & 0b0011_1111);
		else
			return ((bytes[0] & 0b0000_0111) << 18) +
					((bytes[1] & 0b0011_1111) << 12) +
					((bytes[2] & 0b0011_1111) << 6) +
					(bytes[3] & 0b0011_1111);
	}

	private static boolean isValidUTF8(byte[] bytes) {
		if (bytes.length == 1)
			return (bytes[0] & 0b1000_0000) == 0;
		else if (bytes.length == 2)
			return ((bytes[0] & 0b1110_0000) == 0b1100_0000)
					&& isFollowup(bytes[1]);
		else if (bytes.length == 3)
			return ((bytes[0] & 0b1111_0000) == 0b1110_0000)
					&& isFollowup(bytes[1]) && isFollowup(bytes[2]);
		else if (bytes.length == 4)
			return ((bytes[0] & 0b1111_1000) == 0b1111_0000)
					&& isFollowup(bytes[1]) && isFollowup(bytes[2]) && isFollowup(bytes[3]);
		else
			return false;
	}

	private static boolean isFollowup(byte b) {
		return (b & 0b1100_0000) == 0b1000_0000;
	}
}
