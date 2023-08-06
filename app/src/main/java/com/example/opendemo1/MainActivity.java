//////印度人第六部完成/////////////
package com.example.opendemo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.dnn.Dnn;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends CameraActivity{

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    boolean startYolo = false;

    boolean firstTimeYolo = false;

    Net tinyYolo;

    public void YOLO(View Buttom) {
        if (startYolo == false){



            startYolo = true;


            if (firstTimeYolo == false){

                firstTimeYolo = true;
                String tinyYoloCfg = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.cfg";
                String tinyYoloWeights = Environment.getExternalStorageDirectory() + "/dnns/yolov3-tiny.weights";

                tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);
            }
        }
        else{
            startYolo = false;
        }
    }

    Mat gray;//buddy的frame變數

    //Mat curr_gray, prev_gray, rgb, diff;//第五部
    //List<MatOfPoint> cnts;
    //boolean is_init;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        //is_init = false;//


        cameraBridgeViewBase = findViewById(R.id.cameraView);

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                /*
                第五部
                curr_gray = new Mat();
                prev_gray = new Mat();
                rgb = new Mat();
                diff = new Mat();
                cnts = new ArrayList<>();
                */
            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                Mat frame = inputFrame.rgba();
            gray = inputFrame.gray();//gray是buddy的frame變數


            if (startYolo==true) {

                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);



                Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(416, 416), new Scalar(0, 0, 0), false, false);

                tinyYolo.setInput(imageBlob);
                tinyYolo.forward();


                /*邊界偵測(黑白)
                Imgproc.Canny(gray, gray, 80, 100);
                Core.bitwise_not(gray, gray);
                */
            }

            return frame;


                /*
            if(!is_init){

                 prev_gray = inputFrame.gray();
                 is_init = true;
                 return prev_gray;
             }

             rgb = inputFrame.rgba();
             curr_gray = inputFrame.gray();

            /////////////////////////////////////
                Core.absdiff(curr_gray, prev_gray, diff);
                Imgproc.threshold(diff, diff, 40, 255, Imgproc.THRESH_BINARY);
                Imgproc.findContours(diff, cnts, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

                Imgproc.drawContours(rgb, cnts, -1, new Scalar(255, 0, 0), 4);

                for(MatOfPoint m:cnts){
                    Rect r = Imgproc.boundingRect(m);
                    Imgproc.rectangle(rgb, r, new Scalar(0, 0, 255), 3);
                }

                cnts.clear();
            //////////////////////////////////
                prev_gray = curr_gray.clone();
                return rgb;

            */
                //return inputFrame.rgba();
            }
        });
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){

                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
        if(OpenCVLoader.initDebug()){
            cameraBridgeViewBase.enableView();


           /* try {
                InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                File file = new File(getDir("cascade", MODE_PRIVATE), "lbpcascade_frontalface.xml");
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] data = new byte[4096];
                int read_bytes;

                while ((read_bytes = inputStream.read(data)) != -1) {
                    fileOutputStream.write(data, 0, read_bytes);
                }

                cascadeClassifier = new CascadeClassifier(file.getAbsolutePath());
                if(cascadeClassifier.empty()) cascadeClassifier = null;


                inputStream.close();
                fileOutputStream.close();
                file.delete();

            }catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    void getPermission(){
        if(checkSelfPermission(Manifest.permission.CAMERA) !=  PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            getPermission();
        }
    }
}