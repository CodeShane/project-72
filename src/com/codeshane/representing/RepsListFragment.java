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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.codeshane.representing.providers.RepsContract;
import com.codeshane.representing.providers.RepsContract.Tables.Columns;
import com.codeshane.util.DbUtils;
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

	/** Serialization key representing a "zip code" query value.*/
	public static final String	ZIP_CODE = RepsListFragment.class.getName()+ ".zip_code";

	/** Serialization key representing a "zip code +4" query value.*/
	public static final String	ZIP_CODE_PLUS4	= RepsListFragment.class.getName()+ ".zip_code_plus4";

	/** Representatives list loader Identifier. */
	public static final int LOADER_REPS_LIST = 1;

	/** Interface for notifying activities containing this fragment when
	 * an item is selected. */
	public interface OnItemSelectedListener {
		/** Callback for when an item has been selected. */
		public void onItemSelected ( String itemId );
	}

	/** The fragment's callback object, which is notified of list item
	 * clicks. Must be implemented by all containing activities. */
	private OnItemSelectedListener mOnItemSelectedListener;

	/** The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets. */
	private static final String	STATE_ACTIVATED_POSITION	= "activated_position";

	/** The current activated item position. Only used on tablets. */
	private int					mActivatedPosition			= ListView.INVALID_POSITION;
	private String				mLastSearchedZip			= null;

	/** Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes). */
	public RepsListFragment () {}

	/** Casts the {@code ListAdapter} to its implementation type, a {@code SimpleCursorAdapter}.
	 * @see android.support.v4.app.ListFragment#getListAdapter() */
	@Override public SimpleCursorAdapter getListAdapter () {
		return (SimpleCursorAdapter) super.getListAdapter();
	}

	/** Persists the latest valid input zip code.
	 * */
	@TargetApi ( Build.VERSION_CODES.GINGERBREAD )
	private void setLastSearchedZipCode(String zip){
		if (null==zip || (zip.length() != 5 && zip.length() != 9)) { Log.e(TAG,"cannot save invalid zip."); return; }
		mLastSearchedZip = zip;
		Editor e = Representing.prefs().edit()
		.putString(ZIP_CODE, zip.trim())
		.putLong("lastUpdate", 0)
		;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			e.apply();
		} else {
			e.commit(); //TODO sync file access.
		}
	}

	/** Restores the latest valid input zip code. */
	private String getLastSearchedZipCode() {
		if (null==mLastSearchedZip)	{
			mLastSearchedZip = Representing.prefs().getString(ZIP_CODE, "80104");
		}
		return mLastSearchedZip;
	}


	/** @see android.support.v4.app.Fragment#onCreate(android.os.Bundle) */
	@Override public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/** @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater) */
	@Override public void onCreateOptionsMenu ( Menu menu, MenuInflater inflater ) {
		super.onCreateOptionsMenu(menu, inflater);
		//TODO add "share list" option
		//TODO add "share selected" option to details fragment
	}

	/** @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle) */
	@Override public void onActivityCreated ( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
		Log.v(TAG, "onActivityCreated() initLoader(LOADER_REPS_LIST,null,this)");
//		setListShown(false); // custom lists not supported
		getLoaderManager().initLoader(LOADER_REPS_LIST, null, this);
		setListAdapter(new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, null, new String[] { Columns.NAME.getName(),Columns.PARTY.getName(), }, new int[] { android.R.id.text1, android.R.id.text2 },0));

		EditText editZip = (EditText) this.getActivity().findViewById(R.id.editZip);
		editZip.setText(getLastSearchedZipCode());
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
				Log.i(TAG,"afterTextChanged()");
				if (s.length()==5 || s.length()==9) {
					switch (s.length()){
						case 5:
							params.putString(ZIP_CODE, s.toString());
							break;
						case 9:
							// .subSequence inclusive, exclusive [3,4] gets char index 3. get m+, not n+.
							params.putString(ZIP_CODE, s.subSequence(0, 5).toString());
							params.putString(ZIP_CODE_PLUS4, s.subSequence(5, 9).toString()); //TODO (currently unused)
							break;
						default:
							return;
					}

					RepsListFragment.this.setLastSearchedZipCode(params.getString(ZIP_CODE));
					getLoaderManager().restartLoader(LOADER_REPS_LIST, params, RepsListFragment.this);
				}
			}
		});
	}

	// API-11 layout resource.
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

	/** {@code Activity} attach callback requiring {@code Activity} to implement {@code OnItemSelectedListener}.
	 * <p>When attached to an {@link Activity}, throws an {@code IllegalStateException} if that {@code Activity}
	 * does not implement {@link OnItemSelectedListener}. Stores a reference to that callback, which is released in {@link #onDetach() onDetach()}.
	 * */
	@Override public void onAttach ( Activity activity ) {
		super.onAttach(activity);
		Log.v(TAG,"onAttach " + activity.getLocalClassName());
		// Force Activities containing this fragment to implement its OnItemSelectedListener.
		if (!(activity instanceof OnItemSelectedListener)) { throw new IllegalStateException("Activity must implement fragment's callbacks."); }
		mOnItemSelectedListener = (OnItemSelectedListener) activity;
	}

	/** {@code Activity} detach callback. <p>Releases reference to {@link OnItemSelectedListener} callback, which was stored in
	 * {@link #onAttach() onAttach()}.</p>
	 * */
	@Override public void onDetach () {
		super.onDetach();
		Log.v(TAG,"onDetach");
		// Stop callbacks
		mOnItemSelectedListener = null;
	}

	/** When a list item is clicked, notifies attached activity via required listener callback.
	 * */
	@Override public void onListItemClick ( ListView listView, View view, int position, long id ) {
		super.onListItemClick(listView, view, position, id);
		if (null == mOnItemSelectedListener) { return; }
		mOnItemSelectedListener.onItemSelected(String.valueOf(id));
		Log.v(TAG,"onListItemClick pos=" + position + " id=" + id);
	}

	@Override public void onSaveInstanceState ( Bundle outState ) {
		super.onSaveInstanceState(outState);
		 if (mActivatedPosition != ListView.INVALID_POSITION) {
			 outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		 }
	}

	/** Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched. */
	public void setActivateOnItemClick ( boolean activateOnItemClick ) {
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	/** When the user touches an item, this sets the position variable, and (on tablets) styles the item as activated.*/
	private void setActivatedPosition ( int position ) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}
		mActivatedPosition = position;
	}

	public static final String[] projection = {Columns._id.getName(), Columns.NAME.getName(), Columns.PARTY.getName()};

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle) */
	@Override public Loader<Cursor> onCreateLoader ( int loaderId, Bundle loaderArgs ) {
		Log.v(TAG,"onCreateLoader id="+loaderId+" // base: " + RepsContract.URI_BYZIP);

		Uri.Builder build = RepsContract.URI_BYZIP.buildUpon();
		String querySelection = null;
		String[] selectionArgs = null;

		if (null==loaderArgs){
			loaderArgs = new Bundle();
			loaderArgs.putString(ZIP_CODE, getLastSearchedZipCode());
		}

		String zip = Utils.get(loaderArgs.getString(ZIP_CODE),"");
//		String zip4 = Utils.get(loaderArgs.getString(ZIP_CODE_PLUS4),""); //TODO
		querySelection = DbUtils.whereColumn(Columns.ZIP);
		build.appendPath(zip);
		build.appendQueryParameter("zip", zip);
		selectionArgs = new String[]{zip};

        // Now create and return a CursorLoader that will take care of creating a Cursor for the data being displayed.
        return new CursorLoader(
        	getActivity(),
        	/* Uri loaderSpecifiedUri */	build.build(),
        	/* String[] colProjection */	projection,
        	/* String selectionClause */	querySelection,
        	/* String[] selectionArgs */	selectionArgs,
        	/* String sortOrderClause */	null
        	);
	}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object) */
	@Override public void onLoadFinished ( Loader<Cursor> loader, Cursor cursor ) {
		try {
			if (cursor!=null) {
				Log.v(TAG, "onLoadFinished cursor of " + ((null==cursor)?"null":cursor.getCount()) + " rows.");
				getListAdapter().swapCursor(cursor);
				Utils.toLogVerbose(TAG + " onLoadFinished",cursor);
			} else {
				Log.e(TAG,"onLoadFinished: null cursor!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG,"swallowed exception, exporting basic cursor info");
			if (Representing.DEBUG) { Utils.toLog("onLoadFinished",cursor); }
		}
	}

	/** @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader) */
	@Override public void onLoaderReset ( Loader<Cursor> loader ) {
		Log.v(TAG,"onLoaderReset()");
		getListAdapter().swapCursor(null);
	}
}

