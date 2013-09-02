/* UriMapper is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import java.util.ArrayList;

import android.net.Uri;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Sep 1, 2013
 * @version 1
 */ //TODO wip
class UriMapper implements UriConverter {
	public static final String	TAG	= UriMapper.class.getSimpleName();

//	Map<UriPartRelation,Pair<Part>> map = new HashMap<UriPartRelation,Pair<Part>>();

	ArrayList<UriPartMapping> mappings = new ArrayList<UriPartMapping>();

	class UriPartMapping {
		UriPartMapping (UriPart local, UriMappingMethod method, UriPart remote){

		}
	}

	interface UriMappingMethod {

		static UriMappingMethod Default = new UriMappingMethod(){};
	}

	enum UriPart {
		SCHEME(),
		ENCODED_AUTHORITY(),
		AUTHORITY(),
		ENCODED_PATH(),
		PATH(),
		ENCODED_QUERY(),
		QUERY(),
		ENCODED_FRAGMENT(),
		FRAGMENT();

		String get(Uri uri){
			switch(UriPart.this){
				case ENCODED_FRAGMENT:	return uri.getEncodedFragment();
				case ENCODED_PATH:		return uri.getEncodedPath();
				case ENCODED_QUERY:		return uri.getEncodedQuery();
				case FRAGMENT:			return uri.getFragment();
				case ENCODED_AUTHORITY: return uri.getEncodedAuthority();
				case AUTHORITY:			return uri.getAuthority();
				case PATH:				return uri.getPath();
				case QUERY:				return uri.getQuery();
				case SCHEME:			return uri.getScheme();
				default:				return null;
			}
		}

		void put(Uri.Builder build, String value){
			switch(UriPart.this){
				case ENCODED_FRAGMENT:	build.encodedFragment(value);
				case ENCODED_PATH:		build.encodedPath(value);
				case ENCODED_QUERY:		build.encodedQuery(value);
				case FRAGMENT:			build.fragment(value);
				case ENCODED_AUTHORITY: build.encodedAuthority(value);
				case AUTHORITY:			build.authority(value);
				case PATH:				build.path(value);
				case QUERY:				build.query(value);
				case SCHEME:			build.scheme(value);
				default:
			}
		}
	}

	void foo(){
		Uri a = Uri.parse("");

		a.isHierarchical();
		a.isOpaque();
		a.isRelative();
		a.isAbsolute();

//		a.getScheme();
		a.getSchemeSpecificPart();
		a.getEncodedSchemeSpecificPart();
//		a.getAuthority();
		a.getEncodedAuthority();
//		a.getUserInfo();
		a.getEncodedUserInfo();
//		a.getHost();
//		a.getPort();
		a.getPath();
		a.getEncodedPath();
		a.getQuery();
		a.getEncodedQuery();
		a.getFragment();
		a.getEncodedFragment();
		a.getPathSegments();
		a.getLastPathSegment();
	}

	/** @see com.codeshane.util.UriConverter#asRemote(android.net.Uri) */
	@Override public Uri asRemote ( Uri uri ) {
		Uri.Builder build = Uri.EMPTY.buildUpon();


		return build.build();
	}

	/** @see com.codeshane.util.UriConverter#asLocal(android.net.Uri) */
	@Override public Uri asLocal ( Uri uri ) {
		return null;

	}


}
