package bookclub.technion.maymsgphoto.main.takephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.main.BranchActivity;
import bookclub.technion.maymsgphoto.utils.photoutils.ARManager;
import bookclub.technion.maymsgphoto.utils.photoutils.AndroidUtils;
import bookclub.technion.maymsgphoto.utils.photoutils.CameraUtils;
import bookclub.technion.maymsgphoto.utils.photoutils.ShutterButton;
import bookclub.technion.maymsgphoto.utils.photoutils.ShutterButton.OnShutterButtonListener;

import static bookclub.technion.maymsgphoto.commons.Commons.file;

public class TakePhotoActivity extends Activity implements Camera.PictureCallback, Camera.AutoFocusCallback, OnShutterButtonListener {

    static final List<Integer> DELAY_DURATIONS = Arrays.asList(0, 5, 15, 30);
    static final int DEFAULT_DELAY = 5;
    static final String DELAY_PREFERENCES_KEY = "delay";
    int pictureDelay = DEFAULT_DELAY;

    static final String FLASH_MODE_AUTO = "auto";
    static final String FLASH_MODE_ON = "on";
    static final String FLASH_MODE_OFF = "off";

    Map<String, String> flashButtonLabels = new HashMap<String, String>();

    ARManager arManager;
    SurfaceView cameraView;
    int[] maxCameraViewSize;
    int currentZoomLevel = 0, maxZoomLevel = 0;

    ShutterButton shutterButton;
    TextView pictureDelayButton;
    TextView cancelPictureButton;
    TextView switchCameraButton, switchCameraButtonCaption;
    TextView flashButton,flashCaption;
    TextView rotateButton;
    TextView numberOfPicturesButton, numberOfPicturesButtonCaption;
    TextView statusTextField, text1, text2;
    LinearLayout statusFrame;
    ZoomControls zoomControls;

    float zoom=0.0f;
    int zoomint=0;

    boolean orient=false;

    private float oldDist = 1f;
    private PointF mid = new PointF();
    private int mode = 0;
    private boolean down = false;
    private boolean previewing = false;
    private static boolean isSelfie = false;
    String orientation="";

    // only one beep type for now
    //int beepType;
    //int numBeepTypes = 3;
    //Random RAND = new Random();

    // assign ID when we start a timed picture, used in makeDecrementTimerFunction callback. If the ID changes, the countdown will stop.
    int currentPictureID = 0;

    // PictureView works, but for timed pictures it makes more sense to always go to ViewImageActivity
    // since the user may not be by the camera when it takes the picture
    //PictureView pictureView;
    Uri pictureURI;

    Handler handler = new Handler();
    int pictureTimer = 0;
    boolean hasMultipleCameras;

    List<String> flashModes = new ArrayList<String>();
    int selectedFlashMode;
    boolean flashButtonConfigured = false;

    int picturesToTake = 1;
    List<Uri> pictureURIs;
    Display display;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_photo);

    //    OrientationUtils.unlockOrientation(TakePhotoActivity.this);

        flashButtonLabels.put(FLASH_MODE_AUTO, getString(R.string.flashButtonAutoLabel));
        flashButtonLabels.put(FLASH_MODE_ON, getString(R.string.flashButtonOnLabel));
        flashButtonLabels.put(FLASH_MODE_OFF, getString(R.string.flashButtonOffLabel));

        cameraView = (SurfaceView)findViewById(R.id.cameraView);
        arManager = ARManager.createAndSetupCameraView(this, cameraView, null);
        arManager.setCameraOpenedCallback(new Runnable() {public void run() {cameraOpened();}});
        arManager.setCameraStartedCallback(new Runnable() {public void run() {cameraPreviewStarted();}});

        statusFrame=(LinearLayout)findViewById(R.id.statusFrame);

        shutterButton = (ShutterButton)findViewById(R.id.shutterButton);
        shutterButton.setOnShutterButtonListener(this);
        pictureDelayButton = (TextView) findViewById(R.id.pictureDelayButton);

        cancelPictureButton = (TextView)findViewById(R.id.cancelPictureButton);
        flashButton = (TextView)findViewById(R.id.flashButton);
        numberOfPicturesButton = (TextView)findViewById(R.id.numberOfPicturesButton);

        flashCaption = (TextView)findViewById(R.id.flashcaption);
        switchCameraButtonCaption = (TextView)findViewById(R.id.selfiecaption);
        numberOfPicturesButtonCaption = (TextView)findViewById(R.id.numbercaption);

        switchCameraButton = (TextView)findViewById(R.id.switchCameraButton);
        hasMultipleCameras = (CameraUtils.numberOfCameras() > 1);   Log.d("Face===========>",String.valueOf(hasMultipleCameras));
        switchCameraButton.setVisibility(hasMultipleCameras ? View.VISIBLE : View.GONE);
        switchCameraButtonCaption.setVisibility(hasMultipleCameras ? View.VISIBLE : View.GONE);

        Log.d("CameraId===>",String.valueOf(arManager.getCameraId()));

        statusTextField = (TextView)findViewById(R.id.statusText);
        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);

        rotateButton = (TextView)findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orient) {
                    orient=false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else {
                    orient=true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

        AndroidUtils.bindOnClickListener(this, pictureDelayButton, "cycleDelay");
        AndroidUtils.bindOnClickListener(this, cancelPictureButton, "cancelSavePicture");
        AndroidUtils.bindOnClickListener(this, switchCameraButton, "switchCamera");
        AndroidUtils.bindOnClickListener(this, flashButton, "cycleFlashMode");
        AndroidUtils.bindOnClickListener(this, numberOfPicturesButton, "toggleNumberOfPictures");
        AndroidUtils.bindOnClickListener(this, findViewById(R.id.helpButton), "doHelp");
        AndroidUtils.bindOnClickListener(this, findViewById(R.id.libraryButton), "openLibrary");

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.readDelayPreference();

        display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

    }

    // Check screen orientation or screen rotate event here
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation="landscape";
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            orientation="portrait";
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
    //        OrientationUtils.unlockOrientation(TakePhotoActivity.this);
     //       cameraView.setRotation(90.00f);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                down = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                down = false;
                oldDist = spacing(event);
                if(oldDist > 10f){
                    midPoint(mid, event);
                    mode = 1;
                }
                break;

            case MotionEvent.ACTION_UP:
                down = true;
                mode = 0;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                down =false;
                mode = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                if(mode == 1){
                    float newDist = spacing(event);
                    if(newDist > 10f){
                        float m = newDist/oldDist;
                        arManager.zoomin(m);
                    }
                }
                break;
        }
        return true;

    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }
    @Override
    public void onPause() {
        if (pictureTimer > 0) {
            this.cancelSavePicture();
        }
        arManager.stopCamera();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        arManager.startCameraIfVisible();
        AndroidUtils.setSystemUiLowProfile(cameraView);
    }

    // callback from ARManager
    public void cameraOpened() {
        int orientation = display.getOrientation();

        if (maxCameraViewSize==null) {
            maxCameraViewSize = new int[] {cameraView.getWidth(), cameraView.getHeight()};
            switch(orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    Commons.cameraWidth=maxCameraViewSize[0];
                    Commons.cameraHeight=maxCameraViewSize[1];
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:

                    break;
            }

        }
        Commons.cameraWidth=maxCameraViewSize[0];
        Commons.cameraHeight=maxCameraViewSize[1];
        Log.d("WidthCamera=====>",String.valueOf(Commons.cameraWidth));
        Log.d("HeightCamera=====>",String.valueOf(Commons.cameraHeight));

        arManager.setPreferredPreviewSize(maxCameraViewSize[0], maxCameraViewSize[1]);
        CameraUtils.setLargestCameraSize(arManager.getCamera());
        //statusTextField.setText(arManager.getCamera().getParameters().getPictureSize().width+"");
        if (!flashButtonConfigured) {
            configureFlashButton();
            flashButtonConfigured = true;
        }
        Log.d("CameraId1===>",String.valueOf(arManager.getCameraId()));
    }

    public void cameraPreviewStarted() {
//        int orientation = display.getOrientation();
        // resize camera view to scaled size of preview image
        Camera.Size size = arManager.getCamera().getParameters().getPreviewSize();
        Log.d("sizeWidth===>",String.valueOf(size.width));
        int[] scaledWH = AndroidUtils.scaledWidthAndHeightToMaximum(
                size.width, size.height, maxCameraViewSize[0], maxCameraViewSize[1]);

//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//        cameraView.setLayoutParams(lp);

        cameraView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT, Gravity.CENTER));

        //    cameraView.setLayoutParams(new FrameLayout.LayoutParams(scaledWH[0], scaledWH[1], Gravity.CENTER));

//        switch(orientation) {
//            case Configuration.ORIENTATION_PORTRAIT:
//                cameraView.setLayoutParams(new FrameLayout.LayoutParams(Commons.cameraWidth, Commons.cameraHeight, Gravity.CENTER));
//                break;
//            case Configuration.ORIENTATION_LANDSCAPE:
//
//                break;
//        }
        Log.d("CameraId2===>",String.valueOf(arManager.getCameraId()));

        Log.d("Width=====>",String.valueOf(Commons.cameraWidth));
        Log.d("Height=====>",String.valueOf(Commons.cameraHeight));

        if(arManager.getCameraId()==1){
            isSelfie=true;
            switchCameraButton.setText("S/r");
            switchCameraButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_camera_front,// left
                    0,//top
                    0,// right
                    0//bottom
            );
        }
        else {
            isSelfie=false;
            switchCameraButton.setText("R/s");
            switchCameraButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_camera_rear,// left
                    0,//top
                    0,// right
                    0//bottom
            );
        }

    //    OrientationUtils.unlockOrientation(TakePhotoActivity.this);
        setCameraDisplayOrientation(arManager.getCamera());
    }

    public void zoomIn(View view){

    }

    public void zoomOut(View view){

    }

    void updateButtons(boolean allowSave) {
        this.findViewById(R.id.miscButtonBar).setVisibility(allowSave ? View.VISIBLE : View.GONE);
        this.findViewById(R.id.optionsButtonBar).setVisibility(allowSave ? View.VISIBLE : View.GONE);
        shutterButton.setVisibility(allowSave ? View.VISIBLE : View.GONE);
        cancelPictureButton.setVisibility(allowSave ? View.GONE : View.VISIBLE);
    }

    public void cancelSavePicture() {
        pictureTimer = 0;
        ++currentPictureID;
        statusTextField.setText("");
        statusFrame.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.canceledPictureMessage), Toast.LENGTH_SHORT).show();
        updateButtons(true);
    }

    void updateTimerMessage() {
        String messageFormat = getString(R.string.timerCountdownMessageFormat);
        statusTextField.setText(String.valueOf(pictureTimer));
    }

    Runnable makeDecrementTimerFunction(final int pictureID) {
        return new Runnable() {
            public void run() {decrementTimer(pictureID);}
        };
    }

    MediaPlayer.OnCompletionListener releaseMediaPlayerFunction = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mp.release();
        }
    };

    void playTimerBeep() {
        int soundResource = R.raw.beep_sound0;
        MediaPlayer mp = MediaPlayer.create(this, soundResource);
        mp.start();
        mp.setOnCompletionListener(releaseMediaPlayerFunction);
    }

    public void decrementTimer(final int pictureID) {
        if (pictureID!=this.currentPictureID) {
            return;
        }
        boolean takePicture = (pictureTimer==1);
        --pictureTimer;
        if (takePicture) {
            savePictureNow();
            playTimerBeep();
        }
        else if (pictureTimer>0) {
            updateTimerMessage();
            handler.postDelayed(makeDecrementTimerFunction(pictureID), 1000);
            if (pictureTimer<3) playTimerBeep();
        }
    }

    public void savePicture() {
        if (this.pictureDelay==0) {
            statusFrame.setVisibility(View.GONE);
            savePictureNow();
        }
        else {
            statusFrame.setVisibility(View.VISIBLE);
            text1.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
            savePictureAfterDelay(this.pictureDelay);
        }
    }

    void savePictureAfterDelay(int delay) {
        pictureTimer = delay;
        updateTimerMessage();
        currentPictureID++;
        handler.postDelayed(makeDecrementTimerFunction(currentPictureID), 1000);
        //beepType = RAND.nextInt(numBeepTypes);

        updateButtons(false);
    }

    public void savePictureNow() {
        pictureURIs = new ArrayList<Uri>();
        statusTextField.setText("Taking picture...");
        text1.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        arManager.getCamera().autoFocus(this);
    }

    public void switchCamera() {
        flashButtonConfigured = false;
        arManager.switchToNextCamera();
    }

    void configureFlashButton() {
        flashModes.clear();
        if (CameraUtils.cameraSupportsFlash(arManager.getCamera())) {
            if (CameraUtils.cameraSupportsAutoFlash(arManager.getCamera())) {
                flashModes.add(FLASH_MODE_AUTO);
            }
            flashModes.add(FLASH_MODE_OFF);
            flashModes.add(FLASH_MODE_ON);
        }

        if (flashModes.size() > 0) {
            flashButton.setVisibility(View.VISIBLE);
            flashCaption.setVisibility(View.VISIBLE);
            updateFlashMode(0);
            String mode = flashModes.get(selectedFlashMode);
            flashButton.setText(flashButtonLabels.get(mode));

            Log.d("Flash===>",flashButtonLabels.get(mode));

            if(flashButton.getText().equals("Auto"))
                flashButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_flash_auto,// left
                        0,//top
                        0,// right
                        0//bottom
                );
            else if(flashButton.getText().equals("On"))
                flashButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_flash_on,// left
                        0,//top
                        0,// right
                        0//bottom
                );
            else if(flashButton.getText().equals("Off"))
                flashButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_flash_off,// left
                        0,//top
                        0,// right
                        0//bottom
                );
            else flashButton.setCompoundDrawablesWithIntrinsicBounds(
                        0,// left
                        0,//top
                        R.drawable.ic_flash_on,// right
                        0//bottom
                );
            CameraUtils.setFlashMode(arManager.getCamera(), mode);
        }
        else {
            flashButton.setVisibility(View.GONE);
            flashCaption.setVisibility(View.GONE);
        }
    }

    public void cycleDelay() {
        int index = DELAY_DURATIONS.indexOf(this.pictureDelay);
        if (index<0) {
            this.pictureDelay = DEFAULT_DELAY;
        }
        else {
            this.pictureDelay = DELAY_DURATIONS.get((index+1) % DELAY_DURATIONS.size());
        }
        writeDelayPreference();
        updateDelayButton();
    }

    void writeDelayPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(DELAY_PREFERENCES_KEY, this.pictureDelay);
        editor.commit();
    }

    void readDelayPreference() {
        // reads picture delay from preferences, updates this.pictureDelay and delay button text
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int delay = prefs.getInt(DELAY_PREFERENCES_KEY, -1);
        if (!DELAY_DURATIONS.contains(delay)) {
            delay = DEFAULT_DELAY;
        }
        this.pictureDelay = delay;
        updateDelayButton();
    }

    void updateDelayButton() {
        if (pictureDelay==0) {
            pictureDelayButton.setText(getString(R.string.delayButtonLabelNone));
        }
        else {
            String labelFormat = getString(R.string.delayButtonLabelSecondsFormat);
            pictureDelayButton.setText(String.format(labelFormat, this.pictureDelay));
        }
    }

    public void cycleFlashMode() {
        if (flashModes.size() > 0) {
            selectedFlashMode = (selectedFlashMode + 1) % flashModes.size();
            updateFlashMode(selectedFlashMode);
        }
    }

    void updateFlashMode(int mode) {
        selectedFlashMode = mode;
        String modeString = flashModes.get(selectedFlashMode);
        flashButton.setText(modeString.substring(0,1).toUpperCase() + modeString.substring(1));
        if(flashButton.getText().equals("Auto"))
            flashButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_flash_auto,// left
                    0,//top
                    0,// right
                    0//bottom
            );
        else if(flashButton.getText().equals("On"))
            flashButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_flash_on,// left
                    0,//top
                    0,// right
                    0//bottom
            );
        else if(flashButton.getText().equals("Off"))
            flashButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_flash_off,// left
                    0,//top
                    0,// right
                    0//bottom
            );
        else flashButton.setCompoundDrawablesWithIntrinsicBounds(
                    0,// left
                    0,//top
                    R.drawable.ic_flash_on,// right
                    0//bottom
            );
        CameraUtils.setFlashMode(arManager.getCamera(), modeString);
    }

    public void toggleNumberOfPictures() {
        picturesToTake = (picturesToTake==1) ? 4 : 1;
        numberOfPicturesButton.setText(picturesToTake==1 ? R.string.singleImageButtonLabel : R.string.multiImageButtonLabel);
    }

    public void doHelp() {
        AboutActivity.startIntent(this);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, final Camera camera) {
        int pictureNum = (picturesToTake > 1) ? pictureURIs.size() + 1 : 0;
        pictureURI = saveImageData(data, pictureNum);

        Log.d("Orientation===>",orientation);
        if(orientation=="portrait"){

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , pictureURI);
                bitmap=rotateImage(bitmap,90.00f);
                pictureURI=getImageUri(getApplicationContext(),bitmap);
            }
            catch (Exception e)
            {
                //handle exception
            }

        }

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , pictureURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap=rotateImage(bitmap,90.00f);
        pictureURI=getImageUri(getApplicationContext(),bitmap);

        statusTextField.setText("");
        statusFrame.setVisibility(View.GONE);
        updateButtons(true);
        camera.startPreview();

        if (pictureURI!=null) {
            pictureURIs.add(pictureURI);
            if (pictureURIs.size() >= picturesToTake) {
                if (picturesToTake==1) {
                    ViewImageActivity.startActivityWithImageURI(this, pictureURI, "image/jpeg");

//                    Uri photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(pictureURI.getPath()));
//
//                    Intent galleryIntent = new Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    galleryIntent.setDataAndType(photoURI, "image/*");
//                    galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(galleryIntent);
                }
                else {
                    ViewImageGridActivity.startActivityWithImageURIs(this, pictureURIs);
                }
            }
            else {
                // OG Droid and possibly other phones often hang if we call takePicture directly instead of autoFocus
                handler.postDelayed(new Runnable() {
                    public void run() {
                        camera.autoFocus(TakePhotoActivity.this);
                    }
                }, 100);
            }
            // send the same NEW_PICTURE broadcast that the standard camera app does
            try {
                Intent newPictureIntent = new Intent("android.hardware.action.NEW_PICTURE");
                newPictureIntent.setDataAndType(pictureURI, "image/jpeg");
                this.sendBroadcast(newPictureIntent);
            }
            catch(Exception ex) {
                Log.e("CamTimer", "Error broadcasting new picture", ex);
            }
        }
    }

    String savedImageDirectory = Environment.getExternalStorageDirectory() + File.separator + "myPictures";
    Format dateInFilename = new SimpleDateFormat("yyyyMMdd_HHmmss");

    Uri saveImageData(byte[] data, int pictureNum) {
        try {
            File dir = new File(savedImageDirectory);    Log.d("SavePath===>", savedImageDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!dir.isDirectory()) {
                Toast.makeText(this, "Error saving picture: can't create directory " + dir.getPath(), Toast.LENGTH_LONG).show();
                return null;
            }
            String filename = String.format("IMG_" + dateInFilename.format(new Date()));
            if (pictureNum > 0) filename += ("-" + pictureNum);
            filename += ".jpg";

            String path = savedImageDirectory + File.separator + filename;
            FileOutputStream out = new FileOutputStream(path);
            out.write(data);
            out.close();

            AndroidUtils.scanSavedMediaFile(this, path);
            Toast.makeText(this, getString(R.string.savedPictureMessage), Toast.LENGTH_SHORT).show();

            return Uri.fromFile(new File(path));
        }
        catch(Exception ex) {
            Toast.makeText(this, "Error saving picture: " + ex.getClass().getName(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public void openLibrary() {
        startActivity(LibraryActivity.intentWithImageDirectory(this, savedImageDirectory));
    }

    @Override
    public void onShutterButtonFocus(boolean pressed) {
        shutterButton.setImageResource(pressed ? R.drawable.btn_camera_shutter_pressed_holo :
                R.drawable.btn_camera_shutter_holo);
    }

    @Override
    public void onShutterButtonClick() {
        savePicture();
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (x * x + y * y)/2;
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(Commons.chat_photo_edit){
            finish();
            return;
        }
        Intent intent=new Intent(this, BranchActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        overridePendingTransition(0,0);
    }

    public void setCameraDisplayOrientation(android.hardware.Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(getBackFacingCameraId(), camInfo);


        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;  Log.d("Degree===>",String.valueOf(degrees));
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (camInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (camInfo.orientation - degrees + 360) % 360;    Log.d("Result===>",String.valueOf(result));
        }
        camera.setDisplayOrientation(result);
    }

    private int getBackFacingCameraId() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}