/* RestUtils is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.text.TextUtils;

import com.codeshane.util.Log;

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

//	/** Content-provider mime type for multiple result rows of a given authority & table. */
//	public static final String getMimeDir(String authority, String tableName){
//    	return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + authority + tableName;
//    }
//
//	/** Content-provider mime type for a single result row of a given authority & table. */
//	public static final String getMimeItem(String authority, String tableName){
//    	return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + authority + tableName;
//    }

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

	/** Reads an {@code HttpEntity} into a {@code String}.
	 *
	 * @param httpEntity
	 *            The {@link HttpEntity} to be parsed to raw text.
	 * @return String
	 * @throws NullPointerException */
	public static final String toString ( HttpEntity httpEntity ) throws NullPointerException, IllegalStateException, NoSuchElementException, IOException {
		return parseInputStream(httpEntity.getContent());
	}

	/** Convert an input stream to a String. */
	public static final String parseInputStream ( InputStream inStream ) throws IllegalStateException, NoSuchElementException, IOException {
		Scanner scanner = new Scanner(inStream).useDelimiter("\\A");
		String out = scanner.next();
		scanner.close();
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

	/* Returns the first value parameter that isn't null.
	 * <i>Efficiency method to avoid creating an array for varargs when only
	 * using two arguments (the vast majority of cases.)</i> */
	public static final <T> T get(final T nullable, T fallback){
		return (null==nullable)?fallback:nullable;
	}

	/* Returns the first value parameter that isn't null.
	 * <i>Efficiency method to avoid creating an array for varargs when only
	 * using two arguments (the vast majority of cases.)</i> */
	public static final <T> T get(final T... values){
		for (T t : values) {
			if (null!=t) return t;
		}
		return null;
	}

	/** Attempt to close a {@code Closeable}, ignore nulls, catch and log any exception. */
	public static final boolean closeQuietly(Closeable c){
		boolean success = false;
		try {
			if (null!=c){
				c.close();
			}
			success = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return success;
	}

	/** Logs the current thread. */
	public static final void threax(final String threax){
		Log.v("threax ","0x"+Long.toHexString(Thread.currentThread().getId())+threax);
	}

	/** Logs the schema of the current cursor, but not its data
	 * (privacy risk should it accidentally reach production.)
	 * @return void
	 */
	@TargetApi ( Build.VERSION_CODES.HONEYCOMB )
	public static final void dumpCursorSchema ( Cursor cursor ) {
		if (null==cursor) {Log.i(TAG,"dump(cursor) of null cursor."); return;}

		int len = cursor.getColumnCount();
		Log.i(TAG, "dump(cursor) of " + len + " columns and " + cursor.getCount() + " rows, currently at position " + cursor.getPosition());

		StringBuilder sb = new StringBuilder();

		for (int j = 0; j < len; j++) {
			sb.append(' ');
			sb.append(j);
			sb.append(' ');
			sb.append(cursor.getColumnName(j));
			if (Build.VERSION.SDK_INT > 11) { sb.append(' ').append(cursor.getType(j)); }
		}
		Log.i("dump(cursor) " + cursor.getCount() + "columns:", sb.toString());
	}


}
