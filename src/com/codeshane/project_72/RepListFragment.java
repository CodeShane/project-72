package com.codeshane.project_72;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.codeshane.project_72.data.RepresentativeContent;
import com.codeshane.project_72.data.RepresentativeContent.RepresentativeTable;

/** A list fragment representing a list of Reps. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link RepDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface. */
public class RepListFragment
extends ListFragment
implements LoaderCallbacks<Cursor> {

	/** The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets. */
	private static final String	STATE_ACTIVATED_POSITION	= "activated_position";

	/** The fragment's current callback object, which is notified of list item
	 * clicks. */
	private Callbacks			mCallbacks;

	/** The current activated item position. Only used on tablets. */
	private int					mActivatedPosition			= ListView.INVALID_POSITION;

	private Loader mLoader;

	String mLastSearchZipCode = "12345";

	/** A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections. */
	public interface Callbacks {
		/** Callback for when an item has been selected. */
		public void onItemSelected ( String id );
	}

	/** A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity. */
	private Callbacks sDummyCallbacks;
	//= new Callbacks() { @Override public void onItemSelected ( String id ) {} };

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepListFragment () {}

	/** @see android.support.v4.app.ListFragment#getListAdapter() */
	@Override public SimpleCursorAdapter getListAdapter () {
		return (SimpleCursorAdapter) super.getListAdapter();
	}

	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);

			getLoaderManager().initLoader(0, null, this);

			setListAdapter(new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[] { RepresentativeTable.TABLE_NAME }, new int[] { android.R.id.text1 },0));
//			setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, DummyContent.ITEMS));

			SharedPreferences prefs = this.getActivity().getSharedPreferences("com.codeshane.project_72_preferences", 0);
			setLastSearchZipCode(prefs.getString("lastZip", "12345"));

	}

	/** @since Aug 21, 2013
	 * @version Aug 21, 2013
	 * @return void
	 */
	private void setLastSearchZipCode ( String string ) {
		if (null==string||string.equalsIgnoreCase(mLastSearchZipCode)) { return; }
		mLastSearchZipCode = string;

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

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) { throw new IllegalStateException("Activity must implement fragment's callbacks."); }

		mCallbacks = (Callbacks) activity;
	}

	@Override public void onDetach () {
		super.onDetach();

		// Stop callbacks
		mCallbacks = null;
	}

	@Override public void onListItemClick ( ListView listView, View view, int position, long id ) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		if (null==mCallbacks) { return; }
		mCallbacks.onItemSelected(RepresentativeContent.ITEMS.get(position).id);
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

		mActivatedPosition = position;
	}


	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle) */
	@Override public Loader<Cursor> onCreateLoader ( int id, Bundle args ) {
		mLoader = new CursorLoader(
			getActivity(),
			RepresentativeContent.CONTENT_URI,
			new String[] {
				RepresentativeContent.RepresentativeTable.Columns.ID.getName(),
				RepresentativeContent.RepresentativeTable.Columns.NAME.getName(),
				RepresentativeContent.RepresentativeTable.Columns.ZIP.getName()
			},
			RepresentativeContent.RepresentativeTable.Columns.ZIP.getName() +
			" = ?",
			new String[] { mLastSearchZipCode },
			RepresentativeContent.RepresentativeTable.Columns.NAME.getName() +
			" ASC");
		return mLoader;
	}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object) */
	@Override public void onLoadFinished ( Loader<Cursor> cursorLoader, Cursor cursor ) {
		this.getListAdapter().swapCursor(cursor);
	}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader) */
	@Override public void onLoaderReset ( Loader<Cursor> cursorLoader ) {
		this.getListAdapter().swapCursor(null);
	}

}

//public void setZipCode(String zipCode) {
//mZipCode = zipCode;
//performRepSync();
//getLoaderManager().restartLoader(0, null, this);
//}
//private void performRepSync() {
//// fire off a request to synchronize the representatives at the current
//// zip code
//Intent i = new Intent(getActivity(), RepresentativeService.class);
//i.putExtra(RepresentativeService.ARG_ZIP, mZipCode);
//i.setAction(Intent.ACTION_SYNC);
//getActivity().startService(i);
//}