package net.axfab.amy.expr;

import java.util.List;

import net.axfab.amy.data.DataError;

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

	public static void resolve(Operand parent, List<Resolver> parameters) throws ParserException, DataError  {
		switch (parent.getName().toLowerCase()) {
		case "length":
			length(parent, parameters);
			break;
		}
	}

	private static void length(Operand parent, List<Resolver> parameters) throws ParserException, DataError {
		if (parameters.size() != 1) {
			throw new DataError("Wrong number of argument in length() method.");
		} 
		Resolver data = parameters.get(0);
		Operand value = data.getResult();
		if (value.getType() != Primitive.String) {
			throw new DataError("Argument of length() must be a string.");
		} else if (value.getValue() == null ) {
			throw new DataError("Argument of length() can't be null.");
		}
		int lg = ((String)value.getValue()).length();
		parent.setValue(Primitive.Int, lg);
	}
}
