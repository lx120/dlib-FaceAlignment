package com.example.lx.dlib_cmake_demo2.FaceAlignment;

/**
 * Created by Administrator on 2017/8/2.
 */

public class Dlib_API {

    private static long LmsDetection_addr = 0;
    private String LmsDetection_modelPath;

    static{
        System.loadLibrary("android-dlib");
    }

    public Dlib_API() {
    }

    public int[] FaceAlignmentFromMat(long imgAddr, int[] det){
        if (imgAddr == 0 || det == null || det.length%4!=0)
            return null;

        return native_DetectLmsMat(imgAddr, det, LmsDetection_addr);
    }

    public int[] FaceAlignmentFromPath(String imgPath, int[] det){
        if (imgPath == null || det == null || det.length%4!=0 || LmsDetection_modelPath == null)
            return null;

        return native_DetectLmsString(imgPath, det, LmsDetection_modelPath);
    }

    public int[] FaceAlignmentFromByte(byte[] imgData, int[] det, int width,int height){
        if (imgData == null || det == null || width == 0 || height == 0)
            return null;
        return native_DetectLmsByte(imgData,det,LmsDetection_modelPath,width,height);
    }

    public void createFaceAlignmentObj(){
        if(LmsDetection_modelPath == null || LmsDetection_addr!=0)
            return;
        LmsDetection_addr =  native_createLmsDetectObj(LmsDetection_modelPath);
    }

    public void destroyFaceAlignmentObj(){
        if (LmsDetection_addr != 0) {
            native_destroyLmsDetectObj(LmsDetection_addr);
            LmsDetection_addr = 0;
        }
    }

    // can't use native_DetectLmsMat() && native_DetectLmsByte()
    private native int[] native_DetectLmsMat(long imgAddr, int[] det, long objAddr);
    private native int[] native_DetectLmsString(String imgPath, int[] det, String modelPath);
    private native int[] native_DetectLmsByte(byte[] imgData, int[] det, String modelPath,int width,int height);
    private native long native_createLmsDetectObj(String modelPath);
    private native void native_destroyLmsDetectObj(long objAddr);

    public void setLmsDetection_modelPath(String modelPath) {
        LmsDetection_modelPath = modelPath;
    }
}
