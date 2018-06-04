package com.bluestone.intelligence.projectmlkit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {

    private FrameLayout viewPane;
    private Camera camera;
    private CameraSurfaceView cameraSurfaceView;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        context = CameraActivity.this;
        viewPane = findViewById(R.id.frame_view);

        camera = getCameraInstance();

        if (camera == null)
            Toast.makeText(this,"Null Camera",Toast.LENGTH_SHORT).show();

        cameraSurfaceView = new CameraSurfaceView(CameraActivity.this, CameraActivity.this, camera);
        viewPane.addView(cameraSurfaceView);

        /* Hides status bar
         */
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    private boolean checkCameraHardware(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Toast.makeText(this,"Working camera!",Toast.LENGTH_SHORT).show();
            return true;
        }
        else{
            Toast.makeText(this,"Not Working camera!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera camera1 = null;
        try{
            camera1 = Camera.open();
        }
        catch (Exception e){
            Log.d("Camera","Camera open failed");
            Toast.makeText(context,"Camera open failed",Toast.LENGTH_SHORT).show();
        }
        return camera1;
    }

}
