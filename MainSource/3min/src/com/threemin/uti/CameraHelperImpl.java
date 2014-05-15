package com.threemin.uti;

import android.hardware.Camera;

import com.threemin.uti.CameraHelper.CameraInfo2;

public interface CameraHelperImpl {
	 int getNumberOfCameras();

     Camera openCamera(int id);

     Camera openDefaultCamera();

     Camera openCameraFacing(int facing);

     boolean hasCamera(int cameraFacingFront);

     void getCameraInfo(int cameraId, CameraInfo2 cameraInfo);
}
