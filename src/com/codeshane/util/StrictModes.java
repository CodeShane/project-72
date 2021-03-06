/* StrictModes is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import static com.codeshane.util.Device.SUPPORTS_API_09_GINGERBREAD_2_3;
import static com.codeshane.util.Device.SUPPORTS_API_11_HONEYCOMB_3_0;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.os.StrictMode.VmPolicy.Builder;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 12, 2013
 * @version 2
 */
public class StrictModes {
	public static final String	TAG	= StrictModes.class.getPackage().getName() + "." + StrictModes.class.getSimpleName();

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static final Object getPolicyBuilder(){
		if (!SUPPORTS_API_09_GINGERBREAD_2_3) { return null; }
		VmPolicy.Builder builder = new VmPolicy.Builder().detectAll().penaltyLog();
		if (SUPPORTS_API_11_HONEYCOMB_3_0){ builder.detectLeakedClosableObjects(); }
		return builder;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static final Object getThreadPolicyBuilder() {
		if (!SUPPORTS_API_09_GINGERBREAD_2_3) { return null; }
		return new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
	}

	/**
	 * <b>For debug use only.</b>
	 * <p>
	 * Sets strict thread and VM policies on compatible devices.
	 * </p>
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static final void setStrictMode() {
		VmPolicy.Builder vmPolicyBuilder = (VmPolicy.Builder)getPolicyBuilder();
		ThreadPolicy.Builder threadPolicyBuilder = (ThreadPolicy.Builder)getThreadPolicyBuilder();
		if (null==vmPolicyBuilder || null==threadPolicyBuilder) { return; }
		StrictMode.setVmPolicy( vmPolicyBuilder.build() );
		StrictMode.setThreadPolicy( threadPolicyBuilder.build() );
	}

	/**
	 * <b>For debug use only.</b>
	 * <p>
	 * Sets fatally-strict thread and VM policies on compatible devices.
	 * </p>
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static final void setStrictModeFatal() {
		VmPolicy.Builder vmPolicyBuilder = (VmPolicy.Builder)getPolicyBuilder();
		ThreadPolicy.Builder threadPolicyBuilder = (ThreadPolicy.Builder)getThreadPolicyBuilder();
		if (null==vmPolicyBuilder || null==threadPolicyBuilder) { return; }
		StrictMode.setVmPolicy( vmPolicyBuilder.penaltyDeath().build() );
		StrictMode.setThreadPolicy( threadPolicyBuilder.penaltyDeath().build() );
	}

}
