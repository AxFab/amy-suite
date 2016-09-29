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
package net.axfab.amy.data;

public class DataError extends Exception {

	private static final long serialVersionUID = 8836515431166742269L;

	public DataError(String msg) {
		super(msg);
	}

}
