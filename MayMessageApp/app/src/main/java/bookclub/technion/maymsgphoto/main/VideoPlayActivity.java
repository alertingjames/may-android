package bookclub.technion.maymsgphoto.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;

public class VideoPlayActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayActivity";
    RelativeLayout ui_lytvideo;
    ImageView ui_imvback, downloader, sharer;
    VideoView videoView;
    TextView videoUrl;
    private ProgressDialog mProgresDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        sharer=(ImageView)findViewById(R.id.share);
        sharer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Video DownloadURL:\n  ");
                shareIntent.putExtra(Intent.EXTRA_TEXT, Commons.videouri.toString());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
        downloader=(ImageView)findViewById(R.id.download);
        downloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                videoUrl.setVisibility(View.VISIBLE);
//                String videoUrl = Commons.videouri.toString();
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setDataAndType(Uri.parse(videoUrl),"video/mp4");
//                startActivity(i);
            }
        });
        ui_lytvideo=(RelativeLayout) findViewById(R.id.lytvideo);
        videoView=(VideoView) findViewById(R.id.videoView);
        videoView.setBackground(null);
        videoView.setMediaController(new MediaController(this));

        videoView.setVideoURI(Commons.videouri);

//                Uri video = Uri.parse("http://35.162.12.207/uploadfiles/video/2017/01/24_14846816368.mp4");

//                videoView.setVideoURI(Uri.parse("http://35.162.12.207/uploadfiles/video/2017/01/59_14846642323.mp4"));
        videoView.requestFocus();
        videoView.start();

        videoUrl=(TextView)findViewById(R.id.videoUrl);
        videoUrl.setText(Commons.videouri.toString());
//
        ui_imvback=(ImageView)findViewById(R.id.imv_back);
        ui_imvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.videouri=null;
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

    private void showLoading(boolean isShow){
        if (isShow){
            if (mProgresDialog == null)
                mProgresDialog = new ProgressDialog(this);

            mProgresDialog.setMessage("Uploading...");
            mProgresDialog.setIndeterminate(true);
            mProgresDialog.setCancelable(false);
            mProgresDialog.setCanceledOnTouchOutside(false);
            mProgresDialog.show();
        } else {
            if (mProgresDialog.isShowing())
                mProgresDialog.hide();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Commons.videouri=null;
    }
}
