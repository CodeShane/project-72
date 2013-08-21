package com.codeshane.project_72;

import com.codeshane.project_72.data.RepresentativeContent;
import com.codeshane.project_72.data.RepresentativeContent.RepItem;

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

	/** The content this fragment is presenting. */
	private RepItem	mItem;

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepDetailFragment () {}

	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// TODO load content from a content provider.
			mItem = RepresentativeContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

			Cursor cursor = this.getActivity().getContentResolver().query(RepresentativeContent.CONTENT_URI, RepresentativeContent.RepresentativeTable.PROJECTION, RepresentativeContent.RepresentativeTable.Columns.ID.getName() + " = ?",new String[]{""}, "asc");

		}
	}

//	ViewHolder vh;
	TextView tv;

	/** Inflate the view layout and show the current data. */
	@Override public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View rootView = inflater.inflate(R.layout.fragment_rep_detail, container, false);

		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.rep_name)).setText(mItem.name);
			((TextView) rootView.findViewById(R.id.rep_district)).setText(mItem.district);
			((TextView) rootView.findViewById(R.id.rep_state)).setText(mItem.state);
			((TextView) rootView.findViewById(R.id.rep_office)).setText(mItem.office);
			((TextView) rootView.findViewById(R.id.rep_party)).setText(mItem.party);
			((TextView) rootView.findViewById(R.id.rep_phone)).setText(mItem.phone);
			((TextView) rootView.findViewById(R.id.rep_website)).setText(mItem.website);
		}

		return rootView;

//		View rootView = inflater.inflate(R.layout.fragment_representative_detail, container, false);
//
//		mNameTextView = (TextView)rootView.findViewById(R.id.representativeNameTextView);
//		mPartyTextView = (TextView)rootView.findViewById(R.id.partyTextView);
//		mStateTextView = (TextView)rootView.findViewById(R.id.stateValueTextView);
//		mDistrictTextView = (TextView)rootView.findViewById(R.id.districtValueTextView);
//		mPhoneTextView = (TextView)rootView.findViewById(R.id.phoneValueTextView);
//		mOfficeTextView = (TextView)rootView.findViewById(R.id.officeValueTextView);
//		mLinkTextView = (TextView)rootView.findViewById(R.id.linkValueTextView);
//
//		mNameTextView.setText(mRepresentative.getName());
//		mPartyTextView.setText(mRepresentative.getParty());
//		mStateTextView.setText(mRepresentative.getState());
//		mDistrictTextView.setText(mRepresentative.getDistrict());
//		mPhoneTextView.setText(mRepresentative.getPhone());
//		mOfficeTextView.setText(mRepresentative.getOffice());
//		mLinkTextView.setText(mRepresentative.getLink());
//
//		return rootView;
	}

}
