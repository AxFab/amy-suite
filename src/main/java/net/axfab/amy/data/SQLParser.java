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
	private DataRow updateRow;
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
		case "UPDATE":
			readUpdateCore();
			executeUpdate();
			break;
		}
	}

	public DataTable getResults() {
		return results;
	}

	private DataTable[] initExecution ()
	{
		int i=0;
		// Select first row
		DataTable[] tblArray = new DataTable[tables.size()];
		for (Entry<String, DataTable> tbl : tables.entrySet()) {
			tblArray[i] = tbl.getValue();
			if (!tblArray[i++].rewindRow()) {
				return null; // No data on one of the selected tables.
			}
		}
		return tblArray;
	}

	private DataRow buildExecutionRow () throws ParserException, DataError {

		DataRow row = new DataRow();
		for (Entry<String, DataTable> tbl : tables.entrySet()) {
			tbl.getValue().fetchRow(tbl.getKey(), row);
		}
		
		if (whereClause != null) {
			whereClause.invoke(row);
			if (!whereClause.isTrue()) {
				return null;
			}
		}
		if (distinct) {
			// TODO Check that this row doesn't already exists.
		}
		return row;
	}
	

	private DataRow findRow () throws ParserException, DataError {

		DataTable table = tables.entrySet().iterator().next().getValue();
		DataRow row = table.getRow();
		
		if (whereClause != null) {
			whereClause.invoke(row);
			if (!whereClause.isTrue()) {
				return null;
			}
		}
		if (distinct) {
			// TODO Check that this row doesn't already exists.
		}
		return row;
	}
	
	private boolean nextRow(DataTable[] tblArray) {

		int i=0;
		while (!tblArray[i].nextRow()) {
			tblArray[i].rewindRow();
			if (++i >= tblArray.length) {
				return false; // We read all rows combinations
			}
		}
		return true;
	}
	
	private void executeSelect() throws DataError, ParserException {
		
		DataTable[] tblArray = initExecution ();
		if (tblArray == null) {
			return;
		}
		for (;;) {
			DataRow row = buildExecutionRow ();
			if (row != null) {
				results.pushRow(row);
			}
			
			if (!nextRow(tblArray)) {
				break;
			}
		}
	}

	private void executeInsert() throws DataError {
		results = new DataTable("temp").addColumn(new DataColumn("object_created", Primitive.Int));
		// TODO -- Count the number of new values how !? 
		DataRow res = new DataRow();
		res.put("object_created", new DataValue(Primitive.Int, 1));
		results.pushRow(res);
	}

	private void executeUpdate() throws DataError, ParserException {
		results = new DataTable("temp").addColumn(new DataColumn("objects_updated", Primitive.Int));
		DataTable[] tblArray = initExecution ();
		if (tblArray == null || tblArray.length > 1) {
			throw new DataError("An update operation can only be done on a single table.");
		}

		int count = 0;
		for (;;) {
			DataRow row = findRow ();
			if (row != null) {
				row.mergeRow(updateRow);
				++count;
			}
			
			if (!nextRow(tblArray)) {
				break;
			}
		}
		DataRow res = new DataRow();
		res.put("objects_updated", new DataValue(Primitive.Int, count));
		results.pushRow(res);
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


	private void readUpdateCore() throws ParserException, DataError {
		Token token;
		expect("UPDATE");
		readTableOrSubquery();
		expect("SET");
		updateRow = new DataRow();
		DataTable table = tables.values().iterator().next();
		do {
			token = read();
			if (!token.isOfType(TokenClass.IDENTIFIER)) {
				throw new ParserException("Error: expect an identifier instead of " + token.getLiteral() + " at: " + token.getPosition());
			}
			DataColumn column =  table.getColumn(token.getLiteral());
			expect("=="); // We replaced = by ==
			Resolver expr = readExpr();
			Operand result = expr.getResult();
			if (!result.isConst() && result.getType() != Primitive.Undefined) {
				throw new ParserException("Error: expression of updates operation must be constante.");
			}
			updateRow.put(column.getName(), new DataValue(result.getType(), result.getValue()));
			token = read();
		} while (token != null && token.isEquals(","));
		
		if (token.isEquals("WHERE")) {
			whereClause = readExpr();
		} else {
			lexer.unread(token);
		}
	}

	private DataColumn readColumn() throws ParserException, DataError {
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

	private Resolver readExpr() throws ParserException, DataError {
		int parenthesis = 0;
		Resolver res = new Resolver();
		Token token;
		for (;;) {
			token = read();
			if (token == null) {
				break;
			} 
			if (token.isOfType(TokenClass.IDENTIFIER)) {
				res.push(token, new Operand(token, token.getLiteral()));
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
			} else if (token.isOfType(TokenClass.STRING)) {
				String value = token.getLiteral();
				value = value.substring(1, value.length() - 1);
				res.push(token, new Operand(token, Primitive.String, value));
			} else if (token.isOfType(TokenClass.DECIMAL)) {
				res.push(token, new Operand(token, Primitive.Int, Integer.valueOf(token.getLiteral())));
			} else if (token.isOfType(TokenClass.NUMBER)) {
				res.push(token, new Operand(token, Primitive.Double, Double.valueOf(token.getLiteral())));
			} else if (token.isOfType(TokenClass.KEYWORD1)) {
				if (token.isEquals("NULLSTRING")) {
					res.push(token, new Operand(token, Primitive.String, null));
				} else {
					break;
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
			token.setLitteral(token.getLiteral().toUpperCase());
			if (token.getLiteral().equals("=")) {
				token.setLitteral("==");
			} else if (token.getLiteral().equals("AND")) {
				token.setLitteral("&&");
			} else if (token.getLiteral().equals("OR")) {
				token.setLitteral("||");
			} else if (token.getLiteral().equals("IS")) {
				Token token2 = lexer.read();
				token2.setLitteral(token2.getLiteral().toUpperCase());
				if (token2.isEquals("NOT")) {
					token.setLitteral("!=");
				} else {
					lexer.unread(token2);
					token.setLitteral("==");
				}
			}
		}
		return token;
	}
	
	private void expect(String expected) throws ParserException {
		Token token = read();
		if (!token.isEquals(expected)) {
			String msg = "Expecting '" + expected + "' unstead of '" + token.getLiteral() + "' at: " + token.getPosition() + ".";
			throw new ParserException(msg);
		}
	}

}
