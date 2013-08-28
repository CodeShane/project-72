/* WhoAreMyRepsClient is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.rest;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.codeshane.representing.Representing;
import com.codeshane.representing.providers.RepsContract;
import com.codeshane.representing.providers.RepsContract.Representatives;
import com.codeshane.representing.rest.HttpGetTask.OnHttpResponseListener;

/** This service accepts standard ContentProvider requests,
 * converts them for the associated rest resource, requests the resource,
 * and updates the other ContentProvider blah blah
 * <pre>
Intent msgIntent = new Intent(this, SimpleIntentService.class);
msgIntent.putExtra(RestIntentService.ACTION_QUERY, strInputMsg);
startService(msgIntent);
</pre>
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 1
 */
public class RestIntentService extends IntentService implements OnHttpResponseListener {
	public static final String TAG = RestIntentService.class.getName();

	public static final String ACTION_QUERY = "com.codeshane.net.ACTION_QUERY";
	public static final String ACTION_QUERY_RESPONSE = "com.codeshane.net.ACTION_QUERY_RESPONSE";

	/** Identifier for an extra containing a parceled {@code Uri} {@code ContentProvider} Uri for the service to download.
	 * @see ContentProvider
	 * @see Uri */
	public static final String EXTRA_URI_LOCAL = "com.codeshane.net.EXTRA_URI_LOCAL";

	/** Identifier for an extra containing a parceled {@code Uri} {@code ContentProvider} Uri for the service to download.
	 * @see ContentProvider
	 * @see Uri */
	public static final String EXTRA_URI_REMOTE = "com.codeshane.net.EXTRA_URI_REMOTE";

	/** An identifier for an optional extra representing this query.This identifier is not used, simply returned to the sender. */
	public static final String EXTRA_QUERY_ID = "com.codeshane.net.EXTRA_QUERY_ID";

	/** An identifier for an extra holding the package of the component that will process the returned entity. */
	public static final String EXTRA_PACKAGE = "com.codeshane.net.EXTRA_PACKAGE";

	/** An identifier for an extra holding the name of the component that will process the returned entity. */
	public static final String EXTRA_COMPONENT = "com.codeshane.net.EXTRA_COMPONENT";

	/** @see RestIntentService */
	public RestIntentService ( String name ) {
		super(name);
		this.setIntentRedelivery(true);
	}

	interface IntentHandler {
		void handleIntent(Intent intent);
	}

	/** Worker thread.
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent) */
	@Override protected void onHandleIntent ( Intent intent ) {
		String action = intent.getAction();
		if (null==action) return;
		Bundle extras = intent.getExtras();
		if (null==extras) {
			return;
		}

		if (ACTION_QUERY.equalsIgnoreCase(action)) {
			Parcelable parcelable = extras.getParcelable(EXTRA_URI_REMOTE);
			Uri remoteUri = null;
			if (null!=parcelable){
				if (parcelable instanceof Uri){
					remoteUri = (Uri) parcelable;
					Toast.makeText(getApplicationContext(), "URI ".concat(remoteUri.toString()), Toast.LENGTH_LONG).show();
					new HttpGetTask(this).execute(remoteUri);
				}
			}
		} else if (ACTION_QUERY_RESPONSE.equalsIgnoreCase(action)){
			Parcelable parcelable = extras.getParcelable(EXTRA_URI_LOCAL);
			Uri localUri = null;

			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

			if (null!=parcelable){
				if (parcelable instanceof Uri){
					localUri = (Uri) parcelable;
					String jsonResponse = extras.getString(Intent.EXTRA_STREAM);
					ArrayList<ContentValues> items = WhoIsMyRepresentativeJsonParser.parseJsonResult(jsonResponse);
					for (Iterator<ContentValues> it = items.iterator(); it.hasNext();) {
						ContentValues item = (ContentValues) it.next();
						operations.add(ContentProviderOperation.newInsert(localUri).withValues(item).build());
					}


				}
			}
			if (operations.size() > 0) {
				try {
					this.getContentResolver().applyBatch(RepsContract.AUTHORITY, operations);
				} catch (RemoteException ex) {
					ex.printStackTrace();
				} catch (OperationApplicationException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/** @see android.app.Service#onConfigurationChanged(android.content.res.Configuration) */
	@Override public void onConfigurationChanged ( Configuration newConfig ) {
		super.onConfigurationChanged(newConfig);
	}

	/** Parse the returned data from whatever query the GET task performed.
	 * @see com.codeshane.representing.rest.HttpGetTask.OnHttpResponseListener#onHttpResponse(org.apache.http.HttpResponse) */
	@Override public void onHttpResponse ( HttpResponse httpResponse ) {
		HttpEntity httpEntity=null;
		String httpEntityText=null;
		String uris = httpResponse.getFirstHeader(RestIntentService.EXTRA_URI_LOCAL).getValue();

		try {
	        httpEntity = httpResponse.getEntity();
        	if (null == httpEntity) { Log.e(TAG,"null httpEntity"); return; }
			httpEntityText = EntityUtils.toString(httpEntity);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (null == httpEntityText) { Log.e(TAG,"null httpEntityText"); return; } // TODO notify requestor of error

		// Send the data via an intent back to itself so that it can be processed in the worker thread.
		Intent intent = new Intent();
		intent.setClass(Representing.context(), RestIntentService.class);
		intent.setAction(ACTION_QUERY_RESPONSE);
		Uri uri = Uri.parse(uris);
		intent.putExtra( EXTRA_URI_LOCAL, uri);
		intent.putExtra(Intent.EXTRA_STREAM, httpEntityText);
		startService(intent);
	}
}
