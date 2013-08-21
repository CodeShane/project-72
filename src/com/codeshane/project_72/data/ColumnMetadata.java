package com.codeshane.project_72.data;

public interface ColumnMetadata {

	/** @return int index of the Column. */
    public int getIndex();

    /** @return String the Column name. */
    public String getName();

    /** @return the Column type.*/
    public String getType();

    /** @return true if the column is indexed */
    public boolean isIndexed();
}
