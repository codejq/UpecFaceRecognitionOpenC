/*
*   Created by Mohamed Ali Elsayed  on 1/22/2017.
*   as project for master 2 first simister
*   i use some of source code from the link
*   http://stackoverflow.com/questions/22552027/crop-image-with-face-detection-in-android
*   and http://stackoverflow.com/questions/19782215/android-facedetector-not-finding-any-faces-findface-returns-0-every-time
*   this class  will detect the faces in an image then expoert each face into the output
*   directory
*   usage is  new extractFacesFromImage("path/to/the/image.jpg","output/direct//");
*
*/


package com.piisoft.upecfacerecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Landmark;
import com.piisoft.upecfacerecognition.utility.Image;

import java.io.File;
import java.util.List;

import static android.R.attr.x;


public class extractFacesFromImage {

    public  extractFacesFromImage(String imagePath ,  String OutPutPath , Context context){
        File folder = new File(OutPutPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        detectFacesInImage(imagePath , OutPutPath, context);
    }

    private  void detectFacesInImage(String imagePath ,  String OutPutPath){
        //ImageWindow[] imageWindow = null;
        Bitmap image =  Image.bitmapFromJpg(imagePath);
        FaceDetector.Face[] faces = detectFaces(image);
        for(FaceDetector.Face fs:faces)
        {
            if(fs == null){continue;}
            PointF midPoint=new PointF();
            fs.getMidPoint(midPoint);
            float eyeDistance=fs.eyesDistance();

            int left = (int)(midPoint.x - (float)(1.4 * eyeDistance));
            int top = (int)(midPoint.y - (float)(1.8 * eyeDistance));

            Bitmap bmFace = Bitmap.createBitmap(image, (int) left, (int) top, (int) (2.8 * eyeDistance), (int) (3.6 * eyeDistance));
            Bitmap bmp= bmFace.createBitmap(bmFace.getWidth(), bmFace.getHeight(), Bitmap.Config.ARGB_8888);
            Image.saveBitmapToJpg(bmp,OutPutPath, "face_" +  System.currentTimeMillis()  +".jpg" ,256,256);

            //ImageWindow Iw = new ImageWindow(fs.)

        }
        //return  imageWindow;
    }


    private  void detectFacesInImage(String imagePath ,  String OutPutPath, Context context){
        //ImageWindow[] imageWindow = null;
        Bitmap image =  Image.bitmapFromJpg(imagePath);
        if(image == null){
            return;
        }
        SparseArray<com.google.android.gms.vision.face.Face> faces = detectFaces(image,context);
        /*
        for(FaceDetector.Face fs:faces)
        {
            if(fs == null){continue;}
            PointF midPoint=new PointF();
            fs.getMidPoint(midPoint);
            float eyeDistance=fs.eyesDistance();

            int left = (int)(midPoint.x - (float)(1.4 * eyeDistance));
            int top = (int)(midPoint.y - (float)(1.8 * eyeDistance));

            Bitmap bmFace = Bitmap.createBitmap(image, (int) left, (int) top, (int) (2.8 * eyeDistance), (int) (3.6 * eyeDistance));
            Image.saveBitmapToJpg(bmFace,OutPutPath, "face_" +  System.currentTimeMillis()  +".jpg" ,256,256);

            //ImageWindow Iw = new ImageWindow(fs.)

        }
        */
        for (int i = 0; i < faces.size(); ++i) {
            com.google.android.gms.vision.face.Face face = faces.valueAt(i);
            if(face == null){continue;}
            try {
                //Bitmap bmFace = Bitmap.createBitmap(image, (int) face.getPosition().x, (int) face.getPosition().y, (int) face.getWidth(), (int) face.getHeight());
                /*int widthDiffrance = (int) (face.getHeight() - face.getWidth())/2;
                Bitmap bmFace = Bitmap.createBitmap(image, (int) face.getPosition().x - widthDiffrance, (int) face.getPosition().y , (int) face.getHeight(), (int) face.getHeight());
                Image.saveBitmapToJpg(bmFace, OutPutPath, "face_" + System.currentTimeMillis() + ".jpg",256,256);*/
                //Bitmap bmFace = Bitmap.createBitmap(image, (int) face.getPosition().x, (int) face.getPosition().y, (int) face.getWidth(), (int) face.getHeight());

               /*
                please use lanMArk.get(0).getType() == Landmark.LEFT_EYE
                List<Landmark> lanMArk = face.getLandmarks();
                int X = (int) lanMArk.get(0).getPosition().x;
                int Y = (int) lanMArk.get().getPosition().y - 25;
                int width = x + (int) lanMArk.get(Landmark.RIGHT_EAR).getPosition().x;
                int height = Y +  (int) lanMArk.get(Landmark.BOTTOM_MOUTH).getPosition().x + 25;
                Bitmap bmFace2 = Bitmap.createBitmap(image, X, Y, width, height);
                Image.saveBitmapToJpg(bmFace2, OutPutPath, "_only_face_" + System.currentTimeMillis() + ".jpg",256);*/

                Bitmap bmFace = Bitmap.createBitmap(image, (int) face.getPosition().x  , (int) face.getPosition().y , (int) face.getWidth()  , (int) face.getHeight() );
                Image.saveBitmapToJpg(bmFace, OutPutPath, "_only_face_" + System.currentTimeMillis() + ".jpg",256);
            }
            catch (Exception e){
                e.printStackTrace();/*
                try{
                    Bitmap bmFace = Bitmap.createBitmap(image, (int) face.getPosition().x, (int) face.getPosition().y, (int) face.getWidth(), (int) face.getHeight());
                    Image.saveBitmapToJpg(bmFace, OutPutPath, "face_" + System.currentTimeMillis() + ".jpg",256,256);

                }catch (Exception e1){
                    e1.printStackTrace();
                }*/
            }
            /*
            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
            }
            */
        }
        //new FaceDetectorOpenCv(context,imagePath,OutPutPath);
        new File(imagePath).delete();
    }




    private SparseArray<com.google.android.gms.vision.face.Face> detectFaces(Bitmap image , Context context) {

        int h = image.getHeight();
        int w = image.getWidth();
        int max = 10;
        Frame frame = new Frame.Builder().setBitmap(image).build();
        com.google.android.gms.vision.face.FaceDetector detector = new com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                .build();
        SparseArray<com.google.android.gms.vision.face.Face> faces = detector.detect(frame);
        detector.release();
        return  faces;


        /*ImageView pic2 = (ImageView) findViewById(R.id.imageView3);
        pic2.setImageBitmap(image);*/

       // int facesFound = detector.findFaces(image, faces);
       /// return  faces;
        //TextView result = (TextView) findViewById(R.id.textView3);
        /*
        if(facesFound>5){
            result.setText("There are " + facesFound + " faces in this picture, therefore you have a crowd!");
        }
        else{
            result.setText("There are only " + facesFound + " faces in this picture, therefore you do not have a crowd!");
        }
        */

    }

    private FaceDetector.Face[] detectFaces(Bitmap image ) {

        int h = image.getHeight();
        int w = image.getWidth();
        int max = 10;

        FaceDetector detector = new FaceDetector(w, h, max);
        FaceDetector.Face[] faces = new FaceDetector.Face[max];

        /*ImageView pic2 = (ImageView) findViewById(R.id.imageView3);
        pic2.setImageBitmap(image);*/

        int facesFound = detector.findFaces(image, faces);
        return  faces;
        //TextView result = (TextView) findViewById(R.id.textView3);
        /*
        if(facesFound>5){
            result.setText("There are " + facesFound + " faces in this picture, therefore you have a crowd!");
        }
        else{
            result.setText("There are only " + facesFound + " faces in this picture, therefore you do not have a crowd!");
        }
        */

    }


}
