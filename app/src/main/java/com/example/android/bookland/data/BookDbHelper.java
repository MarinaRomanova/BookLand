package com.example.android.bookland.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.bookland.data.BookContract.BookEntry;
import static com.example.android.bookland.data.BookContract.BookEntry.TABLE_NAME;
import static com.example.android.bookland.data.BookContract.BookEntry._ID;

/**
 * Created by Marina on 03.06.2018.
 * BookDbHelper create/update a SQLite database books.db using BookContract
 */

public class BookDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
        Log.v(LOG_TAG,SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void sellBook (long id, int quantity) {
        if (quantity >= 1){
            quantity -=1;
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, quantity);
        }
    }
}
