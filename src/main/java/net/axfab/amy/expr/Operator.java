/*    This file is part of AmySuite.

    AmySuite is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as 
    published by the Free Software Foundation, either version 3 of the 
    License, or (at your option) any later version.

    AmySuite is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public 
    License along with AmySuite.
    If not, see <http://www.gnu.org/licenses/>. 
*/
package net.axfab.amy.expr;

public enum Operator {

	Parenthesis(null, 0, 99, true),
	Operand(null, 0, 0, true),
	
	// Unitary Operators
	IncSfx("++", 1, 2, true),
	DecSfx("--", 1, 2, true),
	IncPfx("++", 1, 3, false),
	DecPfx("--", 1, 3, false),
	Not("!", 1, 3, false),
	BitwiseNot("~", 1, 3, false),
	
	// Binary Operators
	Dot(".", 2, 1, true),
	Call("()", 2, 2, true),
	Mul("*", 2, 5, true),
	Div("/", 2, 5, true),
	Mod("%", 2, 5, true),
	Add("+", 2, 6, true),
	Sub("-", 2, 6, true),
	BitwiseShiftLeft("<<", 2, 7, true),
	BitwiseShiftRight(">>", 2, 7, true),
	Less("<", 2, 8, true),
	More(">", 2, 8, true),
	LessEq("<=", 2, 8, true),
	MoreEq(">=", 2, 8, true),
	Equals("==", 2, 9, true),
	NotEquals("!=", 2, 9, true),
	BitwiseAnd("&", 2, 10, true),
	BitwiseXor("^", 2, 11, true),
	BitwiseOr("|", 2, 12, true),
	And("&&", 2, 13, true),
	Or("||", 2, 14, true),
	NullCoalessence("??", 2, 15, false),
	Assign("=", 2, 15, false),
	Throw("throw", 2, 16, false),
	Comma(",", 2, 17, true),
	
	// Ternary Operator
	ConditionIf("?", 3, 15, false),
	ConditionThen(":", 3, 15, false);

	private int operandCount;
	private int priority;
	private String literal;
	private boolean associativityLeftToRight;
	
	private Operator(String str, int sz, int prio, boolean assoc)
	{
		operandCount = sz;
		priority = prio;
		literal = str;
		associativityLeftToRight = assoc;
	}

	public boolean isOperand() { return operandCount == 0; }
	public boolean isBinary() { return operandCount == 2; }
	public boolean isUnitary() { return operandCount == 1; }
	public int getPriority() { return priority; }

	public boolean isL2R() { return associativityLeftToRight == true; }
	public boolean isR2L() { return associativityLeftToRight == false; }

	public String getLiteral() { return literal; }

	public static Operator find(String str) {
		for (Operator op : values()) {
			if (str.equals(op.getLiteral())) {
				return op;
			}
		}
		throw new IllegalArgumentException("No operator like '"+str+"'.");
	}
}
