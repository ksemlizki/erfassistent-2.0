package org.assist.components.calculator;

import org.assist.tools.Tools;

public class Operation {

	enum Type {
		OPERATOR, DOUBLE
	}

	private Type type;
	private Operator operator;
	private Double value;

	public Type getType() {
		return type;
	}

	public Operation(Operator operator) {
		this.operator = operator;
		type = Type.OPERATOR;
	}

	public Operation(Double value) {
		this.value = value;
		type = Type.DOUBLE;
	}

	public Operator getOperator() {
		return operator;
	}

	public Double getValue() {
		return value;
	}

	public double execute(double value1, double value2) {
		switch (type) {
			case DOUBLE:
				return value.doubleValue();
			case OPERATOR:
				switch (operator) {
					case DIV:
						return value1 / value2;
					case MUL:
						return value1 * value2;
					case PLUS:
						return value1 + value2;
					case MINUS:
						return value1 - value2;
					case PROZENT:
						return value2 * value1 / 100;
					case SQRT:
						return Math.sqrt(value1);
					case X1:
						return (1 / value1);
				}
				break;
		}

		return value.doubleValue();
	}

	@Override
	public String toString() {
		switch (type) {
			case DOUBLE:
				return Tools.DECIMAL_FORMAT.format(value);
			case OPERATOR:
				switch (operator) {
					case DIV:
						return "/";
					case MUL:
						return "*";
					case PLUS:
						return "+";
					case MINUS:
						return "-";
					case PROZENT:
						return "%";
					case SQRT:
						return "SQRT";
					case X1:
						return "1 / X";
				}
				break;
		}
		return "";
	}
}
