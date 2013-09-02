/* RepDetailFragment is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codeshane.representing.providers.Rep;
import com.codeshane.representing.providers.RepsContract;
import com.codeshane.representing.providers.RepsContract.Tables.Columns;
import com.codeshane.util.DbUtils;
import com.codeshane.util.Utils;

/** A fragment representing a single Rep detail screen.
 * This fragment is either contained in a {@link RepresentativeListActivity} in two-pane
 * mode (on tablets) or a {@link RepresentativeDetailActivity} on handsets. */
public class RepDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
	private static final String	TAG	= RepDetailFragment.class.getSimpleName();

	public static final int LOADER_REPS_LIST = 2;

	/** This identifier is to be used with view.setTag/view.getTag
	 * for storing a button intent extra, type-specific to each button's purpose. */
	protected static final int	TAG_ID_EXTRA	= 12;

	View     rep_layout;
	TextView rep_name;
	TextView rep_party;
	TextView rep_state;
	TextView rep_zip;
	TextView rep_district;
	TextView rep_phone;
	TextView rep_office;
	TextView rep_link;

	Rep mItem;

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepDetailFragment () {}

	/** @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle) */
	@Override public void onActivityCreated ( Bundle savedInstanceState ) {
		Log.v(TAG,"onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	/** Inflate the view layout and show the current data. */
	@Override public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v(TAG,"onCreateView");

		if (null!=rep_layout) return rep_layout;
		rep_layout = inflater.inflate(R.layout.rep_detail_fragment, container, false);
		rep_name = (TextView) rep_layout.findViewById(R.id.rep_name);
		rep_party = (TextView) rep_layout.findViewById(R.id.rep_party);
		rep_state = (TextView) rep_layout.findViewById(R.id.rep_state);
		rep_zip = (TextView) rep_layout.findViewById(R.id.rep_zip);
		rep_district = (TextView) rep_layout.findViewById(R.id.rep_district);
		rep_phone = (TextView) rep_layout.findViewById(R.id.rep_phone);
		((Button)rep_phone).setOnClickListener(new OnClickListener(){
			@Override public void onClick ( View v ) {
				String phone = (String) v.getTag(TAG_ID_EXTRA);
				if (null==phone) return;
			    Intent intent = new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:".concat(phone)));
			    boolean success = Utils.activate(getActivity(),intent);
			    if (!success) {
			    	Toast.makeText(getActivity(), getActivity().getString(R.string.no_dialer), Toast.LENGTH_LONG).show();
			    }
			}
		});
		rep_office = (TextView) rep_layout.findViewById(R.id.rep_office);
		((Button)rep_office).setOnClickListener(new OnClickListener(){
			@Override public void onClick ( View v ) {
				String office = (String) v.getTag(TAG_ID_EXTRA);
				if (null==office) return;
				Uri uri = Uri.parse("geo:0,0").buildUpon().appendQueryParameter("q", office.replace(" ", "+")).build();
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			    boolean success = Utils.activate(getActivity(),intent);
			    if (!success) {
			    	Toast.makeText(getActivity(), getActivity().getString(R.string.no_map), Toast.LENGTH_LONG).show();
			    }
			}
		});
		rep_link = (TextView) rep_layout.findViewById(R.id.rep_link);
		((Button)rep_office).setOnClickListener(new OnClickListener(){
			@Override public void onClick ( View v ) {
				String link = (String) v.getTag(TAG_ID_EXTRA);
				if (null==link) return;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			    boolean success = Utils.activate(getActivity(),intent);
			    if (!success) {
			    	Toast.makeText(getActivity(), getActivity().getString(R.string.no_browser), Toast.LENGTH_LONG).show();
			    }
			}
		});

		getLoaderManager().initLoader(LOADER_REPS_LIST, getArguments(), this);
		return rep_layout;
	}

	/** {@inheritDoc} @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, Bundle) */
	@Override public Loader<Cursor> onCreateLoader ( int loaderId, Bundle loaderArgs ) {
		Log.v(TAG,"onCreateLoader id#"+loaderId);
		Toast.makeText(this.getActivity(), "onCreateLoader()", Toast.LENGTH_SHORT).show();
		String item_id = Utils.get(loaderArgs.getString(RepsListActivity.ITEM_ID),"");
		if ("".equalsIgnoreCase(item_id)) {
			Log.e(TAG, "item_id=\"\"");
		} else {
			Log.i(TAG,"item_id="+item_id);
		}

        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
        return new CursorLoader(
        	getActivity(),
        	/* Uri loaderSpecifiedUri */	RepsContract.URI_BASE.buildUpon().appendPath(item_id).build(),
        	/* String[] colProjection */	RepsContract.Tables.Columns.PROJECTION,
        	/* String selectionClause */	DbUtils.whereColumn(Columns._id),
        	/* String[] selectionArgs */	new String[]{ item_id },
        	/* String sortOrderClause */	null
        	);
	}

	/** {@inheritDoc} @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(Loader) */
	@Override public void onLoaderReset ( Loader<Cursor> loader ) { /*mAdapter.swapCursor(null);*/ }
	/** {@inheritDoc} @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(Loader, Object) */
	@Override public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {
		if (cursor.getCount()==1) {
			Log.v(TAG,"onLoadFinished " + cursor.getCount() + " rows.");
		} else {
			Log.e(TAG,"onLoadFinished " + cursor.getCount() + " rows.");
		}
		// won't do much good to try loading row -1..
		Log.v(TAG,"cursor.moveToFirst()? " + cursor.moveToFirst());
//		mAdapter.swapCursor(cursor);
		if (!cursor.moveToFirst()) {
			Log.e(TAG,"!cursor.moveToFirst()");	return;
		}
		if (null==rep_layout){
			Log.e(TAG,"null==rep_layout");
			rep_layout.setEnabled(false);
			return;
		}
		rep_layout.setEnabled(true);

		rep_name.setText(cursor.getString(2));
		rep_party.setText(cursor.getString(3));
		rep_state.setText(cursor.getString(4));
		rep_zip.setText(cursor.getString(5));
		rep_district.setText(cursor.getString(6));
		String phone = cursor.getString(7);
		rep_phone.setText(phone);
		rep_phone.setTag(phone);
		String office = cursor.getString(8);
		rep_office.setText(office);
		rep_office.setTag(office);
		String link = cursor.getString(9);
		rep_link.setText(link);
		rep_link.setTag(link);

	}


}
