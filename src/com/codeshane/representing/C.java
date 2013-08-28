/* ProviderConstants is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import android.content.ContentResolver;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 21, 2013
 * @version 1
 */
public final class C {
	/** @see ContentResolver.SCHEME_CONTENT */
	public static final String SCHEME_CONTENT = "content";
	/** @see ContentResolver.SCHEME_FILE */
	public static final String SCHEME_FILE = "file";
	/** @see ContentResolver.SCHEME_ANDROID_RESOURCE */
	public static final String SCHEME_ANDROID_RESOURCE = "android.resource";

	public static final String SCHEME_HTTP = "http";
	public static final String SCHEME_HTTPS = "https";
	public static final String SCHEME_FTP = "ftp";
	public static final String SCHEME_SFTP = "sftp";
	public static final String SCHEME_SUFFIX = "://";

    public static final String VND = "/vnd.";

    /** @see ContentResolver.CURSOR_ITEM_BASE_TYPE */
    public static final String MIME_CURSOR_ROW_VND = ContentResolver.CURSOR_ITEM_BASE_TYPE + VND;
    /** @see ContentResolver.CURSOR_DIR_BASE_TYPE */
    public static final String MIME_CURSOR_DIR_VND = ContentResolver.CURSOR_DIR_BASE_TYPE + VND;
}
