package com.jalios.gradle.plugin

class JException extends RuntimeException {

	public JException() {
		super();
	}

	public JException(String message, Throwable cause) {
		super(message, cause);
	}

	public JException(String message) {
		super(message);
	}

	public JException(Throwable cause) {
		super(cause);
	}

}
