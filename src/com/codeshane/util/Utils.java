/* RestUtils is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import static com.codeshane.representing.C.MIME_CURSOR_DIR;
import static com.codeshane.representing.C.MIME_CURSOR_ROW;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.codeshane.representing.meta.Column;
import com.codeshane.representing.meta.Table;

/** Static parsing and converting utilities.
 *
 * @author Shane Ian Robinson <shane@codeshane.com>
 * @since Aug 19, 2013
 * @version 3
 * @see HttpEntity
 * @see JSONArray
 * @see JSONObject
 * @see org.apache.http.util.EntityUtils
 * @see org.apache.http.util.EncodingUtils */
public class Utils {
	public static final String	TAG	= Utils.class.getPackage().getName() + "." + Utils.class.getSimpleName();

	private Utils () {}

	/** Content-provider mime type for multiple result rows of a given authority & table. */
	public static final String getMimeDir(String authority, String tableName){
    	return MIME_CURSOR_DIR + "/vnd." + authority + tableName;
    }

	/** Content-provider mime type for a single result row of a given authority & table. */
	public static final String getMimeRow(String authority, String tableName){
    	return MIME_CURSOR_ROW + "/vnd." + authority + tableName;
    }

	/** Parses a {@code String} into a {@code JSONArray}.
	 *
	 * @param String
	 * @return JSONArray */
	public static final JSONArray toJsonArray ( String data ) throws JSONException {
		return new JSONArray(data);
	}

	/** Parses a {@code String} into a {@code JSONObject}.
	 *
	 * @param String
	 * @return JSONArray */
	public static final JSONObject toJsonObject ( String data ) throws JSONException {
		return new JSONObject(data);
	}

	/** Bug workaround(s) relating to http.
	 *
	 * @see HttpURLConnection */
	public static final void disableLegacyKeepAlive () {
		// see HttpURLConnection API doc
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	/** Get the response text from an input stream. {@link http
	 * ://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html} */
	public static final String getResponseText ( InputStream inStream ) {
		return new Scanner(inStream).useDelimiter("\\A").next();
	}

	/** Reads an {@code HttpEntity} into a {@code String}.
	 *
	 * @param httpEntity
	 *            The {@link HttpEntity} to be parsed to raw text.
	 * @return String */
	public static final String toString ( HttpEntity httpEntity ) throws IllegalStateException, IOException {
		return parseInputStream(httpEntity.getContent());
	}

	/** Reads an {@code InputStream} into a {@code String}.
	 *
	 * @param inputStream
	 *            The {@link InputStream} to be parsed to raw text.
	 * @return String */
	public static String parseInputStream ( InputStream inputStream ) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] data = new byte[2048];
		int readLen = 1;
		while (readLen > 0) {
			readLen = inputStream.read(data);
			out.append(new String(data, 0, readLen, "UTF-8"));
		}
		return out.toString();
	}

	public static String parseInputStream2 ( InputStream inputStream ) throws IOException {
		if (null==inputStream) {
			System.out.println("Input Stream == null");
			return "";
		}
//	    FileInputStream fis = new FileInputStream("c:/sample.txt");
//		String inputStreamString = new Scanner(inputStream,"UTF-8").useDelimiter("\\A").next();
		String out = null;
	    Scanner s = null;
	    s = new Scanner(inputStream);
	    s.useDelimiter("\\A");

	    out = s.next();

	    s.close();
	    return out;
	}

	/** Useful for converting a hostname to a Java namespace and back again.
	 * Example: {"www","codeshane","com"} <--> {"com","codeshane","www"}
	 * */
	public static String[] reverseString(String[] hostParts){
//		String[] hostParts = uri.getHost().split("\\."); // consumes the char
		Collections.reverse(Arrays.asList(hostParts));
		return hostParts;
	}

	/** Useful for converting a hostname to a Java namespace and back again.
	 * Example: "www.codeshane.com" <--> "com.codeshane.www"
	 * */
	public static String reverseHostQualifiers(String qualifiedString){
		String[] hostParts = qualifiedString.split("\\."); // consumes the char
		Collections.reverse(Arrays.asList(qualifiedString));
		return TextUtils.join(".", hostParts);
	}

	/** Useful for converting a hostname to a Java namespace and back again.
	 * This returns an array so you can choose which parts to keep.
	 * Example: "www.codeshane.com" <--> "com.codeshane.www"
	 * @see #reverseHostQualifiers(String)
	 * */
	public static String[] reverseHostQualifiersRaw(String qualifiedString){
		String[] hostParts = qualifiedString.split("\\."); // consumes the char
		Collections.reverse(Arrays.asList(qualifiedString));
		return hostParts;
	}

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

	/** Generates a raw SQL Create Table statement suitable for execution via
	 * {code db.execSQL(String)}.
	 *
	 * @see SQLiteDatabase#execSQL(String) */
	public static final String getCreateTableRawStatement ( Table table ) {
		StringBuilder sb = new StringBuilder();
		StringBuilder indexes = new StringBuilder();
		sb.append(table.name()).append(" (");
		for (Column c : table.columns()) {
			sb.append(c.getName()).append(' ').append(c.getType()).append(", ");
			if (true == c.isIndexed()) {
				indexes.append("CREATE INDEX rep_").append(c.getName()).append(" on ").append(table.name()).append("(").append(c.getName()).append(");");
			}
		}
		return sb.append("PRIMARY KEY (").append(BaseColumns._ID).append("));").append(sb).toString();
	}

	public static final void createTable ( SQLiteDatabase db, Table table ) {
		db.execSQL(getCreateTableRawStatement(table));
	}

	// TODO simple migrations
	/** Upgrades should be handled in a more realistic fashion, not just drop &
	 * replace... */
	@Deprecated public static final void upgradeTable ( SQLiteDatabase db, Table table, int oldVersion, int newVersion ) {
		if (oldVersion < 1) {
			db.execSQL("DROP TABLE IF EXISTS " + table.name() + ";");
			createTable(db, table);
			return;
		}

		if (oldVersion != newVersion) { throw new IllegalStateException("Error upgrading the database to version " + newVersion); }

	}

	/** A string for making bulk inserts. */
	public static final String getBulkInsertString ( Table table ) {
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(table.name()).append(" ( ");
		StringBuilder end = new StringBuilder(" ) VALUES (");
		Column[] columns = table.columns();
		int qty = columns.length;

		for (Column column : table.columns()) {
			sb.append(column.getName());
			end.append('?');
			if (column.getIndex() < qty) {
				sb.append(", ");
				end.append(", ");
			}
		}

		end.append(')');
		sb.append(end);
		return sb.toString();
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
}
