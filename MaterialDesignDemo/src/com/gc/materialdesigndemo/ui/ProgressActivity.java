package com.gc.materialdesigndemo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.gc.materialdesign.views.ProgressBarIndeterminateDeterminate;
import com.gc.materialdesigndemo.R;

public class ProgressActivity extends Activity {

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        
        int color = getIntent().getIntExtra("BACKGROUND", Color.BLACK);
        findViewById(R.id.progressBarCircularIndetermininate).setBackgroundColor(color);
    }
}
