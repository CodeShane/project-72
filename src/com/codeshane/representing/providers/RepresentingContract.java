/* RepresentativesTable is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.project_72.model;

import static com.codeshane.project_72.meta.ProviderConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.provider.BaseColumns;

import com.codeshane.project_72.meta.Column;
import com.codeshane.project_72.meta.Table;
import com.codeshane.project_72.providers.RepsContentProvider;
import com.codeshane.util.Utils;

/** Representative table data model.
 * @author Shane Ian Robinson <shane@codeshane.com>
 * @since Aug 19, 2013
 * @version 3
 * */
public final class RepresentativesTable implements Table {
	public static final String	TABLE_NAME		= "RepresentativesTable";

	public static final Uri		CONTENT_URI		= Uri.parse(RepsContentProvider.AUTHORITY);
	// Uri.parse(RepresentativesContent.CONTENT_URI + "/" + TABLE_NAME);

	public static final Uri	A = Uri.fromParts(CONTENT_SCHEME, RepsContentProvider.AUTHORITY, null);

	public static final String MIME_TABLE_ROW = MIME_CURSOR_ROW + "/vnd." + RepsContentProvider.AUTHORITY + TABLE_NAME;
	public static final String MIME_TABLE_DIR = MIME_CURSOR_DIR + "/vnd." + RepsContentProvider.AUTHORITY + TABLE_NAME;
//	public static final String	TYPE_ELEM_TYPE	= MIME_CURSOR_ROW + "/" + "com.codeshane.project_72.data.representatives-table";
//	public static final String	TYPE_DIR_TYPE	= MIME_CURSOR_DIR + "/" + "com.codeshane.project_72.data.representatives-table";

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

		/** @see com.codeshane.project_72.meta.Column#isIndexed() */
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

	public RepresentativesTable () {}

	/** @see com.codeshane.project_72.meta.Table#name() */
	@Override public String name () {
		return null;

	}

	/** @see com.codeshane.project_72.meta.Table#columns() */
	@Override public Column[] columns () {
		return null;

	}

	/** Returns a list of
	 * */
	public static final List<RepItem> findAllItems() {
	    JSONObject serviceResult = Utils.requestWebService("http://url/to/findAllService",5000,5000);

	    List<RepItem> foundItems = new ArrayList<RepItem>(20);

	    try {
	        JSONArray items = serviceResult.getJSONArray("results");

	        for (int i = 0; i < items.length(); i++) {
	            JSONObject obj = items.getJSONObject(i);
	            foundItems.add(
	            new RepItem(
	            		String.valueOf(i),
	            		obj.getString("name"),
	            		obj.getString("party"),
	            		obj.getString("state"),
	            		obj.getString("district"),
	            		obj.getString("phone"),
	            		obj.getString("office"),
	            		obj.getString("link")));
	        }
	    } catch (JSONException e) {
	    }
	    return foundItems;
	}
}
