
package com.piisoft.upecfacerecognition;

        import android.Manifest;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.hardware.Camera;
        import android.media.AudioManager;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.ContactsContract;
        import android.provider.MediaStore;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v4.app.ActivityCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GoogleApiAvailability;
        import com.google.android.gms.vision.CameraSource;
        import com.google.android.gms.vision.MultiProcessor;
        import com.google.android.gms.vision.Tracker;
        import com.google.android.gms.vision.face.Face;
        import com.google.android.gms.vision.face.FaceDetector;
        import com.piisoft.upecfacerecognition.ui.camera.CameraSourcePreview;
        import com.piisoft.upecfacerecognition.ui.camera.GraphicOverlay;
        import com.piisoft.upecfacerecognition.utility.Exif;
        import com.piisoft.upecfacerecognition.utility.Image;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.FilenameFilter;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.util.List;

        import static com.piisoft.upecfacerecognition.utility.Image.rotateImage;
        import static com.piisoft.upecfacerecognition.utility.Image.saveBitmapToJpg;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final  class CameraHiddenCapturePhoto  {
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private long lastImageTakenTime = 0;
    Context context;
    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */

    public   CameraHiddenCapturePhoto(Context _context){
        context = _context;
        createCameraSource(CameraSource.CAMERA_FACING_FRONT,context);
        startCameraSource();
    }






    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource(int cameraSource , Context context) {

       // Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(cameraSource)
                .setRequestedFps(30.0f)
                .build();

    }


    public void saveCurrentImage() {
        if (mCameraSource != null) {
            MuteAudio();
            lastImageTakenTime = System.currentTimeMillis();
            mCameraSource.takePicture(shutterCallback, mPicture);
            //UnMuteAudio();

        }
    }


    public void MuteAudio(){
        AudioManager mAlramMAnager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }


    public void UnMuteAudio(){
        AudioManager mAlramMAnager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        }
    }

    private final CameraSource.ShutterCallback shutterCallback = new CameraSource.ShutterCallback() {
        public void onShutter() {
        }
    };

    private CameraSource.PictureCallback mPicture = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {
            int orientation = Exif.getOrientation(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            switch (orientation) {
                case 90:
                    bitmap = rotateImage(bitmap, 90);

                    break;
                case 180:
                    bitmap = rotateImage(bitmap, 180);

                    break;
                case 270:
                    bitmap = rotateImage(bitmap, 270);

                    break;
                case 0:
                    // if orientation is zero we don't need to rotate this

                default:
                    break;
            }

            String path = Environment.getExternalStorageDirectory().toString() + File.separator + "FaceRecognition";
            String pathToDatabase = path  + File.separator + "faceDatabase";
                    String IamgeName = "stranger_full_image_" + System.currentTimeMillis() + ".jpg";
            saveBitmapToJpg(bitmap, path, IamgeName);
            Toast.makeText(context, "Image Taken Ok", Toast.LENGTH_SHORT).show();
            UnMuteAudio();
            new MyAsyncTask().execute(path + File.separator + IamgeName
                    , path + File.separator + "stranger_faceDatabase"
                    ,pathToDatabase);
        }

    };

    private void ClearStrangerFolder(){
        String strangerFolder = Environment.getExternalStorageDirectory().toString() + File.separator + "FaceRecognition"
                + File.separator + "stranger_faceDatabase";
        File dir = new File(strangerFolder);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }

    private void SendAlertMail(){
        //send mail then

        Mail m = new Mail("laha295@gmail.com", "mohamed@1");
        String[] toArr = {"phptop@gmail.com", "codejq@gmail.com"};
        m.setTo(toArr);
        m.setFrom("phptop@gmail.com");
        m.setSubject("Some one use your device here is the data of the person.");
        m.setBody("Some one use your device here is the data of the person.");

        try {

            String strangerFolder = Environment.getExternalStorageDirectory().toString() + File.separator + "FaceRecognition"
                    + File.separator + "stranger_faceDatabase";
            File root = new File(strangerFolder);
            FilenameFilter pngFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jpg");
                };
            };
            File[] imageFiles = root.listFiles(pngFilter);
            for (File image : imageFiles) {
                //Bitmap stranger = Image.bitmapFromJpg(image);
                m.addAttachment(image.getAbsolutePath());
            }


            if(m.send()) {
                Toast.makeText(context, "Email was sent successfully.", Toast.LENGTH_LONG).show();
            }
        } catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e("MailApp", "Could not send email", e);
        }
    }



private  void CheckAuthrization(){
    String path = Environment.getExternalStorageDirectory().toString() + File.separator + "FaceRecognition";
    String pathToDatabase = path  + File.separator + "faceDatabase";
    String stranger_faceDatabase =  path + File.separator + "stranger_faceDatabase";
    PersonRecognizerService pr = new PersonRecognizerService(pathToDatabase,0);
    pr.train(pathToDatabase,false);



    File root = new File(stranger_faceDatabase);
    FilenameFilter pngFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".jpg");
        };
    };

    File[] imageFiles = root.listFiles(pngFilter);
    for (File image : imageFiles) {
        //Bitmap stranger = Image.bitmapFromJpg(image);

        if( pr.predict(image.getAbsolutePath())){
            Toast.makeText(context, "Ok you are Authorized to Login into this Device", Toast.LENGTH_SHORT).show();
            ClearStrangerFolder();
            return;
        }
    }
    if(imageFiles.length > 0){
        Toast.makeText(context, "Not   are Authorized ", Toast.LENGTH_SHORT).show();
        new SendMailTask().execute("");

    }
}


    private class SendMailTask extends AsyncTask<String, String, String> {

         @Override
        protected String doInBackground(String... strings) {
            // Some long-running task like downloading an image.
            SendAlertMail();
            return "";
        }


    }

    // The types specified here are the input data type, the progress type, and the result type
    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            // Some long-running task like downloading an image.
            new extractFacesFromImage(strings[0], strings[1],context);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // The results of the above method
            // Processing the results here
            CheckAuthrization();
            if (mCameraSource != null) {
                mCameraSource.release();
            }

        }


    }







    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                context);
        if (code != ConnectionResult.SUCCESS) {
            Toast.makeText(context, "check that the device has play services available", Toast.LENGTH_SHORT).show();
        }

        if (mCameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mCameraSource.start();
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            boolean isSmiling = face.getIsSmilingProbability() > 0.01;
            boolean oneShotOnly =   System.currentTimeMillis() - lastImageTakenTime > 5000 ;
            if ( oneShotOnly) {
                saveCurrentImage();
            }

        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {

        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {

        }
    }
}
