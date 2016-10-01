package net.axfab.amy.expr;

public abstract class OperatorAction {

	public static OperatorAction find(Operator operator, Primitive type, Primitive type2) {
		if (type == type2) {
			switch (type) {
			case Boolean:
				return new OperatorActionBoolean();

			case Byte:
			case SByte:
			case Short:
			case UShort:
			case Int:
				return new OperatorActionInteger();

			case Float:
			case Double:
				return new OperatorActionDouble();

			case String:
				return new OperatorActionString();

			case DateOnly:
			case DateTime:
				break;
			default:
				break;
			}
		}
		return null;
	}

	public abstract void evaluate(Operand parent, Operand left, Operand right);
}
