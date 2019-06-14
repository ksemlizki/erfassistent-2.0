package org.assist.tools;

import java.awt.Component;

public interface IExceptionHandler {

	public void handle(Throwable throwable);
	public void handle(Component parent, Throwable throwable);

	public void debug(String text);
}
