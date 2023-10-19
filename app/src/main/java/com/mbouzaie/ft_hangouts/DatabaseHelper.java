package com.mbouzaie.ft_hangouts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, "database.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE contacts(" +
                "_id INTEGER PRIMARY KEY," +
                "full_name TEXT," +
                "phone TEXT," +
                "email TEXT," +
                "street TEXT," +
                "postal_code TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(sqLiteDatabase);
    }

    public void createContact(Contact contact) {
        SQLiteDatabase  sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("full_name", contact.getFullName());
        contentValues.put("phone", contact.getPhone());
        contentValues.put("email", contact.getEmail());
        contentValues.put("street", contact.getStreet());
        contentValues.put("postal_code", contact.getPostalCode());
        long result = sqLiteDatabase.insert("contacts", null, contentValues);
        if (result == -1)
            Log.v(this.getClass().getName(), "Failed to add contact");
        else
            Log.v(this.getClass().getName(), "added contact");
    }

    public Cursor getAllContact() {
        String q = "SELECT * FROM contacts";
        SQLiteDatabase  sqLiteDatabase = this.getReadableDatabase();

        return sqLiteDatabase.rawQuery(q, null);
    }

    public void updateContact(Contact contact) {
        SQLiteDatabase  sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.v(this.getClass().getName(), "Name : " + contact.getFullName() + "-Phone : " + contact.getPhone() +  "-Email : " + contact.getEmail() +  "-Street : " + contact.getStreet() + "-Postal : " + contact.getPostalCode());
        contentValues.put("full_name", contact.getFullName());
        contentValues.put("phone", contact.getPhone());
        contentValues.put("email", contact.getEmail());
        contentValues.put("street", contact.getStreet());
        contentValues.put("postal_code", contact.getPostalCode());
        Log.v("CV : ", contentValues.toString());
        long res = sqLiteDatabase.update("contacts", contentValues, "_id=?", new String[]{String.valueOf(contact.getId())});
        if (res == -1)
            Log.v(this.getClass().getName(), "Failed to update contact");
        else
            Log.v(this.getClass().getName(), "contact updated");
    }

    public void deleteContact(String row_id) {
        SQLiteDatabase  db = this.getWritableDatabase();
        long res = db.delete("contacts", "_id=?", new String[]{row_id});
        if (res == -1)
            Log.v(this.getClass().getName(), "Failed to delete contact");
        else
            Log.v(this.getClass().getName(), "contact deleted");
    }
}
