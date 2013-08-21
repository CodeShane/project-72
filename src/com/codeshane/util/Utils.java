/* RestUtils is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Static parsing and converting utilities.
 *
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 19, 2013
 * @version 2
 * @see HttpEntity
 * @see JSONArray
 * @see JSONObject
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
}
