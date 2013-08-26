/* RepDetailFragment is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import com.codeshane.representing.providers.RepresentingContract;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/** A fragment representing a single Rep detail screen.
 * This fragment is either contained in a {@link RepresentativeListActivity} in two-pane
 * mode (on tablets) or a {@link RepresentativeDetailActivity} on handsets. */
public class RepresentativeDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

	/** The extra-identifier for the item this fragment represents. */
	public static final String		ARG_ITEM_ID	= "item_id";

	/** Standard fragment doesn't have an adapter. */
	private SimpleCursorAdapter mAdapter;

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepresentativeDetailFragment () {}

	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.representative_details, null, new String[] { RepresentingContract.Columns.NAME.getName(), RepresentingContract.Columns.PARTY.getName(), }, new int[] { android.R.id.text1, android.R.id.text2 },0);
	}

	/** @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle) */
	@Override public void onActivityCreated ( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);//R.id.rep_name,R.id.rep_party
	}

	/** Inflate the view layout and show the current data. */
	@Override public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View rootView = inflater.inflate(R.layout.representative_detail_fragment, container, false);
		getLoaderManager().initLoader(0, null, this);//R.id.rep_name,R.id.rep_party
		return rootView;
	}

	/** Columns used by the 'detail' query. */
	private static final String[] PROJECTION = RepresentingContract.PROJECTION;

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle) */
	@Override public Loader<Cursor> onCreateLoader ( int arg0, Bundle loaderArgs ) {
		String mLastSearchZipCode = loaderArgs.getString(RepresentativeListFragment.PARAMS_LOADER_ZIP);
//		String id = getArguments().getString(ARG_ITEM_ID);
        Uri loaderUri = Uri.withAppendedPath(RepresentingContract.URI_BYZIP, Uri.encode(mLastSearchZipCode));

        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
        return
        	new CursorLoader(getActivity(),
        	loaderUri,
        	PROJECTION,
        	" " + RepresentingContract.Columns.ID.name() + " = ? ",
        	new String[]{ mLastSearchZipCode },
        	null);
	}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader) */
	@Override public void onLoaderReset ( Loader<Cursor> loader ) {
		mAdapter.swapCursor(null);
	}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object) */
	@Override public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {
		mAdapter.swapCursor(cursor);
	}
}
