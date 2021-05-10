package bookclub.technion.maymsgphoto.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.classes.Config;
import bookclub.technion.maymsgphoto.classes.FileUtils;
import bookclub.technion.maymsgphoto.commons.Commons;

public class VideoCaptureActivity extends Activity implements View.OnClickListener{
    private static final String TAG ="File===>" ;
    VideoView videoView;
    TextView ui_txvpreview,ui_txvsave,ui_txvsubmit;
    ImageView ui_imvback;
    RelativeLayout ui_lytvideo;
    private static final int VIDEO_CAPTURE = 101;
    String selectedVideoPath="",takenVideoPath="";

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);

        ui_txvpreview=(TextView)findViewById(R.id.txv_preview);
        ui_txvpreview.setOnClickListener(this);
        ui_txvsave=(TextView)findViewById(R.id.txv_save);
        ui_txvsave.setOnClickListener(this);
        ui_txvsubmit=(TextView)findViewById(R.id.txv_submit);
        ui_txvsubmit.setOnClickListener(this);

        ui_lytvideo=(RelativeLayout) findViewById(R.id.lytvideo);
        videoView=(VideoView) findViewById(R.id.videoView);
////
//        videoView.setVideoURI(Uri.parse("http://www.dormmom.com/video/FoldingLaundrySD.mp4"));
////
//        videoView.start();
//
        ui_imvback=(ImageView)findViewById(R.id.imv_back);
        ui_imvback.setOnClickListener(this);
        String option=getIntent().getExtras().get("OPTION").toString();
        if(option.equals("capture"))dispatchTakeVideoIntent();
        else if(option.equals("pick")){selectVideoFromGallery();}
    }
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int SELECT_VIDEO_REQUEST=2;

    private void dispatchTakeVideoIntent() {


//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//
//        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
//
//        // set video quality
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
//        // name
//
//        // start the video capture Intent
//        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);


//        Intent takeVideoIntent = new Intent();
//        takeVideoIntent.setAction("android.media.action.VIDEO_CAPTURE");
//        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//        }


        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

//        // External sdcard location
//        File mediaStorageDir = new File(
//                Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
//                "Camera");
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d(TAG, "Oops! Failed create "
//                        + "" + " directory");
//                return null;
//            }
//        }


        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {

            //    if(fileUri!=null) {

//                takenVideoPath = fileUri.getPath();

            takenVideoPath = getPath(data.getData());

//                Uri videoUri = fileUri;

            Uri videoUri = data.getData();
            Log.d("Uri=========>", takenVideoPath);
            Commons.videouri = videoUri;
            //      Toast.makeText(this,"Uricap=>"+takenVideoPath,Toast.LENGTH_LONG).show();
            showToast("Please wait...");
            //            Toast.makeText(getApplicationContext(), "Video capture successfully", Toast.LENGTH_LONG);
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(takenVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
            videoView.setBackground(new BitmapDrawable(getResources(), thumb));
            videoView.setVideoURI(videoUri);
            Commons.thumb = thumb;  Log.d("Thumnail===>",String.valueOf(thumb.getByteCount()));
            Commons.videoUrl = takenVideoPath;

            Cursor cursor = getContentResolver().query(videoUri, null, null, null, null, null);
//
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
//                String displayName = cursor.getString(
//                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                Log.i(TAG, "Display Name: " + displayName);
//
//                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
//                String size = null;
//                if (!cursor.isNull(sizeIndex)) {
//                    size = cursor.getString(sizeIndex);
//                } else {
//                    size = "Unknown";
//                }
//                Log.i(TAG, "Size: " + size);

            Commons.tempFile = FileUtils.saveTempFile(String.valueOf(n), this, videoUri);   Log.d("TempFile===>",Commons.tempFile.getPath());
            showToast("Video captured successfully!\n Please click Upload button to go to compression of the video.");

            if (cursor != null) {
                cursor.close();
            }
//                try {
//                    if (cursor != null && cursor.moveToFirst()) {
//
//                        String displayName = cursor.getString(
//                                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                        Log.i(TAG, "Display Name: " + displayName);
//
//                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
//                        String size = null;
//                        if (!cursor.isNull(sizeIndex)) {
//                            size = cursor.getString(sizeIndex);
//                        } else {
//                            size = "Unknown";
//                        }
//                        Log.i(TAG, "Size: " + size);
//
//                        Commons.tempFile = FileUtils.saveTempFile(displayName, this, videoUri);
//                        showToast(Commons.tempFile.getPath() + "\n" + "Prepared!");
//
//                    }
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
        }
        //    }

//        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
//            takenVideoPath = getPath(data.getData());
//            Uri videoUri = data.getData();    Log.d("Uri=========>",takenVideoPath);
//            Commons.videouri=videoUri; Toast.makeText(this,"Uricap=>"+takenVideoPath,Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(),"Video capture successfully", Toast.LENGTH_LONG);
//            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(takenVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
//            videoView.setBackground(new BitmapDrawable(getResources(),thumb));
//            videoView.setVideoURI(videoUri);
//            Commons.thumb=thumb;
//            Commons.videoUrl=takenVideoPath;
//        }


        else if(requestCode == SELECT_VIDEO_REQUEST && resultCode == RESULT_OK)
        {
            if(data.getData()!=null)
            {
                selectedVideoPath = getPath(data.getData());
                try{
                    Uri videoUri = data.getData();
                    Commons.videouri=videoUri;
                    Log.d("UriSel=========>", selectedVideoPath);
                    //       Toast.makeText(this,"Urisel=>"+selectedVideoPath,Toast.LENGTH_LONG).show();
                    showToast("Please wait...");
                    //                Toast.makeText(getApplicationContext(),"Video successfully from Gallery", Toast.LENGTH_LONG).show();
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(selectedVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);
                    videoView.setBackground(new BitmapDrawable(getResources(),thumb));//MediaStore.Video.Thumbnails.MINI_KIND
                    videoView.setVideoURI(videoUri);
//                    videoView.setVideoPath("http://35.162.12.207/uploadfiles/video/2017/01/24_14846816368.mp4");
                    Commons.thumb=thumb;
                    Commons.videoUrl=selectedVideoPath;

                    Cursor cursor = getContentResolver().query(videoUri, null, null, null, null, null);

                    try {
                        if (cursor != null && cursor.moveToFirst()) {

                            String displayName = cursor.getString(
                                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            Log.i(TAG, "Display Name: " + displayName);

                            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                            String size = null;
                            if (!cursor.isNull(sizeIndex)) {
                                size = cursor.getString(sizeIndex);
                            } else {
                                size = "Unknown";
                            }
                            Log.i(TAG, "Size: " + size);

                            Commons.tempFile = FileUtils.saveTempFile(displayName, this, videoUri);     Log.d("TempFile2===>",Commons.tempFile.getPath());
                            showToast("Video captured successfully!\n Please click Upload button to go to compression of the video.");

                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }

                }catch (NullPointerException e){

                }   Log.d("Urisel===========>",selectedVideoPath);

            }
            else
            {
                Toast.makeText(getApplicationContext(), "Failed to select video" , Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    public void selectVideoFromGallery()
    {

//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("video/*");
//        startActivityForResult(intent, 3);

        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, SELECT_VIDEO_REQUEST);

//        Intent intent = new Intent();
//        intent.setType("video/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select Video"),101);

//        Intent intent;
//        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
//        {
//         //   intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//        }
//        else
//        {
//        //    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//        }
//        intent.setType("video/*");
//    //    intent.setAction(Intent.ACTION_GET_CONTENT);
//    //    intent.putExtra("return-data", true);
//        startActivityForResult(intent,SELECT_VIDEO_REQUEST);
    }

//    private void gotoLoadPhotoVideoActivity() {
//        Intent intent=new Intent(this,LoadPhotoVideoActivity.class);
//        startActivity(intent);
//        finish();
//    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txv_preview:
                Log.d("Press==>","");
                videoView.setBackground(null);
                videoView.setMediaController(new MediaController(this));
                //videoView.setVideoURI(Commons.videouri);
                videoView.setVideoPath(Commons.videoUrl);
//                Uri video = Uri.parse("http://35.162.12.207/uploadfiles/video/2017/01/24_14846816368.mp4");

//                videoView.setVideoURI(Uri.parse("http://35.162.12.207/uploadfiles/video/2017/01/59_14846642323.mp4"));
                videoView.requestFocus();
                videoView.start();
                initTabBackg();
                ui_txvpreview.setBackgroundColor(Color.parseColor("#fa7822"));
                ui_txvpreview.setTextColor(Color.WHITE);
                break;
            case R.id.txv_save:
                Save();
                initTabBackg();
                ui_txvsave.setBackgroundColor(Color.parseColor("#fa7822"));
                break;
            case R.id.txv_submit:
                Commons._video_post_flag=true;
                initTabBackg();
                ui_txvsubmit.setBackgroundColor(Color.parseColor("#fa7822"));
                ui_txvsubmit.setTextColor(Color.WHITE);
                try {
                    Submit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imv_back:
                finish();
                overridePendingTransition(0,0);
                break;
        }
    }

    private void Submit() throws IOException {

        File f = new File(this.getCacheDir(), "thumbnail");
        f.createNewFile();

//Convert bitmap to byte array
        Bitmap bitmap = Commons.thumb;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();

        Commons.file=f;
        Commons.map=bitmap;

        //    shareVideo();

//        Intent intent=new Intent(this,VendorUploadActivity.class);
//        Intent intent=new Intent(this,ChatActivity.class);
//        startActivity(intent);
        Commons.imagePortion.setVisibility(View.VISIBLE);
        Commons.mapImage.setImageBitmap(bitmap);
        Intent i=new Intent(getApplicationContext(), VideoCompressActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(0,0);
    }


    private void Save() {

        String root = Environment.getExternalStorageDirectory().getPath();
        File myDir = new File(root+ "/req_images");// + "/req_images"
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);

        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            Bitmap bm=Commons.thumb;
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(this,"Urisav==>"+file,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)){
            return true;
        } else {
            return false;
        }
    }
    public void initTabBackg(){
        ui_txvpreview.setBackgroundColor(Color.parseColor("#e8e8e8"));
        ui_txvsave.setBackgroundColor(Color.parseColor("#e8e8e8"));
        ui_txvsubmit.setBackgroundColor(Color.parseColor("#e8e8e8"));
    }

}

