/* RepListActivity is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;

import com.codeshane.util.Log;

/** An activity representing a list of Reps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RepresentativeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link RepresentativeListFragment} and the item details
 * (if present) is a {@link RepresentativeDetailFragment}.
 * <p>
 * This activity also implements the required {@link RepresentativeListFragment.OnItemSelectedListener}
 * interface
 * to listen for item selections. */
public class RepsListActivity extends ActionBarActivity implements RepsListFragment.OnItemSelectedListener {
	protected static final String	TAG	= RepsListActivity.class.getSimpleName();

	/* Identifier for the currently selected item */
	protected static final String	ITEM_ID = "item_id";

	/** Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device. */
	private boolean	mTwoPane;

	@Override protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
			setContentView(R.layout.reps_list_fragment);
			if (findViewById(R.id.rep_detail_container) != null) {
				// The detail container view present only in large-screen layouts
				// (res/values-large and res/values-sw600dp) for two-pane mode.
				mTwoPane = true;

				// In two-pane mode, list items should be given the
				// 'activated' state when touched.
				((RepsListFragment) getSupportFragmentManager().findFragmentById(R.id.rep_list)).setActivateOnItemClick(true);
			}

		    // If exposing deep links into your app, handle intents here.
	}

	/** @see android.support.v7.app.ActionBarActivity#onBackPressed() */
	@Override public void onBackPressed () {
		super.onBackPressed();
		Log.v(TAG,"onBackPressed");
	}

	/** @see android.support.v7.app.ActionBarActivity#onCreatePanelView(int) */
	@Override public View onCreatePanelView ( int featureId ) {
		Log.v(TAG,"onCreatePanelView");
		return super.onCreatePanelView(featureId);
	}

	/** @see android.support.v7.app.ActionBarActivity#onPostResume() */
	@Override protected void onPostResume () {
		Log.v(TAG,"onPostResume");
		super.onPostResume();
	}

	/** @see android.support.v7.app.ActionBarActivity#onPreparePanel(int, android.view.View, android.view.Menu) */
	@Override public boolean onPreparePanel ( int featureId, View view, Menu menu ) {
		Log.v(TAG,"onPreparePanel");
		return super.onPreparePanel(featureId, view, menu);
	}

	/** @see android.support.v4.app.FragmentActivity#onPause() */
	@Override protected void onPause () {
		Log.v(TAG,"onPause");
		super.onPause();
	}

	/** @see android.support.v4.app.FragmentActivity#onResume() */
	@Override protected void onResume () {
		Log.v(TAG,"onResume");
		super.onResume();
	}

	/** @see android.support.v4.app.FragmentActivity#onResumeFragments() */
	@Override protected void onResumeFragments () {
		Log.v(TAG,"onResumeFragments");
		super.onResumeFragments();
	}

	/** Callback method from {@link RepresentativeListFragment.OnItemSelectedListener} indicating that
	 * the item with the given ID has been selected. */
	@Override public void onItemSelected ( String id ) {
		if (mTwoPane) {
			Log.v(TAG,"onItemSelected: Item \"" + id + "\" selected in dual-pane (tablet) mode.");
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			//Pass the new item id to a new fragment, let it find its data
			arguments.putString(ITEM_ID, id);
			RepDetailFragment fragment = new RepDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.rep_detail_container, fragment).commit();
		} else {
			Log.v(TAG,"onItemSelected: Item \"" + id + "\" selected in single-pane mode.");
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, RepDetailActivity.class);
			detailIntent.putExtra(ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
