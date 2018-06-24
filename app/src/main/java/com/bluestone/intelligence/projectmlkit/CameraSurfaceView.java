package com.bluestone.intelligence.projectmlkit;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    public static final String DEBUG_TAG = "CameraSurfaceView Log";
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private Activity mActivity;

    public CameraSurfaceView(Context context, Activity activity, Camera camera)
    {
        super(context);

        mActivity = activity;
        mCamera = camera;
        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder){

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees =180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        mCamera.setDisplayOrientation((info.orientation - degrees +360) % 360);

        try
        {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch (IOException e)
        {
            Log.e(DEBUG_TAG,"Surface created exception: ",e);
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height){
        if (mHolder.getSurface() == null)
            return;

        try{
            mCamera.stopPreview();
        } catch (Exception e){
            Log.d(DEBUG_TAG,"Tried to stop a non-existent preview" + e.getMessage());
        }

        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> prevSizes = params.getSupportedPreviewSizes();
        for(Camera.Size s: prevSizes)
        {
            if((s.height <= height)&&(s.width <= width))
            {
                params.setPreviewSize(s.width, s.height);
                break;
            }
        }
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            // Choose another supported mode
        }

        mCamera.setParameters(params);

        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch (Exception e){
            Log.d(DEBUG_TAG,"Camera preview failed");
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mCamera.stopPreview();
        mCamera.release();
    }

    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return true;
    }
}
