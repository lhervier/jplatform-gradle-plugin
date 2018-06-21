package com.jalios.gradle.plugin.test.util;

import java.nio.charset.StandardCharsets;

import groovy.lang.GString;

public class ByteUtils {

	/**
	 * String.getBytes(charset) does not exist in Groovy
	 * @param s
	 * @return
	 */
	public static final byte[] extractBytes(String s) {
		return s.getBytes(StandardCharsets.UTF_8);
	}
	public static final byte[] extractBytes(GString s) {
		return s.toString().getBytes(StandardCharsets.UTF_8);
	}
}
