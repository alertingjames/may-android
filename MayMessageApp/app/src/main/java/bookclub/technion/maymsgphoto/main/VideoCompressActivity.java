package bookclub.technion.maymsgphoto.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.classes.FileUtils;
import bookclub.technion.maymsgphoto.classes.MediaController;
import bookclub.technion.maymsgphoto.commons.Commons;


public class VideoCompressActivity extends Activity {

    private static final int RESULT_CODE_COMPRESS_VIDEO = 3;
    private static final String TAG = "VideoCompressActivity";
    private EditText editText;
    private ProgressBar progressBar;
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_compress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        editText = (EditText) findViewById(R.id.editText);
        editText.setText(Commons.tempFile.getPath());

        findViewById(R.id.btnSelectVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, RESULT_CODE_COMPRESS_VIDEO);
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();

            if (reqCode == RESULT_CODE_COMPRESS_VIDEO) {
                if (uri != null) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);

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

                            Commons.tempFile = FileUtils.saveTempFile(displayName, this, uri);
                            editText.setText(Commons.tempFile.getPath());

                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        }
    }

    private void deleteTempFile(){
        if(Commons.tempFile != null && Commons.tempFile.exists()){
            Commons.tempFile.delete();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteTempFile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTempFile();
    }

    public void compress(View v) {
//        MediaController.getInstance().scheduleVideoConvert(tempFile.getPath());

        FileUtils.createApplicationFolder();
        new VideoCompressor().execute();
    }

    public class VideoCompressor extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG,"Start video compression");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            return MediaController.getInstance().convertVideo(Commons.tempFile.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            progressBar.setVisibility(View.GONE);
            if(compressed){
                showToast("Successfully compressed!");
                //               Commons._video_compressed=true;
//                Intent intent=new Intent(getApplicationContext(), VendorUploadActivity.class);
//                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        }
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

}
