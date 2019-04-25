package com.tank.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//操作SQLite数据库
public class DBManager {

    private SQLiteDbHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new SQLiteDbHelper(context);
        db = helper.getWritableDatabase();
    }

    //查询前五的分数
    public List<Integer> getScore() {
        Cursor cursor = db.rawQuery("select score from score_info order by score desc", null);
        List<Integer> rs = new ArrayList<>();
        while(cursor.moveToNext()) {
            rs.add(cursor.getInt(0));
        }
        cursor.close();
        return rs;
    }

    //插入分数
    public void insert(int score) {
        db.execSQL("INSERT INTO score_info(score) VALUES" +
                " (?)", new Object[]{score});
    }
}
