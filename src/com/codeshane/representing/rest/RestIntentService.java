/* WhoAreMyRepsClient is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.project_72.providers;

import java.util.HashMap;
import java.util.Map;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import com.codeshane.util.Restful;

/** @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 1
 */
public class WhoIsMyRepRestClient extends IntentService implements Restful {

	public static final String	TAG	= "WhoIsMyRepresentativeClient";
	public static final String DOMAIN = "whoismyrepresentative.com";
	public static final Uri SOURCE = Uri.parse("http://whoismyrepresentative.com/");

	/** @see WhoIsMyRepRestClient */
	public WhoIsMyRepRestClient ( String name ) {
		super(name);
		// TODO stub
	}

	/** @see android.app.IntentService#setIntentRedelivery(boolean) */
	@Override public void setIntentRedelivery ( boolean enabled ) {
		super.setIntentRedelivery(enabled);

	}

	/** @see android.app.IntentService#onStart(android.content.Intent, int) */
	@Override public void onStart ( Intent intent, int startId ) {
		super.onStart(intent, startId);

	}

	/** @see android.app.IntentService#onStartCommand(android.content.Intent, int, int) */
	@Override public int onStartCommand ( Intent intent, int flags, int startId ) {
		return super.onStartCommand(intent, flags, startId);

	}

	/** @see android.app.IntentService#onDestroy() */
	@Override public void onDestroy () {
		super.onDestroy();

	}

	/** Worker thread.
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent) */
	@Override protected void onHandleIntent ( Intent intent ) {

	}

	private static final Map<String,String> URI_REWRITE_MAP;
	static {
		URI_REWRITE_MAP = new HashMap<String,String>();
//		URI_REWRITE_MAP.put(key, value)
		//XXX
	}

	private static Uri rewriteUri(String string){
		Uri uri = Uri.parse(string);
		uri.buildUpon().scheme("http").build();
		//XXX
		return uri;
	}

	/** @see android.app.Service#onConfigurationChanged(android.content.res.Configuration) */
	@Override public void onConfigurationChanged ( Configuration newConfig ) {
		super.onConfigurationChanged(newConfig);
	}

	/** @see android.app.Service#onTaskRemoved(android.content.Intent) */
	@TargetApi ( Build.VERSION_CODES.ICE_CREAM_SANDWICH )
	@Override public void onTaskRemoved ( Intent rootIntent ) {
		super.onTaskRemoved(rootIntent);
	}

	/** @see com.codeshane.util.Restful#create() */
	@Override public boolean create () {
		return false;
	}
	/** @see com.codeshane.util.Restful#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String) */
	@Override public Cursor query ( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		return null;
	}

	/** @see com.codeshane.util.Restful#getType(android.net.Uri) */
	@Override public String getType ( Uri uri ) {
		return null;
	}

	/** @see com.codeshane.util.Restful#insert(android.net.Uri, android.content.ContentValues) */
	@Override public Uri insert ( Uri uri, ContentValues values ) {
		return null;
	}

	/** @see com.codeshane.util.Restful#delete(android.net.Uri, java.lang.String, java.lang.String[]) */
	@Override public int delete ( Uri uri, String selection, String[] selectionArgs ) {
		return 0;
	}

	/** @see com.codeshane.util.Restful#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[]) */
	@Override public int update ( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
		return 0;
	}


}
