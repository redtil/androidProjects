package com.example.rediet.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Rediet on 4/19/16.
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    public static final String EXTRA_PHOTO_FILENAME = "com.bignerdranch.android.criminalintent.photo_filename";

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback(){
        public void onShutter(){
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback(){
        public void onPictureTaken(byte[] data, Camera camera){
            String filename = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream os = null;
            boolean success = true;
            try{
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            }catch(Exception e){
                Log.e(TAG,"Error writing to file " + filename, e);
                success = false;
            }finally{
                try{
                    if(os != null)
                        os.close();
                }catch(Exception e){
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                }
            }
            if(success){
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, i);
            }
            else{
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height){
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for(Camera.Size s : sizes) {
            int area = s.width * s.height;
            if((area > largestArea) && (area <= width*height)){
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);
        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainter);
        mProgressContainer.setVisibility(View.INVISIBLE);
        Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mCamera != null){
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }
        });
        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback(){
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                }catch(IOException exception){
                        Log.e(TAG, "Error settin up preview display", exception);
                    }
                }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if(mCamera == null) return;
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(),width,height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try{
                    mCamera.startPreview();
                }catch(Exception e){
                    Log.e(TAG,"Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            mCamera = Camera.open(0);
        }else{
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }
}
