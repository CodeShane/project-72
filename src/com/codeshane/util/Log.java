/* Log is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved. */

package com.codeshane.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Properties;

/** A drop-in replacement for {@code android.util.Log} and {@code System.out}
 * that allows use of either verbage (sans the "system.out") in either environment.
 * A stub file of android.util.Log is required for Java at compile-time unless
 * it is already on the compiler classpath.
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Jun 8, 2013
 * @version 3
 * @see java.io.PrintStream
 * @see java.io.PrintWriter
 * @see java.io.StringWriter
 * @see java.net.UnknownHostException
 * @see java.util.Properties
 * @see android.util.LogPrinter
 * @see android.util.Log
 */
public class Log {

	public static final String	TAG	= Log.class.getName();

	/* Priorities */
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;

    protected static int err_pri_level = 6;
    protected static boolean use_logcat = false;
    {
    	Properties s = System.getProperties();
    	use_logcat = s.getProperty("java.version", "1.5").equalsIgnoreCase("0") ||
    			s.getProperty("java.vm.name", "").equalsIgnoreCase("Dalvik");
    }

    public static int v(String tag, String msg) {
        return println(VERBOSE, tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return println(VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int d(String tag, String msg) {
        return println(DEBUG, tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return println(DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int i(String tag, String msg) {
        return println(INFO, tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return println(INFO, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int w(String tag, String msg) {
        return println(WARN, tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return println(WARN, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int w(String tag, Throwable tr) {
        return println(WARN, tag, getStackTraceString(tr));
    }

    public static int e(String tag, String msg) {
        return println(ERROR, tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return println(ERROR, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int println(String msg) {
    	System.out.println(msg);
    	return msg.length();
    }

	public static int println (int priority, String tag, String message ) {
		if (use_logcat) {
			android.util.Log.println(priority, tag, message);
			return message.length();
		} else {
			message = mergeMessage(tag,message);

			if (priority >= err_pri_level) {
				System.err.println(message);
				return message.length();
			} else {
				System.out.println(tag+" "+message);
				return message.length();
			}
		}
	}

	public static void setUseLogcat(boolean uselogcat){
		use_logcat = uselogcat;
	}

	public static String mergeMessage ( String tag, String message ) {
		return new StringBuffer(tag).append(' ').append(message).toString();
	}

	public static int println ( PrintStream buffer, int priority, String tag, String message ) {
		buffer.println(message);
		return message.length();
	}

	private Log() {}

	/* This notice applies only to the following method:
	 * "public static String getStackTraceString(Throwable tr)"
	 *
	 * Copyright (C) 2006 The Android Open Source Project
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *      http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */
    /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     * @notice Copyright (C) 2006 The Android Open Source Project
     * @license Apache License, Version 2.0
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

}
