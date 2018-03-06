package com.emedrep.reportthat.Fragments;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.emedrep.reportthat.Library.CameraPreview;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.watchdrug.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by eMedrep Nigeria LTD on 8/9/2017.
 */

public class FragmentCamera extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE =1;
    Button btnTakePicture;
    FrameLayout fPreview;
    private ImageView imgPreview,imgCamera,imgExitCam;
    ProgressBar pgrGen;
    RelativeLayout linCapture;
    static SharedPreferences prefs;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        prefs = getActivity().getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        View v = inflater.inflate(R.layout.camera, container, false);
        imgPreview=(ImageView)v.findViewById(R.id.imgPreview);
        fPreview = (FrameLayout) v.findViewById(R.id.camera_preview);
        btnTakePicture=(Button)v.findViewById(R.id.button_take_picture);
        linCapture=(RelativeLayout)v.findViewById(R.id.linCapture);
        imgCamera=(ImageView)v.findViewById(R.id.imgTake);
        imgExitCam=(ImageView)v.findViewById(R.id.imgExit);
        pgrGen=(ProgressBar)v.findViewById(R.id.pgrGen);

        imgExitCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linCapture.setVisibility(View.GONE);
                btnTakePicture.setVisibility(View.VISIBLE);

                fPreview.setVisibility(View.GONE);
                imgPreview.setVisibility(View.VISIBLE);
                fPreview.removeAllViews();
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
             public void onPictureTaken(byte[] data, Camera camera) {
                 File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

                        if (pictureFile == null) {
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                                    pictureFile.getAbsolutePath(),
                                    pictureFile.getName(), pictureFile.getName());
                        } catch (FileNotFoundException e) {

                        } catch (IOException e) {

                        }

//                        linCapture.setVisibility(View.GONE);
//                        btnTakePicture.setVisibility(View.VISIBLE);

//                        fPreview.setVisibility(View.GONE);
//                        imgPreview.setVisibility(View.VISIBLE);
//                        Toast.makeText(getActivity(),"Image Capture Successful",Toast.LENGTH_LONG).show();
//                        linCapture.setVisibility(View.GONE);
//                        btnTakePicture.setVisibility(View.VISIBLE);

                 Fragment fragment = null;
                 Class fragmentClass = null;
                 FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                  fragmentClass = FragmentCaptures.class;
                         try {
                             fragment = (Fragment) fragmentClass.newInstance();
                         } catch (Exception e) {
                             e.printStackTrace();
                         }

                         fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();



                        fPreview.removeAllViews();
                    }


                };

                Camera.Parameters params = mCamera.getParameters();

                List<String> focusModes = params.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    // Autofocus mode is supported
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    mCamera.setParameters(params);
                }
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean arg0, Camera arg1) {
                                mCamera.takePicture(null, null, mPicture);
                                //mCamera.stopPreview();
                                try {

                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                mCamera.startPreview();

                            }
                        });


            }
        });


        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    linCapture.setVisibility(View.VISIBLE);
                    btnTakePicture.setVisibility(View.GONE);
                    mCamera = getCameraInstance();
                    mPreview = new CameraPreview(getActivity(), mCamera);

                    fPreview.setVisibility(View.VISIBLE);
                    imgPreview.setVisibility(View.GONE);
                    fPreview.addView(mPreview);


//                captureButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // get an image from the camera
//                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                            @Override
//                            public void onAutoFocus(boolean arg0, Camera arg1) {
//                                mCamera.takePicture(null, null, mPicture);
//                                //mCamera.stopPreview();
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                                mCamera.startPreview();
//                            }
//                        });
//                    }
//                });
                }

        });
        // Create an instance of Camera

        return v;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }



    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    private static File getOutputMediaFile(int m) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "WatchDrug");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("WatchDrug", "failed to create directory");
                return null;
            }
        }
        String latValue=prefs.getString("Latitude",null);
        String longValue=prefs.getString("Longitude",null);
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_"+latValue+"_"+longValue+"_" + timeStamp + ".jpg");

        return mediaFile;
    }

}

