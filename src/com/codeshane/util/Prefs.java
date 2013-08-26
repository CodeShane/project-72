/* Prefs is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

/** Can't use my Sprefs because it uses androidannotations, I don't need them, and I'm a bit rushed so
 * lets just make a utility tool real quick instead..
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 23, 2013
 * @version 1
 */
public class Prefs {
	public static final String TAG	= Prefs.class.getName();

	/** <p><b>{@code apply()}</b> commits to memory immediately and begins an asynchronous commit to persistent storage.</p>
	 * <p><b>{@code commit()}</b></p>Performs a synchronous (blocking) call to commit to persistent storage.
	 * <p>On devices that don't support apply(), commit()
	 * @see SharedPreferences.Editor#commit()
	 * */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public boolean commit(final SharedPreferences prefs, final SharedPreferences.Editor e) {
		Thread t = new Thread(new Runnable(){
			@Override public void run () {
				Log.v(TAG, "commit() runnable.run() starting on " + Thread.currentThread().getName());
				synchronized(prefs) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
						e.apply();
					} else {
						e.commit();
					}
				}
			}});
		t.start();
		return true;
	}
}