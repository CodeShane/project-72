/* RestUtils is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

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

import android.content.ContentResolver;
import android.os.Build;
import android.text.TextUtils;

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
    	return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + authority + tableName;
    }

	/** Content-provider mime type for a single result row of a given authority & table. */
	public static final String getMimeItem(String authority, String tableName){
    	return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + authority + tableName;
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




}
