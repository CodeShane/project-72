/* RestLoader is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.project_72.tasks;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/** Converts {@code HttpUriRequest}s into {@code HttpResponse}s asynchronously.
 * Inputing multiple {@link Uri}s into one Task instance will result in them being
 * processed synchronously.
 *
 * @author Shane Ian Robinson <shane@codeshane.com>
 * @since Aug 19, 2013
 * @version 1 */
public class RepGetTask extends AsyncTask<Uri, HttpResponse, HttpResponse> {
	public static final String TAG = RepGetTask.class.getPackage().getName() + "." + RepGetTask.class.getSimpleName();

	/** The response listener. Must be set in constructor. */
	protected OnHttpResponseListener mListener;

	protected HttpClient httpClient;
    {
    	HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 5000);
        httpClient = new DefaultHttpClient(httpParameters);
    }

    protected HttpUriRequest httpUriRequest = null;

	/** @see OnHttpResponseListener */
	public RepGetTask(OnHttpResponseListener onHttpResponseListener) {
		super();
		if (null==onHttpResponseListener) throw new IllegalArgumentException();
		this.mListener = onHttpResponseListener;
	}

	// no instances without data
	@SuppressWarnings ( "unused" )
	private RepGetTask() { super(); }

	/** @see android.os.AsyncTask#onCancelled() */
	@Override protected void onCancelled () {
		Log.v(TAG,"HttpRequestLoader.onCancelled()");
		super.onCancelled();
		if (null!=this.httpUriRequest) { this.httpUriRequest.abort(); }
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[]) */
	@Override protected HttpResponse doInBackground ( Uri... args ) {
		HttpResponse httpResponse = null;

		for (Uri uri : args) {
			httpUriRequest = new HttpGet(uri.toString());
			if (null!=httpResponse) {
				publishProgress(httpResponse);
				httpResponse = null;
			}

			try {
				httpResponse = httpClient.execute(httpUriRequest);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return httpResponse;
	}

	/** Forwards responses to the listener.
	 * @see android.os.AsyncTask#onProgressUpdate(java.lang.Object[]) */
	@Override protected void onProgressUpdate ( HttpResponse... response ) {
		notifyListener(response);
	}

	/** Closes the connection and forwards the response to the listener.
	 * @see HttpResponse
	 * @see OnHttpResponseListener
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[]) */
	@Override protected void onPostExecute ( HttpResponse response ) {
		notifyListener(response);
	}

	/** Notify the listener of each completion. After passing along the httpResponse,
	 * this task will also attempt to consume the entity.
	 *
	 * @since Aug 22, 2013
	 * @version Aug 22, 2013
	 * @return void
	 */
	private void notifyListener ( HttpResponse... response ) {
		if (null==response) {return;}
		if (null != mListener) {
			for (HttpResponse httpResponse : response) {
				mListener.onHttpResponse(httpResponse);
				try {
					httpResponse.getEntity().consumeContent();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/** The callback for receiving the {@code HttpResponse} from a {@code SimpleGetLoader}
	 * upon completion of its execute() method.
	 * @see HttpResponse
	 * */
	public interface OnHttpResponseListener {
		void onHttpResponse(HttpResponse response);
	}
}
