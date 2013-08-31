/* RepDetailFragment is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeshane.representing.providers.RepsContract;
import com.codeshane.representing.providers.RepsContract.Tables.Columns;

import static com.codeshane.util.Utils.*;

/** A fragment representing a single Rep detail screen.
 * This fragment is either contained in a {@link RepresentativeListActivity} in two-pane
 * mode (on tablets) or a {@link RepresentativeDetailActivity} on handsets. */
public class RepDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
	private static final String	TAG	= RepDetailFragment.class.getSimpleName();

	public static final int LOADER_REPS_LIST = 2;

	/** Standard fragment doesn't have an adapter. */
	private SimpleCursorAdapter mAdapter;

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepDetailFragment () {}

	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.rep_details_frame, null, new String[] { Columns.NAME.getName(), Columns.PARTY.getName(), }, new int[] { android.R.id.text1, android.R.id.text2 },0);
	}

	/** @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle) */
	@Override public void onActivityCreated ( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(LOADER_REPS_LIST, null, this);//R.id.rep_name,R.id.rep_party
	}

	/** Inflate the view layout and show the current data. */
	@Override public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View rootView = inflater.inflate(R.layout.rep_detail_fragment, container, false);
		getLoaderManager().initLoader(LOADER_REPS_LIST, null, this);//R.id.rep_name,R.id.rep_party
		return rootView;
	}

	/** {@inheritDoc} @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, Bundle) */
	@Override public Loader<Cursor> onCreateLoader ( int loaderId, Bundle loaderArgs ) {
//		Log.v(TAG,"onCreateLoader id#"+loaderId);

		String item_id = loaderArgs.getString(RepsListActivity.ITEM_ID);
		if (null==item_id) { Log.e(TAG, "item_id=null");} else {Log.v(TAG,"item_id="+item_id);}

//        Uri loaderUri = RepsContract.URI_BYZIP.buildUpon().appendPath(item_id).build();

        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.

        return new CursorLoader(
        	getActivity(),
        	/* Uri loaderSpecifiedUri */	RepsContract.URI_BYZIP.buildUpon().appendPath(loaderArgs.getString("query.selection")).build(),
        	/* String[] colProjection */	RepsContract.Tables.Columns.PROJECTION,
        	/* String selectionClause */	" " + Columns._id.getName() + " = ? ",
        	/* String[] selectionArgs */	new String[]{ item_id },
        	/* String sortOrderClause */	null
        	);
	}
	/** {@inheritDoc} @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(Loader) */
	@Override public void onLoaderReset ( Loader<Cursor> loader ) { mAdapter.swapCursor(null); }
	/** {@inheritDoc} @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(Loader, Object) */
	@Override public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) { mAdapter.swapCursor(cursor); }
}
