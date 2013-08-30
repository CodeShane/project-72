/* ProviderConstants is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

/** Constants for {@code ContentProvider}s.
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 30, 2013
 * @version 1
 */
public class ProviderConstants {
	public static final String	TAG	= ProviderConstants.class.getPackage().getName() + "." + ProviderConstants.class.getSimpleName();

	public static final String KEY_QUERY_URI = "query.uri";
	public static final String KEY_QUERY_PROJECTION = "query.projection";
	public static final String KEY_QUERY_SELECTION = "query.selection";
	public static final String KEY_QUERY_SELECTION_ARGS = "query.selectionArgs";
	public static final String KEY_QUERY_SORT_ORDER = "query.sortOrder";

	public static final String SCHEME_HTTP = "http";
	public static final String SCHEME_SUFFIX = "://";
	public static final String VND = "/vnd.";
}
