package com.codeshane.project_72.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;

/** Table content data model.
 *
 * @author  Shane Ian Robinson <shane@codeshane.com>
 * @since   Aug 19, 2013
 * @version 1
 */
public abstract class RepresentativeContent {

    public static final Uri CONTENT_URI = Uri.parse("content://" + RepresentativeProvider.AUTHORITY);

	/** An array of sample (dummy) items. */
	public static List<RepItem>			ITEMS		= new ArrayList<RepItem>();

	/** A map of sample (dummy) items, by ID. */
	public static Map<String, RepItem>	ITEM_MAP	= new HashMap<String, RepItem>();
	static {
		// Add 3 sample items.
		addItem(new RepItem("0", "Sue Others", "Senior Seat", "DC", "123 Main. St. Suite 1, Awesome, DC", "R", "+1 800 555 1000", "http://others.example.com/"));
		addItem(new RepItem("1", "Bob Givens", "Senior Seat", "DC", "123 Main. St. Suite 2, Awesome, DC", "D", "+1 800 555 1001", "givens.example.com/"));
		addItem(new RepItem("2", "Just Awesome", "Junior Seat", "DC", "123 Main. St. Suite 3, Awesome, DC", "I", "+1 800 555 6592", "http://awesome.example.com/"));
	}

	private static void addItem ( RepItem item ) {
		ITEMS.add(item);
		ITEM_MAP.put(item.name, item);
	}

	/** A dummy item representing a piece of content. */
	public static class RepItem {
		public String id;
		public String name;
		public String district;
		public String state;
		public String office;
		/** @see RepItem */
		public RepItem ( String id, String name, String district, String state, String office, String party, String phone, String website ) {
			super();
			this.id = id;
			this.name = name;
			this.district = district;
			this.state = state;
			this.office = office;
			this.party = party;
			this.phone = phone;
			this.website = website;
		}
		/** @see java.lang.Object#toString() */
		@Override public String toString () {
			return "RepItem [id=" + id + ", name=" + name + ", district=" + district + ", state=" + state + ", office=" + office + ", party=" + party
				+ ", phone=" + phone + ", website=" + website + "]";
		}
		public String party;
		public String phone;
		public String website;
	}


    private RepresentativeContent() {
    }

    /** Representative table data model.
     * @author  Shane Ian Robinson <shane@codeshane.com>
     * @since Aug 19, 2013
     * @version 1
     */
    public static final class RepresentativeTable extends RepresentativeContent {

    	// aka CONTENT_PATH
        public static final String TABLE_NAME = "representative";
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/table-representative";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/table-representative";

        public static final Uri CONTENT_URI = Uri.parse(RepresentativeContent.CONTENT_URI + "/" + TABLE_NAME);

        public static enum Columns implements ColumnMetadata {
            ID(BaseColumns._ID, "integer", false),
            UPDATED("updated", "integer", false),
            NAME("name", "text", true),
            PARTY("party", "text", false),
            STATE("state", "text", true),
            ZIP("zip", "text", true),
            DISTRICT("district", "text", false),
            PHONE("phone", "text", false),
            OFFICE("office", "text", false),
            LINK("link", "text", false);

            private final String mName;
            private final String mType;
            private final boolean mIsIndexed;

            /** Private constructor for static enum initialization.
             * @param name The name for the column.
             * @param type The Sqlite type affinity for the column. */
            private Columns(String name, String type, boolean isIndexed) {
                mName = name;
                mType = type;
                mIsIndexed = isIndexed;
            }

            /** @see ColumnMetadata#getIndex() */
            @Override public int getIndex() {
                return ordinal();
            }

            /** @see ColumnMetadata#getName() */
            @Override public String getName() {
                return mName;
            }

            /** @see ColumnMetadata#getType() */
            @Override public String getType() {
                return mType;
            }

			/** @see com.codeshane.project_72.data.ColumnMetadata#isIndexed() */
			@Override public boolean isIndexed () {
				return mIsIndexed;
			}
        }

        /** Array of column names. */
        public static final String[] PROJECTION = new String[] {
                Columns.ID.getName(),
                Columns.UPDATED.getName(),
                Columns.NAME.getName(),
                Columns.PARTY.getName(),
                Columns.STATE.getName(),
                Columns.ZIP.getName(),
                Columns.DISTRICT.getName(),
                Columns.PHONE.getName(),
                Columns.OFFICE.getName(),
                Columns.LINK.getName()
        };

        private RepresentativeTable() {
            // No private constructor
        }

        public static void createTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Columns.ID.getName() + " " + Columns.ID.getType() + ", " + Columns.UPDATED.getName() + " " + Columns.UPDATED.getType() + ", " + Columns.NAME.getName() + " " + Columns.NAME.getType() + ", " + Columns.PARTY.getName() + " " + Columns.PARTY.getType() + ", " + Columns.STATE.getName() + " " + Columns.STATE.getType() + ", " + Columns.ZIP.getName() + " " + Columns.ZIP.getType() + ", " + Columns.DISTRICT.getName() + " " + Columns.DISTRICT.getType() + ", " + Columns.PHONE.getName() + " " + Columns.PHONE.getType() + ", " + Columns.OFFICE.getName() + " " + Columns.OFFICE.getType() + ", " + Columns.LINK.getName() + " " + Columns.LINK.getType() + ", PRIMARY KEY (" + Columns.ID.getName() + ")" + ");");
            for (Columns c: Columns.values()){
            	if (true==c.mIsIndexed) {
            		db.execSQL("CREATE INDEX rep_"+c.mName+" on "+TABLE_NAME+"("+c.mName+");");
            	}
            }
        }

        // Version 1 : Creation of the table
        public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 1) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
                createTable(db);
                return;
            }

            if (oldVersion != newVersion) {
                throw new IllegalStateException("Error upgrading the database to version " + newVersion);
            }

        }

        static String getBulkInsertString() {
            return new StringBuilder("INSERT INTO ").append(TABLE_NAME).append(" ( ").append(Columns.ID.getName()).append(", ").append(Columns.UPDATED.getName()).append(", ").append(Columns.NAME.getName()).append(", ").append(Columns.PARTY.getName()).append(", ").append(Columns.STATE.getName()).append(", ").append(Columns.ZIP.getName()).append(", ").append(Columns.DISTRICT.getName()).append(", ").append(Columns.PHONE.getName()).append(", ").append(Columns.OFFICE.getName()).append(", ").append(Columns.LINK.getName()).append(" ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)").toString();
        }

        static void bindValuesInBulkInsert(SQLiteStatement stmt, ContentValues values) {
            int i = 1;
            String value;
            stmt.bindLong(i++, values.getAsLong(Columns.ID.getName()));
            stmt.bindLong(i++, values.getAsLong(Columns.UPDATED.getName()));
            value = values.getAsString(Columns.NAME.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.PARTY.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.STATE.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.ZIP.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.DISTRICT.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.PHONE.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.OFFICE.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.LINK.getName());
            stmt.bindString(i++, value != null ? value : "");
        }
    }
}

