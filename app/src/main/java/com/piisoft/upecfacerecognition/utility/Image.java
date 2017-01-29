package com.piisoft.upecfacerecognition.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by a on 1/23/2017.
 */

public class Image {
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    public static  Bitmap bitmapFromJpg(String ImagePath){
        BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig= Bitmap.Config.ARGB_8888;
        return convert(BitmapFactory.decodeFile(ImagePath ,bitmapFatoryOptions), Bitmap.Config.ARGB_8888);
    }

    public static  Bitmap bitmapFromJpg(File ImagePath){
        BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig= Bitmap.Config.ARGB_8888;
        return convert( BitmapFactory.decodeFile(ImagePath.getAbsolutePath() ,bitmapFatoryOptions), Bitmap.Config.ARGB_8888);
    }


    public static void saveBitmapToJpg(Bitmap bitmap, String path, String IamgeName , int scaledWidth , int scaledHeight ) {
        Bitmap dest = scaleCenterCrop(bitmap , scaledWidth,scaledHeight);
        saveBitmapToJpg(dest, path,  IamgeName);
    }


    public static void saveBitmapToJpg(Bitmap bitmap, String path, String IamgeName) {

        File folder = new File(path);
        OutputStream fOut = null;
        File file = new File(path, IamgeName); // the File to save to
        try

        {
            if (!folder.exists()) {
                folder.mkdirs();
            }
            fOut = new FileOutputStream(file);
            bitmap = toGrayscale(bitmap);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            //MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            fOut.flush();
            fOut.close(); // do not forget to close the stream
            bitmap = null;
        } catch (FileNotFoundException exception)
        {
            exception.printStackTrace();

        } catch (IOException e)

        {
            e.printStackTrace();
        }
    }

    private static Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

// Compute the scaling factors to fit the new height and width, respectively.
// To cover the final image, the final scaling will be the bigger
// of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

// Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

// Let's find out the upper left coordinates if the scaled bitmap
// should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

// The target rectangle for the new, scaled version of the source bitmap will now
// be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

// Finally, we create a new bitmap of the specified size and draw our new,
// scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }
}
