package bookclub.technion.maymsgphoto.main.photoedit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adobe.creativesdk.aviary.AdobeImageIntent;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;

public class EditPhotoActivity extends AppCompatActivity {

    public static final String TAG = EditPhotoActivity.class.getSimpleName();
    static final int REQ_CODE_CSDK_IMAGE_EDITOR = 3001;
    static final int REQ_CODE_GALLERY_PICKER = 20;

    private Button mOpenGalleryButton;
    private Button mLaunchImageEditorButton;
    private ImageView mSelectedImageView;

    private Uri mSelectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

    //    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    //    setSupportActionBar(toolbar);

        mOpenGalleryButton = (Button) findViewById(R.id.openGalleryButton);
        mLaunchImageEditorButton = (Button) findViewById(R.id.launchImageEditorButton);
        mSelectedImageView = (ImageView) findViewById(R.id.editedImageView);

        View.OnClickListener openGalleryButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryPickerIntent = new Intent();
                galleryPickerIntent.setType("image/*");
                galleryPickerIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryPickerIntent, "Select an Image"), REQ_CODE_GALLERY_PICKER); // Can be any int
            }
        };
        mOpenGalleryButton.setOnClickListener(openGalleryButtonListener);

        View.OnClickListener launchImageEditorButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSelectedImageUri != null) {
                    /* 1) Create a new Intent */
                    Intent imageEditorIntent = new AdobeImageIntent.Builder(EditPhotoActivity.this)
                            .setData(mSelectedImageUri) // Set in onActivityResult()
                            .build();

                    /* 2) Start the Image Editor with request code 1 */
                    startActivityForResult(imageEditorIntent, REQ_CODE_CSDK_IMAGE_EDITOR);
                }
                else {
                    Toast.makeText(EditPhotoActivity.this, "Select an image from the Gallery", Toast.LENGTH_LONG).show();
                }
            }
        };
        mLaunchImageEditorButton.setOnClickListener(launchImageEditorButtonListener);

        mSelectedImageUri= Commons.imageUri;

        try{
            if (mSelectedImageUri != null) {
                    /* 1) Create a new Intent */
                Intent imageEditorIntent = new AdobeImageIntent.Builder(EditPhotoActivity.this)
                        .setData(mSelectedImageUri) // Set in onActivityResult()
                        .build();

                    /* 2) Start the Image Editor with request code 1 */
                startActivityForResult(imageEditorIntent, REQ_CODE_CSDK_IMAGE_EDITOR);
            }
            else {
                Toast.makeText(EditPhotoActivity.this, "Select an image from the Gallery", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            showToast(e.getMessage());
        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    /* Handle the results */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQ_CODE_GALLERY_PICKER:
                    mSelectedImageUri = data.getData();
                    mSelectedImageView.setImageURI(mSelectedImageUri);

                    break;

                case REQ_CODE_CSDK_IMAGE_EDITOR:

                    /* Set the image! */
                    Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                    try{

                       mSelectedImageView.setImageURI(editedImageUri);

     //                   Commons.imageView.setImageURI(editedImageUri);

//                        Commons.imageUriSave=editedImageUri;
//                        Commons.imagePathSave=(new File(editedImageUri.getPath())).getAbsolutePath();
//
//                        Commons.imageUriSaveShare=editedImageUri;
//                        Commons.imagePathSaveShare=(new File(editedImageUri.getPath())).getAbsolutePath();

                        Bitmap bitmap=getBitmapFromUri(editedImageUri);

                        String ImagePath = MediaStore.Images.Media.insertImage(
                                getContentResolver(),
                                bitmap,
                                "demo_image",
                                "demo_image"
                        );

                        Uri URI = Uri.parse(ImagePath);

                        Commons._edited_photo=true;

                        Commons.imageUriSave=URI;
                        Commons.imagePathSave=ImagePath;

                        Commons.imageUri=URI;

                        Commons.imageUriSaveShare=URI;
                        Commons.imagePathSaveShare=ImagePath;
                        Commons.fileShare=new File(ImagePath);

                        Log.d("savePath===>", Commons.imagePathSave);
                        Log.d("saveUri===>", Commons.imageUriSave.toString()+"/"+Commons.imageUriSave.getPath());

                        Intent intent=new Intent(getApplicationContext(),Main2Activity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0,0);

                    }catch (Exception e){
                        e.printStackTrace();
                        showToast(e.getMessage());
                        }
                    catch (OutOfMemoryError e){
                        showToast(e.getMessage());
                    }

                    break;

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
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
