/* RepDetailActivity is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codeshane.util.Log;

/** An activity representing a single Rep detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RepresentativeListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link RepresentativeDetailFragment}. */
public class RepDetailActivity extends ActionBarActivity {
	public static final String TAG = RepDetailActivity.class.getSimpleName();

//	@TargetApi ( Build.VERSION_CODES.HONEYCOMB )
	@Override protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"onCreate");
		setContentView(R.layout.rep_details_frame);

//		if (Build.VERSION.SDK_INT >= 7 ){// Build.VERSION_CODES.HONEYCOMB){
			// Show the Up button in the action bar.
			this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		}

		// fragments automatically re-added to its container after config,
		// so we don't need to manually add it.
		// http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putString(RepsListActivity.ITEM_ID, getIntent().getStringExtra(RepsListActivity.ITEM_ID));
			// Create the detail fragment and add it to the activity using a fragment transaction.
			RepDetailFragment fragment = new RepDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.rep_detail_container, fragment).commit();
		}
		View view = findViewById(R.id.rep_detail_container);
		if (null!=view) {
			view.setBackgroundResource(android.R.color.darker_gray);
		} else {
			Toast.makeText(this, "WTF is this!?", Toast.LENGTH_LONG).show();
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
