/* RepApplication is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing;

import com.codeshane.representing.BuildConfig;
import com.codeshane.util.Bitwise;
import com.codeshane.util.Log;
import com.codeshane.util.StrictModes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 1
 */
public class Representing extends Application {
	public static final String	TAG	= Representing.class.getName();
	public static final String	PACKAGE	= Representing.class.getPackage().getName();

	/** Debug mode state.
	 * <p>Used so compiler can automatically remove all code blocks of ({@code if (DEBUG){}} (which should proceed all calls to Log)</p>
	 * <p>Log calls can also be removed using ProGuard.</p>
	 * (@link http://code.google.com/p/android/p/android/issues/detail?id=27940 :Android Bug 27940}
	 * @DevNote Isn't read from AndroidManifest.xml so when false, {@code if(DEBUG)...} statements can be removed by compiler as dead/unreachable code.</p>
	 * @DevNote Eclipse generated BuildConfig.DEBUG doesn't work properly, and isn't generated by external build tools.
	 * */
	public static final boolean DEBUG = false;

	/** Strict mode state.
	 * <p>Used to test app latency and quickly assess potential ANRs.</p>
	 * */
	public static final boolean STRICT_MODE = false;

	private static Context mAppContext;
	private static SharedPreferences mPrefs;

	@Override public void onCreate() {
		super.onCreate();
		Log.setUseLogcat(true);

		mAppContext = getApplicationContext();
		mPrefs = this.getSharedPreferences(PACKAGE+"_preferences", 0);

		if (!DEBUG) { return; }
		if (STRICT_MODE) {
			Log.w(TAG, "application.setStrictMode()");
			StrictModes.setStrictMode();
			StrictMode.enableDefaults();
		}

		int flags = this.getApplicationInfo().flags;

		// DEBUG must match AndroidManifest to enable debugging and preventing release-mode info & performance leaks.
		// Should only occur if there is some kind of build error.
		// BuildConfig.DEBUG is generated but not reliable.
		boolean debugflag = Bitwise.isSet(ApplicationInfo.FLAG_DEBUGGABLE, flags);
		boolean testFlag = Bitwise.isSet(ApplicationInfo.FLAG_TEST_ONLY, flags);
		int logLevel = (DEBUG != debugflag)?Log.ERROR:Log.INFO;

		Log.println(logLevel, TAG, "    application.class.getName() = " + Representing.class.getName());
		Log.println(logLevel, TAG, "              application.DEBUG = " + DEBUG);
		Log.println(logLevel, TAG, "              BuildConfig.DEBUG = " + BuildConfig.DEBUG);
		Log.println(logLevel, TAG, "ApplicationInfo.FLAG_DEBUGGABLE = " + debugflag);
		Log.println(logLevel, TAG, "ApplicationInfo.FLAG_TEST_ONLY  = " + testFlag);
		Log.println(logLevel, TAG, "");

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
