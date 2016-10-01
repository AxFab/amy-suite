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

import net.axfab.amy.lexer.Token;

public class Operand {

	private Operator operator;
	private Operand left;
	private Operand right;
	private Operand parent;
	private boolean isConst;
	private String name;
	private Primitive type;
	private Object value;
	private Token token;

	public Operand(Token token, String name) {
		this.token = token;
		this.operator = Operator.Operand;
		this.name = name;
		this.type = Primitive.Undefined;
		this.value = null;
		this.isConst = false;
	}

	public Operand(Token token, Operator parenthesis) {
		this.token = token;
		this.operator = parenthesis;
		this.name = null;
		this.type = Primitive.Undefined;
		this.value = null;
		this.isConst = false;
	}

	public Operand(Token token, Primitive type, Object value) {
		this.token = token;
		this.operator = Operator.Operand;
		this.name = null;
		this.isConst = true;
		this.setValue(type, value);
	}

	public Operator getOperator() {
		return operator;
	}

	public Operand getLeft() {
		return left;
	}

	public Operand getRight() {
		return right;
	}

	public Operand getParent() {
		return parent;
	}

	public boolean isConst() {
		return isConst;
	}

	public String getName() {
		return name;
	}

	public Primitive getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public Token getToken() {
		return token;
	}

	public void resolve(Operand... operands) {
		this.left = operands[0];
		this.right = operands[1];
		this.left.parent = this;
		this.right.parent = this;
		resolve();
	}

	public void resolve() {
		OperatorAction action = OperatorAction.find(this.operator, left.type, right.type);
		if (action == null) {
			this.type = Primitive.Error;
			this.isConst = false;
			// Push an error or what !?
		} else {
			action.evaluate(this, left, right);
		}
	}

	void setValue(Primitive type, Object value) {
		this.type = type;
		this.value = value;
	}

	public int getPriority() {
		return operator.getPriority();
	}

}
