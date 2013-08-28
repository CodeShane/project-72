/* DbHelper is part of a CodeShane™ solution.
 * Copyright © 2013 Shane Ian Robinson. All Rights Reserved.
 * See LICENSE file or visit codeshane.com for more information. */

package com.codeshane.representing.providers;

import static com.codeshane.util.DbUtils.execSqlAsTransaction;
import static com.codeshane.util.Tables.createTableStatement;
import static com.codeshane.util.Tables.dropTableStatement;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTransactionListener;
import android.util.Log;

import com.codeshane.representing.meta.Table;

/**
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 22, 2013
 * @version 1
 */
public class RepsDatabaseHelper extends SQLiteOpenHelper {
	public static final String	TAG	= RepsDatabaseHelper.class.getPackage().getName() + "." + RepsDatabaseHelper.class.getSimpleName();

	public static final String DATABASE_NAME = RepsContract.DATABASE_NAME;

	public static final int DATABASE_VERSION = 6;

	RepsDatabaseHelper ( Context context ) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/** No longer needed as CursorLoaders handle closing cursors. */
//	private final ArrayList<Cursor> mCursors = new ArrayList<Cursor>();

	private static final ArrayList<Table> mTables;
	static {
		mTables = new ArrayList<Table>();
		mTables.add(new RepsContract.Representatives());
	}

	/** Synchronized access only. Get via {@link RepsDatabaseHelper#getDatabase(Context)} only. */
//	private SQLiteDatabase mDatabase = null;

    /** Gets the cached database or loads it again.
     * @param context
     * @return SQLiteDatabase
     * @see SQLiteOpenHelper#getWritableDatabase()
     * */
//    @SuppressWarnings("deprecation")
//    public synchronized SQLiteDatabase getDatabase(Context context) {
//        // Return the cached database (if available.)
//        if (mDatabase == null || !mDatabase.isOpen()) {
//            mDatabase = getWritableDatabase();
//        }
//        Log.i(TAG,"mDatabase==null?"+(null==mDatabase));
//        Log.i(TAG,"mDatabase.isOpen()"+mDatabase.isOpen());
//
//        return mDatabase;
//    }

    /** Called only once in application life to create and populate the initial database. */
	@Override public void onCreate ( SQLiteDatabase db ) {
		StringBuilder sb = new StringBuilder();
		for (Table table : mTables) {
			if(!table.onCreation(db)){
				sb = createTableStatement(table, sb);
			}
		}
		Log.v(TAG,"Transaction: "+sb.toString());
		execSqlAsTransaction(sb.toString(), db);
	}

	public static final SQLiteTransactionListener transactionListener = new SQLiteTransactionListener() {
		@Override public void onBegin () { Log.w(TAG,"Begin Sqlite transaction."); }
		@Override public void onCommit () { Log.w(TAG,"Successful Sqlite transaction."); }
		@Override public void onRollback () { Log.e(TAG,"Create tables - Rollback Sqlite transaction."); }
	};

	/** Give the table the opportunity to upgrade itself. If it returns true, the
	 * table will be deleted and re-created.
	 * */
	@Override public void onUpgrade ( SQLiteDatabase db, int oldVersion, int newVersion ) {
		for (Table table : mTables) {
			StringBuilder sb = new StringBuilder();
			if (table.onUpgrade(db, oldVersion, newVersion)) {
				// TODO simple migrations - Upgrades should be handled in a more realistic fashion, not just drop & replace...
				sb = dropTableStatement(table, sb);
				sb = createTableStatement(table, sb);
			}
			Log.v(TAG,"Transaction: "+sb.toString());
			execSqlAsTransaction(sb.toString(), db);
		}
	}

	@Override public void onOpen ( SQLiteDatabase db ) {}

	/* This is all handled by CursorLoaders now.
	// Used to Close Cursors, MySqlDatabase, and DbHelper in that specific (correct) order.
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
	}*/
}
