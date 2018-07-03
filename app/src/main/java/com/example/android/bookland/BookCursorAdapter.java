package com.example.android.bookland;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_PRICE;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_QUANTITY;

/**
 * Created by Marina on 02.07.2018.
 */

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor cursor) {

        super(context, cursor, 0);
    }

    //A new blank list item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //Binds book data
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextVIew = (TextView) view.findViewById(R.id.price);

        //find the columns of required book's attributes
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);

        // Extract properties from cursor
        String name = cursor.getString(nameColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        String price = cursor.getString(priceColumnIndex);

        // Populate fields with extracted properties
        nameTextView.setText(name);
        priceTextVIew.setText(context.getString(R.string.eur_currency) +" " + price);

        int quantityInt = Integer.parseInt(quantity);

        switch (quantityInt) {
            case 1:
                quantityTextView.setText(R.string.one_is_left);
                break;
            case 0:
                quantityTextView.setText(R.string.sold_out);
                break;
            default:
                quantityTextView.setText(quantity + " " + context.getString(R.string.in_stock));
        }
    }
}
