#include <jni.h>
#include <cstdint>
#include <iostream>
#include <strstream>

#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "face_detection.h"

using namespace std;
using namespace cv;

seeta::FaceDetection *detector;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jintArray JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceDetection_SeetaFace_1API_native_1FaceDetectionMat
(JNIEnv *env, jobject obj, jlong imgAddr, jlong objAddr)
{
    if(imgAddr == 0 || objAddr ==0)
        return NULL;
    cv::Mat &img = *(cv::Mat *)imgAddr;
    detector = (seeta::FaceDetection *)objAddr;

    cv::Mat img_gray;
    if (img.channels() != 1)
        cv::cvtColor(img, img_gray, cv::COLOR_BGR2GRAY);
    else
        img_gray = img;

    seeta::ImageData img_data;
    img_data.data = img_gray.data;
    img_data.width = img_gray.cols;
    img_data.height = img_gray.rows;
    img_data.num_channels = 1;

       std::vector<seeta::FaceInfo> faces = detector->Detect(img_data);

    int32_t num_face = static_cast<int32_t>(faces.size());
    if(0 == num_face ){
        return NULL;
    }
    const int arr_length = num_face*4;
    int *face_index = (int*)malloc(sizeof(int)*arr_length);
    for(int i = 0; i<num_face; i++){
        face_index[i*4+0] = faces[i].bbox.x;
        face_index[i*4+1] = faces[i].bbox.y;
        face_index[i*4+2] = faces[i].bbox.width;
        face_index[i*4+3] = faces[i].bbox.height;

        // __android_log_print(ANDROID_LOG_INFO,"CMDetectFace,..............................人脸坐标： ","i=%d",face_index[i*4+0]);
    }
    jintArray detected_faces = env->NewIntArray(arr_length);
    env->SetIntArrayRegion(detected_faces, 0, arr_length,face_index);
    free(face_index);
    return detected_faces;
}

JNIEXPORT jintArray JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceDetection_SeetaFace_1API_native_1FaceDetectionString
(JNIEnv *env, jobject obj, jstring imgPath, jlong objAddr)
{
    const char* c_imgPath = env->GetStringUTFChars(imgPath,0);
    if (c_imgPath == NULL || objAddr == 0)
        return NULL;
    cv::Mat img = cv::imread(static_cast<string>(c_imgPath));
    detector = (seeta::FaceDetection *)objAddr;

    cv::Mat img_gray;
    if (img.channels() != 1)
        cv::cvtColor(img, img_gray, cv::COLOR_BGR2GRAY);
    else
        img_gray = img;

    seeta::ImageData img_data;
    img_data.data = img_gray.data;
    img_data.width = img_gray.cols;
    img_data.height = img_gray.rows;
    img_data.num_channels = 1;

    std::vector<seeta::FaceInfo> faces = detector->Detect(img_data);

    int32_t num_face = static_cast<int32_t>(faces.size());
    if(0 == num_face ){
        return NULL;
    }
    const int arr_length = num_face*4;
    int *face_index = (int*)malloc(sizeof(int)*arr_length);
    for(int i = 0; i<num_face; i++){
        face_index[i*4+0] = faces[i].bbox.x;
        face_index[i*4+1] = faces[i].bbox.y;
        face_index[i*4+2] = faces[i].bbox.width;
        face_index[i*4+3] = faces[i].bbox.height;
    }
    jintArray detected_faces = env->NewIntArray(arr_length);
    env->SetIntArrayRegion(detected_faces, 0, arr_length,face_index);
    free(face_index);
    return detected_faces;
}

JNIEXPORT void JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceDetection_SeetaFace_1API_native_1setFaceDetectionParams
(JNIEnv *env, jobject obj, jint MinFaceSize, jfloat ScoreThresh, jfloat ScaleFactor, jint W_step, jint H_step)
{
    if(detector == NULL)
        return;
    detector->SetMinFaceSize(MinFaceSize);
    detector->SetScoreThresh(ScoreThresh);
    detector->SetImagePyramidScaleFactor(ScaleFactor);
    detector->SetWindowStep(W_step, H_step);
}

JNIEXPORT jlong JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceDetection_SeetaFace_1API_native_1CreateFaceDetectionObj
(JNIEnv *env, jobject obj, jstring modelPath)
{
    const char* c_modelPath = env->GetStringUTFChars(modelPath,0);
    if (c_modelPath == NULL)
        return 0;
    detector = new seeta::FaceDetection(c_modelPath);
    detector->SetMinFaceSize(40);
    detector->SetScoreThresh(2.f);
    detector->SetImagePyramidScaleFactor(0.8f);
    detector->SetWindowStep(4, 4);

    return (long)detector;
}

JNIEXPORT void JNICALL Java_com_example_lx_dlib_1cmake_1demo2_FaceDetection_SeetaFace_1API_native_1DestroyFaceDetectionObj
        (JNIEnv *env, jobject obj, jlong objAddr)
{
    if (objAddr != 0)
        delete (seeta::FaceDetection *) objAddr;
}

#ifdef __cplusplus
}
#endif
