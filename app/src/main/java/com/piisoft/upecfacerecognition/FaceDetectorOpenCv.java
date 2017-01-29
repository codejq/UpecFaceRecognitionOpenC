package com.piisoft.upecfacerecognition;

/**
 * Created by a on 1/29/2017.
 * source from http://stackoverflow.com/questions/28231066/how-to-crop-the-detected-face-image-in-opencv-java
 */

import android.content.Context;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import static org.opencv.highgui.Highgui.imwrite;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FaceDetectorOpenCv {
    private static Mat cropImage;
    private static Context context;
    private static String ImagePath ;
    private static String OutPutPath;

    public  FaceDetectorOpenCv(Context _context ,String ImagePath , String OutPutPath){
        context = _context;
        OpenCVLoader.initOpenCV("2.4.10",context.getApplicationContext(),mLoaderCallback);
        System.loadLibrary("opencv_java" );
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(context) {
        @Override
        public void onManagerConnected(int status) {
            if(status == LoaderCallbackInterface.SUCCESS){
                Log.e(""," LoaderCallbackInterface.SUCCESS ");
                detectFaces( ImagePath ,  OutPutPath);
            }
        }
    };

    public static void detectFaces(String ImagePath , String OutPutPath) {
        int x = 0,y = 0,height = 0,width = 0;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("\nRunning FaceDetector");

        // load cascade file from application resources
        InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
        File mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(mCascadeFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {

                os.write(buffer, 0, bytesRead);

        }
        is.close();
        os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MatOfRect face_Detections = new MatOfRect();
        Log.e(""," Before CascadeClassifier loaded ");
        CascadeClassifier faceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        Log.e(""," CascadeClassifier loaded suscfully");
        Mat image = Highgui.imread(ImagePath);
        faceDetector.detectMultiScale(image, face_Detections);
        System.out.println(String.format("Detected %s faces",  face_Detections.toArray().length));
        Rect rect_Crop=null;
        for (Rect rect : face_Detections.toArray()) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
            Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
            Mat image_roi = new Mat(image,rectCrop);
            Highgui.imwrite(OutPutPath +  File.separator +  "opencv_image_" +  File.separator + System.currentTimeMillis() + ".jpg",image_roi);
        }







    }

}