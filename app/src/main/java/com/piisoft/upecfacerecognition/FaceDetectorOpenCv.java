package com.piisoft.upecfacerecognition;

/**
 * Created by a on 1/29/2017.
 * source from http://stackoverflow.com/questions/28231066/how-to-crop-the-detected-face-image-in-opencv-java
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.piisoft.upecfacerecognition.utility.Image;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
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
    private final   Context context;
    private static String ImagePath ;
    private static String OutPutPath;
    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv_java");
    }

    public  FaceDetectorOpenCv(String _ImagePath , String _OutPutPath , Context _context){
        this.context = _context;
        ImagePath = _ImagePath;
        OutPutPath = _OutPutPath;

         BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(context) {
            @Override
            public void onManagerConnected(int status) {
                if(status == LoaderCallbackInterface.SUCCESS){
                    Log.e(""," LoaderCallbackInterface.SUCCESS ");
                    detectFaces( ImagePath ,  OutPutPath);
                }
            }
        };
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }



    public  void detectFaces(String ImagePath , String OutPutPath) {
        int x = 0,y = 0,height = 0,width = 0,mAbsoluteFaceSize=0;
        (new File(OutPutPath)).mkdirs();
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
        Mat image = Highgui.imread(ImagePath,Highgui.CV_LOAD_IMAGE_COLOR);
        Mat mGray = Highgui.imread(ImagePath,Highgui.CV_LOAD_IMAGE_GRAYSCALE );




        float  mRelativeFaceSize   = 0.2f;
        height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            //  mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);


        faceDetector.detectMultiScale(mGray, face_Detections, 1.1, 2, 2,
                new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        System.out.println(String.format("Detected %s faces",  face_Detections.toArray().length));
        Rect rect_Crop=null;
        for (Rect rect : face_Detections.toArray()) {
            Mat m = image.submat(rect);
            Bitmap mBitmap = Bitmap.createBitmap(m.width(),m.height(), Bitmap.Config.ARGB_8888);
            Mat coloredImage = new Mat();
            Imgproc.cvtColor(m, coloredImage, Imgproc.COLOR_BGR2RGB);
            Utils.matToBitmap(coloredImage, mBitmap);
            Image.saveBitmapToJpg(mBitmap, OutPutPath, "opencv_image_" + System.currentTimeMillis() + ".jpg",256);
            /*
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
            Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
            Mat image_roi = new Mat(image,rectCrop);
            Highgui.imwrite(OutPutPath +  File.separator +  "opencv_image_" +  File.separator + System.currentTimeMillis() + ".jpg",image_roi);
            */
        }

        new File(ImagePath).delete();





    }

}