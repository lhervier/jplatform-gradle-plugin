package com.jalios.gradle.plugin.test.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import groovy.lang.GString;

public class ByteUtils {

	/**
	 * String.getBytes(charset) does not exist in Groovy
	 * @param s
	 * @return
	 */
	public static final byte[] extractBytes(String s) {
		return extractBytes(s, StandardCharsets.UTF_8);
	}
	public static final byte[] extractBytes(GString s) {
		return extractBytes(s.toString(), StandardCharsets.UTF_8);
	}
	public static final byte[] extractBytes(GString s, Charset charset) {
		return extractBytes(s.toString(), charset);
	}
	public static final byte[] extractBytes(String s, Charset charset) {
		return s.getBytes(charset);
	}
	public static final byte[] extractBytes(GString s, String charset) {
		return extractBytes(s.toString(), charset);
	}
	public static final byte[] extractBytes(String s, String charset) {
		try {
			return s.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
