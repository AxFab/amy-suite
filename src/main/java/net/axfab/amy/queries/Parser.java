/*    This file is part of AmySuite.

    AmySuite is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AmySuite is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AmySuite.  If not, see <http://www.gnu.org/licenses/>. 
*/
package net.axfab.amy.queries;

import net.axfab.amy.data.DataBase;
import net.axfab.amy.data.DataColumn;
import net.axfab.amy.data.DataTable;
import net.axfab.amy.expr.Expression;
import net.axfab.amy.lexer.Token;
import net.axfab.amy.lexer.TokenClass;
import net.axfab.amy.lexer.Tokenizer;

public class Parser {

	Tokenizer lexer = new Tokenizer();
	DataBase db;
	DataTable results;

	public void parse(String text) throws ParseException {
		lexer.setup(text);
		readSelectCore();
	}
	
	private void readSelectCore() throws ParseException {
		// select-core := 
		//   'SELECT' ('DISTINCT' | 'ALL')? result-column (',' result-column)*
		expect("SELECT");
		Token token = read();
		if (token.isEquals("DISTINCT")) {
			// TODO distinct
		} else if (token.isEquals("ALL")) {
			// TODO all
		} else {
			lexer.unread(token);
		}
		
		do {
			readResultColumn();
			token = read();
		} while (token != null && token.isEquals(","));

		//   ('FROM' (join-clause | table-or-subquery (',' table-or-subquery)*))?
		//   ('WHERE' expr)?
		//   ('GROUP' 'BY' expr (',' expr)*) ('HAVING' expr)?
		for (int i=0; ;) {
			if (i < 1 && token.isEquals("FROM")) {
				i = 1;
				do {
					readTableOrSubquery();
					token = read();
				} while (token != null && token.isEquals(","));
				if (token.isEquals("JOIN")) {
					// TODO Join
				}
			} else if (i < 2 && token.isEquals("WHERE")) {
				i = 2;
				readExpr();
				token = read();
//			} else if (i < 3 && token.isEquals("GROUP")) {
//				i = 3;
//				// TODO 'BY' expr* (HAVING expr)
//				token = lexer.read();
			} else {
				lexer.unread(token);
				return;
			}
		}
	}

	/** Read column name and prepare result table */
	private void readResultColumn() throws ParseException {
		// result-column := (expr ('AS'? column-alias)? | (table-name '.')? NAME)
		Token token;
		do {
			Expression expr = readExpr();
			token = read();
			if (token.isEquals("AS")) {
				token = read();
				if (!token.isOfType(TokenClass.IDENTIFIER)) {
					throw new ParseException("");
				}
				results.addColumn(new DataColumn(expr, token.getLitteral()));
			} else {
				results.addColumn(new DataColumn(expr, null));
			}
		} while (token != null && token.isEquals(","));
		lexer.unread(token);
	}
	
	private void readTableOrSubquery() throws ParseException {
		// table-or-subquery :=
		//   (NAME '.')? NAME ('AS'? ALIAS)? ('INDEXED BY' NAME | 'NOT INDEXED')?
		//   '(' select-stmt ')'
		String schema = null, table = null, alias = null;
		Token token = read();
		if (token.isEquals("(")) {
			// TODO Read sub-query SELECT
		} else if (token.isOfType(TokenClass.IDENTIFIER)) {
			table = token.getLitteral();
			token = read();
			if (token.isEquals(".")) {
				schema = table;
				token = read();
				if (!token.isOfType(TokenClass.IDENTIFIER)) {
					throw new ParseException("...");
				}
				table = token.getLitteral();
				token = read();
			}

			// AS + alias
			addTable(table, schema, alias);
		}
	}
	private Expression readExpr() {
		// TODO 
		return null;
	}

	private void addTable(String table, String schema, String alias) {
		// TODO Auto-generated method stub
		
	}

	private Token read() {
		Token token = read();
		if (token.isOfType(TokenClass.KEYWORD1)) {
			token.setLitteral(token.getLitteral().toUpperCase());
		}
		return token;
	}

	private void expect(String value) throws ParseException {
		Token token = read();
		if (!token.isEquals(value)) {
			throw new ParseException("Expected the '" + value + "' instead of '" + token.getLitteral() + "'.");
		}
	}

	public static void main(String[] args) throws ParseException {
		
		Parser p = new Parser();
		p.parse("SELECT comment FROM database WHERE user = 'Me'");
		
	}

}
