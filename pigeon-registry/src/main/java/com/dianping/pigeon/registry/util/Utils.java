/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.registry.util;

public final class Utils {

	public static long getLong(final byte[] b) {

		assert b.length == 8 : "Invalid number of bytes for long conversion";
		int high = getInt(new byte[] { b[0], b[1], b[2], b[3] });
		int low = getInt(new byte[] { b[4], b[5], b[6], b[7] });
		return ((long) (high) << 32) + (low & 0xFFFFFFFFL);
	}

	public static int getInt(final byte[] b) {

		assert b.length == 4 : "Invalid number of bytes for integer conversion";
		return ((b[0] << 24) & 0xFF000000) + ((b[1] << 16) & 0x00FF0000) + ((b[2] << 8) & 0x0000FF00)
				+ (b[3] & 0x000000FF);
	}

	public static void validateWeight(int weight) {
		if (weight < Constants.MIN_WEIGHT || weight > Constants.MAX_WEIGHT)
			throw new IllegalArgumentException("weight should be in range [" + Constants.MIN_WEIGHT + "-"
					+ Constants.MAX_WEIGHT + "]");
	}

	public static String unescapeServiceName(String serviceName) {
		return serviceName.replace(Constants.PLACEHOLDER, Constants.PATH_SEPARATOR);
	}

	public static String escapeServiceName(String serviceName) {
		return serviceName.replace(Constants.PATH_SEPARATOR, Constants.PLACEHOLDER);
	}
}
