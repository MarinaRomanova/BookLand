package com.example.android.bookland;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.android.bookland.data.BookContract.BookEntry;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_PRICE;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static com.example.android.bookland.data.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static com.example.android.bookland.data.BookContract.BookEntry._ID;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;
    Button orderButton;
    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri myCurrentBookUri;
    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean bookHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the bookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            view.setFocusableInTouchMode(true);
            return false;
        }
    };
    private EditText myNameEditText;
    private EditText myPriceEditText;
    private EditText myQuantityEditText;
    private EditText mySupplierEditText;
    private EditText myPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        orderButton = (Button) findViewById(R.id.order_button);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        myCurrentBookUri = intent.getData();
        if (myCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            orderButton.setVisibility(View.GONE);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.editor_activity_title_book_details));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        myNameEditText = (EditText) findViewById(R.id.edit_book_name);
        myNameEditText.setFocusable(false);
        myPriceEditText = (EditText) findViewById(R.id.edit_price);
        myPriceEditText.setFocusable(false);
        myQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        myQuantityEditText.setFocusable(false);
        mySupplierEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mySupplierEditText.setFocusable(false);
        myPhoneEditText = (EditText) findViewById(R.id.edit_phone);
        myPhoneEditText.setFocusable(false);

        myNameEditText.setOnTouchListener(mTouchListener);
        myQuantityEditText.setOnTouchListener(mTouchListener);
        myPriceEditText.setOnTouchListener(mTouchListener);
        mySupplierEditText.setOnTouchListener(mTouchListener);
        myPhoneEditText.setOnTouchListener(mTouchListener);
    }

    private void saveBook() {
        //Read from input fields
        String nameString = myNameEditText.getText().toString().trim();
        String priceString = myPriceEditText.getText().toString().trim();
        String quantityString = myQuantityEditText.getText().toString().trim();
        String supplierString = mySupplierEditText.getText().toString().trim();
        String phoneString = myPhoneEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (myCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(phoneString)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        ContentValues values = new ContentValues();
        //Verifies that entered information is valid before updating the database
        if (!TextUtils.isEmpty(nameString)) {
            values.put(BookEntry.COLUMN_PRODUCT_NAME, nameString);
        } else {
            myNameEditText.setError(getString(R.string.name_error));
            requestFocus(myNameEditText);
        }
        if (!TextUtils.isEmpty(priceString)&&(Integer.parseInt(priceString) >0)) {
            int priceInt = Integer.parseInt(priceString);
            values.put(BookEntry.COLUMN_PRICE, priceInt);
        } else {
            myPriceEditText.setError(getString(R.string.price_error));
            requestFocus(myPriceEditText);
        }
        if (!TextUtils.isEmpty(quantityString)) {
            int quantityInt = Integer.parseInt(quantityString);
            values.put(BookEntry.COLUMN_QUANTITY, quantityInt);
        } else {
            values.put(BookEntry.COLUMN_QUANTITY, 0);
        }
        if (!TextUtils.isEmpty(supplierString)) {
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierString);
        } else {
            mySupplierEditText.setError(getString(R.string.supplier_error));
            requestFocus(mySupplierEditText);
        }
        if (!TextUtils.isEmpty(phoneString)) {
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE, phoneString);
        } else {
            myPhoneEditText.setError(getString(R.string.phone_error));
            requestFocus(myPhoneEditText);
        }

        // Determine if this is a new or existing book by checking if myCurrentBookUri is null or not
        if (myCurrentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider, returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (!(newUri == null)) {
                Toast.makeText(this, getString(R.string.editor_save_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: myCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because myCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(myCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected != 0) {
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        if (!TextUtils.isEmpty(nameString) && !TextUtils.isEmpty(priceString) &&(Integer.parseInt(priceString) >0) &&
                !TextUtils.isEmpty(supplierString) && !TextUtils.isEmpty(phoneString)){
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (myCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (myCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the content URI already identifies the book.
            int rowsDeleted = getContentResolver().delete(myCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID, COLUMN_PRODUCT_NAME, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_SUPPLIER_NAME, COLUMN_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, myCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            // Update the views on the screen with the values from the database
            myNameEditText.setText(name);
            myPriceEditText.setText(Integer.toString(price));
            myQuantityEditText.setText(Integer.toString(quantity));
            mySupplierEditText.setText(supplier);
            myPhoneEditText.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        myNameEditText.setText("");
        myPriceEditText.setText("");
        myQuantityEditText.setText("");
        mySupplierEditText.setText("");
        myPhoneEditText.setText("");
    }

    public void callSupplier(View view) {
        EditText phoneInput = (EditText) findViewById(R.id.edit_phone);
        String phoneString = phoneInput.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneString));
        startActivity(intent);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
