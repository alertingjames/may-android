package bookclub.technion.maymsgphoto.utils.photoutils;

/**
 * Created by a on 5/17/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ARManager implements SurfaceHolder.Callback {

    Activity activity;
    Context context;
    SurfaceView cameraView;
    Camera.PreviewCallback previewCallback;
    Runnable cameraOpenedCallback;
    Runnable cameraStartedCallback;
    boolean cameraInitialized = false;

    Camera camera;
    Camera.Parameters params;
    boolean cameraViewReady = false;
    int cameraId = 0;
    int currentZoomLevel = 1, maxZoomLevel = 0;

    int preferredPreviewWidth = 0, preferredPreviewHeight = 0;
    int numPreviewCallbackBuffers = 0;

    public ARManager(Activity _activity, SurfaceView _cameraView, Camera.PreviewCallback _previewCallback) {
        this.activity = _activity;
        this.cameraView = _cameraView;
        this.previewCallback = _previewCallback;
    }

    public void setupCameraView() {
        cameraView.getHolder().addCallback(this);
        cameraView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public static ARManager createAndSetupCameraView(Activity _activity, SurfaceView _cameraView, Camera.PreviewCallback _previewCallback) {
        ARManager manager = new ARManager(_activity, _cameraView, _previewCallback);
        manager.setupCameraView();
        return manager;
    }

    public void setPreferredPreviewSize(int width, int height) {
        this.preferredPreviewWidth = width;
        this.preferredPreviewHeight = height;
    }

    public void setNumberOfPreviewCallbackBuffers(int n) {
        this.numPreviewCallbackBuffers = n;
    }

    public void setCameraOpenedCallback(Runnable callback) {
        cameraOpenedCallback = callback;
    }

    public void setCameraStartedCallback(Runnable callback) {
        cameraStartedCallback = callback;
    }

    public boolean startCamera() {
        if (camera==null) {
            try {
                camera = CameraUtils.openCamera(cameraId);
                params = camera.getParameters();
                if (!cameraInitialized && cameraOpenedCallback!=null) {
                    cameraOpenedCallback.run();
                }
                camera.setPreviewDisplay(cameraView.getHolder());
                if (preferredPreviewWidth>0 && preferredPreviewHeight>0) {
                    CameraUtils.setNearestCameraPreviewSize(camera, preferredPreviewWidth, preferredPreviewHeight);
                }

                if (numPreviewCallbackBuffers > 0) {
                    CameraUtils.createPreviewCallbackBuffers(camera, this.numPreviewCallbackBuffers);
                    CameraUtils.setPreviewCallbackWithBuffer(camera, this.previewCallback);
                }
                else {
                    camera.setPreviewCallback(this.previewCallback);
                }

                if (CameraUtils.getCameraInfo(this.cameraId).isRotated180Degrees()) {
                    // We need both camera.setDisplayOrientation to rotate the preview image, and
                    // Camera.Parameters.setRotation to rotate the actual pictures that we take.
                    camera.setDisplayOrientation(180);
                    Camera.Parameters params = camera.getParameters();
                    params.setRotation(180);
                    camera.setParameters(params);


                }

                camera.startPreview();
                if (!cameraInitialized && cameraStartedCallback!=null) {
                    cameraStartedCallback.run();
                }
                cameraInitialized = true;
            }
            catch(Exception ex) {
                camera = null;
            }
        }
        return (camera!=null);
    }


    public void startCameraIfVisible() {
        if (cameraViewReady) {
    //        params = camera.getParameters();
            startCamera();
        }
    }

    public void stopCamera() {
        if (camera!=null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void switchToCamera(int _cameraId) {
        if (camera!=null) {
            stopCamera();
        }
        this.cameraId = _cameraId;
        this.cameraInitialized = false;
        startCameraIfVisible();
    }

    public void switchToNextCamera() {
        switchToCamera((cameraId + 1) % CameraUtils.numberOfCameras());
    }

    // SurfaceHolder callbacks
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.cameraViewReady = true;
        startCameraIfVisible();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // all done in surfaceChanged
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.cameraViewReady = false;
        stopCamera();
    }


    public Camera getCamera() {
        return camera;
    }
    public int getCameraId() {
        return cameraId;
    }

    public void zoomin(float m){
        if(params.isZoomSupported()){
            params = camera.getParameters();
            maxZoomLevel = params.getMaxZoom();

            float zoom = (float)currentZoomLevel;
            if( m > 1)
                zoom +=  m;
            else
                zoom = zoom *m;

            currentZoomLevel = (int)zoom;

            if(currentZoomLevel > maxZoomLevel)
                currentZoomLevel = maxZoomLevel;
//            if(currentZoomLevel < 1)
//                currentZoomLevel = 1;

            params.setZoom(currentZoomLevel);
            camera.setParameters(params);

        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
