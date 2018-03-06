package com.emedrep.reportthat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.emedrep.reportthat.Db.ReportSql;
import com.emedrep.reportthat.Library.CameraPreview;
import com.emedrep.reportthat.Library.Constant;
import com.emedrep.reportthat.Library.GPSTracker;
import com.emedrep.reportthat.Library.Library;
import com.emedrep.reportthat.Library.Utilities;
import com.emedrep.reportthat.Model.Report;
import com.emedrep.watchdrug.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class Camera extends Activity implements SensorEventListener {
    private android.hardware.Camera mCamera;
    private CameraPreview mPreview;
    private SensorManager sensorManager = null;
    private int orientation;
    private ExifInterface exif;
    private int deviceHeight;
    public static final int MEDIA_TYPE_IMAGE =1;
    private Button ibCapture;
    private FrameLayout flBtnContainer,fPreview;
    // private File sdRoot;
    //  private String dir;
    private String fileName;
    private ImageView rotatingImage;
    private int degrees = -1;
    static SharedPreferences prefs;
    SharedPreferences.Editor editor;

    static GPSTracker gpsTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        prefs = getSharedPreferences(Constant.PREFERENCE_NAME, 0);
        // Setting all the path for the image
        //  sdRoot = Environment.getExternalStorageDirectory();
        //  dir = "/DCIM/Camera/";

        editor = prefs.edit();
        // Getting all the needed elements from the layout
        rotatingImage = (ImageView) findViewById(R.id.imageView1);

        ibCapture = (Button) findViewById(R.id.ibCapture);
        flBtnContainer = (FrameLayout) findViewById(R.id.flBtnContainer);
        fPreview = (FrameLayout)findViewById(R.id.camera_preview);
        // Getting the sensor service.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Selecting the resolution of the Android device so we can create a
        // proportional preview
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        deviceHeight = display.getHeight();
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(Camera.this, mCamera);
        fPreview.addView(mPreview);


        // Add a listener to the Capture button
        ibCapture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
//                final android.hardware.Camera.PictureCallback mPicture = new android.hardware.Camera.PictureCallback() {
//                    public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
//
////                        SaveImageTask mAuthTask = new SaveImageTask();
////                        mAuthTask.execute(data);
//
//                        try {
//                             getOutputMediaFile(data);
//
//
////                            File pictureFile = getOutputMediaFile(data);
//
////                            if (pictureFile == null) {
////                                return;
////                            }
////                            FileOutputStream fos = new FileOutputStream(pictureFile);
////                            fos.write(data);
////                            fos.close();
////                            MediaStore.Images.Media.insertImage(getContentResolver(),
////                                    pictureFile.getAbsolutePath(),
////                                    pictureFile.getName(), pictureFile.getName());
////                        } catch (FileNotFoundException e) {
//
//                        }
//                        catch (Exception e) {
//
//                        }
//
//
//
//
//
//                    }
//
//
//                };

//                android.hardware.Camera.Parameters params = mCamera.getParameters();
//
//                List<String> focusModes = params.getSupportedFocusModes();
//                if (focusModes.contains(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO)) {
//                    // Autofocus mode is supported
//                    params.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
//                   // params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_ON);
//                    mCamera.setParameters(params);
//                }
//                mCamera.autoFocus(new android.hardware.Camera.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean arg0, android.hardware.Camera arg1) {
//                        mCamera.takePicture(null, null, mPicture);
//                       // mCamera.stopPreview();
//                        try {
////
//                          Thread.sleep(1000);
//                        } catch (InterruptedException e) {
////                            // TODO Auto-generated catch block
////                            e.printStackTrace();
//                        }
//                      // mCamera.startPreview();
//
//                    }
//                });



            }
        });




    }


    private android.hardware.Camera.PictureCallback mPicture = new android.hardware.Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {

           new SaveImageTask().execute(data);


        }
        };


    @Override
    public void onPause() {
        super.onPause();
       // releaseCamera();              // release the camera immediately on pause event
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
        }
    }



    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
           // mPreview.getHolder().removeCallback(mPreview);
        }
    }
    private boolean checkSDCard() {
        boolean state = false;

        String sd = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sd)) {
            state = true;
        }

        return state;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static android.hardware.Camera getCameraInstance() {
        android.hardware.Camera c = null;
        try {
            // attempt to get a Camera instance
            c = android.hardware.Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }

        // returns null if camera is unavailable
        return c;
    }




    private void getOutputMediaFile(byte[] m) {
 try {
     String memoryType = "";
     File mediaStorageDir = null;
//        if(checkSDCard()){
//            Toast.makeText(this,"Yes it as",Toast.LENGTH_LONG).show();
//            memoryType="SDCARD";
//
//             mediaStorageDir = new File(
//                    Environment
//                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                    "WatchDrug");
//            if (!mediaStorageDir.exists()) {
//                if (!mediaStorageDir.mkdirs()) {
//
//                    Log.d("WatchDrug", "failed to create directory");
//                    return null;
//                }
//            }
//        }
//        else {
     //   Toast.makeText(this,"NO it NOT",Toast.LENGTH_LONG).show();
     // memoryType="INTERNAL";

     //  ContextWrapper cw=new ContextWrapper(this);
     // mediaStorageDir=cw.getDir("ImageDir",Context.MODE_PRIVATE);

     //  File myPath=new File(directory)
     //  }


     String latValue = prefs.getString("Latitude", null);
     String longValue = prefs.getString("Longitude", null);
     gpsTracker = new GPSTracker(this);
     if (!gpsTracker.getIsGPSTrackingEnabled()) {
         gpsTracker.showSettingsAlert();
     }
     if (latValue == null) {
         latValue = String.valueOf(gpsTracker.getLatitude());
         longValue = String.valueOf(gpsTracker.getLongitude());
     }

     String timeStamp = new SimpleDateFormat("HHmmSS")
             .format(new Date());
     File mediaFile;
     //mediaFile = new File(mediaStorageDir.getPath() + File.separator
     //   + "IMG_"+latValue+"_"+longValue+"_" +timeStamp+ ".jpg");
     String picName = "IMG_" + latValue + "_" + longValue + "_" + timeStamp + ".jpg";
     Bitmap bitmap = BitmapFactory.decodeByteArray(m, 0, m.length);

     Utilities.saveFle(this, bitmap, picName);
    // Geocoder geocoder;
     List<Address> addresses;

     Report report = new Report();
     try {
//         geocoder = new Geocoder(this, Locale.getDefault());
//         addresses = geocoder.getFromLocation(Double.parseDouble(latValue), Double.parseDouble(longValue), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//         String address = addresses.get(0).getAddressLine(0);
//         String city = addresses.get(0).getLocality();
//         String state = addresses.get(0).getAdminArea();
//         String country = addresses.get(0).getCountryName();
//         String postalCode = addresses.get(0).getPostalCode();
//         String knownName = addresses.get(0).getFeatureName();
//         String relativeAddress = addresses.get(0).getSubAdminArea();
//         String premise = addresses.get(0).getSubAdminArea();
//         String subThoroughFare = addresses.get(0).getSubThoroughfare();
//         String thoroughFare = addresses.get(0).getThoroughfare();
//         report.subThoroughFare = subThoroughFare;
//         report.relativeAddress = relativeAddress;
//         report.thoroughFare = thoroughFare;
//         report.premise = premise;
//         report.address = address;
//         report.country = country;
//         report.city = city;
//         report.knownName = knownName;
//         report.state = state;
//         report.postalCode = postalCode;
     } catch (Exception e) {
         e.printStackTrace();
     }
     finally {


         report.CaptureFile = picName;

         report.latitude = latValue;
         report.longitude = longValue;
         report.date= new SimpleDateFormat("dd/MM/yyyy KK:mm a")
                 .format(new Date());
         report.storageType = memoryType;
         report.isReported = "0";

         ReportSql sql = new ReportSql(this);

        long createIndex = sql.createReport(report);
         Library.StartActivity(Camera.this, MasterActivity.class);
         // Toast.makeText(this,"address:" +address+"city:"+city+"state: "+state+"country:"+country+"postal code"+postalCode+"known name"+knownName, Toast.LENGTH_LONG).show();
     }
 }catch (Exception ex){
     Log.d("Storage error",ex.getMessage());
 }
       // return mediaFile;
    }




    private class SaveImageTask extends AsyncTask<byte[],Void, Void> {


        @Override
        protected Void doInBackground(byte[]... params) {
            try {

                getOutputMediaFile(params[0]);


            } catch (Exception e) {

           }
            return null;
        }
    }









    /**
     * Putting in place a listener so we can get the sensor data only when
     * something changes.
     */
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                RotateAnimation animation = null;
                if (event.values[0] < 4 && event.values[0] > -4) {
                    if (event.values[1] > 0 && orientation != ExifInterface.ORIENTATION_ROTATE_90) {
                        // UP
                        orientation = ExifInterface.ORIENTATION_ROTATE_90;
                        animation = getRotateAnimation(270);
                        degrees = 270;
                    } else if (event.values[1] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_270) {
                        // UP SIDE DOWN
                        orientation = ExifInterface.ORIENTATION_ROTATE_270;
                        animation = getRotateAnimation(90);
                        degrees = 90;
                    }
                } else if (event.values[1] < 4 && event.values[1] > -4) {
                    if (event.values[0] > 0 && orientation != ExifInterface.ORIENTATION_NORMAL) {
                        // LEFT
                        orientation = ExifInterface.ORIENTATION_NORMAL;
                        animation = getRotateAnimation(0);
                        degrees = 0;
                    } else if (event.values[0] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_180) {
                        // RIGHT
                        orientation = ExifInterface.ORIENTATION_ROTATE_180;
                        animation = getRotateAnimation(180);
                        degrees = 180;
                    }
                }
                if (animation != null) {
                    rotatingImage.startAnimation(animation);
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    /**
     * Calculating the degrees needed to rotate the image imposed on the button
     * so it is always facing the user in the right direction
     *
     * @param toDegrees
     * @return
     */
    private RotateAnimation getRotateAnimation(float toDegrees) {
        float compensation = 0;

        if (Math.abs(degrees - toDegrees) > 180) {
            compensation = 360;
        }

        // When the device is being held on the left side (default position for
        // a camera) we need to add, not subtract from the toDegrees.
        if (toDegrees == 0) {
            compensation = -compensation;
        }

        // Creating the animation and the RELATIVE_TO_SELF means that he image
        // will rotate on it center instead of a corner.
        RotateAnimation animation = new RotateAnimation(degrees, toDegrees - compensation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        // Adding the time needed to rotate the image
        animation.setDuration(250);

        // Set the animation to stop after reaching the desired position. With
        // out this it would return to the original state.
        animation.setFillAfter(true);

        return animation;
    }

    /**
     * STUFF THAT WE DON'T NEED BUT MUST BE HEAR FOR THE COMPILER TO BE HAPPY.
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}