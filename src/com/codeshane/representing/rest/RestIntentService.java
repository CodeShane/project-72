/* WhoAreMyRepsClient is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.rest;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.IntentService;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.codeshane.representing.Representing;
import com.codeshane.representing.providers.RepsContract;
import com.codeshane.representing.providers.RepsContract.Tables.Columns;
import com.codeshane.util.Log;
import com.codeshane.util.Utils;

/** This service accepts standard ContentProvider requests,
 * converts them for the associated rest resource, requests the resource,
 * and updates the ContentProvider.
 *
 * <pre>
Context context = this.getContext().getApplicationContext();
Intent requestLatest = new Intent(RestIntentService.ACTION_QUERY)
.putExtra(Route.EXTRA_URI_LOCAL, uri)
.putExtra(Route.EXTRA_URI_REMOTE, remoteUri)
.setClass(this.getContext().getApplicationContext(), RestIntentService.class);
context.startService(requestLatest);
</pre>
 *
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 1
 */
public class RestIntentService extends IntentService {
	public static final String TAG = RestIntentService.class.getName();

	/** The Action representing a query against a remote data source. */
	public static final String ACTION_QUERY = "com.codeshane.net.ACTION_QUERY";

	/** An identifier for an optional extra representing this query.This identifier is not used, simply returned to the sender. */
	public static final String EXTRA_QUERY_ID = "com.codeshane.net.EXTRA_QUERY_ID";

	/** An identifier for an extra holding the package of the component that will process the returned entity. */
	public static final String EXTRA_PACKAGE = "com.codeshane.net.EXTRA_PACKAGE";

	/** An identifier for an extra holding the name of the component that will process the returned entity. */
	public static final String EXTRA_COMPONENT = "com.codeshane.net.EXTRA_COMPONENT";

	/** Alert! An IntentService must have a NO-ARGS constructor that calls a super constructor using the implementation's name as an argument. */
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

	/** Worker thread for the {@code IntentService}. This particular {@link IntentService} simply executes a rest request,
	 * parses the result, and pushes changes to the local {@link ContentProvider}. If there are any errors in the process,
	 * it simply logs the problem and aborts, waiting to be re-queried.
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent) */
	@Override protected void onHandleIntent ( Intent intent ) {

		// Get all necessary data from intent; log & return if we can't.
		String action = intent.getAction();
		if (null==action) {	Log.e(TAG, "onHandleIntent - null action String"); return; }
		if (!ACTION_QUERY.equalsIgnoreCase(action)) { Log.e(TAG, "Unknown action."); return; }

		Bundle extras = intent.getExtras();
		if (null==extras) {	Log.e(TAG, "onHandleIntent - null extras Bundle"); return; }

		Route route = Route.from(extras);
		if (null==route||null==route.getLocal()||null==route.getRemote()) { Log.e(TAG, "onHandleIntent - invalid Route"); return; }


		/* Establish an http connection and perform the GET request. */
	    HttpResponse httpResponse = getHttpResponse(route.getRemote().toString());
		if (null==httpResponse) { Log.e(TAG,"httpResponse null"); return; }
		if (Representing.DEBUG) Utils.toLog(TAG, httpResponse);


		/* Extract httpEntity from httpResponse */
		HttpEntity httpEntity = httpResponse.getEntity();
        if (null == httpEntity) { Log.e(TAG,"null httpEntity"); return; }


	    /* Convert httpEntity to content String */
        String content = getContent(httpEntity);
		if (null==content) { Log.e(TAG, "content null"); return; } else { Log.v(TAG, "content = "+content); }


		/* Parse content String into a List of ContentValues */
		ArrayList<ContentValues> items = WhoIsMyRepresentativeJsonParser.parseJsonResult(content);
		if (items.size()==0) { Log.e(TAG,"no items"); return; } else { Log.v(TAG, "Parsed items: "+items.size()); }
		// Append values not returned from WhoIsMyRepresentative.com, but are part of the results anyways:
		String zip = route.getLocal().getQueryParameter("zip");
		for (ContentValues contentValues : items) { contentValues.put(Columns.ZIP.getName(), zip); }


		/* Generate a ContentProviderOperation for each ContentValues item. */
		ArrayList<ContentProviderOperation> operations = generateContentProviderOperations(items, route.getLocal());
		if (operations.size()==0) { Log.e(TAG,"no operations"); return; }
		Log.i(TAG,"operations="+operations.size());


		/* Execute the ContentProviderOperations */
		ContentProviderResult[] results = executeContentProviderOperations(operations);
		if (null==results) { Log.e(TAG, "op results null."); return; }
		if (0==results.length) { Log.e(TAG, "0 op results."); return; }
		Log.i(TAG,"onHandleIntent SUCCESS! "+results.length+" results.");
	}

	/** An HttpClient with basic settings.
	 * @return HttpClient */
	private HttpClient getHttpClient(){
		Utils.disableLegacyKeepAlive();
	    HttpParams httpParameters = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
	    HttpConnectionParams.setSoTimeout(httpParameters, 5000);
	    HttpClient httpClient = new DefaultHttpClient(httpParameters);
	    return httpClient;
	}

	/** Initiates an http GET request against the target url and returns
	 * the {@code HttpResponse}.
	 * @param url the request should be sent to.
	 *  @return HttpResponse */
	private HttpResponse getHttpResponse (String url) {
		HttpClient httpClient = getHttpClient();

	    HttpUriRequest httpUriRequest = null;
		httpUriRequest = new HttpGet(url);

		HttpResponse httpResponse = null;
		try {
			if (Representing.DEBUG) {
				Log.w(TAG,"Mocking http response");
				httpResponse = new MockHttpResponse();
			} else {
				Log.w(TAG,"Executing http uri request");
				httpResponse = httpClient.execute(httpUriRequest);
			}
		} catch (UnknownHostException ex) {
			Log.w(TAG, "Unable to connect to REST server.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpResponse;
	}

	/** Converts an {@code HttpEntity}'s content into a {@code String}.
	 * @param httpEntity An HttpEntity to extract the content from.
	 * @return String
	 */
	private String getContent ( HttpEntity httpEntity ) {
		String content = null;
		InputStream inputStream = null;
		try {
			inputStream = httpEntity.getContent();
			if (null==inputStream) { Log.e(TAG,"null inputStream"); return null; }
			content = Utils.parseInputStream(inputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return content;
	}

	/** Generates the {@code ContentProviderOperation}s to insert {@code ContentValues} into the given {@code ContentProvider} {@code Uri}.
	 * @param items ArrayList<ContentValues> intended for insertion
	 * @param insertTo Uri to insert the data to.
	 * @return ArrayList<ContentProviderOperation>
	 * @see ContentValues
	 * @see ContentProviderOperation
	 * @see ContentProvider
	 * @see Uri
	 *  */
	private static final ArrayList<ContentProviderOperation> generateContentProviderOperations(ArrayList<ContentValues> items, Uri insertTo){
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
		Log.v(TAG, "Generating " + items.size() + " insert operations toward: " + insertTo.toString());

		for (ContentValues item : items) {
			if (null!=item) {
				ContentProviderOperation.Builder c = ContentProviderOperation.newInsert(insertTo);
				c.withValues(item).withYieldAllowed(true);
				operations.add(c.build());

				String cols = new String();
				for (Entry<String, Object> entry : item.valueSet()) {
					cols += entry.getKey() + ", ";
				}
				Log.v(TAG, "added item with cols: " + cols);
			} else {
				Log.e(TAG,"null item");
			}
			Log.i(TAG,"operations ="+operations.size()+"+");
		}
		return operations;
	}

	private static final ContentProviderResult[] executeContentProviderOperations(ArrayList<ContentProviderOperation> operations){
		Utils.threax("executeContentProviderOperations");
		ContentProviderResult[] results = null;
		try {
			results = Representing.context().getContentResolver().applyBatch(RepsContract.AUTHORITY, operations);
			for (ContentProviderResult r : results) {
				Log.v(TAG, "Results! " + r.count + " to " + r.uri.toString());
			}
		} catch (RemoteException ex) {
			ex.printStackTrace();
		} catch (OperationApplicationException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return results;
	}
}
