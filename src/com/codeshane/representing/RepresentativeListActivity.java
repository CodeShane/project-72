/* RepListActivity is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
public class RepresentativeListActivity extends FragmentActivity
implements RepresentativeListFragment.OnItemSelectedListener {

	/** Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device. */
	private boolean	mTwoPane;

	@Override protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
			setContentView(R.layout.representative_list_activity);
			if (findViewById(R.id.representative_detail_container) != null) {
				// The detail container view present only in large-screen layouts
				// (res/values-large and res/values-sw600dp) for two-pane mode.
				mTwoPane = true;

				// In two-pane mode, list items should be given the
				// 'activated' state when touched.
				((RepresentativeListFragment) getSupportFragmentManager().findFragmentById(R.id.rep_list)).setActivateOnItemClick(true);
			}

		    // If exposing deep links into your app, handle intents here.
	}

	/** Callback method from {@link RepresentativeListFragment.OnItemSelectedListener} indicating that
	 * the item with the given ID has been selected. */
	@Override public void onItemSelected ( String id ) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			//Pass the new item id to a new fragment, let it find its data
			arguments.putString(RepresentativeDetailFragment.ARG_ITEM_ID, id);
			RepresentativeDetailFragment fragment = new RepresentativeDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.representative_detail_container, fragment).commit();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, RepresentativeDetailActivity.class);
			detailIntent.putExtra(RepresentativeDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);

		}
	}
}
