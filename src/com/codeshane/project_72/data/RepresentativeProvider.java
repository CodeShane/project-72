package com.codeshane.project_72.data;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;

import com.codeshane.project_72.data.RepresentativeContent.RepresentativeTable;

/** ContentProvider for the Representatives table.
 *
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 19, 2013
 * @version 1
 */
public final class RepresentativeProvider extends ContentProvider {

    public static final String AUTHORITY = "com.codeshane.project_72.Representatives";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final String DATABASE_NAME = "RepresentativesDb";
	public static final Uri REP_URI = Uri.withAppendedPath(
			RepresentativeProvider.AUTHORITY_URI, RepresentativeContent.RepresentativeTable.TABLE_NAME);

    // Version 1 : Creation of the database
    public static final int DATABASE_VERSION = 1;

    private static final UriMatcher sUriMatcher;
    static {
    	sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    	sUriMatcher.addURI(AUTHORITY, RepresentativeContent.RepresentativeTable.TABLE_NAME, 0);
    	sUriMatcher.addURI(AUTHORITY, RepresentativeContent.RepresentativeTable.TABLE_NAME+"/#", 0);
    }
    private enum UriType {
        REPRESENTATIVE(RepresentativeTable.TABLE_NAME, RepresentativeTable.TABLE_NAME, RepresentativeTable.TYPE_ELEM_TYPE),
        REPRESENTATIVE_ID(RepresentativeTable.TABLE_NAME + "/#", RepresentativeTable.TABLE_NAME, RepresentativeTable.TYPE_DIR_TYPE);

        private String mTableName;
        private String mType;

        UriType(String matchPath, String tableName, String type) {
            mTableName = tableName;
            mType = type;
            sUriMatcher.addURI(AUTHORITY, matchPath, ordinal());
        }

        String getTableName() {
            return mTableName;
        }

        String getType() {
            return mType;
        }
    }

    static {
        // Ensures UriType is initialized
        UriType.values();
    }

    private static UriType matchUri(Uri uri) {
        int match = sUriMatcher.match(uri);
        if (match < 0) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return UriType.class.getEnumConstants()[match];
    }

    private SQLiteDatabase mDatabase;

    /** Gets the cached database or loads it again.
     * @param context
     * @return SQLiteDatabase
     * @see SQLiteOpenHelper#getWritableDatabase()
     * */
    @SuppressWarnings("deprecation")
    public synchronized SQLiteDatabase getDatabase(Context context) {
        // Return the cached database (if available.)
        if (mDatabase == null || !mDatabase.isOpen()) {
            DatabaseHelper helper = new DatabaseHelper(context, DATABASE_NAME);
            mDatabase = helper.getWritableDatabase();
            if (mDatabase != null) {
                mDatabase.setLockingEnabled(true);
            }
        }

        return mDatabase;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context, String name) {
            super(context, name, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            RepresentativeTable.createTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            RepresentativeTable.upgradeTable(db, oldVersion, newVersion);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase(context);
        String id;

        int result = -1;

        switch (uriType) {
            case REPRESENTATIVE_ID:
                id = uri.getPathSegments().get(1);
                result = db.delete(uriType.getTableName(), whereWithId(selection),
                        addIdToSelectionArgs(id, selectionArgs));
                break;
            case REPRESENTATIVE:
                result = db.delete(uriType.getTableName(), selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return matchUri(uri).getType();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase(context);
        long id;

        Uri resultUri;

        switch (uriType) {
            case REPRESENTATIVE:
                id = db.insert(uriType.getTableName(), "foo", values);
                resultUri = id == -1 ? null : ContentUris.withAppendedId(uri, id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Notify with base uri (not the unwatched new uri)
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = getDatabase(getContext());
        db.beginTransaction();
        try {
            int numOperations = operations.size();
            ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
                db.yieldIfContendedSafely();
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase(context);

        int numberInserted = 0;
        SQLiteStatement insertStmt;

        db.beginTransaction();
        try {
            switch (uriType) {
                case REPRESENTATIVE:
                    insertStmt = db.compileStatement(RepresentativeTable.getBulkInsertString());
                    for (ContentValues value : values) {
                        RepresentativeTable.bindValuesInBulkInsert(insertStmt, value);
                        insertStmt.execute();
                        insertStmt.clearBindings();
                    }
                    insertStmt.close();
                    db.setTransactionSuccessful();
                    numberInserted = values.length;

                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } finally {
            db.endTransaction();
        }

        // Notify with base uri (not the unwatched new uri)
        context.getContentResolver().notifyChange(uri, null);
        return numberInserted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        Cursor c = null;
        UriType uriType = matchUri(uri);
        Context context = getContext();

        SQLiteDatabase db = getDatabase(context);
        String id;

        switch (uriType) {
            case REPRESENTATIVE_ID:
                id = uri.getPathSegments().get(1);
                c = db.query(uriType.getTableName(), projection, whereWithId(selection),
                        addIdToSelectionArgs(id, selectionArgs), null, null, sortOrder);
                break;
            case REPRESENTATIVE:
                c = db.query(uriType.getTableName(), projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
        }

        if ((c != null) && !isTemporary()) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    private String whereWithId(String selection) {
        StringBuilder sb = new StringBuilder(256);
        sb.append(BaseColumns._ID);
        sb.append(" = ?");
        if (selection != null) {
            sb.append(" AND (");
            sb.append(selection);
            sb.append(')');
        }
        return sb.toString();
    }

    private String[] addIdToSelectionArgs(String id, String[] selectionArgs) {

        if (selectionArgs == null) {
            return new String[] { id };
        }

        int length = selectionArgs.length;
        String[] newSelectionArgs = new String[length + 1];
        newSelectionArgs[0] = id;
        System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, length);
        return newSelectionArgs;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        SQLiteDatabase db = getDatabase(context);

        int result = -1;

        switch (uriType) {
            case REPRESENTATIVE_ID:
                String id = uri.getPathSegments().get(1);
                result = db.update(uriType.getTableName(), values, whereWithId(selection),
                    addIdToSelectionArgs(id, selectionArgs));
                break;
            case REPRESENTATIVE:
                result = db.update(uriType.getTableName(), values, selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public boolean onCreate() {
        return true;
    }
}
