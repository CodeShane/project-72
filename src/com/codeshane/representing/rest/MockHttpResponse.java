/* MockHttpResponse is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.rest;

import java.io.UnsupportedEncodingException;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

/** Used to simulate an {@code HttpResponse} with {@code StringEntity} containing json text.
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Sep 1, 2013
 * @version 1
 */
public class MockHttpResponse extends BasicHttpResponse {
	/** @see MockHttpResponse */
	private static final StatusLine mStatusLine = new StatusLine(){
		@Override public ProtocolVersion getProtocolVersion () { return new ProtocolVersion("http", 1, 1); }
		@Override public String getReasonPhrase () { return "Because I said so."; }
		@Override public int getStatusCode () { return 200; }
	};
	public MockHttpResponse () {
		super(mStatusLine);
	}
	@Override public StringEntity getEntity () {
		StringEntity t = null;
		try {
			t = new StringEntity("{ \"results\": [{\"name\": \"Austin Scott\", \"party\": \"R\", \"state\": \"GA\", \"district\": \"8\", \"phone\": \"202-225-6531\", \"office\": \"516 Cannon House Office Building\", \"link\": \"http://austinscott.house.gov\" }, {\"name\": \"Saxby Chambliss\", \"party\": \"R\", \"state\": \"GA\", \"district\": \"Senior Seat\", \"phone\": \"202-224-3521\", \"office\": \"416 Russell Senate Office Building\", \"link\": \"http://www.chambliss.senate.gov\" }, {\"name\": \"John Isakson\", \"party\": \"R\", \"state\": \"GA\", \"district\": \"Junior Seat\", \"phone\": \"202-224-3643\", \"office\": \"131 Russell Senate Office Building\", \"link\": \"http://www.isakson.senate.gov\" }]}");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return t;
	}
}
