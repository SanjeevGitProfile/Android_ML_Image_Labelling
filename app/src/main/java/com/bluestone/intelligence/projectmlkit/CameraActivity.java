package com.bluestone.intelligence.projectmlkit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class CameraActivity extends AppCompatActivity {

    private FrameLayout viewPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        viewPane = findViewById(R.id.frame_view);

        CameraSurfaceView cameraSurfaceView = new CameraSurfaceView(CameraActivity.this,this);
        viewPane.addView(cameraSurfaceView);

        /* Hides status bar
         */
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
