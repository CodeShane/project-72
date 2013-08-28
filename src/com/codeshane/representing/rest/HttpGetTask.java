/* RestLoader is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.rest;

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
public class HttpGetTask extends AsyncTask<Uri, HttpResponse, HttpResponse> {
	public static final String TAG = HttpGetTask.class.getPackage().getName() + "." + HttpGetTask.class.getSimpleName();

	/** The response listener. Must be set in constructor. */
	protected OnHttpResponseListener mListener;

	protected HttpClient httpClient = null;

    protected HttpUriRequest httpUriRequest = null;

	private Uri	localUri;

	/** @see OnHttpResponseListener */
	public HttpGetTask(OnHttpResponseListener onHttpResponseListener) {
		super();
		if (null==onHttpResponseListener) throw new IllegalArgumentException();
		this.mListener = onHttpResponseListener;
		HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 5000);
        httpClient = new DefaultHttpClient(httpParameters);
	}

	// no instances without a listener.
	@SuppressWarnings ( "unused" )
	private HttpGetTask() { super(); }

	/** @see android.os.AsyncTask#onCancelled() */
	@Override protected void onCancelled () {
		Log.v(TAG,"HttpRequestLoader.onCancelled()");
		super.onCancelled();
		if (null!=this.httpUriRequest) { this.httpUriRequest.abort(); }
	}

	/** Begin visiting the given Uri(s) and returning the response(s) to the listener.
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[]) */
	@Override protected HttpResponse doInBackground ( Uri... args ) {
		HttpResponse httpResponse = null;

		for (Uri uri : args) {
			String uris = uri.toString();
			httpUriRequest = new HttpGet(uris);

			try {
				httpResponse = httpClient.execute(httpUriRequest);
				if (null!=httpResponse) {
					httpResponse.addHeader(RestIntentService.EXTRA_URI_LOCAL,this.localUri.toString());
					publishProgress(httpResponse);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				httpResponse = null;
			}
		}

		return null;
	}

	/** Forwards responses to the listener.
	 * @see android.os.AsyncTask#onProgressUpdate(java.lang.Object[]) */
	@Override protected void onProgressUpdate ( HttpResponse... httpResponse ) {
		notifyListener(httpResponse);
	}

	/** Closes the connections.
	 * @see HttpResponse
	 * @see OnHttpResponseListener
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[]) */
	@Override protected void onPostExecute ( HttpResponse httpResponse ) {
		// this HttpResponse will always be null.
		assert (null==httpResponse);
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
				if (null!=localUri) {
					httpResponse.addHeader(RestIntentService.EXTRA_URI_LOCAL, localUri.toString());
				}
				mListener.onHttpResponse(httpResponse);
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


	/** @since Aug 28, 2013
	 * @version Aug 28, 2013
	 * @return AsyncTask<Uri,HttpResponse,HttpResponse>
	 */
	public AsyncTask<Uri, HttpResponse, HttpResponse> setLocalUri ( Uri uri ) {
		if (null!=uri){
			this.localUri = uri;
		}
		return this;
	}
}
