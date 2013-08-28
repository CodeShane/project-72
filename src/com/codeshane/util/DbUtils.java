/* DbUtils is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.provider.BaseColumns;
import android.util.Log;

import com.codeshane.representing.meta.Column;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 27, 2013
 * @version 1
 */
public class DbUtils {
	public static final String	TAG	= DbUtils.class.getPackage().getName() + "." + DbUtils.class.getSimpleName();

	/** Generates a statement that includes id and a given where statement. For
	 * example:
	 *
	 * @param selection
	 *            ex: "name = frank"
	 * @returns: where statement ex: "_ID = ? AND (name = frank)" */
	public static final String whereWithId ( String selection ) {
		StringBuilder sb = new StringBuilder(256);
		sb.append(BaseColumns._ID);
		sb.append(" = ?");
		if (selection != null) {
			sb.append(" AND (");
			sb.append(selection);
			sb.append(')');
		}
		return sb.toString();
	}

	/** Generates a where statement for a given column. For example:
	 *
	 * @param column
	 *            ex: a column named "myColumn"
	 * @returns: where statement ex: "myColumn = ?" */
	public static final String whereColumn ( Column column ) {
		return whereColumn(column.getName());
	}

	/** Generates a where statement for a given column name. For example:
	 *
	 * @param column
	 *            ex: a column named "myColumn"
	 * @returns: where statement ex: "myColumn = ?" */
	public static final String whereColumn ( String columnName ) {
		return new StringBuilder(256).append(columnName).append(" = ?").toString();
	}

	/** Creates a new String[] of selectionArgs that includes an additional
	 * column name.
	 *
	 * @param selectionArgs
	 *            as String[]
	 * @returns: selectionArgs as new String[] */
	public static final String[] addColumnToSelectionArgs ( Column column, String[] selectionArgs ) {
		return addColumnToSelectionArgs(column.getName(), selectionArgs);
	}

	/** Creates a new String[] of selectionArgs that includes an additional
	 * column name.
	 *
	 * @param selectionArgs
	 *            as String[]
	 * @returns: selectionArgs as new String[] */
	public static final String[] addColumnToSelectionArgs ( String columnName, String[] selectionArgs ) {
		if (selectionArgs == null) { return new String[] { columnName }; }

		int length = selectionArgs.length;
		String[] newSelectionArgs = new String[length + 1];
		newSelectionArgs[0] = columnName;
		System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, length);
		return newSelectionArgs;
	}

	public static final String	CREATE_TABLE	= "CREATE TABLE ";
	public static final String	CREATE_INDEX	= "CREATE INDEX rep_";

	// TODO simple migrations - Upgrades should be handled in a more realistic fashion, not just drop & replace...

	public static final boolean execSqlAsTransaction (String statement, SQLiteDatabase db){
		if (null==statement || null==db) {
			if (null==statement) {
				Log.e(TAG,"Can't perform SqlTransaction without statement.");
			}
			else if (null==db) {
				Log.e(TAG,"Can't perform SqlTransaction without db.");
			}
		}
		boolean success=false;
		db.beginTransactionWithListener(transactionListener);
		try {
			db.execSQL(statement);
			db.setTransactionSuccessful();
			success=true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return success;
	}

	public static final SQLiteTransactionListener transactionListener = new SQLiteTransactionListener() {
		@Override public void onBegin () { Log.w(TAG,"Begin Sqlite transaction."); }
		@Override public void onCommit () { Log.w(TAG,"Successful Sqlite transaction."); }
		@Override public void onRollback () { Log.e(TAG,"Create tables - Rollback Sqlite transaction."); }
	};


}
