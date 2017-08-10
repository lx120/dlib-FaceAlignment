cmake_minimum_required(VERSION 3.4.1)

# Use C++11 will case error, can not create dlib shape obj
# set(CMAKE_CXX_STANDARD 11)
# set(CMAKE_CXX_STANDARD_REQUIRED ON)
# 设置支持-std=gnu+++11特性
set(CMAKE_VERBOSE_MAKEFILE on)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/../libs/${ANDROID_ABI})

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/../../../../opencv)
set(SeetaFace_DIR ${CMAKE_SOURCE_DIR}/../../../../seetaface)
set(Dlib_DIR ${CMAKE_SOURCE_DIR}/../../../../dlib)

add_library(lib_opencv SHARED IMPORTED)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION
    ${OpenCV_DIR}/lib/${ANDROID_ABI}/libopencv_java3.so
)

add_library(lib_seeta_facedet SHARED IMPORTED)
set_target_properties(lib_seeta_facedet PROPERTIES IMPORTED_LOCATION
    ${SeetaFace_DIR}/lib/${ANDROID_ABI}/libseeta_facedet_lib.so
)

add_library(lib_dlib STATIC IMPORTED)
set_target_properties(lib_dlib PROPERTIES
                      IMPORTED_LOCATION
                      "${Dlib_DIR}/lib/${ANDROID_ABI}/libdlib.a")

INCLUDE_DIRECTORIES(${OpenCV_DIR}/include)
INCLUDE_DIRECTORIES(${SeetaFace_DIR}/include)
# INCLUDE_DIRECTORIES(./dlib)

add_library( android-seetaface

             SHARED

             seetaface_detection.cpp )

find_library( log-lib
              log )

target_link_libraries(
                       android-seetaface

                       lib_opencv
                       lib_seeta_facedet
                       ${log-lib} )

add_library( android-dlib

             SHARED

             dlib_FaceAlignment.cpp )

target_link_libraries( android-dlib

                       lib_opencv
                       lib_dlib
                       ${log-lib} )
# ADD_SUBDIRECTORY(one)
