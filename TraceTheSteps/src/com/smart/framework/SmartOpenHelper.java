package com.smart.framework;

import java.io.BufferedInputStream;
import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SmartOpenHelper extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase myDataBase;
    private String DB_SQL;
    private SmartVersionHandler smartVersionHandler;

    SmartOpenHelper(Context context, String dbname, int dbversion, String dbSqlName, SmartVersionHandler smartVersionHandler) throws IOException {
        super(context, dbname, null, dbversion);
        this.context = context;
        this.DB_SQL = dbSqlName;
        this.smartVersionHandler = smartVersionHandler;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            BufferedInputStream inStream = new BufferedInputStream(context.getAssets().open(DB_SQL));
            String sql = "";
            int character = -2;
            do {
                character = inStream.read();
                if ((character != -1) && (character != -2))
                    sql += (char) character;
                else
                    break;
            } while (true);
            System.out.println("onCreate DB SQL = " + sql.split("\n"));
            String[] arrSQL = sql.split("\n");

            for (int i = 0; i < arrSQL.length; i++) {
                db.execSQL(arrSQL[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.smartVersionHandler != null) {
            this.smartVersionHandler.onInstalling(SmartApplication.REF_SMART_APPLICATION);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            BufferedInputStream inStream = new BufferedInputStream(context.getAssets().open(DB_SQL));
            String sql = "";
            int character = -2;
            do {
                character = inStream.read();
                if ((character != -1) && (character != -2))
                    sql += (char) character;
                else
                    break;
            } while (true);

            System.out.println("onUpgrade DB SQL = " + sql.split("\n"));
            String[] arrSQL = sql.split("\n");
            for (int i = 0; i < arrSQL.length; i++) {
                db.execSQL(arrSQL[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.smartVersionHandler != null) {
            this.smartVersionHandler.onUpgrading(oldVersion, newVersion, SmartApplication.REF_SMART_APPLICATION);
        }
    }

    public SQLiteDatabase getOpenDatabase() {
        return myDataBase;
    }

    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

}
