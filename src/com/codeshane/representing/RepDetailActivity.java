/* RepDetailActivity is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/** An activity representing a single Rep detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RepresentativeListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link RepresentativeDetailFragment}. */
public class RepDetailActivity extends FragmentActivity {

	@TargetApi ( Build.VERSION_CODES.HONEYCOMB )
	@Override protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rep_details);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		// fragments automatically re-added to its container after config,
		// so we don't need to manually add it.
		// http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(RepDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(RepDetailFragment.ARG_ITEM_ID));
			RepDetailFragment fragment = new RepDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.rep_detail_container, fragment).commit();
		}
	}

	@Override public boolean onOptionsItemSelected ( MenuItem item ) {
		switch (item.getItemId()) {
			case android.R.id.home: // This ID represents the Home or Up button.
				// Use NavUtils to allow users to nav up instead of back:
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				NavUtils.navigateUpTo(this, new Intent(this, RepsListActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
