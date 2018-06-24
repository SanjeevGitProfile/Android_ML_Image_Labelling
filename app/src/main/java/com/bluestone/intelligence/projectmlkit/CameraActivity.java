package com.bluestone.intelligence.projectmlkit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.util.List;

public class CameraActivity extends AppCompatActivity{

    private FrameLayout viewPane;
    private TextView displayText;
    private Button captureButton;

    private static Context context;

    private Camera camera;
    private CameraSurfaceView cameraSurfaceView;

    private FirebaseVisionLabelDetectorOptions options;
    private FirebaseVisionImage image;
    private FirebaseVisionImageMetadata metadata;
    private FirebaseVisionLabelDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        context = CameraActivity.this;
        viewPane = findViewById(R.id.frame_view);
        displayText = findViewById(R.id.text_view);
        captureButton = findViewById(R.id.capture_button);

        options = new FirebaseVisionLabelDetectorOptions.Builder()
                        .setConfidenceThreshold(0.8f).build();

        metadata = new FirebaseVisionImageMetadata.Builder()
                .setWidth(1280)
                .setHeight(720)
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(0)
                .build();

        detector = FirebaseVision.getInstance().getVisionLabelDetector();

        camera = getCameraInstance();

        if (camera == null)
            Toast.makeText(this,"Null Camera",Toast.LENGTH_SHORT).show();

        cameraSurfaceView = new CameraSurfaceView(CameraActivity.this, CameraActivity.this, camera);
        viewPane.addView(cameraSurfaceView);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        displayText.setText("ML KIT");

        /* Hides status bar

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        */
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

    public void takePicture(){
        camera.takePicture(null,null,pictureCallback);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            image = FirebaseVisionImage.fromByteArray(data,metadata);
            getResult();
            captureButton.setEnabled(false);
        }
    };

    public void getResult() {
        Task<List<FirebaseVisionLabel>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionLabel> labels) {
                                        String text ="", entityId;

                                        for (FirebaseVisionLabel label: labels) {
                                            text += label.getLabel() + "\n";
                                            entityId = label.getEntityId();
                                            float confidence = label.getConfidence();
                                        }
                                        displayText.setText(text);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
    }
}
