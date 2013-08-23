/* DbUtils is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import com.codeshane.project_72.meta.Column;
import com.codeshane.project_72.meta.Table;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 1
 */
public class DbUtils {
	public static final String	TAG	= DbUtils.class.getPackage().getName() + "." + DbUtils.class.getSimpleName();

	public static final String CREATE_TABLE = "CREATE TABLE ";
	public static final String CREATE_INDEX = "CREATE INDEX rep_";

	/** Generates a raw SQL Create Table statement suitable for execution via {code db.execSQL(String)}.
	 * @see SQLiteDatabase#execSQL(String) */
    public static final String getCreateTableRawStatement(Table table) {
    	StringBuilder sb = new StringBuilder();
    	StringBuilder indexes = new StringBuilder();
    	sb.append(table.name()).append(" (");
    	for (Column c: table.columns()) {
    		sb.append(c.getName()).append(' ').append(c.getType()).append(", ");
    		if (true==c.isIndexed()){
    				indexes.append("CREATE INDEX rep_").append(c.getName()).append(" on ").append(table.name()).append("(").append(c.getName()).append(");");
    		}
    	}
    	return sb.append("PRIMARY KEY (").append(BaseColumns._ID).append("));").append(sb).toString();
    }

    public static final void createTable(SQLiteDatabase db, Table table) {
    	db.execSQL(getCreateTableRawStatement(table));
    }

    //TODO simple migrations
    /** Upgrades should be handled in a more realistic fashion, not just drop & replace...
     * */
    @Deprecated
    public static final void upgradeTable(SQLiteDatabase db, Table table, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("DROP TABLE IF EXISTS " + table.name() + ";");
            createTable(db, table);
            return;
        }

        if (oldVersion != newVersion) {
            throw new IllegalStateException("Error upgrading the database to version " + newVersion);
        }

    }

    /** A string for making bulk inserts.
     * */
    public static final String getBulkInsertString(Table table) {
        StringBuilder sb= new StringBuilder("INSERT INTO ").append(table.name()).append(" ( ");
        StringBuilder end = new StringBuilder(" ) VALUES (");
        Column[] columns = table.columns();
        int qty = columns.length;

        for (Column column : table.columns()) {
        	sb.append(column.getName());
        	end.append('?');
        	if (column.getIndex()<qty){
        		sb.append(", ");
        		end.append(", ");
        	}
        }

        end.append(')');
        sb.append(end);
        return sb.toString();
    }

	/** Binds values from {@code ContentValues} to an {@code SQLiteStatement} according to a {@code Table} schema.
	 *
	 * @see SQLiteProgram#bindLong */
   public static final void bindValuesInBulkInsert(Table table, SQLiteStatement stmt, ContentValues values) {
       int i = 1;
       String temp;
       Column[] columns = table.columns();
       for (Column col : columns){
       	if (col.getType().equalsIgnoreCase("text")){
       		temp = values.getAsString(col.getName());
               stmt.bindString(i++, temp != null ? temp : "");
       	} else if (col.getType().equalsIgnoreCase("integer")) {
       		stmt.bindLong(i++, values.getAsLong(col.getName()));
       	}
       	//TODO implement other types
       }
   }

}
