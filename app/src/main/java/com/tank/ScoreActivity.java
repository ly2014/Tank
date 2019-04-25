package com.tank;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.tank.db.DBManager;

import java.util.List;

public class ScoreActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);
        DBManager db = new DBManager(this);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/xk.ttf");

        TextView rank1 = findViewById(R.id.rank1);
        TextView rank2 = findViewById(R.id.rank2);
        TextView rank3 = findViewById(R.id.rank3);
        TextView rank4 = findViewById(R.id.rank4);
        TextView rank5 = findViewById(R.id.rank5);
        rank1.setTypeface(tf);
        rank2.setTypeface(tf);
        rank3.setTypeface(tf);
        rank4.setTypeface(tf);
        rank5.setTypeface(tf);
        List<Integer> scores = db.getScore();
        if(scores.size() > 0) {
            rank1.setText("1. " + scores.get(0));
        }
        if(scores.size() > 1) {
            rank2.setText("2. " + scores.get(1));
        }
        if(scores.size() > 2) {
            rank3.setText("3. " + scores.get(2));
        }
        if(scores.size() > 3) {
            rank4.setText("4. " + scores.get(3));
        }
        if(scores.size() > 5) {
            rank5.setText("1. " + scores.get(4));
        }
    }
}
