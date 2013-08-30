package com.codeshane.representing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.codeshane.representing.providers.RepsContract;
import com.codeshane.representing.providers.RepsContract.Tables.Columns;
import com.codeshane.util.Log;
import com.codeshane.util.Utils;
import com.codeshane.util.Views;

/** A list fragment representing a list of Reps. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link RepresentativeDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnItemSelectedListener}
 * interface. */
public class RepsListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	protected static final String	TAG	= RepsListFragment.class.getSimpleName();

	public static final String	ZIP_CODE = RepsListFragment.class.getName()+ ".zip_code";
	public static final String	ZIP_CODE_PLUS4	= RepsListFragment.class.getName()+ ".zip_code_plus4";

	public static final int LOADER_REPS_LIST = 1;

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

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepsListFragment () {}

	/** @see android.support.v4.app.ListFragment#getListAdapter() */
	@Override public SimpleCursorAdapter getListAdapter () {
		return (SimpleCursorAdapter) super.getListAdapter();
	}

	/** @see android.support.v4.app.ListFragment#getListAdapter() */
	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
	}

	// @see SearchView sv;
	@TargetApi ( Build.VERSION_CODES.GINGERBREAD )
	private void saveSearchZipCode(String zip){
		Editor e = Representing.prefs().edit().putString(ZIP_CODE, zip.trim());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			e.apply();
		} else {
			e.commit(); //TODO sync file access.
		}
	}

	/** @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle) */
	@Override public void onActivityCreated ( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);

		getLoaderManager().initLoader(LOADER_REPS_LIST, null, this);
		setListAdapter(new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, null, new String[] { Columns.NAME.getName(),Columns.PARTY.getName(), }, new int[] { android.R.id.text1, android.R.id.text2 },0));

		EditText editZip = (EditText) this.getActivity().findViewById(R.id.editZip);
		customizeEntry(editZip); // XXX
		editZip.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override public void onFocusChange ( View v, boolean hasFocus ) {
				Views.setSubdued(v, 0.8f, hasFocus);
			}
		});

	}

	/** just a placeholder method
	 *   //TODO refactor
	 *   //TODO store density for use elsewhere
	 *   //TODO make the setminheight part into a function to ensure everything that is clickable is at least 40dp
	 *   //TODO redo the text change listener
	 */
	private void customizeEntry ( EditText editZip ) {
		DisplayMetrics metrics = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		editZip.setMinHeight((int) (40*metrics.density));
		InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editZip, InputMethodManager.SHOW_IMPLICIT);
		editZip.requestFocus();
		editZip.addTextChangedListener(new TextWatcher(){
			Bundle params = new Bundle();
			@Override public void beforeTextChanged ( CharSequence s, int start, int count, int after ) {}
			@Override public void onTextChanged ( CharSequence s, int start, int before, int count ) {}
			@Override public void afterTextChanged ( Editable s ) {
				//TODO filter input, add a separator or setup a +4 box

				if (s.length()==5 || s.length()==9) {
					switch (s.length()){
						case 5:
							params.putString(ZIP_CODE, s.toString());
							break;
						case 9:
							// .subSequence inclusive, exclusive [3,4] gets char index 3. get m+, not n+.
							String zz=s.subSequence(0, 5).toString();
							assert (zz.length()==5);
							String z4=s.subSequence(5, 9).toString();
							assert (zz.length()==4);
							Log.e(TAG+" URI ZZ",zz);
							Log.e(TAG+" URI Z4",z4);
							params.putString(ZIP_CODE, zz);
							params.putString(ZIP_CODE_PLUS4, z4);
							break;
						default:
							return;
					}
					getLoaderManager().restartLoader(LOADER_REPS_LIST, params, RepsListFragment.this);
				}
			}
		});
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
		if (null != mCallbacks) { return; }
		mCallbacks.onItemSelected(String.valueOf(id));
	}

	@Override public void onSaveInstanceState ( Bundle outState ) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition == ListView.INVALID_POSITION) { return; }
		// Serialize and persist the activated item position.
		outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
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
	@Override public Loader<Cursor> onCreateLoader ( int loaderId, Bundle loaderArgs ) {
		Log.v(TAG,"onCreateLoader id="+loaderId+" // base: " + RepsContract.URI_BYZIP);

		Uri.Builder build = RepsContract.URI_BYZIP.buildUpon();
		String querySelection = null;
		String[] selectionArgs = null;

		if (null!=loaderArgs){
			String zip = Utils.get(loaderArgs.getString(ZIP_CODE),"");
			String zip4 = Utils.get(loaderArgs.getString(ZIP_CODE_PLUS4),"");
			String zips = zip.concat(Utils.get(zip4,""));

			if (null!=zip) {
//				if (null==zip4) {
					querySelection = Columns.ZIP + " = ?"; // TODO split?
					build.appendPath(zips)
					.appendQueryParameter("zip", zip);
					selectionArgs = new String[]{zip};
//				}
//				else {
//					querySelection = Columns.ZIP + " = ? OR " + Columns.ZIP + " = ?"; // TODO split?
//					build.appendPath(zips)
//					.appendQueryParameter("zip", zip)
//					.appendQueryParameter("zip4", zip4);
//					selectionArgs = new String[]{zip,zip4};
//				}
			} // FYI: else { querySelection = null; }

			Log.e(TAG+" URI onCreateLoader querySelection:",querySelection);
		} // FYI: else { querySelection = null; }

        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
        return new CursorLoader(
        	getActivity(),
        	/* Uri loaderSpecifiedUri */	build.build(),
        	/* String[] colProjection */	new String[]{Columns._id.getName(), Columns.NAME.getName(), Columns.PARTY.getName()},
        	/* String selectionClause */	querySelection, // Columns.ZIP.getName() + " = ? ",
        	/* String[] selectionArgs */	selectionArgs,
        	/* String sortOrderClause */	null
        	);
	}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object) */
	@TargetApi ( Build.VERSION_CODES.HONEYCOMB )
	@Override public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {
		try {
			Log.v(TAG, "onLoadFinished cursor of " + ((null==cursor)?"null":cursor.getCount()) + " rows.");
			if (cursor!=null) { getListAdapter().swapCursor(cursor); }
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG,"swallowed exception, exporting cursor info");
			if (Representing.DEBUG) { Utils.dumpCursorSchema(cursor); }
		}
	}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader) */
	@Override public void onLoaderReset ( Loader<Cursor> loader ) {getListAdapter().swapCursor(null);}
}

