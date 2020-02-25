package com.ezpass.smopaye_mobile.DBLocale_Notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DbUser extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "smopaye_dbUser";
    private static final String TABLE_Users = "utilisateur";
    private static final String KEY_ID = "id";
    private static final String KEY_NOM = "nom";
    private static final String KEY_PRENOM = "prenom";
    private static final String KEY_SEXE = "sexe";
    private static final String KEY_TEL = "tel";
    private static final String KEY_CNI = "cni";
    private static final String KEY_SESSION = "session";
    private static final String KEY_ADRESSE = "adresse";
    private static final String KEY_ID_CARTE = "id_carte";
    private static final String KEY_TYPEUSER = "typeUser";
    private static final String KEY_IMAGEURL = "imageURL";
    private static final String KEY_STATUS = "status";
    private static final String KEY_ABONNEMENT = "abonnement";
    private static final String KEY_DATE = "dateEnreg";


    public DbUser(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NOM + " TEXT," + KEY_PRENOM + " TEXT," + KEY_SEXE+ " TEXT,"
                + KEY_TEL + " TEXT, " + KEY_CNI + " TEXT, " + KEY_SESSION + " TEXT, " + KEY_ADRESSE + " TEXT, " + KEY_ID_CARTE + " TEXT, "
                + KEY_TYPEUSER + " TEXT, " + KEY_IMAGEURL + " TEXT, " + KEY_STATUS + " TEXT, " + KEY_ABONNEMENT + " TEXT, "
                + KEY_DATE + " TEXT " + ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Users);
        // Create tables again
        onCreate(db);
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //

    // Adding new User Details
    public void insertInfoUser(String nom1, String prenom1, String sexe1,
                               String tel1, String cni1, String session1,
                               String adresse1, String id_carte1, String typeUser1,
                               String imageURL1, String status1, String abonnement1, String dateEnregistrement){
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_NOM, nom1);
        cValues.put(KEY_PRENOM, prenom1);
        cValues.put(KEY_SEXE, sexe1);
        cValues.put(KEY_TEL, tel1);
        cValues.put(KEY_CNI, cni1);
        cValues.put(KEY_SESSION, session1);
        cValues.put(KEY_ADRESSE, adresse1);
        cValues.put(KEY_ID_CARTE, id_carte1);
        cValues.put(KEY_TYPEUSER, typeUser1);
        cValues.put(KEY_IMAGEURL, imageURL1);
        cValues.put(KEY_STATUS, status1);
        cValues.put(KEY_ABONNEMENT, abonnement1);
        cValues.put(KEY_DATE, dateEnregistrement);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_Users,null, cValues);
        db.close();
    }


    // Get Number Notifications
    public String GetLastNumberInsertBD(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(id), tel FROM "+ TABLE_Users;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String number= cursor.getString(1);
        cursor.close();
        return  number;
    }

/*
    // Get User Details
    public ArrayList<HashMap<String, String>> GetUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT title, description, dateEnreg FROM "+ TABLE_Users + " ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("title",cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            user.put("description",cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
            user.put("dateEnreg",cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            userList.add(user);
        }
        return  userList;
    }



    // Get Number Notifications
    public String GetNumNotifications(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM "+ TABLE_Users + " WHERE notification = 0";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String count= cursor.getString(0);
        cursor.close();
        return  count;
    }

    // Update User Details
    public void UpdateNumNotification(String notification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_NOTIFICATION_STATUT, notification);
        int count = db.update(TABLE_Users, cVals, null, null);
    }*/



    // Get User Details based on userid
  /*  public ArrayList<HashMap<String, String>> GetUserByUserId(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, location, designation FROM "+ TABLE_Users;
        Cursor cursor = db.query(TABLE_Users, new String[]{KEY_NAME, KEY_LOC, KEY_DESG}, KEY_ID+ "=?",new String[]{String.valueOf(userid)},null, null, null, null);
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("name",cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.put("designation",cursor.getString(cursor.getColumnIndex(KEY_DESG)));
            user.put("location",cursor.getString(cursor.getColumnIndex(KEY_LOC)));
            userList.add(user);
        }
        return  userList;
    }
    // Delete User Details
    public void DeleteUser(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Users, KEY_ID+" = ?",new String[]{String.valueOf(userid)});
        db.close();
    }*/


    // Update User Details
   /* public int UpdateUserDetails(String titre, String description, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_TITLE, titre);
        cVals.put(KEY_DESCRIPTION, description);
        int count = db.update(TABLE_Users, cVals, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        return  count;
    }*/
}
