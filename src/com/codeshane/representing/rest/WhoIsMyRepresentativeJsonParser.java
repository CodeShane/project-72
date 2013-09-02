/* WimrJsonParser is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.rest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import com.codeshane.util.Log;

import com.codeshane.representing.providers.Rep;

/** Utility class to enable consumption of the DSL REST API resources at http://whoismyrepresentative.com/api.
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 25, 2013
 * @version 1
 */
public class WhoIsMyRepresentativeJsonParser {
	public static final String	TAG	= WhoIsMyRepresentativeJsonParser.class.getPackage().getName() + "." + WhoIsMyRepresentativeJsonParser.class.getSimpleName();

	/** Given the json response, converts it into RepItems.
	 * @param jsonResponse A json-formatted string from the indicated API.
	 * @return repItems ArrayList of RepItem */
	public static final ArrayList<ContentValues> parseJsonResult (String jsonResult){
		ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();

		if (null==jsonResult) {
			Log.e(TAG,"Can't parse a null String");
			return contentValues;
		}
		if (jsonResult.equalsIgnoreCase("")) {
			Log.e(TAG,"Can't parse an empty String");
			return contentValues;
		}
		if (jsonResult.equalsIgnoreCase("<result message='No Data Found' />")) {
			Log.i(TAG,"Returned correctly, but no results.");
			return contentValues;
		}

		ArrayList<Rep> repItems = new ArrayList<Rep>();

		JSONObject jsonRoot = null;
		JSONArray results = null;

		try {
			jsonRoot = new JSONObject(jsonResult);
		} catch (JSONException ex) {}
		if (null==jsonRoot) { Log.e(TAG, "Couldn't parse received data as json."); return null; }

		try {
			results = jsonRoot.getJSONArray("results");
		} catch (JSONException ex) {}
		if (null==results) { Log.e(TAG, "Couldn't parse expected \"results\" json array."); return null; }

		for (int i = 0; i < results.length(); i++) {
			JSONObject item = null;
			try {
				item = results.getJSONObject(i);
			} catch (JSONException ex) {}
			if (null==item) { Log.e(TAG, "Couldn't find expected \'item\' json object."); return null; }

			Rep ri = Rep.update(null,item);
			Log.i(TAG,"ri as string: "+ri.toJson().toString());

			repItems.add(ri);

			contentValues.add(ri.toContentValues());

		}
		Log.v(TAG,repItems.size() + " repItems");

		return contentValues;
	}
}
