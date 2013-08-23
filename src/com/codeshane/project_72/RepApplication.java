/* RepApplication is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.project_72;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 1
 */
public class RepApplication extends Application {
	public static final String	TAG	= RepApplication.class.getPackage().getName() + "." + RepApplication.class.getSimpleName();

	private static Context mAppContext;
	private static SharedPreferences mPrefs;

	@Override public void onCreate() {
		super.onCreate();
		mAppContext = getApplicationContext();
		mPrefs = this.getSharedPreferences("com.codeshane.project_72_preferences", 0);
	}

	/**
	 * Returns the application's {@code Context}. Useful for classes that need
	 * a Context but don't have one of their own.
	 *
	 * @return application context
	 */
	public static Context context() {
		return mAppContext;
	}

	/** Return's the application's default {@code SharedPreferences}.
	 * Keeps preferences available from anywhere in the application, all in one place.
	 *  */
	public static SharedPreferences prefs() {
		return mPrefs;
	}
}
