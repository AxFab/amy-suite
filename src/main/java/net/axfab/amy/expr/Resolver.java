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

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedTransferQueue;

import net.axfab.amy.data.DataRow;
import net.axfab.amy.data.DataValue;
import net.axfab.amy.lexer.Token;

public class Resolver {

	/**
	 * Status represent the type of last operand or operator pushed on the
	 * expression.
	 */
	private static enum ExpressionStatus {
		/**
		 * Indicates that the expression or an sub-expression just started. You
		 * can consider the previous token is '('.
		 */
		Start,
		/** The token placed at the left is an operand, not an operator. */
		Operand,
		/**
		 * The last token pushed is a binary operator that expect an operand at
		 * right and left.
		 */
		BinaryOperator,
		/**
		 * The last token pushed is a unitary operator with left to right
		 * associativity.
		 */
		UnaryOperatorLeftRight,
		/**
		 * The last token pushed is a unitary operator with right to left
		 * associativity.
		 */
		UnaryOperatorRightLeft,
		/** Indicates that the expression is not valid. */
		Error,
	};

	/** the post-fix stack allow to store operand(as value) as they come. */
	private Stack<Operand> postFixStack_;

	/**
	 * The in-fix stack store operators as they come.
	 * 
	 * @note If a lower priority need to be pushed, every operators will be
	 *       poped, processed and pushed back on the post-fix stack.
	 */
	private Stack<Operand> inFixStack_;

	/** Store an error message in case of invalid expression */
	private String errMsg_;

	/**
	 * Keep track of the expression state to push operand in the correct order.
	 */
	private ExpressionStatus status_;

	/** Keep track of the last pushed token. */
	private Token last_;

	/** Index used internaly for SSA variable naming. */
	//private int ssaNameIdx;

	/** Static array used internaly for SSA variable naming. */
	//private final String ssaNameArr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/** Initializes a new instance of the <see cref="Resolver" /> class. */
	public Resolver() {
		this.reset();
	}

	/**
	 * Push an operand on the post-fix stack.
	 * 
	 * @param node
	 *            An operand object without operator.
	 */
	private void addPostFixOperand(Operand node) {
		assert node.getOperator() == Operator.Operand;
		postFixStack_.push(node);
	}

	/**
	 * Resolve an operator and push it pack on the post-fix stack.
	 * 
	 * @param node
	 *            An operand object with an operator.
	 * @throws ParserException
	 */
	private void addPostFixOperator(Operand node) throws ParserException {
		Operand opRg;
		Operand opLf;

		if (node.getPriority() == 0) {
			throw new ParserException(
					"Internal error, the definition for this operator can't be found: " + node.getOperator());
		}

		if (node.getOperator().isUnitary()) {
			if (postFixStack_.size() < 1) {
				throw new ParserException("Missing operand for the operator: " + node.getOperator());
			}
			opRg = postFixStack_.pop();
			opLf = new Operand(null, Primitive.Error, false);

		} else if (node.getOperator().isBinary()) {
			if (postFixStack_.size() < 2) {
				throw new ParserException("Missing operands for the operator: " + node.getOperator());
			}
			opRg = postFixStack_.pop();
			opLf = postFixStack_.pop();

		} else {
			// TODO Parenthesis can be there if the expression is incorrect
			throw new ParserException("Invalid operator: " + node.getOperator());
		}

		node.resolve(opLf, opRg);
		postFixStack_.push(node);
	}

	/**
	 * Set the expression in error and keep track of an error message.
	 * 
	 * @param msg">An
	 *            error message.
	 * @return Always return false to be return by the calling function.
	 */
	private boolean error(String msg) {
		if (this.status_ == ExpressionStatus.Error) {
			return false;
		}
		this.status_ = ExpressionStatus.Error;
		if (last_ != null) {
			this.errMsg_ = msg + " after " + last_.toString() + ".";
		} else {
			this.errMsg_ = msg + " to begin.";
		}
		return false;
	}

	/**
	 * Set the last token and check for expression consistency using the saved
	 * status.
	 * 
	 * @param token
	 *            An generic object used to get info about the token.
	 * @param op
	 *            the operator value to pushed.
	 */
	private boolean setLast(Token token, Operator op) {
		if (op == Operator.Operand || op == Operator.Parenthesis) {
			if (this.status_ != ExpressionStatus.Start && this.status_ != ExpressionStatus.BinaryOperator
					&& this.status_ != ExpressionStatus.UnaryOperatorLeftRight) {
				return error("Unexpected operand");
			}
			this.status_ = op == Operator.Operand ? ExpressionStatus.Operand : ExpressionStatus.Start;
		} else if (op.isUnitary()) {
			if (this.status_ == ExpressionStatus.Operand) {
				return error("Unexpected prefix operator");
			}
			this.status_ = ExpressionStatus.UnaryOperatorLeftRight;
		} else {
			if (this.status_ != ExpressionStatus.Operand) {
				return error("Unexpected operator");
			}
			this.status_ = ExpressionStatus.BinaryOperator;
		}

		this.last_ = token;
		return true;
	}

	public void reset() {
		postFixStack_ = new Stack<Operand>();
		inFixStack_ = new Stack<Operand>();
		errMsg_ = null;
		status_ = ExpressionStatus.Start;
		last_ = null;
	}

	public boolean push(Token token, Operand operand) {
		Operator opcode = operand.getOperator();
		if (!opcode.isOperand()) {
			throw new IllegalArgumentException("");
		}
		if (!setLast(token, opcode)) {
			return false;
		}

		addPostFixOperand(operand);
		return true;
	}

	public boolean push(Token token, Operator opcode) throws ParserException {
		if (!setLast(token, opcode)) {
			return false;
		}

		Operand node = new Operand(token, opcode);
		while (inFixStack_.size() > 0 && inFixStack_.peek().getPriority() <= node.getPriority()) {
			Operand nd = inFixStack_.pop();
			addPostFixOperator(nd);
		}

		inFixStack_.push(node);
		return true;
	}

	public boolean openParenthese(Token token) {
		if (!setLast(token, Operator.Parenthesis))
			return false;

		Operand node = new Operand(token, Operator.Parenthesis);
		inFixStack_.push(node);
		return true;
	}

	public boolean closeParenthese(Token token) throws ParserException {
		if (this.status_ != ExpressionStatus.Operand)
			return error("unexpected parenthese");

		this.last_ = token;
		this.status_ = ExpressionStatus.Operand;
		Operand node = inFixStack_.pop();
		while (node.getOperator() != Operator.Parenthesis) {
			addPostFixOperator(node);
			if (inFixStack_.size() == 0)
				return error("closing parenthese without openning");
			node = inFixStack_.pop();
		}
		return true;
	}

	public boolean compile() throws ParserException {
		if (this.status_ == ExpressionStatus.Error) {
			return false;
		}

		for (Operand op : inFixStack_) {
			if (op.getOperator() == Operator.Parenthesis) {
				return error("Missing clossing parenthese");
			}
		}

		if (this.status_ != ExpressionStatus.Operand) {
			return error("Expression incompleted, expected operand");
		}
		while (inFixStack_.size() > 0) {
			Operand pop = inFixStack_.pop();
			addPostFixOperator(pop);
		}

		if (postFixStack_.size() != 1) {
			return error("Unexpected error, unable to resolve the expression");
		}
		return true;
	}

	public Operand getResult() throws ParserException {
		if (status_ == ExpressionStatus.Error) {
			throw new ParserException("Invalid expression");
		}
		if (postFixStack_.size() != 1 || inFixStack_.size() != 0) {
			throw new ParserException("Incomplete expression");
		}
		return postFixStack_.peek();
	}

	public void invoke(DataRow row) {
		Operand top = null;
		Queue<Operand> pool = new LinkedTransferQueue<>();
		try {
			top = getResult();
			pool.add(top);
		} catch (Exception ex) {
			// TODO -- log
		}
		while (!pool.isEmpty()) {
			Operand op = pool.poll();
			if (op.isConst()) {
				continue;
			}
			if (op.getLeft() != null) {
				pool.add(op.getLeft());
			}
			if (op.getRight() != null) {
				pool.add(op.getRight());
			}
			if (op.getOperator() != Operator.Operand) {
				continue;
			}

			String name = op.getName();
			DataValue value = row.getColumn(name);
			op.setValue(value.getType(), value.getValue());
		}
		top.resolve(); // TODO -- all the way, down to up
	}

	public boolean isTrue() throws ParserException {
		Operand res = getResult();
		return res.getType() == Primitive.Boolean && res.getValue().equals(true);
	}

	public boolean isBoolean() throws ParserException {
		Operand res = getResult();
		return res.getType() == Primitive.Boolean;
	}

	public String getError() {
		return errMsg_;
	}
}
