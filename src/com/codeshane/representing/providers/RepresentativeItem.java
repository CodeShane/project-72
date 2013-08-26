/* RepItem is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.project_72.model;

import org.json.JSONArray;
import org.json.JSONException;

import android.database.Cursor;

/** Data holder representing a Representative instance. Data can be populated
 * manually, or by a cursor or JSONArray (so long as the received data order
 * doesn't change!)
 *
 * @author Shane Ian Robinson <shane@codeshane.com>
 * @since Aug 22, 2013
 * @version 1 */
public class RepItem {
	public static final String	TAG	= RepItem.class.getPackage().getName() + "." + RepItem.class.getSimpleName();

	public String id;
	public String name;
	public String party;
	public String district;
	public String state;
	public String office;
	public String phone;
	public String link;

	public RepItem ( String id, String name, String party, String district, String state, String office, String phone, String link ) {
		this.id = id;
		this.name = name;
		this.party = party;
		this.district = district;
		this.state = state;
		this.office = office;
		this.phone = phone;
		this.link = link;
	}

	/** Populates an existing (or new if param is null) RepItem instance with
	 * data from a cursor.
	 *
	 * @return repItem (chainable) */
	public static RepItem update ( RepItem repItem, Cursor c ) {
		if (null == repItem) { return new RepItem(
				c.getString(0), c.getString(1), c.getString(2), c.getString(3),
				c.getString(4), c.getString(5), c.getString(6), c.getString(7)); }
		repItem.id = c.getString(0);
		repItem.name = c.getString(1);
		repItem.party = c.getString(2);
		repItem.district = c.getString(3);
		repItem.state = c.getString(4);
		repItem.office = c.getString(5);
		repItem.phone = c.getString(6);
		repItem.link = c.getString(7);
		return repItem;
	}

	/** Populates an existing (or new if param is null) RepItem instance with
	 * data from a Json array.
	 *
	 * @return repItem (chainable) */
	public static RepItem update ( RepItem repItem, JSONArray c ) {
		try {
			if (null == repItem) { return new RepItem(
				c.getString(0), c.getString(1), c.getString(2), c.getString(3),
				c.getString(4), c.getString(5), c.getString(6), c.getString(7)); }
			repItem.id = c.getString(0);
			repItem.name = c.getString(1);
			repItem.party = c.getString(2);
			repItem.district = c.getString(3);
			repItem.state = c.getString(4);
			repItem.office = c.getString(5);
			repItem.phone = c.getString(6);
			repItem.link = c.getString(7);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return repItem;
	}

	@Override public String toString () {
		return "RepItem [id=" + id + ", name=" + name + ", district=" + district + ", state=" + state + ", office=" + office + ", party=" + party + ", phone="
			+ phone + ", link=" + link + "]";
	}

	public JSONArray toJson() {
		return new JSONArray().put(id).put(name).put(party).put(district).put(state).put(office).put(phone).put(link);
	}


}
