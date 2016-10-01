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
package net.axfab.amy.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.axfab.amy.expr.Operand;
import net.axfab.amy.expr.Operator;
import net.axfab.amy.expr.ParserException;
import net.axfab.amy.expr.Primitive;
import net.axfab.amy.expr.Resolver;
import net.axfab.amy.lexer.Language;
import net.axfab.amy.lexer.Token;
import net.axfab.amy.lexer.TokenClass;
import net.axfab.amy.lexer.Tokenizer;

public class SQLParser {

	private Tokenizer lexer = new Tokenizer(
			Language.load("net/axfab/amy/SQL.yml"));
	private DataBase db;
	private DataTable results;
	private Map<String, DataTable> tables;
	private Resolver whereClause;
	private boolean distinct;
	
	public SQLParser(DataBase db) {
		this.db = db;
	}
	
	public void execute(String query) throws ParserException, DataError {
		results = new DataTable("temp");
		tables = new HashMap<>();
		distinct = false;
		lexer.setup(query);
		Token token = read();
		lexer.unread(token);
		switch (token.getLiteral()) {
		case "SELECT":
			readSelectCore();
			executeSelect();
			break;
		case "INSERT":
			readInsertCore();
			executeInsert();
			break;
		}
	}

	public DataTable getResults() {
		return results;
	}

	private void executeSelect() throws DataError, ParserException {
		int i=0;
		// Select first row
		DataTable[] tblArray = new DataTable[tables.size()];
		for (Entry<String, DataTable> tbl : tables.entrySet()) {
			tblArray[i] = tbl.getValue();
			if (!tblArray[i++].rewindRow()) {
				return; // No data on one of the selected tables.
			}
		}
		
		for (;;) {
			// Build a row
			DataRow row = new DataRow();
			for (Entry<String, DataTable> tbl : tables.entrySet()) {
				tbl.getValue().fetchRow(tbl.getKey(), row);
			}
			
			whereClause.invoke(row);
			if (whereClause.isTrue()) {
				if (distinct) {
					// TODO Check that this row doesn't already exists.
				}
				results.pushRow(row);
			}
			
			// Next row
			i = 0;
			while (!tblArray[i].nextRow()) {
				tblArray[i].rewindRow();
				if (++i >= tblArray.length) {
					return; // We read all rows combinations
				}
			}
		}
	}

	private void executeInsert() {
		// TODO Auto-generated method stub
		
	}


	private void readSelectCore() throws ParserException, DataError {
		// SELECT (DISTINCT | ALL)? column (, column)* 
		Token token;
		expect("SELECT");
		token = read();
		if (token.isEquals("DISTINCT")) {
			distinct = true;
		} else if (token.isEquals("ALL")) {
			distinct = false;
		} else {
			lexer.unread(token);
		}
		
		do {
			readColumn();
			token = read();
		} while (token != null && token.isEquals(","));
		
		for (int i=0;;) {
			if (i < 1 && token.isEquals("FROM")) {
				i = 1;
				do {
					readTableOrSubquery();
					token = read();
				} while (token != null && token.isEquals(","));
				if (token.isEquals("JOIN")) {
					// TODO -- Change join style!
				}
			} else if (i < 2 && token.isEquals("WHERE")) {
				i = 2;
				whereClause = readExpr();
				token = read();
			} else {
				lexer.unread(token);
				return;
			}
		}
	}


	private void readInsertCore() throws ParserException, DataError {
		// INSERT INTO table (column (, column)*) VALUES (...)
		Token token;
		expect("INSERT");
		expect("INTO");
		readTableOrSubquery();
		expect("(");
		DataTable table = tables.values().iterator().next();
		List<DataColumn> columns = new LinkedList<>();
		do {
			DataColumn column = readColumn();
			column = table.getColumn(column.getName());
			columns.add(column);
			token = read();
		} while (token != null && token.isEquals(","));
		lexer.unread(token);
		expect(")");
		expect("VALUES");
		do {
			DataRow row = readValues(columns);
			table.pushRow(row);
			token = read();
		} while (token != null && token.isEquals(","));
		lexer.unread(token);
	}

	private DataColumn readColumn() throws ParserException {
		Resolver expr = readExpr();
		String name = expr.getResult().getName();
		Token token = read();
		if (token.isEquals("AS")) {
			token = read();
			if (!token.isOfType(TokenClass.IDENTIFIER)) {
				throw new ParserException("Expects an identifier after AS.");
			}
			name = token.getLiteral();
		} else {
			lexer.unread(token);
		}
		
		DataColumn column = new DataColumn(expr, name);
		results.addColumn(column);
		return column;
	}

	private void readTableOrSubquery() throws ParserException, DataError {
		String schema = null, name = null, alias = null;
		Token token = read();
		if (token.isEquals("(")) {
			// TODO -- read subquery
		} else if (token.isOfType(TokenClass.IDENTIFIER)) {
			name = token.getLiteral();
			token = read();
			if (token.isEquals(".")) {
				schema = name;
				token = read();
				if (!token.isOfType(TokenClass.IDENTIFIER)) {
					throw new ParserException("Expects an identifier after '" + schema + ".'.");
				}
				name = token.getLiteral();
			} else {
				lexer.unread(token);
			}
			
			alias = name;
			token = read();
			if (token.isEquals("AS")) {
				token = read();
				if (!token.isOfType(TokenClass.IDENTIFIER)) {
					throw new ParserException("Expects an identifier after AS.");
				}
				alias = token.getLiteral();
			} else {
				lexer.unread(token);
			}
			
			DataTable table = db.loadTable(name, schema);
			if (table == null) {
				throw new DataError("Unable to find the table: " + name);
			}
			tables.put(alias, table);
		}
		
	}

	private DataRow readValues(List<DataColumn> columns) throws ParserException, DataError {
		Token token;
		int idx = 0;
		DataRow row = new DataRow();
		expect("(");
		do {
			DataColumn column = columns.get(idx++);
			Resolver expr = readExpr();
			Operand value = expr.getResult();
			if (!value.isConst()) {
				throw new DataError("Invalid value for column " + column.getName());
			}
			row.put(column.getName(), new DataValue(value.getType(), value.getValue()));
			token = read();
		} while (token != null && token.isEquals(","));
		lexer.unread(token);
		expect(")");
		return row;
	}

	private Resolver readExpr() throws ParserException {
		int parenthesis = 0;
		Resolver res = new Resolver();
		Token token;
		for (;;) {
			token = read();
			if (token == null) {
				break;
			} else if (token.isOfType(TokenClass.IDENTIFIER)) {
				res.push(token, new Operand(token, token.getLiteral()));
			} else if (token.isOfType(TokenClass.STRING)) {
				String value = token.getLiteral();
				value = value.substring(1, value.length() - 1);
				res.push(token, new Operand(token, Primitive.String, value));
			} else if (token.isOfType(TokenClass.OPERATOR)) {
				if (token.isEquals("(")) {
					parenthesis++;
					res.openParenthese(token);
				} else if (token.isEquals(")")) {
					if (parenthesis <= 0) {
						break;
					}
					res.closeParenthese(token);
				} else {
					res.push(token, Operator.find(token.getLiteral()));
				}
			} else {
				break;
			}
		}
		
		lexer.unread(token);
		res.compile();
		return res;
	}

	private Token read() {
		Token token = lexer.read();
		if (token == null) {
			return null;
		} else if (token.isOfType(TokenClass.KEYWORD1)) {
			token.setLitteral(token.getLiteral().toUpperCase());
		} else if (token.isOfType(TokenClass.OPERATOR)) {
			if (token.getLiteral().equals("=")) {
				token.setLitteral("==");
			}
		}
		return token;
	}
	
	private void expect(String expected) throws ParserException {
		Token token = read();
		if (!token.isEquals(expected)) {
			String msg = "Expecting " + expected + " unstead of " + token.getLiteral() + ".";
			throw new ParserException(msg);
		}
	}

}
