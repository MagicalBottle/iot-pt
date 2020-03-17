package com.utils;


public class StringUtils {

	public static boolean isNotNull(String value) {
		if (value == null || value.equals(""))
			return false;
		return true;
	}

	public static boolean isNumber(String value) {
		if (value == null || value.equals(""))
			return false;
		try {
			Long a = Long.valueOf(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}




}
