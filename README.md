# BookLand
A simple app to track inventory of a bookstore

This app was created as a final assignment of Android Basics Google and Udacity online course. 

## Overview

The app contains activities and/or fragments for the user to:

* Add Inventory
* See Product Details
* Edit Product Details
* See a list of all inventory from a Main Activity
* Uses SQLite database to track the inventory


## Detailed description

The app contains CatalogActivity and EditorActivity.

When there is no information to display in the database, the layout displays a TextView with instructions
on how to populate the database.

![bookland_mainactivity](https://user-images.githubusercontent.com/36896406/45930715-e043b000-bf63-11e8-8f51-5d9adedb38cc.png)

Useer can either insert dummy data or enter a new book manually.

![bookland_mainpage_insert](https://user-images.githubusercontent.com/36896406/45930716-e043b000-bf63-11e8-8251-e8d3fe3a0241.png)

![bookland_add_new_book](https://user-images.githubusercontent.com/36896406/45930708-df128300-bf63-11e8-8767-8f2d40486c42.png)

The entered information is then verified. 

![bookland_add_new_book_verification](https://user-images.githubusercontent.com/36896406/45930709-df128300-bf63-11e8-9726-4a836b8f4259.png)

Dialogs are displayed to confirm exiting the EditorActivity without clicking on the save button.

![bookland_bookdetails_dialog_screen](https://user-images.githubusercontent.com/36896406/45930710-dfab1980-bf63-11e8-9ecf-ea33e0558bc4.png)

Each list item also contains a <SaleButton> that reduces the total quantity of that particular product by one 
(include logic so that no negative quantities are displayed). The new quanity is updated in the database.

![bookland_buy_button](https://user-images.githubusercontent.com/36896406/45930712-dfab1980-bf63-11e8-9c79-51d9ce01d690.png)

It is possible to access book details from CatalogActivity and Edit the entered information, as well as to contact the supplier
via an intent to a phone app using the Supplier Phone Number stored in the database.
The Product Detail Layout also contains buttons that increase and decrease the available quantity displayed.
The code verifies that no negative quantities are displayed (zero is the lowest amount).

![bookland_bookdetails_fragment](https://user-images.githubusercontent.com/36896406/45930711-dfab1980-bf63-11e8-834b-bd8739e39f15.png)
