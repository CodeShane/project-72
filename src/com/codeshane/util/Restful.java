/* Restful is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/** A wrapping interface for any form of data storage. ContentProvider
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 21, 2013
 * @version 1
 */
public interface Restful<T> {

	/** @see android.content.ContentProvider#create() */
	public boolean create ();

	/** @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String) */
	public Cursor query ( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder );

	/** @see android.content.ContentProvider#getType(android.net.Uri) */
	public String getType ( Uri uri );

	/** @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues) */
	public Uri insert ( Uri uri, ContentValues values );

	/** @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[]) */
	public int delete ( Uri uri, String selection, String[] selectionArgs );

	/** @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[]) */
	public int update ( Uri uri, ContentValues values, String selection, String[] selectionArgs );

}
