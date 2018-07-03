package com.example.android.bookland.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_PRICE;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static com.example.android.bookland.data.BookContract.BookEntry.CONTENT_ITEM_TYPE;
import static com.example.android.bookland.data.BookContract.BookEntry.CONTENT_LIST_TYPE;
import static com.example.android.bookland.data.BookContract.BookEntry.TABLE_NAME;
import static com.example.android.bookland.data.BookContract.BookEntry._ID;

/**
 * Created by Marina on 01.07.2018.
 */

public class BookProvider extends ContentProvider {
    //Tag for the log messages
    private static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    private static final UriMatcher myUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        myUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        myUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    BookDbHelper myDbHelper;

    @Override
    public boolean onCreate() {
        myDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase database = myDbHelper.getReadableDatabase();

        //Cursor that holds the result of the query
        Cursor resultCursor;

        //matching URI to a specific code
        int match = myUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                resultCursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = _ID + "=?"; //the selection argument will be a String array containing the actual ID
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                resultCursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown URI " + uri);
        }
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri); //set notification uri on the cursor in case data changes at this URI so that the cursor is updated
        return resultCursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = myUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return CONTENT_LIST_TYPE;
            case BOOK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = myUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        //Check that  the name is not null
        String name = values.getAsString(COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product name is required");
        }

        Integer quantity = values.getAsInteger(COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity is not valid");
        }

        String supplier = values.getAsString(COLUMN_SUPPLIER_NAME);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier name is required");
        }

        Integer price = values.getAsInteger(COLUMN_PRICE);
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price is not valid");
        }

        String phone = values.getAsString(COLUMN_SUPPLIER_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Phone is required");
        }

        SQLiteDatabase database = myDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = myDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = myUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                getContext().getContentResolver().notifyChange(uri, null);
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = myUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                getContext().getContentResolver().notifyChange(uri, null);
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product name is required");
            }
        }

        if (values.containsKey(COLUMN_PRICE)) {
            Integer price = values.getAsInteger(COLUMN_PRICE);
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("Price is not valid");
            }
        }

        if (values.containsKey(COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Quantity is not valid");
            }
        }

        if (values.containsKey(COLUMN_SUPPLIER_NAME)) {
            String supplier = values.getAsString(COLUMN_SUPPLIER_NAME);
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier name is required");
            }
        }

        if (values.containsKey(COLUMN_SUPPLIER_PHONE)) {
            String phone = values.getAsString(COLUMN_SUPPLIER_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("Phone is required");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = myDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

}
