package com.piisoft.upecfacerecognition;

import static  com.googlecode.javacv.cpp.opencv_highgui.*;
import static  com.googlecode.javacv.cpp.opencv_core.*;

import static  com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public  class PersonRecognizerService {

    public final static int MAXIMG = 100;
    FaceRecognizer faceRecognizer;
    String mPath;
    int count=0;
    labels labelsFile;

    static  final int WIDTH= 256;
    static  final int HEIGHT= 256;;
    private int mProb=999;
    public double distnace= 0.0;
    private  int recognition_threshold = 70;

    public PersonRecognizerService(String path , int eRecognizer , int _recognition_threshold  )
    {
        //faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(2,8,8,8,200);
        chooseRecognizer(eRecognizer);
        recognition_threshold = _recognition_threshold;
        // path=Environment.getExternalStorageDirectory()+"/facerecog/faces/";
        mPath=path;

    }



    void chooseRecognizer(int nRec)
    {
        switch(nRec) {
            case 0: faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(2,8,8,8,200);
                break;
            case 1: faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(1,8,8,8,100);
                break;
            case 2: faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer();
                break;
            case 3: faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib.createEigenFaceRecognizer(10,123.0);
                break;
        }

    }

    void add(Mat m, String description) {
        Bitmap bmp= Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(m,bmp);
        bmp= Bitmap.createScaledBitmap(bmp, WIDTH, HEIGHT, false);

        FileOutputStream f;
        try {
            f = new FileOutputStream(mPath+description+"-"+count+".jpg",true);
            count++;
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, f);
            f.close();

        } catch (Exception e) {
            Log.e("error",e.getCause()+" "+e.getMessage());
            e.printStackTrace();

        }
    }

    public boolean train(String  imagePath , boolean forthTrain) {
        String tarningResult = imagePath + File.separator + "faceRecognizer.txt";
        File tarningResultFile = new File(tarningResult);
        if(!forthTrain && tarningResultFile.exists()) {
            faceRecognizer.load(tarningResult);
            return true;
        }

        File root = new File(imagePath);
        FilenameFilter pngFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg");
            };
        };

        File[] imageFiles = root.listFiles(pngFilter);
        if(imageFiles.length < 1){
            return  true;
        }
        MatVector images = new MatVector(imageFiles.length);
        int[] labels = new int[imageFiles.length];
        int counter = 0;

        IplImage img=null;
        IplImage grayImg;

        for (File image : imageFiles) {
            String p = image.getAbsolutePath();
            img = cvLoadImage(p);

            if (img==null)
                Log.e("Error","Error cVLoadImage");
            Log.i("image",p);

            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(img, grayImg, CV_BGR2GRAY);
            images.put(counter, grayImg);
            labels[counter] = counter;

            counter++;
        }
        if (counter>0) {
            faceRecognizer.train(images, labels);
            faceRecognizer.save(tarningResult);
        }

        return true;
    }

    public boolean canPredict()
    {
        return true;

    }

    public boolean predict(Bitmap m) {

        Bitmap bmp= Bitmap.createBitmap(m.getWidth(), m.getHeight(), Bitmap.Config.ARGB_8888);
        int n[] = new int[1];
        double p[] = new double[1];
        IplImage ipl = BitmapToIplImage(bmp,-1, -1);
//		IplImage ipl = MatToIplImage(m,-1, -1);

        /*
                    img = cvLoadImage(p);

            if (img==null)
                Log.e("Error","Error cVLoadImage");
            Log.i("image",p);

            grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(img, grayImg, CV_BGR2GRAY);
            images.put(counter, grayImg);
        */

        faceRecognizer.predict(ipl, n, p);
        Log.e("Result:",n[0] + "");
        Log.e("Result:",p[0] + "");


        return  (n[0]!=-1 && p[0] < 30.0) ;
    }


    public boolean predict(String ImagePath) {
        int n[] = new int[1];
        double p[] = new double[1];
        IplImage img=null;
        IplImage grayImg;
        img = cvLoadImage(ImagePath);
        grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(img, grayImg, CV_BGR2GRAY);
        faceRecognizer.predict(grayImg, n, p);
        Log.e("Result:",n[0] + "");
        Log.e("Result:",p[0] + "");
        distnace = p[0];

        return  (n[0]!=-1 && p[0] < recognition_threshold) ;
    }




    IplImage MatToIplImage(Mat m,int width,int heigth)
    {


        Bitmap bmp=Bitmap.createBitmap(m.width(), m.height(), Bitmap.Config.ARGB_8888);


        Utils.matToBitmap(m, bmp);
        return BitmapToIplImage(bmp,width, heigth);

    }

    IplImage BitmapToIplImage(Bitmap bmp, int width, int height) {

        if ((width != -1) || (height != -1)) {
            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, width, height, false);
            bmp = bmp2;
        }

        IplImage image = IplImage.create(bmp.getWidth(), bmp.getHeight(),
                IPL_DEPTH_8U, 4);

        bmp.copyPixelsToBuffer(image.getByteBuffer());

        IplImage grayImg = IplImage.create(image.width(), image.height(),
                IPL_DEPTH_8U, 1);

        cvCvtColor(image, grayImg, opencv_imgproc.CV_BGR2GRAY);

        return grayImg;
    }



    protected void SaveBmp(Bitmap bmp,String path)
    {
        FileOutputStream file;
        try {
            file = new FileOutputStream(path , true);

            bmp.compress(Bitmap.CompressFormat.JPEG,100,file);
            file.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("",e.getMessage()+e.getCause());
            e.printStackTrace();
        }

    }


    public void load() {
       // train();

    }

    public int getProb() {
        // TODO Auto-generated method stub
        return mProb;
    }


}
