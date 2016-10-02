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
import java.util.Map;

public class DataBase {

	private String defaultSchema = "_";
	private Map<String, DataTable> tables = new HashMap<>();
	
	public boolean isSchemaName(String litteral) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTableName(String name, String schema) {
		return tables.containsKey(buildKey(name, schema));
	}

	public boolean isFunctionName(String litteral, String schema) {
		// TODO Auto-generated method stub
		return false;
	}

	public DataTable loadTable(String name, String schema) {
		return tables.get(buildKey(name, schema));
	}

	public DataTable createTable(String name, String schema) {
		DataTable table = new DataTable(name);
		tables.put(buildKey(name, schema), table);
		return table;
	}
	
	public DataTable execute(String query) {
		SQLParser parser = new SQLParser(this);
		try {
			parser.execute(query);
		} catch (Exception ex) {
			System.err.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
		return parser.getResults();
	}
	
	private String buildKey(String name, String schema) {
		if (schema == null) {
			schema = defaultSchema;
		}
		return schema + "." + name;
	}
}
