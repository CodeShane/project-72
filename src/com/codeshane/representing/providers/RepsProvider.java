package com.codeshane.project_72.providers;

import static com.codeshane.project_72.model.RepresentativesTable.MIME_TABLE_DIR;
import static com.codeshane.project_72.model.RepresentativesTable.MIME_TABLE_ROW;
import static com.codeshane.project_72.model.RepresentativesTable.TABLE_NAME;
import static com.codeshane.util.DbUtils.bindValuesInBulkInsert;
import static com.codeshane.util.DbUtils.getBulkInsertString;
import static com.codeshane.util.Utils.addColumnToSelectionArgs;
import static com.codeshane.util.Utils.whereColumn;
import static com.codeshane.util.Utils.whereWithId;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.util.Log;

import com.codeshane.project_72.RepApplication;
import com.codeshane.project_72.meta.ProviderConstants;
import com.codeshane.project_72.meta.Table;
import com.codeshane.project_72.model.RepresentativesTable;
import com.codeshane.project_72.model.RepresentativesTable.Columns;
import com.codeshane.util.Restful;

//import static com.codeshane.util.Utils.*;

/** ContentProvider for the Representatives table.
 *
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 19, 2013
 * @version 1
 */
public final class RepsContentProvider extends ContentProvider implements Restful {

	public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.codeshane.project_72.io.RepresentativesProvider";
    public static final String DATABASE_NAME = "RepresentativesDb";
    static {
        Uri.parse("content://" + AUTHORITY + "/integrityCheck");
    }

    Table mTable = new RepresentativesTable();//new RepresentativesContent.RepresentativesTable(); //XXX
    DbHelper bHelper = new DbHelper(this.getContext(), DATABASE_NAME, 1, mTable);

//	public static final Uri REP_URI = Uri.withAppendedPath(
//			RepresentativesProvider.AUTHORITY_URI, RepresentativesContent.RepresentativesTable.TABLE_NAME);

    // Version 1 : Creation of the database
    public static final int DATABASE_VERSION = 1;

    /** For resolving URI lookups by table (optionally by ID)*/
    private static final UriMatcher sUriMatcher;
    static {
    	sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    	// com.example.authority.Provider/table/
    	sUriMatcher.addURI(ProviderConstants.CONTENT_SCHEME+AUTHORITY, RepresentativesTable.TABLE_NAME, 0);
    	// com.example.authority.Provider/table/123
    	sUriMatcher.addURI(AUTHORITY, RepresentativesTable.TABLE_NAME+"/#", 0);
    }

    private static enum UriType {
    	// com.example.authority.Provider/table/
        ALL(AUTHORITY, TABLE_NAME, MIME_TABLE_DIR),
        // com.example.authority.Provider/table/123
        REP_BY_ID(AUTHORITY, TABLE_NAME + "/#", MIME_TABLE_ROW),
        MEMS_BY_ZIP(AUTHORITY, TABLE_NAME+"/getall_mems_byzip/#", MIME_TABLE_DIR),
        MEMS_BY_ZIP4(AUTHORITY, TABLE_NAME+"/getall_mems_byzip/#", MIME_TABLE_DIR),
        REP_BY_NAME(AUTHORITY, TABLE_NAME+"/getall_reps_byname/*", MIME_TABLE_DIR),
        REP_BY_STATE(AUTHORITY, TABLE_NAME+"/getall_reps_bystate/*", MIME_TABLE_DIR),
        SEN_BY_NAME(AUTHORITY, TABLE_NAME+"/getall_sens_byname/*", MIME_TABLE_DIR),
        SEN_BY_STATE(AUTHORITY, TABLE_NAME+"/getall_sens_bystate/*", MIME_TABLE_DIR)
        ;

        private String mTableName; //XXX????
        private String mType;

        UriType(String matchPath, String tableName, String type) {
            mTableName = tableName; // path
            mType = type;           // type
            // enum.ordinal()			// identifier
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

        Log.i("XXX 113 ("+match+") uri := ",uri.toString());
        if (match < 0) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return UriType.class.getEnumConstants()[match];
    }

    private SQLiteDatabase mDatabase;

    private static DbHelper dbHelper=null;

    SQLiteDatabase getDatabase() {
    	return dbHelper.getDatabase(getContext());
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase();
        String id;

        int result = -1;

        switch (uriType) {
            case REP_BY_ID:
                id = uri.getPathSegments().get(1);
                result = db.delete(uriType.getTableName(), whereWithId(selection),
                        addColumnToSelectionArgs(id, selectionArgs));
                break;
			default:
				break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override public String getType(Uri uri) {
        return matchUri(uri).getType();
    }

    @Override public Uri insert(Uri uri, ContentValues values) {
        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase();
        long id;

        Uri resultUri;

        switch (uriType) {
            case REP_BY_ID:
            	id = db.insert(uriType.getTableName(), null, values);
                resultUri = (id == -1) ? null : ContentUris.withAppendedId(uri, id);
                break;
            case ALL:
                id = db.insert(uriType.getTableName(), null, values);
                resultUri = (id == -1) ? null : ContentUris.withAppendedId(uri, id);
                break;
            default:
            	Log.i("XXX 193",uri.toString());
            	resultUri=null;
                //XXX throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Notify with base uri (not the unwatched new uri)
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = getDatabase();
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
        SQLiteDatabase db = getDatabase();

        int numberInserted = 0;
        SQLiteStatement insertStmt;

        db.beginTransaction();
        try {
            switch (uriType) {
                case ALL:
                    insertStmt = db.compileStatement(getBulkInsertString(mTable));
                    for (ContentValues value : values) {
                        bindValuesInBulkInsert(mTable, insertStmt, value);
                        insertStmt.execute();
                        insertStmt.clearBindings();
                    }
                    insertStmt.close();
                    db.setTransactionSuccessful();
                    numberInserted = values.length;

                    break;

                default:
                    throw new IllegalArgumentException("bulkInsert : Unknown URI " + uri);
            }
        } finally {
            db.endTransaction();
        }

        // Notify with base uri (not the unwatched new uri)
        context.getContentResolver().notifyChange(uri, null);
        return numberInserted;
    }

    private static final Class<? extends Restful> restfulServiceClass = WhoIsMyRepRestClient.class;

    /** Request the REST client update the database for the given query URI.
     * */
    public void requestRestUpdate(Uri uri){
        Context context = getContext();

        SharedPreferences prefs = RepApplication.prefs();

        if (prefs.getLong("lastUpdate", 0)+3600000<System.currentTimeMillis()){
        	Intent requestLatest = new Intent(Intent.ACTION_DEFAULT)
        	.putExtra(Intent.EXTRA_STREAM, uri) // Include the parcelable Uri
        	.setClass(context, restfulServiceClass);
        	context.sendBroadcast(requestLatest);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor c = null;
        UriType uriType = matchUri(uri);
        SQLiteDatabase db = getDatabase();

//		Normalize the request parameters by type
        switch (uriType) {
			case REP_BY_ID:
				//XXX uri.withAppendedId?
				selection = whereWithId(selection);
				break;
			case MEMS_BY_ZIP4:
				//XXX
			case MEMS_BY_ZIP:
				selection = whereColumn(Columns.ZIP);
				break;
			case REP_BY_NAME:
			case SEN_BY_NAME:
				selection = whereColumn(Columns.NAME);
				break;
			case REP_BY_STATE:
			case SEN_BY_STATE:
				selection = whereColumn(Columns.STATE);
				break;
			case ALL:
			default:
        }

//      Perform the database query
//        c = managedQuery(uriType.getTableName(), projection, selection, selectionArgs, null, null, sortOrder);
        c = db.query(uriType.getTableName(), projection, selection, selectionArgs, null, null, sortOrder);

        if ((c != null) && !isTemporary()) {
        	this.bHelper.mCursors.add(c);
        	// Register the cursor to watch the uri for changes
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        UriType uriType = matchUri(uri);
        SQLiteDatabase db = getDatabase();
        int result = -1;
        switch (uriType) {
            case REP_BY_ID:
                String id = uri.getPathSegments().get(1);
                result = db.update(uriType.getTableName(), values, whereWithId(selection),
                    addColumnToSelectionArgs(id, selectionArgs));
                break;
                default:
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public boolean create() {
        return true;
    }

	/** @see android.content.ContentProvider#onCreate() */
	@Override public boolean onCreate () {
		return false;
	}

}
