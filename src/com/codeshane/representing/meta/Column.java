/* Column is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.meta;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 21, 2013
 * @version 1
 */
public interface Column {

	/** Set this suffix on the primary key column, which should have a name "_id" and must have type = "INTEGER"
	 * */
	public static final String PRI_KEY_AUTO = "PRIMARY KEY AUTOINCREMENT";

	/** @return An optional appendix for the sql create table statement part for this column "myname TEXT". May return null. */
	public String getCreationSuffix();

	/** The column number, or id, identifies the column on a table.
	 * Column 0 is presumed to be the primary key.
	 * Don't forget, many database implementations layer their own ids.
	 * @return int the id of the Column. */
    public int getId(); //TODO refactor into table

    /** @return String the Column name. */
    public String getName();

    /** The type MUST be INTEGER for the Primary Key and Foreign Keys in this implementation.
     * @return the Column sqlite type.*/
    public String getType();

    /** @return true if the column should be indexed */
    public boolean isIndexed();

}
