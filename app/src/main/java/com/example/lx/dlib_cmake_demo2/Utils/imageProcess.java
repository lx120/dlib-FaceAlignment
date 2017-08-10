package com.example.lx.dlib_cmake_demo2.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

/**
 * Created by lx on 2017/4/5.
 */
public class imageProcess {

    public static byte[] getBitmapData(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the

        return temp;
    }

    public static int getChannelsOfimg(byte[] img, int width,int height){
        int ch = img.length / (width*height);
        if (ch < 1){
            ch = 1;
        }
        return ch;
    }

    public static Bitmap drawFaceRect(Bitmap src,int[] faces, int face_num){
        Bitmap mBitmapDisplayed = src.copy(src.getConfig(),true);
        Canvas canvas = new Canvas();   //
        canvas.setBitmap(mBitmapDisplayed);
        Paint paint = new Paint();

        int tStokeWid = 1+(src.getWidth()+src.getHeight())/300;
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);//不填充
        paint.setStrokeWidth(tStokeWid);  //线的宽度

        for(int i=0; i<face_num; i++){
            int x = faces[i*4+0];
            int y = faces[i*4+1];
            int w = faces[i*4+2];
            int h = faces[i*4+3];
            int left = x ;
            int top = y ;
            int right = x + w;
            int bottom = y + w;

            canvas.drawRect(left, top, right, bottom, paint);
        }

        return mBitmapDisplayed;
    }

    public static Mat bitmapToMat(Bitmap Imag){
        Mat re = new Mat();
        Utils.bitmapToMat(Imag, re);
        return re;
    }

    public static Bitmap matToBitmap(Mat Imag){
        Bitmap re = Bitmap.createBitmap(Imag.cols(), Imag.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(Imag, re);

        return re;
    }

    public static Bitmap drawFaceRectandlandmarks(Bitmap src,int[] landmarks, int[] faces, int face_num){
        Bitmap mBitmapDisplayed = src.copy(src.getConfig(),true);
        Canvas canvas = new Canvas();   //
        canvas.setBitmap(mBitmapDisplayed);
        Paint paint = new Paint();

        int tStokeWid = 1+(src.getWidth()+src.getHeight())/3000;
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);//不填充
        paint.setStrokeWidth(tStokeWid);  //线的宽度

        for(int i=0; i<face_num; i++){
            int x = faces[i*4+0];
            int y = faces[i*4+1];
            int w = faces[i*4+2];
            int h = faces[i*4+3];
            int left = x ;
            int top = y ;
            int right = x + w;
            int bottom = y + w;

            canvas.drawRect(left, top, right, bottom, paint);
        }

        int num_landmarks = landmarks.length/2;
        for (int i = 0; i< num_landmarks; i++){
            canvas.drawCircle(landmarks[i], landmarks[i+num_landmarks], tStokeWid, paint);
        }

        return mBitmapDisplayed;
    }

    public static void drawLandmarksToMat(Mat img, int[] lms){
        int length = lms.length/2;
        for(int i =0;i<length;i++){
            Imgproc.circle(img,new Point(lms[i],lms[i+length]),0,new Scalar(255,255,0),2);
        }
    }

}
