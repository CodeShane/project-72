package com.codeshane.representing;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codeshane.representing.providers.RepresentingContract;

/** A list fragment representing a list of Reps. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link RepresentativeDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnItemSelectedListener}
 * interface. */
public class RepresentativeListFragment
extends ListFragment //{
implements LoaderCallbacks<Cursor> {

	private static final String	PARAMS_REST	= "com.codeshane.represents.restful.args";
	private static final String	PARAMS_LOADER = "com.codeshane.represents.loaders.args";
	private static final String	TYPE_URI = "com.codeshane.type.uri";

	/** Interface for notifying activities containing this fragment when
	 * an item is selected. */
	public interface OnItemSelectedListener {
		/** Callback for when an item has been selected. */
		public void onItemSelected ( String itemId );
	}

	/** The fragment's callback object, which is notified of list item
	 * clicks. Must be implemented by all containing activities. */
	private OnItemSelectedListener			mCallbacks;

	/** The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets. */
	private static final String	STATE_ACTIVATED_POSITION	= "activated_position";


	/** The current activated item position. Only used on tablets. */
	private int					mActivatedPosition			= ListView.INVALID_POSITION;

	public static final String	PARAMS_LOADER_ZIP = "com.codeshane.represents.loaders.zip";

	private String mLastSearchZipCode;

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepresentativeListFragment () {}

	/** @see android.support.v4.app.ListFragment#getListAdapter() */
	@Override public SimpleCursorAdapter getListAdapter () {
		return (SimpleCursorAdapter) super.getListAdapter();
	}

	/** @see android.support.v4.app.ListFragment#getListAdapter() */
	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		mLastSearchZipCode = Representing.prefs().getString(PARAMS_LOADER_ZIP, "12345");
//		setListAdapter() moved to onActivityCreated for Loader, ContentProvider
	}

	/** @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle) */
	@Override public void onActivityCreated ( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);

	    // REST call parameters
	    Bundle restArgs = new Bundle();
	    restArgs.putString("mode", "get");

		// Loader args for LoaderManager to use to maintain & reload Loaders state.
//	    Bundle loaderArgs = new Bundle();
//	    loaderArgs.putParcelable(PARAMS_REST, restArgs);
//	    Uri loaderUri = Uri.withAppendedPath(RepresentingContract.CONTENT_URI_BYZIP, Uri.encode(mLastSearchZipCode));
//	    loaderArgs.putParcelable(TYPE_URI, loaderUri);

		getLoaderManager().initLoader(0, null, this);
		setListAdapter(new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, null, new String[] { RepresentingContract.Columns.NAME.getName(), RepresentingContract.Columns.PARTY.getName(), }, new int[] { android.R.id.text1, android.R.id.text2 },0));
	}

	//Use API-11 layout
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    return inflater.inflate(R.layout.list_content, container, false);
	}

	@Override public void onViewCreated ( View view, Bundle savedInstanceState ) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override public void onAttach ( Activity activity ) {
		super.onAttach(activity);

		// Force Activities containing this fragment to implement its OnItemSelectedListener.
		if (!(activity instanceof OnItemSelectedListener)) { throw new IllegalStateException("Activity must implement fragment's callbacks."); }

		mCallbacks = (OnItemSelectedListener) activity;
	}

	@Override public void onDetach () {
		super.onDetach();

		// Stop callbacks
		mCallbacks = null;
	}

	@Override public void onListItemClick ( ListView listView, View view, int position, long id ) {
		super.onListItemClick(listView, view, position, id);

		// Notify callbacks interface (the activity, if the fragment
		// is attached to one) that an item has been selected.
		if (null == mCallbacks) { return; }
		mCallbacks.onItemSelected(String.valueOf(id)); // was: mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
	}

	@Override public void onSaveInstanceState ( Bundle outState ) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/** Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched. */
	public void setActivateOnItemClick ( boolean activateOnItemClick ) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition ( int position ) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}
		android.content.CursorLoader cl;

		mActivatedPosition = position;
	}

	/** Columns used by the 'list' query. */
	private static final String[] PROJECTION = {
		RepresentingContract.Columns.NAME.getName(),
		RepresentingContract.Columns.PARTY.getName()
	};

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle) */
	@Override public Loader<Cursor> onCreateLoader ( int arg0, Bundle loaderArgs ) {
		Uri loaderUri = Uri.withAppendedPath(RepresentingContract.URI_BYZIP, Uri.encode(mLastSearchZipCode));

        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
        return
        	new CursorLoader(
        	getActivity(),
        	loaderUri,
        	PROJECTION,
        	" " + RepresentingContract.Columns.ZIP.name() + " = ? ",
        	new String[]{mLastSearchZipCode},
        	RepresentingContract.Columns.NAME.getName() + " COLLATE LOCALIZED ASC");
	}


	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object) */
	@Override public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {getListAdapter().swapCursor(cursor);}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader) */
	@Override public void onLoaderReset ( Loader<Cursor> loader ) {getListAdapter().swapCursor(null);}

}

