/* RepresentativesTable is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.providers;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.codeshane.representing.C;
import com.codeshane.representing.meta.Column;
import com.codeshane.representing.meta.Table;

/** Operational specification contract for the "Reps" ContentProvider and all tables it provides.
 *
 * @author Shane Ian Robinson <shane@codeshane.com>
 * @since Aug 19, 2013
 * @version 3
 * Common practices?
 * @see android.provider.BaseColumns
 * @see android.provider.CalendarContract
 * @see android.provider.CalendarContract.CalendarColumns
 * @see android.provider.CalendarContract.SyncColumns
 * */
public final class RepresentingContract implements Table {

	public static final String VENDOR = "com.codeshane";

	public static final String PROJECT = "representing";

	/** The authority for the ContentProvider of this data */
    public static final String AUTHORITY = VENDOR + "." + PROJECT;

    public static final String DATABASE_NAME = "RepresentingDB";

	public static final String	TABLE_NAME = "representatives";//RepresentativesTable";

	public static final Uri URI_BASE = Uri.parse(C.SCHEME_CONTENT + C.SCHEME_SUFFIX + AUTHORITY);

	/** Use URI_BYZIP.buildUpon().appendPath(~zipString~).build() */
	public static final Uri URI_BYZIP = URI_BASE.buildUpon().appendPath("getall_mems").build();

	/** Permission required to save data to this database. */
	static final String	PERMISSION_SAVE_REPRESENTATIVES	= "com.codeshane.representing.permission.SAVE_REPRESENTATIVES";

	/** The MIME type of {@link #CONTENT_URI} providing a directory of status messages. */
	public static final String CONTENT_TYPE = C.MIME_CURSOR_DIR_VND + VENDOR + "." + TABLE_NAME;

	/** The MIME type of a {@link #CONTENT_URI} a single status message. */
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + VENDOR + "." + TABLE_NAME;

	public RepresentingContract () {}

	/** @see com.codeshane.representing.meta.Table#name() */
	@Override public String name () {
		return TABLE_NAME;
	}

	/** @see com.codeshane.representing.meta.Table#columns() */
	@Override public Column[] columns () { return Columns.values(); }

	/** @see com.codeshane.representing.meta.Table#upgrade(android.database.sqlite.SQLiteDatabase, int, int) */
	@Override public boolean upgrade ( SQLiteDatabase db, int oldversion, int newversion ) {
		return false;
	}

	/** @see com.codeshane.representing.meta.Table#create(android.database.sqlite.SQLiteDatabase) */
	@Override public boolean create ( SQLiteDatabase db ) {
		return false;
	}

	public static enum Columns implements Column {

		ID(BaseColumns._ID, "integer", false), UPDATED("updated", "integer", false), NAME("name", "text", true), PARTY("party", "text", false), STATE("state",
			"text", true), ZIP("zip", "text", true), DISTRICT("district", "text", false), PHONE("phone", "text", false), OFFICE("office", "text", false), LINK(
			"link", "text", false);

		private final String	mName;
		private final String	mType;
		private final boolean	mIsIndexed;

		/** Private constructor for static enum initialization.
		 *
		 * @param name
		 *            The name for the column.
		 * @param type
		 *            The Sqlite type affinity for the column. */
		private Columns ( String name, String type, boolean isIndexed ) {
			mName = name;
			mType = type;
			mIsIndexed = isIndexed;
		}

		/** @see Column#getIndex() */
		@Override public int getIndex () {
			return ordinal();
		}

		/** @see Column#getName() */
		@Override public String getName () {
			return mName;
		}

		/** @see Column#getType() */
		@Override public String getType () {
			return mType;
		}

		/** @see com.codeshane.representing.meta.Column#isIndexed() */
		@Override public boolean isIndexed () {
			return mIsIndexed;
		}
	} // end Column

	/** Array of column names. */
	public static final String[]	PROJECTION	= new String[Columns.values().length];
	static {
		for (Columns c : Columns.values()) {
			PROJECTION[c.ordinal()] = c.name();
		}
	};

}
