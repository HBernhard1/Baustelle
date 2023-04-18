package com.example.baustelle.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.Observable;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.baustelle.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "Baustelle.db";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "Mitarbeiter";

    // below variable is for our id column.
    private static final String ID_COL = "_ID";

    // below variable is for our course name column
    private static final String NAME_COL = "name";

    // below variable id for our course duration column.
    private static final String DURATION_COL = "duration";

    // below variable for our course description column.
    private static final String DESCRIPTION_COL = "description";

    // below variable is for our course tracks column.
    private static final String TRACKS_COL = "tracks";

    // creating a constructor for our database handler.
    public DBHandler(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            boolean OK = CreateAndSyncData(db);
        }
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        boolean OK = CreateAndSyncData(db);
    }

    public void updateZeit(String id, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id.length() > 0) {
            db.insert("ZeitErf", null, values);
        }
        else {
            int u = db.update("ZeitErf", values, "_ID=?", new String[]{id});
        }

    }

    // this method is use to add new course to our sqlite database.
    public void addNewCourse(String courseName, String courseDuration, String courseDescription, String courseTracks) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, courseName);
        values.put(DURATION_COL, courseDuration);
        values.put(DESCRIPTION_COL, courseDescription);
        values.put(TRACKS_COL, courseTracks);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String getUser(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT Full_Name FROM users WHERE Email=?";
        String[] select = {user};

        try {
            Cursor c = db.rawQuery(query,select);
            if (c.getCount() > 0 ) {
                String fullname = c.getColumnName(0);
                c.close();
                return fullname;
            }
            else {
                c.close();
                return "";
            }
        }
        catch (SQLException sql) {
            Log.println(Log.ERROR,"sql", sql.toString());
            return "";
        }
    }

    public boolean CreateAndSyncData(SQLiteDatabase db) {
//        db = this.getWritableDatabase();
        String query;
        try {
            query = "CREATE TABLE IF NOT EXISTS " + "Users" + "  ("
                    + "_ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Email" + " TEXT,"
                    + "Full_Name" + " TEXT,"
                    + "Adresse" + " TEXT)";
            db.execSQL(query);

            query = "CREATE TABLE IF NOT EXISTS " + "ZeitErf" + "  ("
                    + "_ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Baustelle" + " TEXT,"
                    + "Mitarbeiter" + " TEXT,"
                    + "ZeitSOLL" + " DECIMAL(4,2),"
                    + "ZeitIST" + " DECIMAL(4,2))";
            db.execSQL(query);

            query = "Update Zeiterf set Mitarbeiter='Susi' WHERE Mitarbeiter IS NULL ";
            db.execSQL(query);

            query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NAME_COL + " TEXT,"
                    + DURATION_COL + " TEXT,"
                    + DESCRIPTION_COL + " TEXT,"
                    + TRACKS_COL + " TEXT)";
            db.execSQL(query);

            Cursor c = db.rawQuery("SELECT * FROM Users", null);
            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put("Email", "bernhard@edv-hofer.com");
                values.put("Full_Name", "Bernhard Hofer");
                values.put("Adresse", "Siberweg 4");
                db.insert("Users", null, values);
                c.close();
            }
        }
        catch (SQLException sql) {
            Log.println(Log.DEBUG, "sql", sql.toString());
        }

        db.close();
        return true;
    }

    public Cursor fetchMit() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM Mitarbeiter ";
        String[] select = {};
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
            int anzRec = c.getCount();
            if ( c.getCount() > 0) {
                Log.d("Anz Rec", "Count = " + c.getCount() );
            }
        } catch (SQLException sql) {
            Log.d("sql", sql.toString());
        }
        return c;
    }

    public Cursor fetchZeit(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM ZeitErf";
        if (!id.isEmpty()) query += " WHERE ID=" + id;
        String[] select = {};
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
            int anzRec = c.getCount();
            if ( c.getCount() > 0) {
                Log.d("Anz Rec Zeiterfassung", "Count = " + c.getCount() );
            }
        } catch (SQLException sql) {
            Log.d("sql", sql.toString());
        }
        return c;
    }

    @SuppressLint("Range")
    public List<String> queryMA(String szId) {
        SQLiteDatabase db = getWritableDatabase();
        List<String> list = new ArrayList<String>() {};

        Cursor c1 = db.rawQuery("SELECT * FROM Mitarbeiter", null);
        if (c1.moveToFirst()) {
            do {
                list. add(c1.getString(c1.getColumnIndex("name")));
            }  while(c1.moveToNext());
        }

        c1.close();
        db.close();
        return list; //<<<<< Note should check the returned Card for null
    }
}
