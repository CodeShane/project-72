/* RepDetailFragment is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.project_72;

import com.codeshane.project_72.model.RepItem;
import com.codeshane.project_72.model.RepresentativesTable;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** A fragment representing a single Rep detail screen.
 * This fragment is either contained in a {@link RepListActivity} in two-pane
 * mode (on tablets) or a {@link RepDetailActivity} on handsets. */
public class RepDetailFragment extends Fragment {

	/** The fragment argument representing the item ID that this fragment
	 * represents. */
	public static final String		ARG_ITEM_ID	= "item_id";

	TextView rep_name;
	TextView rep_district;
	TextView rep_state;
	TextView rep_office;
	TextView rep_party;
	TextView rep_phone;
	TextView rep_link;

	/** The content this fragment is presenting. */
	private RepItem	repItem;

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepDetailFragment () {}

	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			Cursor cursor = this.getActivity().getContentResolver().query(RepresentativesTable.CONTENT_URI, RepresentativesTable.PROJECTION, RepresentativesTable.Columns.ID.getName() + " = ? ",new String[]{ARG_ITEM_ID}, null);
			if (cursor.moveToFirst()){
				repItem = RepItem.update(repItem, cursor);
				updateUi();
			}
			cursor.close();
		}
	}

	TextView tv;

	/** Inflate the view layout and show the current data. */
	@Override public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View rootView = inflater.inflate(R.layout.fragment_rep_detail, container, false);
		rep_name = (TextView)rootView.findViewById(R.id.rep_name);
		rep_district = (TextView)rootView.findViewById(R.id.rep_district);
		rep_state = (TextView)rootView.findViewById(R.id.rep_state);
		rep_office = (TextView)rootView.findViewById(R.id.rep_office);
		rep_party = (TextView)rootView.findViewById(R.id.rep_party);
		rep_phone = (TextView)rootView.findViewById(R.id.rep_phone);
		rep_link = (TextView)rootView.findViewById(R.id.rep_link);
		return rootView;
	}

	/** Takes the place of a full-blown adapter since we just need to update a few fields. */
	void updateUi(){
		if (repItem != null) {
			rep_name.setText(repItem.name);
			rep_district.setText(repItem.district);
			rep_state.setText(repItem.state);
			rep_office.setText(repItem.office);
			rep_party.setText(repItem.party);
			rep_phone.setText(repItem.phone);
			rep_link.setText(repItem.link);
		}
	}

}
