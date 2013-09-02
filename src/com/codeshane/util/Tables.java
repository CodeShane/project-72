/* Tables is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import java.util.Locale;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteProgram;
import android.database.sqlite.SQLiteStatement;

import com.codeshane.representing.meta.Column;
import com.codeshane.representing.meta.Table;

/** Utility class of static methods for working with Tables.
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 27, 2013
 * @version 1
 */
public class Tables {
	public static final String	TAG	= Tables.class.getPackage().getName() + "." + Tables.class.getSimpleName();

	/** A string for making bulk inserts. */
	public static final StringBuilder bulkInsertStatement ( Table table, StringBuilder sb ) {
		if (null==sb) sb = new StringBuilder();
		sb.append("INSERT INTO ").append(table.name()).append(" ( ");
		StringBuilder end = new StringBuilder(" ) VALUES (");
		Column[] columns = table.columns();
		int qty = columns.length;

		for (Column column : table.columns()) {
			sb.append(column.getName());
			end.append('?');
			if (column.getId() < qty) {
				sb.append(", ");
				end.append(", ");
			}
		}

		end.append(')');
		sb.append(end);
		return sb;
	}

	/** Binds values from {@code ContentValues} to an {@code SQLiteStatement}
	 * according to a {@code Table} schema.
	 *
	 * @see SQLiteProgram#bindLong */
	public static final void bindValuesInBulkInsert ( Table table, SQLiteStatement stmt, ContentValues values ) {
		int i = 1;
		String temp;
		Column[] columns = table.columns();
		for (Column col : columns) {
			if (col.getType().equalsIgnoreCase("text")) {
				temp = values.getAsString(col.getName());
				stmt.bindString(i++, temp != null ? temp : "");
			} else if (col.getType().equalsIgnoreCase("integer")) {
				stmt.bindLong(i++, values.getAsLong(col.getName()));
			}
			// TODO implement other types
		}
	}

	/**
	* Generate a 'create table' statement for a Table.
	* StringBuilder is optional.
	* */
	public static final StringBuilder createTableStatement ( Table table, StringBuilder sb ) throws IndexOutOfBoundsException, NullPointerException, SQLException {
		Column[] columns = table.columns();

		if (null==sb) sb = new StringBuilder();

		sb.append("CREATE TABLE IF NOT EXISTS " + table.name() + " (");

		sb = appendColumnStatement(columns[0], sb); // so ", " isn't prepended

		int len = columns.length;

		for (int i = 1; i < len; i++) { // i=1 so that col 0 isn't repeated
			final Column c = columns[i];
			sb.append(", ");
			sb = appendColumnStatement(c, sb);
		}
		sb.append(");");

		for (Column c : table.columns()) {
			if (true == c.isIndexed()) {
				sb.append(table.name()).append(" (").append("CREATE INDEX rep_").append(c.getName()).append(" on ").append(table.name()).append("(").append(c.getName()).append(");");
			}
		}

		return sb;
	}

	/**
	 * StringBuilder is optional.
	 * */
	public static final StringBuilder appendColumnStatement(Column column, StringBuilder sb) {
		if (null==sb) sb = new StringBuilder();
		String name = column.getName();
		sb.append(name).append(' ').append(column.getType().toUpperCase(Locale.US));
		String constraints = column.getConstraints();
		if (null!=constraints){ sb.append(' ').append(constraints); }
		return sb;
	}
	/** @return StringBuilder the drop table statement */
	public static final StringBuilder dropTableStatement ( Table table, StringBuilder sb ) {
		if (null==sb) {
			sb = new StringBuilder();
		}
		sb.append("DROP TABLE IF EXISTS " + table.name() + ";");
		return sb;
	}
}
