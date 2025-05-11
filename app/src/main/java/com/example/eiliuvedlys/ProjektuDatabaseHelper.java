package com.example.eiliuvedlys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjektuDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "projektai.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_PROJEKTAI = "projektai";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PAVADINIMAS = "pavadinimas";
    private static final String COLUMN_EILE = "eile";
    private static final String COLUMN_PASTABA = "pastaba";
    private static final String COLUMN_IMAGE = "imageResId";
    private static final String COLUMN_IMAGE_URI = "imageUri";

    private static final String TABLE_SEKIMAS = "sekimas";
    private static final String TABLE_PRIMINIMAI = "priminimai";

    private Context context;

    public ProjektuDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROJEKTAI_TABLE = "CREATE TABLE " + TABLE_PROJEKTAI + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PAVADINIMAS + " TEXT,"
                + COLUMN_EILE + " INTEGER,"
                + COLUMN_PASTABA + " TEXT,"
                + COLUMN_IMAGE + " INTEGER,"
                + COLUMN_IMAGE_URI + " TEXT)";
        db.execSQL(CREATE_PROJEKTAI_TABLE);

        String CREATE_SEKIMAS_TABLE = "CREATE TABLE " + TABLE_SEKIMAS + " ("
                + "id INTEGER PRIMARY KEY, "
                + "eile INTEGER)";
        db.execSQL(CREATE_SEKIMAS_TABLE);

        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("eile", 0);
        db.insert(TABLE_SEKIMAS, null, values);

        String CREATE_PRIMINIMAI_TABLE = "CREATE TABLE " + TABLE_PRIMINIMAI + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "projektoId INTEGER,"
                + "eile INTEGER,"
                + "tekstas TEXT)";
        db.execSQL(CREATE_PRIMINIMAI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJEKTAI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEKIMAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRIMINIMAI);
        onCreate(db);
    }

    public long pridetiProjekta(Projektas projektas) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAVADINIMAS, projektas.getPavadinimas());
        values.put(COLUMN_EILE, projektas.getEile());
        values.put(COLUMN_PASTABA, projektas.getPastaba());
        values.put(COLUMN_IMAGE, projektas.getImageResId());
        values.put(COLUMN_IMAGE_URI, projektas.getImageUri());
        long id = db.insert(TABLE_PROJEKTAI, null, values);
        db.close();
        return id;
    }

    public List<Projektas> gautiVisusProjektus() {
        List<Projektas> projektai = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROJEKTAI, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Projektas projektas = new Projektas();
                projektas.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                projektas.setPavadinimas(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAVADINIMAS)));
                projektas.setEile(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EILE)));
                projektas.setPastaba(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASTABA)));
                projektas.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)));
                projektas.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)));
                projektai.add(projektas);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return projektai;
    }

    public Projektas gautiProjekta(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROJEKTAI, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Projektas projektas = new Projektas();
            projektas.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            projektas.setPavadinimas(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAVADINIMAS)));
            projektas.setEile(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EILE)));
            projektas.setPastaba(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASTABA)));
            projektas.setImageResId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)));
            projektas.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)));
            cursor.close();
            return projektas;
        }

        return null;
    }

    public void atnaujintiProjekta(Projektas projektas) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAVADINIMAS, projektas.getPavadinimas());
        values.put(COLUMN_EILE, projektas.getEile());
        values.put(COLUMN_PASTABA, projektas.getPastaba());
        values.put(COLUMN_IMAGE, projektas.getImageResId());
        values.put(COLUMN_IMAGE_URI, projektas.getImageUri());
        db.update(TABLE_PROJEKTAI, values, COLUMN_ID + " = ?", new String[]{String.valueOf(projektas.getId())});
        db.close();
    }

    public void istrintiProjekta(int id) {
        Projektas projektas = gautiProjekta(id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROJEKTAI, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        if (projektas != null && projektas.getImageUri() != null) {
            Uri uri = Uri.parse(projektas.getImageUri());
            File failas = new File(uri.getPath());
            if (failas.exists()) failas.delete();
        }
    }

    public int gautiEilesSekima() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT eile FROM " + TABLE_SEKIMAS + " WHERE id = 1", null);
        int eile = 0;
        if (cursor.moveToFirst()) {
            eile = cursor.getInt(0);
        }
        cursor.close();
        return eile;
    }

    public void nustatytiEilesSekima(int eile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("eile", eile);
        db.insertWithOnConflict(TABLE_SEKIMAS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void pridetiPriminima(Priminimas p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("projektoId", p.getProjektoId());
        values.put("eile", p.getEile());
        values.put("tekstas", p.getTekstas());
        db.insert(TABLE_PRIMINIMAI, null, values);
        db.close();
    }

    public List<Priminimas> gautiPriminimusProjekto(int projektoId) {
        List<Priminimas> sarasas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRIMINIMAI, null, "projektoId=?",
                new String[]{String.valueOf(projektoId)}, null, null, "eile ASC");

        if (cursor.moveToFirst()) {
            do {
                Priminimas p = new Priminimas();
                p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                p.setProjektoId(cursor.getInt(cursor.getColumnIndexOrThrow("projektoId")));
                p.setEile(cursor.getInt(cursor.getColumnIndexOrThrow("eile")));
                p.setTekstas(cursor.getString(cursor.getColumnIndexOrThrow("tekstas")));
                sarasas.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sarasas;
    }

    public void istrintiPriminima(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRIMINIMAI, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void atnaujintiPriminima(Priminimas p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("eile", p.getEile());
        values.put("tekstas", p.getTekstas());
        db.update(TABLE_PRIMINIMAI, values, "id=?", new String[]{String.valueOf(p.getId())});
        db.close();
    }
}