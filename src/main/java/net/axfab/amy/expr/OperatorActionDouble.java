package net.axfab.amy.expr;

public class OperatorActionDouble extends OperatorAction {

	@Override
	public void evaluate(Operand parent, Operand left, Operand right) {

		double vA = (Double)left.getValue();
		double vB = (Double)right.getValue();
		switch(parent.getOperator()) {
		case Add:
			parent.setValue(Primitive.Double, vA + vB);
			break;
		case Div:
			parent.setValue(Primitive.Double, vA + vB);
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
			parent.setValue(Primitive.Double, vA % vB);
			break;
		case More:
			parent.setValue(Primitive.Boolean, vA > vB);
			break;
		case MoreEq:
			parent.setValue(Primitive.Boolean, vA >= vB);
			break;
		case Mul:
			parent.setValue(Primitive.Double, vA * vB);
			break;
		case NotEquals:
			parent.setValue(Primitive.Boolean, vA != vB);
			break;
		case Sub:
			parent.setValue(Primitive.Double, vA - vB);
			break;
		default:
			throw new IllegalArgumentException("Invalid operation " + parent.getOperator() + " for type Double.");
		}

	}

}
