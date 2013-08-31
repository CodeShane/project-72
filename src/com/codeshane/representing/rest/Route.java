/* Route is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.rest;

import android.content.ContentProvider;
import android.net.Uri;
import android.os.Bundle;
import com.codeshane.util.Log;

/** A pair of URIs, one representing a local destination / return address, and the other a remote / correspondent.
 * Convenience methods for transmitting in bundles / extras.
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 28, 2013
 * @version 1
 * */
 public final class Route {
	public static final String	TAG	= Route.class.getPackage().getName() + "." + Route.class.getSimpleName();

	/** Identifier for an extra containing a parceled {@code Uri} {@code ContentProvider} Uri for the service to download.
	 * @see ContentProvider
	 * @see Uri */
	public static final String EXTRA_URI_LOCAL = "com.codeshane.net.EXTRA_URI_LOCAL";

	/** Identifier for an extra containing a parceled {@code Uri} {@code ContentProvider} Uri for the service to download.
	 * @see ContentProvider
	 * @see Uri */
	public static final String EXTRA_URI_REMOTE = "com.codeshane.net.EXTRA_URI_REMOTE";

	private final Uri mLocal;
	private final Uri mRemote;
	public Route ( Uri mLocal, Uri mRemote ) {
		super();
		this.mLocal = mLocal;
		this.mRemote = mRemote;
//		Log.v(TAG,  "new Route - mLocal".concat(mLocal.toString()));
//		Log.v(TAG,  "new Route - mRemote".concat(mRemote.toString()));
	}
	public Uri getLocal () { return mLocal; }
	public Uri getRemote () { return mRemote; }
	public final void to(Bundle bundle){
		bundle.putParcelable(EXTRA_URI_LOCAL, mLocal);
		bundle.putParcelable(EXTRA_URI_REMOTE, mRemote);
	}
	public static final Route from(Bundle bundle){
		Uri l=((Uri)bundle.getParcelable(EXTRA_URI_LOCAL));
		Uri r=((Uri)bundle.getParcelable(EXTRA_URI_REMOTE));
		return new Route(l,r);
	}
}