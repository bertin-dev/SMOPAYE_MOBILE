package com.ezpass.smopaye_mobile.DBLocale_Notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ezpass.smopaye_mobile.NotifFragmentModel.Item;

import java.util.ArrayList;
import java.util.HashMap;


public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "smopaye_db_notif";
    private static final String TABLE_Users = "userdetails";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NOTIFICATION_STATUT = "notification"; // 0 = pas encore vu  && 1 = vues
    private static final String KEY_IMAGE = "image_notification";
    private static final String KEY_DATE = "dateEnreg";


    public DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_NOTIFICATION_STATUT + " TEXT," + KEY_IMAGE + " TEXT," + KEY_DATE + " TEXT " + ")";
        //String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_DATE + " TEXT " + ")";
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
    public void insertUserDetails(String titre, String description, String etat_notification, int img_notif, String dateEnregistrement){
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_TITLE, titre);
        cValues.put(KEY_DESCRIPTION, description);
        cValues.put(KEY_NOTIFICATION_STATUT, etat_notification);
        cValues.put(KEY_IMAGE, img_notif);
        cValues.put(KEY_DATE, dateEnregistrement);
        //cValues.put(KEY_DESG, designation);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_Users,null, cValues);
        db.close();
    }



    // Get User Details
    /*public ArrayList<HashMap<String, String>> GetUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT title, description, image_notification, dateEnreg FROM "+ TABLE_Users + " ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("title",cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
            user.put("description",cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
            user.put("image_notification",cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
            user.put("dateEnreg",cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            userList.add(user);
        }
        return  userList;
    }*/


    public ArrayList<Item> listAllNotification(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Item> notif_list = new ArrayList<>();
        String query = "SELECT * FROM "+ TABLE_Users + " ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){

            String id = cursor.getString(cursor.getColumnIndex(KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
            String thumbnail = cursor.getString(cursor.getColumnIndex(KEY_IMAGE));
            String price = cursor.getString(cursor.getColumnIndex(KEY_DATE));
            notif_list.add(new Item(id, name, description, Integer.parseInt(thumbnail), price));
        }
        cursor.close();
        return  notif_list;
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

    // Update Statut Notifications
    public void UpdateNumNotification(String notification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_NOTIFICATION_STATUT, notification);
        int count = db.update(TABLE_Users, cVals, null, null);
    }


    // Delete ALL Notifications
    public void DeleteAllNotifications(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Users, "", null);
        db.close();
    }

    // Delete One Notification
    public void DeleteOneNotification(int userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Users, KEY_ID + " = ?", new String[]{String.valueOf(userid)});
        db.close();
    }




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
    public int UpdateUserDetails(String titre, String description, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(KEY_TITLE, titre);
        cVals.put(KEY_DESCRIPTION, description);
        int count = db.update(TABLE_Users, cVals, KEY_ID+" = ?",new String[]{String.valueOf(id)});
        return  count;
    }
}
