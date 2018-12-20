package android.wings.websarva.com.usagehis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
    データベースヘルパー
*/
public class Dbhelper extends SQLiteOpenHelper {

    // データベースファイルの名前
    private static final String DATABASE_NAME = "kari.db";
    // データベースのバージョン
    private static final  int DATABASE_VERSION = 1;


    public Dbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
