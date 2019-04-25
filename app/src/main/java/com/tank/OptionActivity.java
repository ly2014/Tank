package com.tank;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class OptionActivity extends Activity {
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        sp = getSharedPreferences("option.txt", MODE_PRIVATE);
        int audio = sp.getInt("audio", 1);

        Switch sch = findViewById(R.id.switch1);
        sch.setChecked(audio == 1 ? true : false);

        sch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                if(isChecked) {
                    editor.putInt("audio", 1);
                }else {
                    editor.putInt("audio", 1);
                }
                editor.commit();
            }
        });
    }
}
