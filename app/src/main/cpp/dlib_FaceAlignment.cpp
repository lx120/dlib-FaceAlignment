#include <jni.h>
#include <iostream>
#include "dlib/image_processing/frontal_face_detector.h"
#include "dlib/image_processing/render_face_detections.h"
#include "dlib/image_processing.h"
#include "dlib/image_io.h"
#include "dlib/opencv/cv_image.h"

#include "dlib/opencv/cv_image.h"
#include "dlib/image_loader/load_image.h"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/core/core.hpp"
#include "opencv2/core/types_c.h"


using namespace dlib;
using namespace std;
using namespace cv;

shape_predictor sp;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jintArray JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceAlignment_Dlib_1API_native_1DetectLmsMat
(JNIEnv *env, jobject obj, jlong imgAddr, jintArray det, jlong objAddr)
{
    jint *rect = env->GetIntArrayElements(det,0);
    if(imgAddr == 0 || rect == NULL || objAddr == 0)
        return NULL;
    cv::Mat &inputImage = *((cv::Mat*)imgAddr);
    inputImage.convertTo(inputImage, CV_8UC3);
    dlib::cv_image<dlib::bgr_pixel> img(inputImage);  // error

    sp = *((shape_predictor *)objAddr);

    dlib::rectangle face_det(rect[0], rect[1], rect[0]+rect[2]-1, rect[1]+rect[3]-1);

    full_object_detection shape = sp(img, face_det);

    int length = (int)shape.num_parts();
    int *ld_index = (int*)malloc(sizeof(int)*length*2);
    for (int i = 0; i < length; i++)
    {
        ld_index[i] = (int) shape.part(i).x();
        ld_index[i+length] = (int) shape.part(i).y();
        cv::circle(inputImage, cvPoint((int)shape.part(i).x(), (int)shape.part(i).y()), 2, cv::Scalar(0, 0, 255), -1);
    }
    jintArray detected_landmarks = env->NewIntArray(length*2);
    env->SetIntArrayRegion(detected_landmarks, 0, length*2, ld_index);
    free(ld_index);

    return detected_landmarks;
}

JNIEXPORT jintArray JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceAlignment_Dlib_1API_native_1DetectLmsString
(JNIEnv *env, jobject obj, jstring imgPath, jintArray det, jstring modelPath)
{
    const char* c_imgPath = env->GetStringUTFChars(imgPath,0);
    const char* c_modelPath = env->GetStringUTFChars(modelPath,0);
    jint *rect = env->GetIntArrayElements(det,0);
    if(c_imgPath == NULL || rect == NULL || c_modelPath == NULL){
        return NULL;
    }

    //deserialize(c_modelPath) >> sp;
    //array2d<rgb_pixel> img;
    //load_image(img, c_imgPath);

    cv::Mat inputImage = cv::imread(c_imgPath, CV_LOAD_IMAGE_COLOR);
    dlib::cv_image<dlib::bgr_pixel> img(inputImage);

    dlib::rectangle face_det(rect[0], rect[1], rect[0]+rect[2]-1, rect[1]+rect[3]-1);

    // detect lanmarks
    full_object_detection shape = sp(img, face_det);

    int length = (int)shape.num_parts();
    int *ld_index = (int*)malloc(sizeof(int)*length*2);
    for (int i = 0; i < length; i++)
    {
        ld_index[i] = (int) shape.part(i).x();
        ld_index[i+length] = (int) shape.part(i).y();
        //cv::circle(*inputImage, cvPoint((int)shape.part(i).x(), (int)shape.part(i).y()), 2, cv::Scalar(0, 0, 255), -1);
    }
    jintArray detected_landmarks = env->NewIntArray(length*2);
    env->SetIntArrayRegion(detected_landmarks, 0, length*2, ld_index);
    free(ld_index);

    return detected_landmarks;
}

JNIEXPORT jintArray JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceAlignment_Dlib_1API_native_1DetectLmsByte
(JNIEnv *env, jobject obj, jbyteArray imgData, jintArray det, jstring modelPath,jint width, jint height)
{
    jbyte *cbuf;
    cbuf = env->GetByteArrayElements(imgData, JNI_FALSE );
    jint *rect = env->GetIntArrayElements(det,0);
    const char* c_modelPath = env->GetStringUTFChars(modelPath,0);
    if (cbuf == NULL || rect == NULL || c_modelPath == NULL) {
        return NULL;
    }

    cv::Mat inputImage(height, width, CV_8UC4, (unsigned char *) cbuf);
    inputImage.convertTo(inputImage, CV_8UC3);
    dlib::cv_image<dlib::bgr_pixel> img(inputImage);  // error

    deserialize(c_modelPath) >> sp;

    //frontal_face_detector detector = get_frontal_face_detector();
    // std::vector<dlib::rectangle> dets = detector(img,0.0);

    dlib::rectangle face_det(rect[0], rect[1], rect[0]+rect[2]-1, rect[1]+rect[3]-1);

    // detect lanmarks
    full_object_detection shape = sp(img, face_det);

    int length = (int)shape.num_parts();
    int *ld_index = (int*)malloc(sizeof(int)*length*2);
    for (int i = 0; i < length; i++)
    {
        ld_index[i] = (int) shape.part(i).x();
        ld_index[i+length] = (int) shape.part(i).y();
        //cv::circle(*inputImage, cvPoint((int)shape.part(i).x(), (int)shape.part(i).y()), 2, cv::Scalar(0, 0, 255), -1);
    }
    jintArray detected_landmarks = env->NewIntArray(length*2);
    env->SetIntArrayRegion(detected_landmarks, 0, length*2, ld_index);
    free(ld_index);

    return detected_landmarks;
}

JNIEXPORT jlong JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceAlignment_Dlib_1API_native_1createLmsDetectObj
(JNIEnv *env, jobject obj, jstring modelPath)
{
    const char* c_modelPath = env->GetStringUTFChars(modelPath,0);
    if(c_modelPath == NULL){
        return 0;
    }
    dlib::deserialize(c_modelPath) >> sp;
    return (long)(&sp);
}

JNIEXPORT void JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceAlignment_Dlib_1API_native_1destroyLmsDetectObj
(JNIEnv *env, jobject obj, jlong objAddr)
{
    if(objAddr != 0){
        delete (shape_predictor *)objAddr;
    }
}

#ifdef __cplusplus
}
#endif
