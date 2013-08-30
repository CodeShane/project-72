/* Device is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import android.os.Build;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Jun 24, 2013
 * @version 3
 */
public final class Device {
	public static final String	TAG	= Device.class.getPackage().getName() + "." + Device.class.getSimpleName();

	public static final boolean SUPPORTS_API_01_BASE_1_0					=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE;
	public static final boolean SUPPORTS_API_02__BASE_1_1					=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE_1_1;
	public static final boolean SUPPORTS_API_03__CUPCAKE_1_5				=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE;
	public static final boolean SUPPORTS_API_04_DONUT_1_6					=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT;
	public static final boolean SUPPORTS_API_05_ECLAIR_2_0					=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
	public static final boolean SUPPORTS_API_06__ECLAIR_2_0_1				=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1;
	public static final boolean SUPPORTS_API_07__ECLAIR_2_1					=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
	public static final boolean SUPPORTS_API_08_FROYO_2_2					=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	public static final boolean SUPPORTS_API_09_GINGERBREAD_2_3				=	Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	public static final boolean SUPPORTS_API_10_GINGERBREAD_2_3_3			=	Build.VERSION.SDK_INT >= 0xa; // Build.VERSION_CODES.GINGERBREAD_MR1;
	public static final boolean SUPPORTS_API_11_HONEYCOMB_3_0				=	Build.VERSION.SDK_INT >= 0xb; // Build.VERSION_CODES.HONEYCOMB;
	public static final boolean SUPPORTS_API_12__HONEYCOMB_3_1				=	Build.VERSION.SDK_INT >= 0xc; // Build.VERSION_CODES.HONEYCOMB_MR1;
	public static final boolean SUPPORTS_API_13__HONEYCOMB_3_2				=	Build.VERSION.SDK_INT >= 0xd; // Build.VERSION_CODES.HONEYCOMB_MR2;
	public static final boolean SUPPORTS_API_14_ICE_CREAM_SANDWICH_4_0		=	Build.VERSION.SDK_INT >= 0xe; // Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	public static final boolean SUPPORTS_API_15__ICE_CREAM_SANDWICH_4_0_3	=	Build.VERSION.SDK_INT >= 0xf; // Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
	public static final boolean SUPPORTS_API_16_JELLY_BEAN_4_1				=	Build.VERSION.SDK_INT >= 0x10; // Build.VERSION_CODES.JELLY_BEAN;
	public static final boolean SUPPORTS_API_17__JELLY_BEAN_4_2				=	Build.VERSION.SDK_INT >= 0x11; // Build.VERSION_CODES.JELLY_BEAN_MR1;

	/**
	 * @return java.io.File.separator
	 * @default 	/
	 * */
	public static final String getFileSeparator() { return System.getProperty("file.separator"); }
	/**
	 * @return System class path
	 * @default 		.
	 * */
	public static final String getJavaClasspath() { return System.getProperty("java.class.path"); }
	/**
	 * @return Location of the VM on the file system
	 * @default /system
	 * */
	public static final String getJavaHome() { return System.getProperty("java.home"); }
	/**
	 * @return See java.io.File.createTempFile
	 * @default /sdcard
	 * */
	public static final String getJavaIoTmpdir() { return System.getProperty("java.io.tmpdir"); }
	/**
	 * @return JNI libraries Search path
	 * @default /system/lib
	 * */
	public static final String getJavaLibrary() { return System.getProperty("java.library.path"); }
	/**
	 * @return Human-readable VM vendor
	 * @default The Android Project
	 * */
	public static final String getJavaVendor() { return System.getProperty("java.vendor"); }
	/**
	 * @return VM vendor's web site URL
	 * @default http://www.android.com/
	 * */
	public static final String getJavaVendorUrl() { return System.getProperty("java.vendor.url"); }
	/**
	 * @return VM libraries
	 * @default 			version 0.9
	 * */
	public static final String getJavaSpecificationVersion() { return System.getProperty("java.specification.version"); }
	/**
	 * @return VM libraries vendor
	 * @default 		The Android Project
	 * */
	public static final String getJavaSpecificationVendor() { return System.getProperty("java.specification.vendor"); }
	/**
	 * @return VM libraries name
	 * @default 		Dalvik Core Library
	 * */
	public static final String getJavaSpecificationName() { return System.getProperty("java.specification.name"); }
	/**
	 * @return VM implementation version
	 * @default 1.2.0
	 * */
	public static final String getJavaVmVersion() { return System.getProperty("java.vm.version"); }
	/**
	 * @return VM implementation vendor
	 * @default The Android Project
	 * */
	public static final String getJavaVmVendor() { return System.getProperty("java.vm.vendor"); }
	/**
	 * @return VM implementation name
	 * @default 	Dalvik
	 * */
	public static final String getJavaVmName() { return System.getProperty("java.vm.name"); }
	/**
	 * @return VM specification version
	 * @default 0.9
	 * */
	public static final String getJavaVmSpecificationVersion() { return System.getProperty("java.vm.specification.version"); }
	/**
	 * @return VM specification vendor
	 * @default 	The Android Project
	 * */
	public static final String getJavaVmSpecificationVendor() { return System.getProperty("java.vm.specification.vendor"); }
	/**
	 * @return VM specification name
	 * @default 	Dalvik Virtual Machine Specification
	 * */
	public static final String getJavaVmSpecificationName() { return System.getProperty("java.vm.specification.name"); }
	/**
	 * @return The system line separator
	 * @default \n
	 * */
	public static final String getLineSeparator() { return System.getProperty("line.separator"); }
	/**
	 * @return OS architecture
	 * @default 			armv7l
	 * */
	public static final String getOsArch() { return System.getProperty("os.arch"); }
	/**
	 * @return OS (kernel) name
	 * @default 		Linux
	 * */
	public static final String getOsName() { return System.getProperty("os.name"); }
	/**
	 * @return OS (kernel)
	 * @default version 2.6.32.9-g103d848
	 * */
	public static final String getOsVersion() { return System.getProperty("os.version"); }
	/**
	 * @return See java.io.File.pathSeparator
	 * @default
	 * */
	public static final String getPathSeparator() { return System.getProperty("path.separator"); }
	/**
	 * @return Base of non-absolute paths
	 * @default /
	 * */
	public static final String getUserDir() { return System.getProperty("user.dir"); }

}
