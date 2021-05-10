package bookclub.technion.maymsgphoto.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import bookclub.technion.maymsgphoto.MayMsgPhotoApplication;
import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;

public class ViewActivity extends AppCompatActivity {

    ImageView back, image0, downloader, sharer;
    NetworkImageView imageView;
    ImageLoader _imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        _imageLoader = MayMsgPhotoApplication.getInstance().getImageLoader();
        imageView=(NetworkImageView) findViewById(R.id.image);
        image0=(ImageView) findViewById(R.id.image0);
        if (Commons.photoUrl.length() > 0) {
            imageView.setImageUrl(Commons.photoUrl,_imageLoader);
            Commons.photoUrl="";
        }
        else if(Commons.resId!=0){
            imageView.setDefaultImageResId(Commons.resId);
            Commons.resId=0;
        } else if(Commons.bitmap!=null) {
            imageView.setVisibility(View.GONE);
            image0.setImageBitmap(Commons.bitmap);
        }
        //    Commons.bitmap=null;
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });
        image0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.latLng!=null){
                    Intent intent=new Intent(getApplicationContext(), TalkLocationViewActivity.class);
                    startActivity(intent);
                }
            }
        });
        sharer=(ImageView)findViewById(R.id.share);
        sharer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                Commons.bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File file = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    file.createNewFile();
                    fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
        downloader=(ImageView)findViewById(R.id.download);
        downloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                Commons.bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File file = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    file.createNewFile();
                    fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();

                    Toast.makeText(getApplicationContext(),"file downloaded at "+file.getPath(),Toast.LENGTH_LONG).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}