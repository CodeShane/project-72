/* Views is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

/** Utility class for working with views.
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 29, 2013
 * @version 1
 */
public class Views {
	public static final String	TAG	= Views.class.getPackage().getName() + "." + Views.class.getSimpleName();

	/** Alternate the view's opacity between 80% and 100%.
	 * <i>Convenience method for {@link Views#setSubdued(View, float, boolean)}</i>
	 * @since Aug 29, 2013
	 * @version 1
	 * @android 11
	 * @return True if the operation was executed.
	 * @see Views#swapSubdued(View, float)
	 * @see Views#setSubdued(View, float, boolean)
	 */
	@TargetApi ( Build.VERSION_CODES.HONEYCOMB )
	public static final boolean swapSubdued (View v) {
		return swapSubdued(v, 0.8f);
	}

	/** Alternate the view's opacity between 80% and 100%.
	 * <i>Convenience method for {@link Views#setSubdued(View, float, boolean)}</i>
	 * @since Aug 29, 2013
	 * @version 1
	 * @android 11
	 * @return True if the operation was executed.
	 * @see Views#swapSubdued(View)
	 * @see Views#setSubdued(View, float, boolean)
	 */
	@TargetApi ( Build.VERSION_CODES.HONEYCOMB )
	public static final boolean swapSubdued ( View v, float alpha ) {
		return setSubdued(v,alpha,(v.getAlpha()==alpha));
	}

	/** Alternate the view's opacity between 80% and 100%.
	 * @since Aug 29, 2013
	 * @version 1
	 * @android 11
	 * @return True if the operation was executed.
	 * @see Views#swapSubdued(View)
	 * @see Views#swapSubdued(View, float)
	 */
	@TargetApi ( Build.VERSION_CODES.HONEYCOMB )
	public static final boolean setSubdued ( View v, float alpha, boolean subdued ) {
		if (Build.VERSION.SDK_INT > 11){
			v.setAlpha((subdued)?alpha:1f);
		}
		return false;
	}

}
