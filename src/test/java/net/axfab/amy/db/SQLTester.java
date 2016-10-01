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
package net.axfab.amy.db;

import junit.framework.TestCase;
import net.axfab.amy.data.DataBase;
import net.axfab.amy.data.DataColumn;
import net.axfab.amy.data.DataTable;
import net.axfab.amy.expr.Primitive;

public class SQLTester extends TestCase {

	public void testSQLSelect() {
		DataBase db = new DataBase();
		
		db.createTable("myTable", null)
			.addColumn(new DataColumn("user", Primitive.String))
			.addColumn(new DataColumn("comment", Primitive.String));
		
		DataTable res;
		res = db.execute("INSERT INTO myTable(user, comment) VALUES('Me', 'Awesome!')");
		res.dump();
		res = db.execute("INSERT INTO myTable(user, comment) VALUES('Fabien', 'Still me.')");
		res.dump();
		res = db.execute("SELECT comment FROM myTable WHERE user = 'Me'");
		res.dump();	
	}
}
