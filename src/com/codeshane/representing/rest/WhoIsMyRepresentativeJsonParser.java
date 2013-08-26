/* WimrJsonParser is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.rest;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.codeshane.representing.providers.RepresentativeItem;

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
		ArrayList<RepresentativeItem> repItems = new ArrayList<RepresentativeItem>();
		ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();

		try {
			JSONObject jsonRoot = new JSONObject(jsonResult);
			JSONArray results = jsonRoot.getJSONArray("results");
			for (int i = 0; i < results.length(); i++) {
				JSONObject item = results.getJSONObject(i);

				if(i==0){
					JSONArray names = item.names();
					int q = names.length();
					for (int j = 0; j < q; j++) {
						Log.i(TAG,"jsonName="+names.optString(j,""));
					}
				}

				RepresentativeItem ri = RepresentativeItem.update(null,item);
				if (null!=ri){
					Log.i(TAG,"RepItem!!>>>"+ri.toJson()+">>>"+ri.toString());
					repItems.add(ri);
					contentValues.add(ri.toContentValues());
				} else {
					Log.e(TAG, "RepItem didn't hatch..");
				}

			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return contentValues;
	}
}
