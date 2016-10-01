package net.axfab.amy.expr;

public class OperatorActionBoolean extends OperatorAction {

	@Override
	public void evaluate(Operand parent, Operand left, Operand right) {

		boolean vA = (Boolean)left.getValue();
		boolean vB = (Boolean)right.getValue();
		switch(parent.getOperator()) {
		case And:
			parent.setValue(Primitive.Boolean, vA && vB);
			break;
		case Equals:
			parent.setValue(Primitive.Boolean, vA == vB);
			break;
		case NotEquals:
			parent.setValue(Primitive.Boolean, vA != vB);
			break;
		case Or:
			parent.setValue(Primitive.Boolean, vA || vB);
			break;
		default:
			throw new IllegalArgumentException("Invalid operation " + parent.getOperator() + " for type Boolean.");
		}


	}

}
