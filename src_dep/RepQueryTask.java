/* RestLoader is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.project_72.tasks;

import org.apache.http.HttpResponse;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.codeshane.project_72.model.RepresentativesTable;

/** Converts {@code HttpUriRequest}s into {@code HttpResponse}s asynchronously.
 * @author Shane Ian Robinson <shane@codeshane.com>
 * @since Aug 19, 2013
 * @version 1 */
public class RepQueryTask extends AsyncTask<Uri, Integer, Cursor> {
	public static final String	TAG	= RepQueryTask.class.getName();

	/** The response listener. Must be set in constructor. */
	private final QueryTaskListener mListener;

	private ContentResolver	resolver;

	private Context mContext = null;

	/** @see QueryTaskListener */
	public RepQueryTask(Context context, QueryTaskListener onHttpResponseListener) {
		super();
		this.mContext = context;
		this.mListener = onHttpResponseListener;

	}

	/** @see QueryTaskListener */
	private RepQueryTask() { super(); mListener=null; }

	/** @see android.os.AsyncTask#onCancelled() */
	@Override protected void onCancelled () {
		Log.v(TAG,"HttpRequestLoader.onCancelled()");
		super.onCancelled();
	}

	/** @see android.os.AsyncTask#doInBackground(java.lang.Object[]) */
	@Override protected Cursor doInBackground ( Uri... args ) {
		return mContext.getContentResolver().query(RepresentativesTable.CONTENT_URI, RepresentativesTable.PROJECTION, RepresentativesTable.Columns.ID.getName() + " = ?",new String[]{""}, "asc");
	}

	/** Closes the connection and forwards the response to the listener.
	 * @see HttpResponse
	 * @see QueryTaskListener
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[]) */
	protected void onPostExecute ( Cursor cursor ) {
		if(cursor != null){
        	if (null != mListener) { mListener.onQueryResponse(cursor); }
		}
	}

	/** The callback for receiving the {@code HttpResponse} from a {@code SimpleGetLoader}
	 * upon completion of its execute() method.
	 * @see HttpResponse
	 * */
	public interface QueryTaskListener {
		void onQueryResponse(Cursor cursor);
	}
}
