package net.axfab.amy.expr;

public class OperatorActionString extends OperatorAction {

	@Override
	public void evaluate(Operand parent, Operand left, Operand right) {
		String vA = (String)left.getValue();
		String vB = (String)right.getValue();
		switch(parent.getOperator()) {
		case Add:
			parent.setValue(Primitive.String, vA + vB);
			break;
		case Equals:
			parent.setValue(Primitive.Boolean, vA.equals(vB));
			break;
		case Less:
			parent.setValue(Primitive.Boolean, vA.compareTo(vB) < 0);
			break;
		case LessEq:
			parent.setValue(Primitive.Boolean, vA.compareTo(vB) <= 0);
			break;
		case More:
			parent.setValue(Primitive.Boolean, vA.compareTo(vB) > 0);
			break;
		case MoreEq:
			parent.setValue(Primitive.Boolean, vA.compareTo(vB) >= 0);
			break;
		case NotEquals:
			parent.setValue(Primitive.Boolean, !vA.equals(vB));
			break;
		case NullCoalessence:
			parent.setValue(Primitive.String, vA != null ? vA : vB);
			break;
		default:
			throw new IllegalArgumentException("Invalid operation " + parent.getOperator() + " for type String.");
		}
	}

}
