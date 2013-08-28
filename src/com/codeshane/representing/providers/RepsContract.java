/* RepresentativesTable is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.providers;

import android.net.Uri;

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
public interface RepsContract {

	public static final String VENDOR = "com.codeshane";

	public static final String PROJECT = "representing";

	/** The authority for the ContentProvider of this data */
    public static final String AUTHORITY = VENDOR + "." + PROJECT;

    public static final Uri URI_BASE = Uri.parse(C.SCHEME_CONTENT + C.SCHEME_SUFFIX + AUTHORITY);

    public static final String DATABASE_NAME = "RepresentingDB";

	public static final String PATH_ZIP = "getall_mems";
	public static final String PATH_REP_NAME = "getall_reps_byname";
	public static final String PATH_REP_STATE = "getall_reps_bystate";
	public static final String PATH_SEN_NAME = "getall_sens_byname";
	public static final String PATH_SEN_STATE = "getall_sens_bystate";

	/** Use URI_BYZIP.buildUpon().appendPath(~zipString~).build() */
	public static final Uri URI_BYZIP = URI_BASE.buildUpon().appendPath("getall_mems").build();

	/** Permission required to save data to this database. */
	static final String	PERMISSION_SAVE_REPRESENTATIVES	= "com.codeshane.representing.permission.SAVE_REPRESENTATIVES";

	/** This sub-contract represents the resources/tables accessible through this Content Provider. */
	public static enum Tables implements Table {
		Representatives(6);

		private int mVersion;

		/** Private constructor for static enum initialization.
		 * @param type The Sqlite type affinity
		 * @param isIndexed True if the column should be indexed.
		 * */
		private Tables (int version) {
			mVersion = version;
		}

		/** @see com.codeshane.representing.meta.Table#getName() */
		@Override public int version() { return mVersion; }

		/** @see com.codeshane.representing.meta.Table#getColumns() */
		@Override public Column[] columns() { return Columns.values(); }

		/** The MIME type of {@link #CONTENT_URI} providing a directory of content items. */
		public final String getDirMimeType() { return C.MIME_CURSOR_DIR_VND + VENDOR + "." + name(); }

		/** The MIME type of a {@link #CONTENT_URI} a single content item. */
		public final String getItemMimeType() { return C.MIME_CURSOR_ROW_VND + VENDOR + "." + name(); }

		public static enum Columns implements Column {

			_id("INTEGER", false), UPDATED("INTEGER", false), NAME("TEXT", true), PARTY("TEXT", false), STATE("TEXT", true),
			ZIP("TEXT", true), DISTRICT("TEXT", false), PHONE("TEXT", false), OFFICE("TEXT", false), LINK("TEXT", false);

			private final String	mType;
			private final boolean	mIsIndexed;

			private Columns ( String type, boolean isIndexed ) {
				mType = type;
				mIsIndexed = isIndexed;
			}

			@Override public int getId () { return ordinal(); }

			/* Caution: not to be confused with the enum constant method name() which returns the enum's
			 * text equivalent, the name of its variable/type; this is distinct so that migrations won't require
			 * renaming the enum constants themselves.*/
			@Override public String getName () { return name(); }
			@Override public String getType () { return mType; }
			@Override public boolean isIndexed () { return mIsIndexed; }
			@Override public String getCreationSuffix () { return (this.equals(_id)) ? Column.PRI_KEY_AUTO: null; }

			/** Array of column names. */
			public static final String[] PROJECTION = new String[Columns.values().length];
			static {
				for (Columns c : Columns.values()) {
					PROJECTION[c.ordinal()] = c.getName();
				}
			};
		} // end Columns
	} // end 'representatives' table

}

