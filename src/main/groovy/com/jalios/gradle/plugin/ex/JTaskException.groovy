package com.jalios.gradle.plugin.ex

class JTaskException extends RuntimeException {

	public JTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public JTaskException(String message) {
		super(message);
	}

	public JTaskException(Throwable cause) {
		super(cause);
	}

}
