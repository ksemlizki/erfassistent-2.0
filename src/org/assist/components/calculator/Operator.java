package org.assist.components.calculator;

public enum Operator {
	PLUS, MINUS, SQRT(true), PROZENT, MUL, DIV, X1(true);

	boolean unary;

	private Operator() {
		this(false);
	}

	private Operator(boolean unary) {
		this.unary = unary;
	}

	public boolean isUnary() {
		return unary;
	}
}
