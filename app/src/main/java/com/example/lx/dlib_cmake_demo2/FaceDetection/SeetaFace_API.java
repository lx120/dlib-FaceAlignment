package com.example.lx.dlib_cmake_demo2.FaceDetection;

/**
 * Created by Administrator on 2017/8/6.
 */

public class SeetaFace_API {

    private static long FaceDetection_addr;
    private static String FaceDetection_modelPath;

    static{
        System.loadLibrary("android-seetaface");
    }

    public SeetaFace_API() {
    }

    public int[] FaceDetectionFromMat(long imgAddr){
        if(imgAddr == 0 || FaceDetection_addr == 0)
            return null;

        return native_FaceDetectionMat(imgAddr, FaceDetection_addr);
    }

    public int[] FaceDetectionFromPath(String imgPath){
        if(imgPath == null || FaceDetection_addr == 0)
            return null;

        return native_FaceDetectionString(imgPath, FaceDetection_addr);
    }

    public void setFaceDetectionParams(int MinFaceSize, float ScoreThresh,
                                  float ScaleFactor, int W_step, int H_step){
        if(MinFaceSize < 20)
            MinFaceSize = 20;
        if(ScoreThresh < 0)
            ScoreThresh = 2.f;
        if(ScaleFactor < 0 || ScaleFactor >1)
            ScaleFactor = 0.8f;
        if(W_step < 0)
            W_step = 4;
        if(H_step < 0)
            H_step = 4;
        native_setFaceDetectionParams(MinFaceSize,ScoreThresh,ScaleFactor,W_step,H_step);
    }

    public void CreateFaceDetectionObj(){
        if(FaceDetection_modelPath != null)
            FaceDetection_addr = native_CreateFaceDetectionObj(FaceDetection_modelPath);
    }

    public void DestroyFaceDetectionObj(){
        if (FaceDetection_addr != 0)
            native_DestroyFaceDetectionObj(FaceDetection_addr);
    }

    private native int[] native_FaceDetectionMat(long imgAddr, long objAddr);
    private native int[] native_FaceDetectionString(String imgPath, long objAddr);
    private native void native_setFaceDetectionParams(int MinFaceSize, float ScoreThresh,
                                                     float ScaleFactor, int W_step, int H_step);
    private native long native_CreateFaceDetectionObj(String modelPath);
    private native void native_DestroyFaceDetectionObj(long objAddr);

    public void setFaceDetection_modelPath(String modelPath) {
        FaceDetection_modelPath = modelPath;
    }
}
