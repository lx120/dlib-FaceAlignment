package com.example.lx.dlib_cmake_demo2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lx.dlib_cmake_demo2.FaceAlignment.Dlib_API;
import com.example.lx.dlib_cmake_demo2.FaceDetection.SeetaFace_API;
import com.example.lx.dlib_cmake_demo2.Utils.PictureUtils;
import com.example.lx.dlib_cmake_demo2.Utils.imageProcess;
import com.example.lx.dlib_cmake_demo2.Utils.loadModel;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO = 1;
    private boolean isCreateObj = false;
    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    private ImageView mPhotoView;
    private MyZoomImageView mDetectedImgView;
    private ImageButton mPhotoButton;
    private TextView mTextView;

    private File currentImageFile;
    private Bitmap photoImage;

    private loadModel mloadMode;
    private SeetaFace_API seetaface;
    private Dlib_API dlib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_and_title);

        mloadMode = new loadModel(this);
        dlib = new Dlib_API();
        seetaface = new SeetaFace_API();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    mloadMode.loadModel_to_storage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                seetaface.setFaceDetection_modelPath(mloadMode.getFacedetectionPath());
                dlib.setLmsDetection_modelPath(mloadMode.getFacealignmentPath());
                seetaface.CreateFaceDetectionObj();
                dlib.createFaceAlignmentObj();
                isCreateObj = true;
            }
        }.start();

        // 绑定View
        BindView();
    }

    // 照相触发事件
    public void takePhotoClick(View v){
        startActivityForResult(captureImage, REQUEST_PHOTO);
    }

    // 人脸检测和对齐触发事件
    public void FacedetectionAndAlignmentClick(View v){
        if(photoImage == null){
            photoImage = BitmapFactory.decodeResource(getResources(), R.drawable.demo1);
            mPhotoView.setImageBitmap(photoImage);
            //photoImage = PictureUtils.resizeBitmap(photoImage,640,480);
            PictureUtils.saveImage(photoImage,currentImageFile.getAbsolutePath());
        }
        while(!isCreateObj){}

        Mat cvImg = imageProcess.bitmapToMat(photoImage);
        // 1.facedetection
        int[] faces = detectFace(cvImg);
        if(faces == null)
            return;
        int nums = faces.length/4;
        // 2.facealignment
        long startTime, consumingTime;
        int[] lms;
        startTime = System.currentTimeMillis();
        for (int i = 0; i<nums; i++){
            int[] det = new int[]{faces[i*4],faces[i*4+1],faces[i*4+2],faces[i*4+3]};
            lms = dlib.FaceAlignmentFromPath(currentImageFile.getAbsolutePath(),det);
            imageProcess.drawLandmarksToMat(cvImg,lms);
        }
        consumingTime = System.currentTimeMillis() - startTime;
        mTextView.append("face alignment cost: " + consumingTime);
        mDetectedImgView.setImageBitmap(imageProcess.matToBitmap(cvImg));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        // 获得拍照得到的图片，Bitmap格式
        if (requestCode == REQUEST_PHOTO){
            // 原图
            photoImage = BitmapFactory.decodeFile(currentImageFile.getAbsolutePath());
            // 压缩图
            //photoImage = PictureUtils.getScaledBitmap(currentImageFile.getAbsolutePath(), this);
            int w = (photoImage.getHeight()>photoImage.getWidth())?480:640;
            int h = (photoImage.getHeight()>photoImage.getWidth())?640:480;
            photoImage = PictureUtils.resizeBitmap(photoImage,w,h);
            PictureUtils.saveImage(photoImage, currentImageFile.getAbsolutePath());
            mPhotoView.setImageBitmap(photoImage);
            Log.d("window_size: ",photoImage.getWidth()+"......"+photoImage.getHeight());
        }

    }

    private void BindView() {

        mDetectedImgView = (MyZoomImageView)findViewById(R.id.srcImagView);
        mPhotoView = (ImageView)findViewById(R.id.photo);
        mTextView = (TextView)findViewById(R.id.text_1);
        mPhotoButton = (ImageButton)findViewById(R.id.camera);

        //final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);       // MediaStore.ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE"
        boolean canTakePhoto = (captureImage.resolveActivity(getPackageManager())!=null);
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto){
            Uri imageUri;

            currentImageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"temp.jpg");
            // /storage/emulated/0/Android/data/com.facealignment.brl.facedetectiondemo1/files/Pictures/temp.jpg

            if(!currentImageFile.exists()){
                try {
                    currentImageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                imageUri = Uri.fromFile(currentImageFile);
            }else{
                // Android7.0以上调用
                String name = this.getPackageName() + ".fileprovider";
                imageUri = FileProvider.getUriForFile(MainActivity.this, name, currentImageFile);
            }

            // 设置拍照得到的照片的保存路径
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
    }

    private int[] detectFace(Mat cvImg){
        long startTime, consumingTime;
        startTime = System.currentTimeMillis();
        int[] faces = seetaface.FaceDetectionFromMat(cvImg.getNativeObjAddr());
        consumingTime = System.currentTimeMillis() - startTime;
        if (faces == null) {
            Toast.makeText(this, "detecte face: " + 0, Toast.LENGTH_LONG).show();
            mTextView.setText("detect face: " + 0);
            return null;
        }
        int nums = faces.length/4;
        mTextView.setText("image size(w*h): " + photoImage.getWidth() + "*" + photoImage.getHeight()+"\n");
        mTextView.append("detect faces: " + nums+"\n");
        mTextView.append("detect face cost: " + consumingTime + "\n");
        return faces;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        seetaface.DestroyFaceDetectionObj();
        dlib.destroyFaceAlignmentObj();
    }

}
