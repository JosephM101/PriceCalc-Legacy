package com.josephm101.pricecalc;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DATAMODELS_LIST";
    public static final String DB_TABLE_NAME = "item";
    public static final String DB_ITEM_ID = "_id";
    public static final String DB_ITEM_NAME = "name";
    public static final String DB_ITEM_PRICE = "price";
    public static final String DB_ITEM_TAXABLE = "is_taxable";
    public static final String DB_ITEM_QUANTITY = "quantity";
    private static final int DATABASE_VERSION = 2;

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + DB_TABLE_NAME + " (" +
                DB_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DB_ITEM_NAME + " TEXT, " +
                DB_ITEM_PRICE + " TEXT, " +
                DB_ITEM_TAXABLE + " TEXT, " +
                DB_ITEM_QUANTITY + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}