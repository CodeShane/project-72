/* DbHelper is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.providers;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codeshane.representing.meta.Table;

import static com.codeshane.util.Utils.*;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 1
 */
public class RepsDbHelper extends SQLiteOpenHelper {
	public static final String	TAG	= RepsDbHelper.class.getPackage().getName() + "." + RepsDbHelper.class.getSimpleName();

	RepsDbHelper ( Context context, String name, int version, Table mTable ) {
		super(context, name, null, version);
		this.mTables.add(mTable);
	}

	ArrayList<Cursor> mCursors = new ArrayList<Cursor>();

	ArrayList<Table> mTables = new ArrayList<Table>();

	/** Synchronized access only. Get via {@link RepsDbHelper#getDatabase(Context)} only. */
	private SQLiteDatabase mDatabase = null;

    /** Gets the cached database or loads it again.
     * @param context
     * @return SQLiteDatabase
     * @see SQLiteOpenHelper#getWritableDatabase()
     * */
    @SuppressWarnings("deprecation")
    public synchronized SQLiteDatabase getDatabase(Context context) {
        // Return the cached database (if available.)
        if (mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = getWritableDatabase();
            if (mDatabase != null) {
                mDatabase.setLockingEnabled(true);
            }
        }
        return mDatabase;
    }

    /** Give the table the opportunity to create itself. If it returns true,
     * the table will will be ignored.
     * */
	@Override public void onCreate ( SQLiteDatabase db ) {
		for (Table t : mTables) {
			if (!t.create(db)) {
				createTable(db, t);
			};
		}
	}

	/** Give the table the opportunity to upgrade itself. If it returns true, the
	 * table will be deleted and re-created.
	 * */
	@Override public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
		for (Table t : mTables) {
			if (t.upgrade(db, oldVersion, newVersion)) {
				upgradeTable(db,t,oldVersion, newVersion);
			}
		}
	}

	@Override public void onOpen ( SQLiteDatabase db ) {}

	/** Closes Cursors, MySqlDatabase, and DbHelper in that specific (correct) order.
	 * */
	@Override public synchronized void close () {
		if (null != mCursors) {
			for (Cursor c : mCursors) {
				try {
					c.close();
				} catch (Exception e) {
					Log.e(TAG, "c.close() ex: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		if (null != mDatabase) {
				try {
					mDatabase.close();
					mDatabase = null;
				} catch (Exception e) {
					Log.e(TAG, "mDatabase.close() ex: " + e.getMessage());
					e.printStackTrace();
				}
		}
		try {
			super.close();
		} catch (Exception e) {
			Log.e(TAG, "dbhelper.close() ex: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
