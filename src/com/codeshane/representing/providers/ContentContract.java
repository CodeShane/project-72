/* ContentContract is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.providers;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 28, 2013
 * @version 1
 */
@Deprecated // WIP // still considering interop advantages/disadvantages..
public interface ContentContract {

	/** Vendor should be a unique hierarchical full-stop delimited identifier for the entity responsible for the content.
	 * Example: "com.exanple" */
	String vendor();

	/** Authority should be a project-unique identifier for this contract, possibly hierarchical.
	 * This will typically be the table name.
	 * Examples: "content" "provider" "provider.news" "news" "News"
	 * */
	String name(); // was project

	/** Authority should be a hierarchical full-stop delimited identifier for the Content.
	 * Recommend a concatenation of vendor + "." + name;
	 * Example: "com.example.project" */
	String authority();

	/* to be continued .. */
}