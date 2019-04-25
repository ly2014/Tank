package com.tank;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class MainView extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
    }
}
