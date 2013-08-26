/* RepListActivity is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.project_72;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/** An activity representing a list of Reps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link RepListFragment} and the item details
 * (if present) is a {@link RepDetailFragment}.
 * <p>
 * This activity also implements the required {@link RepListFragment.Callbacks}
 * interface
 * to listen for item selections. */
public class RepListActivity
extends FragmentActivity
implements
RepListFragment.Callbacks,
OnEditorActionListener,
EditTextFragment.OnFinishedEditTextDialogListener
{ // , LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ARGS_URI = "com.codeshane.schema.ARGS_URI";
    public static final String ARGS_PARAMS = "com.codeshane.args.params";

    public static final int LOADER_ID_REPRESENTATIVES = 1;

	public SimpleCursorAdapter mAdapter;

	/** Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device. */
	private boolean	mTwoPane;

	@Override protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rep_list);

		    FragmentManager fm = getSupportFragmentManager();

		    ListFragment list = new ListFragment();

		    FragmentTransaction ft = fm.beginTransaction();
		    ft.add(android.R.id.list, list);
		    ft.commit();

//		    mAdapter = new ArrayAdapter<String>(this, andr
//		    list.setListAdapter(mAdapter);

		    // REST call parameters
		    Bundle restParams = new Bundle();
		    restParams.putString("q", "android");

		    // These are the loader arguments. They are stored in a Bundle because
		    // LoaderManager will maintain the state of our Loaders for us and
		    // reload the Loader if necessary. This is the whole reason why
		    // we have even bothered to implement RESTLoader.
		    Bundle args = new Bundle();
		    args.putParcelable(ARGS_URI, Uri.parse("")); //XXX
		    args.putParcelable(ARGS_PARAMS, restParams);

		    // Initialize the Loader.
		    getSupportLoaderManager().initLoader(LOADER_ID_REPRESENTATIVES, args, (LoaderCallbacks<Cursor>) this);

		if (findViewById(R.id.rep_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((RepListFragment) getSupportFragmentManager().findFragmentById(R.id.rep_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/** Callback method from {@link RepListFragment.Callbacks} indicating that
	 * the item with the given ID has been selected. */
	@Override public void onItemSelected ( String id ) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			//Pass the new item id to a new fragment, let it find its data
			arguments.putString(RepDetailFragment.ARG_ITEM_ID, id);
			RepDetailFragment fragment = new RepDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.rep_detail_container, fragment).commit();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, RepDetailActivity.class);
			detailIntent.putExtra(RepDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

    static long lastEditorAction = 0;

	/** @see android.widget.TextView.OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent) */
	@Override public boolean onEditorAction ( TextView v, int actionId, KeyEvent event ) {
		long now = System.currentTimeMillis();
		if (lastEditorAction+500<now && v instanceof EditText) {
			Toast.makeText(this, "Hi, ".concat(((EditText)v).getText().toString()), Toast.LENGTH_SHORT).show();
			lastEditorAction = now;
		}
		return false;
	}

	// lets just use an edit text.
	@Deprecated
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditTextFragment editNameDialog = new EditTextFragment();
        editNameDialog.show(fm, "Fragment Editor");
    }
//    EditText et = (EditText) editNameDialog.getView().findViewById(android.R.id.edit);
//    Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
}
