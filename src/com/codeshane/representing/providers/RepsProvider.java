package com.codeshane.representing.providers;

import static android.content.ContentResolver.CURSOR_DIR_BASE_TYPE;
import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;
import static com.codeshane.representing.providers.RepsContract.AUTHORITY;
import static com.codeshane.representing.providers.RepsContract.DATABASE_NAME;
import static com.codeshane.representing.providers.RepsContract.Representatives.TABLE_NAME;
import static com.codeshane.util.DbUtils.addColumnToSelectionArgs;
import static com.codeshane.util.DbUtils.whereColumn;
import static com.codeshane.util.DbUtils.whereWithId;
import static com.codeshane.util.Tables.bindValuesInBulkInsert;
import static com.codeshane.util.Tables.bulkInsertStatement;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
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

import com.codeshane.representing.C;
import com.codeshane.representing.Representing;
import com.codeshane.representing.meta.Table;
import com.codeshane.representing.providers.RepsContract.Representatives.Columns;
import com.codeshane.representing.rest.RestIntentService;
import com.codeshane.util.UriConverter;
/** This ContentProvider serves the Representatives table.
 *
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 19, 2013
 * @version 1
 */
public final class RepsProvider extends ContentProvider implements RepsContract {

	public static final String TAG = RepsProvider.class.getName();

//	private Table mTable;
    private RepsDatabaseHelper dbHelper;
    /** SqliteDbOpenHelper. Retrieve SQLiteDatabase the via getDatabase() */

//    SQLiteDatabase getDatabase() {
//    	Log.i(TAG,"getDatabase()");
//    	if (null==dbHelper){
//    		Log.e(TAG,"dbHelper was null!");
//    		dbHelper = new RepsDatabaseHelper(this.getContext());
//    	}
//    	return dbHelper.getDatabase(getContext());
//    }

    private static final UriConverter whoIsMyRep = new UriConverter(){
    	private static final String AUTHORITY_REMOTE = "whoismyrepresentative.com";
		@Override public Uri asRemote ( Uri uri ) {

	    	Log.i(TAG,"asRemote()");
			UriType uriType = RepsProvider.matchUri(uri);

			Uri.Builder build = new Uri.Builder();
			build.scheme(C.SCHEME_HTTP);
			build.authority(AUTHORITY_REMOTE);

			List<String> segments = uri.getPathSegments();

			int qtyParams = segments.size();
			if (qtyParams<2 || qtyParams>3 ) {
				throw new RuntimeException("Invalid number of segments. There should be 2 or 3, found "+qtyParams+".");
			}

			String part = segments.get(0).concat(".php");
			build.appendEncodedPath(part);

			// Now get the name, zip, or state:
			part = segments.get(1);

			switch (uriType) {
				case MEMS_BY_ZIP:
					String zip = segments.get(1);
					int zl = zip.length();
					if (zl==5 || zl==9) {
						build.appendQueryParameter("zip", zip.substring(0, 5));
					} else if (zl==9) {
						build.appendQueryParameter("zip", zip.substring(0, 5));
						build.appendQueryParameter("zip4", zip.substring(5, 9));
					} else {
						throw new IllegalArgumentException("ZIP malformed");
					}
					break;
				case REP_BY_NAME:
				case SEN_BY_NAME:
					build.appendQueryParameter("name", part);
					break;
				case REP_BY_STATE:
				case SEN_BY_STATE:
					build.appendQueryParameter("state", part);
					break;
				default:
					break;
			}
			build.appendQueryParameter("output", "json");
			return build.build();
		}

		@Override public Uri asLocal ( Uri uri ) {
			return null;
		}
    };

	/** @see android.content.ContentProvider#onCreate() */
	@Override public boolean onCreate () {
		Log.d(TAG,"onCreate()");
		dbHelper = new RepsDatabaseHelper(this.getContext());
		return true;
	}

    public static final int DATABASE_VERSION = 1;

    public static enum UriType {
//        ALL(AUTHORITY, "/", "members", MIME_CURSOR_DIR, "ALL"),
        // com.example.authority.Provider/table/123
    	A("a", "/", "", ContentResolver.CURSOR_DIR_BASE_TYPE, TABLE_NAME),
        REPS(AUTHORITY.toString(), "/", "", ContentResolver.CURSOR_DIR_BASE_TYPE, TABLE_NAME),
        REPS_BY_ID(AUTHORITY.toString(), "/#", "", CURSOR_ITEM_BASE_TYPE, "REP_BY_ID"),
        MEMS_BY_ZIP(AUTHORITY.toString(),"getall_mems/#", "getall_mems", CURSOR_DIR_BASE_TYPE, TABLE_NAME),
        REP_BY_NAME(AUTHORITY,"getall_reps_byname/*", "getall_reps_byname", CURSOR_DIR_BASE_TYPE, TABLE_NAME),
        REP_BY_STATE(AUTHORITY,"getall_reps_bystate/*", "getall_reps_bystate", CURSOR_DIR_BASE_TYPE, TABLE_NAME),
        SEN_BY_NAME(AUTHORITY,"getall_sens_byname/*", "getall_sens_byname", CURSOR_DIR_BASE_TYPE, TABLE_NAME),
        SEN_BY_STATE(AUTHORITY,"getall_sens_bystate/*", "getall_sens_bystate", CURSOR_DIR_BASE_TYPE, TABLE_NAME);

        private String mAuthority;
        private String mMatchPath;
        private String mPathPart;
        private String mType;
        private String mTable;

        UriType(String authority, String matchPath, String pathPart, String type, String table) {
        	// enum.ordinal()			// identifier
	    	Log.i(TAG,"new UriType()");
        	mAuthority = authority;
        	mMatchPath = matchPath;
        	mPathPart = pathPart;
            mType = type+C.VND+table;
            mTable = table;
        }
		public String getAuthority() { return mAuthority; }
		public String getMatchPath () { return mMatchPath; }
		public String getPathPart() { return mPathPart; }
		public String getType() { return mType; }
		public String getTableName() { return mTable; }
		/** @since Aug 28, 2013
		 * @version Aug 28, 2013
		 * @return Table
		 */
		public Table getTable () {
			return null;

		}
	}


    /** For resolving URI lookups by table (optionally by ID)*/
    public static final UriMatcher sUriMatcher;
    static {
    	UriMatcher t = new UriMatcher(UriMatcher.NO_MATCH);
    	for (UriType u: UriType.values()){
        	t.addURI(u.getAuthority(), u.getMatchPath(), u.ordinal());
        }
    	sUriMatcher = t;
    }

    /** Classify a URI into a UriType, which determines how it is handled. */
    public static final UriType matchUri(Uri uri) {
    	Log.i(TAG,"birthing a dragon");
        int match = sUriMatcher.match(uri);
        if (match < 0) {
//            throw new IllegalArgumentException("Unknown URI " + uri.toString());
            //XXX For production:
        	Log.e(TAG, "Unknown URI "+uri.toString());
            return UriType.REPS;
        }
        return UriType.class.getEnumConstants()[match];
    }

    /** Request the REST client update the database for the given query URI.
     * */
    private void requestRestUpdate(Uri uri){
        SharedPreferences prefs = Representing.prefs();
        Log.i(TAG,"requestRestUpdate");

        Uri remoteUri = whoIsMyRep.asRemote(uri);

        //TODO Separate this to enable forcing
        if (prefs.getLong("lastUpdate", 0)+3600000<System.currentTimeMillis()){
        	Context context = Representing.context();
        	Intent requestLatest = new Intent(Intent.ACTION_DEFAULT)
        	.putExtra(RestIntentService.EXTRA_URI_LOCAL, uri) // Include the parcelable Uri
        	.putExtra(RestIntentService.EXTRA_URI_REMOTE, remoteUri) // Include the parcelable Uri
        	.setClass(context, RestIntentService.class);
        	context.sendBroadcast(requestLatest);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        UriType uriType = matchUri(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String id;

        int result = -1;

        switch (uriType) {
            case REP_BY_NAME:
                id = uri.getPathSegments().get(1);
                result = db.delete(uriType.getTableName(), "name = ?",
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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;

        Uri resultUri;

        switch (uriType) {
            case REP_BY_NAME:
            	id = db.insert(uriType.getTableName(), null, values);
                resultUri = (id == -1) ? null : ContentUris.withAppendedId(uri, id);
                break;
            default:
            	Log.i(TAG, "176 "+uri.toString());
            	return null;
        }

        // Notify with base uri (not the unwatched new uri)
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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

    @Override public int bulkInsert(Uri uri, ContentValues[] values) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int numberInserted = 0;
        SQLiteStatement insertStmt;

        db.beginTransaction();
        try {
            switch (uriType) {
                case REPS:
                	String tableName=uriType.getTableName();
                    insertStmt = db.compileStatement(bulkInsertStatement(uriType.getTable(), null).toString());
                    for (ContentValues value : values) {
                        bindValuesInBulkInsert(uriType.getTable(), insertStmt, value);
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

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        UriType uriType = matchUri(uri);

//		Normalize the request parameters by type
        switch (uriType) {
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
			case REPS:
			default:
        }


        /* This innocuous little line kicks off the REST download process, while enabling the provider to load and return from the database. */
    	//XXX this ran, even though the app crashed after //  requestRestUpdate(uri);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

//      Perform the database query
        Cursor c = db.query(uriType.getTableName(), projection, selection, selectionArgs, null, null, sortOrder);

        if ((c != null) && !isTemporary()) {
//        	this.dbHelper.mCursors.add(c);
        	// Register the cursor to watch the uri for changes
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;

    }

    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        UriType uriType = matchUri(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = -1;
        switch (uriType) {
            case REP_BY_NAME:
                String id = uri.getPathSegments().get(1);
                result = db.update(uriType.getTableName(), values, whereWithId(selection),
                    addColumnToSelectionArgs(id, selectionArgs));
                break;
                default:
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }


}

