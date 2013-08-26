/* UriConverter is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import android.net.Uri;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 25, 2013
 * @version 1
 */
public interface UriConverter {
	Uri asRemote(Uri uri);
	Uri asLocal(Uri uri);
}
