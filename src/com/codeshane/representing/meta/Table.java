/* Table is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.meta;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 2
 */
public interface Table {

	String name();
	Column[] columns();

	/** Implementations should handle upgrading their data. If no changes need to be made,
	 * simply return false. Caution: only return true if you accept data loss!
	 * By default (false,) the helper ignores the upgrade.
	 * @return true if the helper should "upgrade" the table. By default, this means dropping & re-creating the table! Return false to have the helper ignore the upgrade. */
	boolean upgrade(SQLiteDatabase db, int oldversion, int newversion);

	/** Implement this to handle custom table creation, then return true.
	 * By default (false,) the helper will create the table at the latest version.
	 * @since Aug 23, 2013
	 * @version Aug 23, 2013
	 * @return true if the create request was handled, false if the helper should create it. */
	boolean create ( SQLiteDatabase db );

}
