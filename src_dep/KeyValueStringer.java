/* KeyValueStringer is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

/** Basically made this to put key-value pairs together for a GET query until
 * I remember the class & method for doing this in the api. No more coding at 2am. hahaha (ya right)
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Sep 12, 2012
 * @version 1
 */
public class KeyValueStringer {
	public static final String	TAG	= KeyValueStringer.class.getPackage().getName() + "." + KeyValueStringer.class.getSimpleName();

	public static String[] PARAMS_HTTP_GET_QUERY_PARAMS = new String[]{"?", "", "", "=", "", "", "&amp;", ""};

	public static final int PARAM_ROOT_PREFIX = 0;
	public static final int PARAM_KEY_PREFIX = 1;
	public static final int PARAM_KEY_SUFFIX = 2;
	public static final int PARAM_K_V_INFIX = 3;
	public static final int PARAM_VALUE_PREFIX = 4;
	public static final int PARAM_VALUE_SUFFIX = 5;
	public static final int PARAM_PAIR_DELIM = 6;
	public static final int PARAM_ROOT_SUFFIX = 7;

	/** Concatenates {@code Map}ped pairs of {@code String}s with delimiters.
	 * <p>The params array must be eight strings, to be used as delimiters. Use an empty string, "", in any positions
	 * that are not to be delineated.
	 * <p><b>Delimiter array positions:</b><i>0</i><i>1</i>key<i>2</i><i>3</i><i>4</i>value<i>5</i><i>6</i><i>7</i></p>
	 * <p><b>example array:</b>  {&lt;&gt;="",}</p>
	 * <p><b>example output:</b>  {&lt;key&gt;="value",&lt;key&gt;="value"}</p>
	 * <p><b>http GET query parameters:</b>  <i>0</i><i>1</i>key<i>2</i><i>3</i><i>4</i>value<i>5</i><i>6</i><i>7</i><i>8</i></p>
	 * <p><b>http GET query parameters:</b>  <i>0</i><i>1</i>key<i>2</i><i>3</i><i>4</i>value<i>5</i><i>6</i><i>7</i><i>8</i></p>
	 * @param stringPairs The {@code <String,String>} pairs (often key-value pairs) to be concatenated.
	 * */
	public static String concatenatePairs(Map<String,String> stringPairs, String[] params) {

		if (null==stringPairs || 0==stringPairs.size()) {
			return "";
		}

		if (null==params || params.length !=8) {
			Log.e(TAG,"KeyValueStringer did not receive valid params.");
			params=PARAMS_HTTP_GET_QUERY_PARAMS;
		}

		Iterator<Map.Entry<String,String>> it = stringPairs.entrySet().iterator();

		StringBuilder builder = new StringBuilder(params[PARAM_ROOT_PREFIX]);

		boolean hasNext = it.hasNext();

		while (hasNext) {
			Entry<String,String> keyValuePair = it.next();
			builder.append(params[PARAM_KEY_PREFIX]).append(keyValuePair.getKey()).append(params[PARAM_KEY_SUFFIX]);
			builder.append(params[PARAM_K_V_INFIX]);
			try {
				builder.append(params[PARAM_VALUE_PREFIX]);
				builder.append(URLEncoder.encode(keyValuePair.getValue(), "UTF-8"));
				builder.append(params[PARAM_VALUE_SUFFIX]);
			} catch (UnsupportedEncodingException ex) {
				Log.e(TAG, "UnsupportedEncodingException: \"UTF-8\" failed, trying system encoding.");
				builder.append(URLEncoder.encode(keyValuePair.getValue()));
			}
			hasNext = it.hasNext();
			if (hasNext) {
				builder.append(PARAM_PAIR_DELIM);
			}
		}

		return builder.toString();
	}

	//TODO convert to builder
//	public class Params {
//		public String setPrefix; public String setSuffix;
//		public String keyPrefix; private String	keySuffix;
//		public String keyValueInfix; public String pairDelim;
//		private String valuePrefix; public String valueSuffix;
//
//		/** @see Params */
//		public Params (
//			String setPrefix, String setSuffix,
//			String keyPrefix, String keySuffix,
//			String keyValueInfix, String pairDelim,
//			String valuePrefix, String valueSuffix
//			) {
//			super();
//			this.setPrefix = setPrefix; this.setSuffix = setSuffix;
//			this.keyPrefix = keyPrefix; this.keySuffix = keySuffix;
//			this.keyValueInfix = keyValueInfix; this.pairDelim = pairDelim;
//			this.valuePrefix = valuePrefix; this.valueSuffix = valueSuffix;
//		}
//	}
}
