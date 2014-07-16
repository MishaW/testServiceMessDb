package dbmessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mw.kgmspread.AEvent;

/**
 * Created by mishaw on 22.06.14.
 */
public class DBMessageHelper extends SQLiteOpenHelper implements NameTableFieldDB {

    private SQLiteDatabase sqLiteDatabase;
    private static final String DB_NAME = "Message";

    public DBMessageHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.sqLiteDatabase.execSQL("create table " +TABLE_MESSAGE + " ( "
                + FIELD_ID + " INTEGER primary key autoincrement, "
                + FIELD_TYPE + " INTEGER, "
                + FIELD_MESSAGE + " TEXT, "
                + FIELD_NUMBER + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public void dbOpen(boolean isRead){
//        if (isRead)
//            sqLiteDatabase = getReadableDatabase();
//        else
            sqLiteDatabase = getWritableDatabase();
    }

    public void dbClose(){
        sqLiteDatabase.close();
    }

    public Cursor getAllMessage(){
        return sqLiteDatabase.query("MessageAEvent", null, null, null, null, null, null);
    }

    public void insertAEvent(AEvent aEvent, String message){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_TYPE, aEvent.event.toInt());
        contentValues.put(FIELD_NUMBER, aEvent.number);
        contentValues.put(FIELD_MESSAGE, message);
        sqLiteDatabase.insert(TABLE_MESSAGE, null, contentValues);
    }

    public boolean isSushestvuet(AEvent aEvent){
        String select = FIELD_TYPE + " = ? and " + FIELD_NUMBER + " = ?";
        String selectArgs[] = { String.valueOf(aEvent.event.toInt()), String.valueOf(aEvent.number) };
        Cursor cursor = sqLiteDatabase.query(TABLE_MESSAGE, null, select, selectArgs, null, null, null);
        if ( cursor == null || cursor.getCount() == 0 ){
            return false;
        } else {
            return true;
        }
    }

    public void delete(AEvent aEvent) {
        String select = FIELD_TYPE + " = ? and " + FIELD_NUMBER + " = ?";
        String selectArgs[] = { String.valueOf(aEvent.event.toInt()), String.valueOf(aEvent.number) };
        sqLiteDatabase.delete(TABLE_MESSAGE, select, selectArgs);
    }
}
