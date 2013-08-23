/* RestUtils is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.provider.BaseColumns;

import com.codeshane.project_72.meta.Column;

/** Static parsing and converting utilities.
 *
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 19, 2013
 * @version 2
 * @see HttpEntity
 * @see JSONArray
 * @see JSONObject
 * @see org.apache.http.util.EntityUtils
 * @see org.apache.http.util.EncodingUtils
 */
public class Utils {
	public static final String	TAG	= Utils.class.getPackage().getName() + "." + Utils.class.getSimpleName();

	private Utils(){}

	/** Parses a {@code String} into a {@code JSONArray}.
	 * @param String
	 * @return JSONArray
	 * */
	public static final JSONArray toJsonArray(String data) throws JSONException{
		return new JSONArray(data);
	}

	/** Parses a {@code String} into a {@code JSONObject}.
	 * @param String
	 * @return JSONArray
	 * */
	public static final JSONObject toJsonObject(String data) throws JSONException{
		return new JSONObject(data);
	}

	/** Bug workaround(s) relating to http.
	 * @see HttpURLConnection
	 */
	public static final void disableLegacyKeepAlive() {
	    // see HttpURLConnection API doc
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}

	/** Get the response text from an input stream.
	 * {@link http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html}
	 * */
	public static final String getResponseText(InputStream inStream) {
	    return new Scanner(inStream).useDelimiter("\\A").next();
	}

	/**  Reads an {@code HttpEntity} into a {@code String}.
	 * @param httpEntity The {@link HttpEntity} to be parsed to raw text.
	 * @return String
	 */
	public static final String toString(HttpEntity httpEntity) throws IllegalStateException, IOException {
		return parseInputStream(httpEntity.getContent());
	}

	/** Reads an {@code InputStream} into a {@code String}.
	 * @param httpEntity The {@link HttpEntity} to be parsed to raw text.
	 * @return String
	 */
	public static String parseInputStream(InputStream inputStream) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] data = new byte[2048];
        int readLen = 1;
        while (readLen > 0) {
        	readLen = inputStream.read(data);
            out.append(new String(data, 0, readLen, "UTF-8"));
        }
        return out.toString();
    }


    /** Generates a statement that includes id and a given where statement. For example:
     * @param selection ex: "name = frank"
     * @returns: where statement ex: "_ID = ? AND (name = frank)"
     * */
    public static final String whereWithId(String selection) {
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
     * @param column ex: a column named "myColumn"
     * @returns: where statement ex: "myColumn = ?"
     * */
    public static final String whereColumn(Column column) {
    	return whereColumn(column.getName());
    }

    /** Generates a where statement for a given column name. For example:
     * @param column ex: a column named "myColumn"
     * @returns: where statement ex: "myColumn = ?"
     * */
    public static final String whereColumn(String columnName) {
    	return new StringBuilder(256).append(columnName).append(" = ?").toString();
    }

    /** Creates a new String[] of selectionArgs that includes an additional column name.
     * @param selectionArgs as String[]
     * @returns: selectionArgs as new String[]
     * */
    public static final String[] addColumnToSelectionArgs(Column column, String[] selectionArgs) {
    	return addColumnToSelectionArgs(column.getName(), selectionArgs);
    }

    /** Creates a new String[] of selectionArgs that includes an additional column name.
     * @param selectionArgs as String[]
     * @returns: selectionArgs as new String[]
     * */
    public static final String[] addColumnToSelectionArgs(String columnName, String[] selectionArgs) {
        if (selectionArgs == null) {
            return new String[] { columnName };
        }

        int length = selectionArgs.length;
        String[] newSelectionArgs = new String[length + 1];
        newSelectionArgs[0] = columnName;
        System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, length);
        return newSelectionArgs;
    }

}
