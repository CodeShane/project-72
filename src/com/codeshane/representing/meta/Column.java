/* Column is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.meta;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 21, 2013
 * @version 1
 */
public interface Column {

	/** @return int index of the Column. */
    public int getIndex();

    /** @return String the Column name. */
    public String getName();

    /** @return the Column sqlite type.*/
    public String getType();

    /** @return true if the column is indexed */
    public boolean isIndexed();
}
