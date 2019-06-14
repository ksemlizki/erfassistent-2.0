package org.assist.tools;

import java.awt.Component;

public class ExceptionHandler {

	private static IExceptionHandler exceptionHandler;

	public static void registerExceptionHandler(IExceptionHandler pExceptionHandler) {
		ExceptionHandler.exceptionHandler = pExceptionHandler;
	}

	public static void handle(Throwable exception) {
		if (exceptionHandler != null) {
			exceptionHandler.handle(exception);
		}
		else {
			exception.printStackTrace();
		}
	}

	public static void handle(Component parent, Throwable exception) {
		if (exceptionHandler != null) {
			exceptionHandler.handle(parent, exception);
		}
		else {
			exception.printStackTrace();
		}
	}

	public static void debug(String text) {
		if (exceptionHandler != null) {
			exceptionHandler.debug(text);
		}
	}

}
