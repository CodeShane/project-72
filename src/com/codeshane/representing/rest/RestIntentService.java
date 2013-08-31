/* WhoAreMyRepsClient is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import com.codeshane.representing.Representing;
import com.codeshane.representing.providers.RepsContract;
import com.codeshane.util.Log;
import com.codeshane.util.Utils;

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
public class RestIntentService extends IntentService {
	public static final String TAG = RestIntentService.class.getName();

	public static final String ACTION_QUERY = "com.codeshane.net.ACTION_QUERY";
	public static final String ACTION_QUERY_RESPONSE = "com.codeshane.net.ACTION_QUERY_RESPONSE";

	/** An identifier for an optional extra representing this query.This identifier is not used, simply returned to the sender. */
	public static final String EXTRA_QUERY_ID = "com.codeshane.net.EXTRA_QUERY_ID";

	/** An identifier for an extra holding the package of the component that will process the returned entity. */
	public static final String EXTRA_PACKAGE = "com.codeshane.net.EXTRA_PACKAGE";

	/** An identifier for an extra holding the name of the component that will process the returned entity. */
	public static final String EXTRA_COMPONENT = "com.codeshane.net.EXTRA_COMPONENT";

	/** Alert! IntentService must have a NO-ARGS constructor that calls a super constructor using the implementation's name as an argument. Because. */
	public RestIntentService () {
		super(RestIntentService.class.getName());
		this.setIntentRedelivery(true);
		Log.setUseLogcat(true);
		Utils.threax("RestIntentService");
	}

	/** @see android.app.Service#onConfigurationChanged(android.content.res.Configuration) */
	@Override public void onConfigurationChanged ( Configuration newConfig ) {
		super.onConfigurationChanged(newConfig);
	}

	/** Worker thread.
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent) */
	@Override protected void onHandleIntent ( Intent intent ) {
//		Log.v(TAG, "onHandleIntent");
		Utils.threax("onHandleIntent");
		// Get all necessary data from intent; log & return if we can't.

		String action = intent.getAction();
		if (null==action) {	Log.e(TAG, "onHandleIntent - null action String"); return; }
		if (!ACTION_QUERY.equalsIgnoreCase(action)) { Log.e(TAG, "Unknown action."); return; }

		Bundle extras = intent.getExtras();
		if (null==extras) {	Log.e(TAG, "onHandleIntent - null extras Bundle"); return; }

		Route route = Route.from(extras);
		if (null==route) { Log.e(TAG, "onHandleIntent - null Route"); return; }

		/* Establish an http connection and perform the GET request. */
		String url = route.getRemote().toString();
//		HttpResponse httpResponse = simpleRestRequest(route.getRemote().toString());
		Utils.disableLegacyKeepAlive();

	    HttpParams httpParameters = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
	    HttpConnectionParams.setSoTimeout(httpParameters, 5000);
	    HttpClient httpClient = new DefaultHttpClient(httpParameters);
	    HttpUriRequest httpUriRequest = null;

	    HttpResponse httpResponse = null;

		httpUriRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpUriRequest);
		} catch (UnknownHostException ex) {
			Log.w(TAG, "Unable to connect to REST server.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null==httpResponse) { Log.e(TAG,"httpResponse null"); return; }


		/* Log response status, headers*/
		Utils.toLog(TAG, httpResponse);


		/* Extract httpEntity from httpResponse */

		HttpEntity httpEntity = httpResponse.getEntity();
        if (null == httpEntity) { Log.e(TAG,"null httpEntity"); return; }


	    /* Convert httpEntity to content String */

        String content = null;
        InputStream inputStream = null;
		try {
			inputStream = httpEntity.getContent();
		} catch (IllegalStateException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (null==inputStream) { Log.e(TAG,"null inputStream"); return; }

		try {
			content = Utils.parseInputStream(inputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (null==content) { Log.e(TAG, "content null"); return; } else { Log.v(TAG, "content = "+content); }


		/* Parse content String into a List of ContentValues */

		ArrayList<ContentValues> items = WhoIsMyRepresentativeJsonParser.parseJsonResult(content);
//		Log.v(TAG, "Parsed items: "+items.size());
		if (items.size()==0) { Log.e(TAG,"no items"); return; }

		/* Generate a ContentProviderOperation for each ContentValues item. */

		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		for (ContentValues item : items) {
			if (null==item) {
				Log.e(TAG,"null item"); break;
			}
//			dump(item);
			Route a = route;
			Uri b = a.getLocal();
			ContentProviderOperation.Builder c = ContentProviderOperation.newInsert(b);
			ContentValues d = item;
			if (null==c) {
				Log.e(TAG,"c is null?");
			}
			c.withValues(d);
			Log.i(TAG,"operations ="+operations.size()+"+");
			operations.add(c.build());
		}
		if (operations.size()==0) { Log.e(TAG,"no operations"); return; }
		Log.i(TAG,"operations="+operations.size());

		/* Execute the ContentProviderOperations */

		try {
			ContentProviderResult[] results = Representing.context().getContentResolver().applyBatch(RepsContract.AUTHORITY, operations);
			for (ContentProviderResult r : results) {
				Log.v(TAG, "Results! " + r.count + " to " + r.uri.toString());
			}
		} catch (RemoteException ex) {
			ex.printStackTrace();
		} catch (OperationApplicationException ex) {
			ex.printStackTrace();
		}

	}

	/** @since Aug 30, 2013
	 * @version Aug 30, 2013
	 * @return void
	 */
	@TargetApi ( Build.VERSION_CODES.HONEYCOMB )
	private void dump ( ContentValues item ) {
		Log.v(TAG,"dump(ContentValues)..");
		if (null==item) { Log.v(TAG,"dump(ContentValues) item==null"); return; }
		Set<String> keySet = item.keySet();
		for (String key: keySet){
			Log.v(TAG,"dump(ContentValues) item:"+item.getAsString(key));
		}
		Log.v(TAG,"..dump(ContentValues)");
	}

//	/** Startup with basic parameters and initiate a rest request to the given url.
//	 * @since Aug 29, 2013
//	 * @version Aug 29, 2013
//	 * @return HttpResponse
//	 */
//	private static final HttpResponse simpleRestRequest (String url) {
//		Utils.disableLegacyKeepAlive();
//
//	    HttpParams httpParameters = new BasicHttpParams();
//	    HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
//	    HttpConnectionParams.setSoTimeout(httpParameters, 5000);
//	    HttpClient httpClient = new DefaultHttpClient(httpParameters);
//	    HttpUriRequest httpUriRequest = null;
//
//	    HttpResponse httpResponse = null;
//
//		httpUriRequest = new HttpGet(url);
//		try {
//			httpResponse = httpClient.execute(httpUriRequest);
//		} catch (UnknownHostException ex) {
//			Log.w(TAG, "Unable to connect to REST server.");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return httpResponse;
//	}
}
