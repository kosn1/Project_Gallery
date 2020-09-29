package gr.uth.displayphotosv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="Gallery.db";
    private static DatabaseHelper mInstance = null;

    //FILE TABLE
    private static final String FILE_TABLE="file";
    private static final String FILE_ID ="file_id";
    private static final String FILE_PATH ="file_path";

    //TAG TABLE
    private static final String TAG_TABLE="tag";
    private static final String TAG_ID ="tag_id";
    private static final String TAG_NAME ="tag_name";

    //TAGofPHOTO TABLE
    private static final String TAG_OF_FILE_TABLE="tag_of_file";
    private static final String TAG_OF_FILE_TAG_ID ="tag_id";
    private static final String TAG_OF_FILE_FILE_ID ="file_id";


    private DatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, 1);
    }


    /*Use the application context, which will ensure that you
    don't accidentally leak an Activity's context.
    The static getInstance method ensures that only one DatabaseHelper will ever exist
    at any given time. If the mInstance object has not been initialized, one will be created.
    If one has already been created then it will simply be returned.*/
    public static DatabaseHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //CREATE FILE TABLE
        db.execSQL("CREATE TABLE " + FILE_TABLE +
                " ("+ FILE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ FILE_PATH +" TEXT)");

        //CREATE TAG TABLE
        db.execSQL("CREATE TABLE " + TAG_TABLE +
                " ("+ TAG_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ TAG_NAME +" TEXT)");

        //CREATE TAG_OF_FILE TABLE
        db.execSQL("CREATE TABLE " + TAG_OF_FILE_TABLE +" ("+ TAG_OF_FILE_TAG_ID +" INTEGER, "+
                TAG_OF_FILE_FILE_ID +" INTEGER, FOREIGN KEY ( "+TAG_OF_FILE_TAG_ID+") REFERENCES "+TAG_TABLE+"( "+TAG_ID+"), "+
                "FOREIGN KEY ( "+TAG_OF_FILE_FILE_ID+") REFERENCES "+FILE_TABLE+"( "+FILE_ID+"))");

        //DEFAULT TAGS
        insertSampleTag(db,"Animals");
        insertSampleTag(db,"Art");
        insertSampleTag(db,"Family");
        insertSampleTag(db,"Food");
        insertSampleTag(db,"Friends");
        insertSampleTag(db,"Fun");
        insertSampleTag(db,"Holiday");
        insertSampleTag(db,"Music");
        insertSampleTag(db,"Nature");
        insertSampleTag(db,"People");
        insertSampleTag(db,"Places");
        insertSampleTag(db,"Sports");
        insertSampleTag(db,"Travel");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE);
        onCreate(db);
    }


    public void insertSampleTag(SQLiteDatabase db, String tagName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_NAME,tagName);
        db.insert(TAG_TABLE,null,contentValues);

    }

    //get Tags
    public Cursor getAllTags(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TAG_TABLE,null );
    }

    //insert new File path
    public boolean insertNewFile(String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FILE_PATH,filePath);
        long result = db.insert(FILE_TABLE,null,contentValues);
        db.close();
        return result != -1;
    }

    //check if file path has already been inserted to db
    public boolean checkIfPathExistsAlready(String filePath){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from "+FILE_TABLE,null);
        boolean check = false;
        while (result.moveToNext()){
            if(result.getString(1).equals(filePath)){
                check = true;
                break;
            }
        }
        result.close();
        db.close();
        return check;
    }

    public boolean addTagToFile(Integer tagID, Integer fileID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_OF_FILE_TAG_ID,tagID);
        contentValues.put(TAG_OF_FILE_FILE_ID,fileID);
        long result = db.insert(TAG_OF_FILE_TABLE,null,contentValues);
        db.close();
        return result != -1;
    }

    public Integer getTagId(String tag) {
        Integer id=0;
        // Select Query
        String selectQuery = "SELECT  "+TAG_ID+" FROM " + TAG_TABLE+" WHERE "+TAG_NAME+" = '"+tag+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            id=cursor.getInt(0);
        }
        // closing connection
        cursor.close();
        db.close();
        return id;
    }

    public Integer getFileID(String filePath) {
        Integer id=0;
        // Select Query
        String selectQuery = "SELECT  "+FILE_ID+" FROM " + FILE_TABLE+" WHERE "+FILE_PATH+" = '"+filePath+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            id=cursor.getInt(0);
        }
        // closing connection
        cursor.close();
        db.close();
        return id;
    }

    //check if the selected tag is already added in the selected file
    public boolean checkTag(Integer tagID, Integer fileID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from "+TAG_OF_FILE_TABLE,null);
        boolean check=false;
        while (result.moveToNext()){
            if(result.getInt(0)==tagID && result.getInt(1)==fileID){
                check=true;
                break;
            }
        }
        result.close();
        db.close();
        return check;
    }
}
