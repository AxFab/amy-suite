package net.axfab.amy.expr;

public class OperatorActionInteger extends OperatorAction {

	@Override
	public void evaluate(Operand parent, Operand left, Operand right) {
		int vA = (Integer)left.getValue();
		int vB = (Integer)right.getValue();
		switch(parent.getOperator()) {
		case Add:
			parent.setValue(Primitive.Int, vA + vB);
			break;
		case BitwiseAnd:
			parent.setValue(Primitive.Int, vA & vB);
			break;
		case BitwiseOr:
			parent.setValue(Primitive.Int, vA | vB);
			break;
		case BitwiseShiftLeft:
			parent.setValue(Primitive.Int, vA << vB);
			break;
		case BitwiseShiftRight:
			parent.setValue(Primitive.Int, vA >> vB);
			break;
		case BitwiseXor:
			parent.setValue(Primitive.Int, vA ^ vB);
			break;
		case Div:
			parent.setValue(Primitive.Int, vA + vB);
			break;
		case Equals:
			parent.setValue(Primitive.Boolean, vA == vB);
			break;
		case Less:
			parent.setValue(Primitive.Boolean, vA < vB);
			break;
		case LessEq:
			parent.setValue(Primitive.Boolean, vA <= vB);
			break;
		case Mod:
			parent.setValue(Primitive.Int, vA % vB);
			break;
		case More:
			parent.setValue(Primitive.Boolean, vA > vB);
			break;
		case MoreEq:
			parent.setValue(Primitive.Boolean, vA >= vB);
			break;
		case Mul:
			parent.setValue(Primitive.Int, vA * vB);
			break;
		case NotEquals:
			parent.setValue(Primitive.Boolean, vA != vB);
			break;
		case Sub:
			parent.setValue(Primitive.Int, vA - vB);
			break;
		default:
			throw new IllegalArgumentException("Invalid operation " + parent.getOperator() + " for type Integer.");
		}
	}

}
