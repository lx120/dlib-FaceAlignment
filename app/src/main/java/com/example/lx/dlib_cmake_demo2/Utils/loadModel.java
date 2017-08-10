package com.example.lx.dlib_cmake_demo2.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lx on 2017/4/2.
 */
public class loadModel {

    private static final String MODEL_FOLDER = "model";
    private static final String facedetectionName = "seeta_fd_frontal_v1.0.bin";
    private static final String facealignmentName = "shape_predictor_68_face_landmarks.dat";

    private String facedetectionPath = "";
    private String facealignmentPath = "";

    private AssetManager mAssets;
    private Context mContext;

    public loadModel(Context context ) {
        mContext = context;
        mAssets = context.getAssets();
    }

    public boolean loadModel_to_storage() throws IOException {
        File externalFilesDir = mContext.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS);

        String[] modelNames = mAssets.list(MODEL_FOLDER);
        for (String modelName : modelNames){
            File modelFile = new File(externalFilesDir, modelName);
            if(modelName.equals(facedetectionName)){
                facedetectionPath = modelFile.getAbsolutePath();
            }
            if (modelName.equals(facealignmentName)){
                facealignmentPath = modelFile.getAbsolutePath();
            }

            if (modelFile.exists()){
                continue;
            }else{
                modelFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(modelFile.getAbsolutePath());
            InputStream is = mAssets.open(MODEL_FOLDER+"/"+modelName);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            is.close();
            fos.close();
        }
        return true;
    }

    public String getFacedetectionPath(){
        return facedetectionPath;
    }

    public String getFacealignmentPath() {
        return facealignmentPath;
    }
}
