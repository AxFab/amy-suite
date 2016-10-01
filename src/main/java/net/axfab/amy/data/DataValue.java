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

import net.axfab.amy.expr.Primitive;

public class DataValue {

	private Primitive type;
	private Object value;
	
	public DataValue(Primitive type, Object value) throws DataError
	{
		this.type = type;
		switch (type) {
		case Boolean:
			this.value = (Boolean)value;
			break;

		case Byte:
		case SByte:
		case Short:
		case UShort:
		case Int:
			break;

		case Float:
		case Double:
			this.value = (Double)value;
			break;
			

		case String:
			this.value = (String)value;
			break;

		case DateOnly:
		case DateTime:
			break;
			
		default:
			throw new DataError("Unsupported type.");
		}
	}

	public Primitive getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public DataValue copy() throws DataError {
		return new DataValue(type, value);
	}
}
