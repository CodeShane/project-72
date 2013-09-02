/* RepItem is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.providers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

/** Data holder representing a Representative instance. Data can be populated
 * manually, or by a cursor or JSONObject.
 *
 * @author Shane Ian Robinson <shane@codeshane.com>
 * @since Aug 22, 2013
 * @version 1 */
public class Rep {
	public static final String	TAG	= Rep.class.getPackage().getName() + "." + Rep.class.getSimpleName();

	public static final String[] PROJECTION = {"id", "name", "party", "district", "state", "office", "phone", "link"};

	public long id;
	public String name;
	public String party;
	public String district;
	public String state;
	public String office;
	public String phone;
	public String link;

	public Rep ( long id, String name, String party, String district, String state, String office, String phone, String link ) {
		this.id = -1;
		this.name = name;
		this.party = party;
		this.district = district;
		this.state = state;
		this.office = office;
		this.phone = phone;
		this.link = link;
	}

	/** Populates an existing (or new if param is null) RepItem instance with
	 * data from a {@code Cursor}.
	 *
	 * @return repItem (chainable)
	 * @see Cursor */
	public static Rep update ( Rep repItem, Cursor c ) {
		if (null == repItem) { return new Rep(
				-1, c.getString(0), c.getString(1), c.getString(2),
				c.getString(3), c.getString(4), c.getString(5), c.getString(6)); }
		repItem.id = -1;
		repItem.name = c.getString(0);
		repItem.party = c.getString(1);
		repItem.district = c.getString(2);
		repItem.state = c.getString(3);
		repItem.office = c.getString(4);
		repItem.phone = c.getString(5);
		repItem.link = c.getString(6);
		return repItem;
	}

	/** Populates an existing (or new if param is null) RepItem instance with
	 * data from a Json array.
	 *
	 * @return repItem (chainable) */ // JSONObject can use .getString(name)
	public static Rep update ( Rep repItem, JSONObject object ) {
		if (null == repItem) { return new Rep(-1,
			object.optString("name"),  object.optString("party"), object.optString("district"),
			object.optString("state"), object.optString("office"),
			object.optString("phone"), object.optString("link"));
		}
		repItem.name = object.optString("name", "");
		repItem.party = object.optString("party", "");
		repItem.district = object.optString("district", "");
		repItem.state = object.optString("state", "");
		repItem.office = object.optString("office", "");
		repItem.phone = object.optString("phone", "");
		repItem.link = object.optString("link", "");
		return repItem;
	}

	@Override public String toString () {
		return "RepItem [id=" + id + ", name=" + name + ", district=" + district + ", state=" + state + ", office=" + office + ", party=" + party + ", phone="
			+ phone + ", link=" + link + "]";
	}

	public JSONObject toJson() {
		try {
			return new JSONObject().put("id", id).put("name", name).put("party", party).put("district", district).put("state", state).put("office", office).put("phone", phone).put("link", link);
		} catch (JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	public ContentValues toContentValues () {
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("party", party);
		contentValues.put("district", district);
		contentValues.put("state", state);
		contentValues.put("office", office);
		contentValues.put("phone", phone);
		contentValues.put("link", link);
		return contentValues;
	}


}
