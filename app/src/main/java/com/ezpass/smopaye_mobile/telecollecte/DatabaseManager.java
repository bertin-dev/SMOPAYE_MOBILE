package com.ezpass.smopaye_mobile.telecollecte;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Telecollecte.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseManager( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql = "create table recette ("
                + "    idRecette integer primary key autoincrement,"
                + "    idCarte text not null,"
                + "    montant integer not null,"
                + "    dateEnregistrement integer not null"
                + ")";
        db.execSQL( strSql );
        Log.i( "DATABASE", "methode onCreate invoquer" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //String strSql = "alter table T_Scores add column ...";
        String strSql = "drop table recette";
        db.execSQL( strSql );
        this.onCreate( db );
        Log.i( "DATABASE", "mise a jour invoquer" );
    }

    public void insertScore( String idcarte, int montant ) {
        idcarte = idcarte.replace( "'", "''" );
        String strSql = "insert  into recette (idCarte, montant, dateEnregistrement) values ('"
                + idcarte + "', " + montant + ", " + new Date().getTime() + ")";
        this.getWritableDatabase().execSQL( strSql );
        Log.i( "DATABASE", "insertion invoquer" );
    }

    public void viderdata(){

        String strSql = "DELETE FROM `recette` WHERE 1";
        this.getWritableDatabase().execSQL(strSql);
        Log.i( "DATABASE", "surpression  invoquer" );

    }

    public List<ScoreData> montantFinal(){
        List<ScoreData> montant =new ArrayList<>();
        String strSql = "select SUM(montant) as montantFinal from recette";
        Cursor cursor = this.getReadableDatabase().rawQuery( strSql, null );

        cursor.moveToFirst();
        while( ! cursor.isAfterLast() ) {
            ScoreData score = new ScoreData( cursor.getInt( 0 ) );

            montant.add(score);
        }
        return montant;
    }


    public List<ScoreData> readTop10() {
        List<ScoreData> recette = new ArrayList<>();

        // 1ère technique : SQL
        //String strSql = "select * from T_Scores order by score desc limit 10";
        //Cursor cursor = this.getReadableDatabase().rawQuery( strSql, null );

        // 2nd technique "plus objet"
        Cursor cursor = this.getReadableDatabase().query( "recette",
                new String[] { "idRecette", "idCarte", "SUM(montant)", "dateEnregistrement" },
                null, null, null, null, "montant desc", "10000000" );
        cursor.moveToFirst();
        while( ! cursor.isAfterLast() ) {
            ScoreData score = new ScoreData( cursor.getInt( 0 ), cursor.getString( 1 ),
                    cursor.getInt( 2 ), new Date( cursor.getLong( 3 ) ) );
            recette.add( score );
            cursor.moveToNext();
        }
        cursor.close();

        return recette;
    }



    public List<ScoreData> readAll() {
        List<ScoreData> recette = new ArrayList<>();

        // 1ère technique : SQL
        //String strSql = "select * from T_Scores order by score desc limit 10";
        //Cursor cursor = this.getReadableDatabase().rawQuery( strSql, null );

        // 2nd technique "plus objet"
        Cursor cursor = this.getReadableDatabase().query( "recette",
                new String[] { "idRecette", "idCarte","montant", "dateEnregistrement" },
                null, null, null, null, "idRecette desc", "10" );
        cursor.moveToFirst();
        while( ! cursor.isAfterLast() ) {
            ScoreData score = new ScoreData( cursor.getInt( 0 ), cursor.getString( 1 ),
                    cursor.getInt( 2 ), new Date( cursor.getLong( 3 ) ) );
            recette.add( score );
            cursor.moveToNext();
        }
        cursor.close();

        return recette;
    }

    public int nbrCarte() {
        int id = 0;
        // 1ère technique : SQL
        //String strSql = "select * from T_Scores order by score desc limit 10";
        //Cursor cursor = this.getReadableDatabase().rawQuery( strSql, null );

        // 2nd technique "plus objet"
        Cursor cursor = this.getReadableDatabase().query( "recette",
                new String[] { "count(idRecette)" },
                null, null, null, null, null );
        cursor.moveToFirst();
        while( ! cursor.isAfterLast() ) {
            id = cursor.getInt( 0 );
            cursor.moveToNext();
        }
        cursor.close();

        return id;
    }


}