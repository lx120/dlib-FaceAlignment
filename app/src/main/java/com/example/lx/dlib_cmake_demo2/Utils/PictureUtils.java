package com.example.lx.dlib_cmake_demo2.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lx on 2017/4/1.
 */
public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, Activity activity){

        Point size = new Point();
        // 获取手机屏幕的宽和高
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);

        return getScaledBitmap(path, size.x, size.y);

    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int desHeight){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;       // 设置不返回真实的Bitmap数据 而是返回图像真实宽高
        BitmapFactory.decodeFile(path, options);   //解析出路径path下的图片

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if(srcWidth > destWidth || srcHeight > desHeight){
            if (srcWidth >destWidth){
                inSampleSize = Math.round(srcWidth/destWidth);
            }else{
                inSampleSize = Math.round(srcHeight/desHeight);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;  // 控制图片缩放倍数

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap resizeBitmap(Bitmap bm, int w, int h){
        int width = bm.getWidth();
        int height = bm.getHeight();

        // 计算缩放比例
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);

        return newbm;
    }

    public static void saveImage(Bitmap bm, String savePath)
    {
        //String path = "/sdcard/"+bm.toString()+".jpg";
        try
        {
            FileOutputStream fos = new FileOutputStream(savePath);
            bm.compress(Bitmap.CompressFormat.JPEG,100, fos);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
