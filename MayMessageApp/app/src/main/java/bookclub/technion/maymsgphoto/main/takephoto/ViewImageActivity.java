package bookclub.technion.maymsgphoto.main.takephoto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import bookclub.technion.maymsgphoto.MayMsgPhotoApplication;
import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.commons.Constants;
import bookclub.technion.maymsgphoto.commons.ReqConst;
import bookclub.technion.maymsgphoto.main.BranchActivity;
import bookclub.technion.maymsgphoto.main.castle.DisplayMediaActivity;
import bookclub.technion.maymsgphoto.main.castle.ImportMediaActivity;
import bookclub.technion.maymsgphoto.main.castle.UploadGoogleDriveActivity;
import bookclub.technion.maymsgphoto.main.photoedit.EditPhotoActivity;
import bookclub.technion.maymsgphoto.main.photoedit.Main2Activity;
import bookclub.technion.maymsgphoto.utils.CircularImageView;
import bookclub.technion.maymsgphoto.utils.CircularNetworkImageView;
import bookclub.technion.maymsgphoto.utils.MultiPartRequest;
import bookclub.technion.maymsgphoto.utils.photoutils.AndroidUtils;

import static bookclub.technion.maymsgphoto.main.RegisterActivity.callbackManager;
import static com.adobe.creativesdk.foundation.internal.utils.Util.getContext;

public class ViewImageActivity extends Activity{

    public static final int DELETE_RESULT = Activity.RESULT_FIRST_USER;
    public final int REQUEST_FOR_GOOGLE_PLUS=200;

    ImageView imageView, imageViewEdit, back, backButton, share, lock, facebook, linkedin, twitter, instagram, google;
    TextView editbutton, editImageButton, lockbutton, ok, commentbutton,shareButton,
            rotationbutton, gallerybutton,gallerybutton0, datetimeView, cancel, link;
    TextView commentImage;
    FrameLayout editFrame, frame;
    RelativeLayout commentImageFrame;
    LinearLayout editTextFrame, topBar, bottomBar, shareLockFrame, sharebuttonBar;
    TextView textArea, editCancel, editDone, editSave, editSetting, fontSample, fontsize;
    EditText editArea;
    ScrollView commentFrame;
    EditText comment;
    CircularImageView logo1, logo2;
    private int seekR, seekG, seekB ;
    SeekBar redSeekBar, greenSeekBar, blueSeekBar,fontSeekBar;
    LinearLayout mScreen;
    Uri imageUri, imageUriSave=null;
    String imagePathSave="";
    PermissionHelper permissionHelper;
    int rotation=0;
    boolean flag_from_gallery=false;
    boolean flag_edit_done=false;
    boolean flag_edit_done_done=false;
    boolean flag_gallery=false;
    float dX, dY;
    int color;
    AlertDialog b;
    boolean is_sharing=false;
    int flag_sharing=0;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private static final String TAG = "ViewImageActivity";
    ProgressDialog _progressDlg;
    int _idx;
    String sharePhotoUrl="";
    private boolean canPresentShareDialogWith;


    // these matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    private ImageView view, fin;
    private  Bitmap bmap;

    public static Intent startActivityWithImageURI(Activity parent, Uri imageURI, String type) {
        Intent intent = new Intent(parent, ViewImageActivity.class);
        intent.setDataAndType(imageURI, type);
        parent.startActivityForResult(intent, 0);
        return intent;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_image);

        permissionHelper = PermissionHelper.getInstance(this);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        // Create a callbackManager to handle the login responses.
        callbackManager = CallbackManager.Factory.create();

        shareDialog = new ShareDialog(this);

        canPresentShareDialogWith = ShareDialog.canShow(ShareLinkContent.class);

        // This part is optional
        shareDialog.registerCallback(callbackManager, callback);

        Commons.imageUri=null;
        Commons.imageUriSave=null;

        frame=(FrameLayout)findViewById(R.id.frame);
        editFrame=(FrameLayout)findViewById(R.id.editFrame);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                frame.setVisibility(View.VISIBLE);
//                editFrame.setVisibility(View.GONE);
//                flag_edit_done=false;
//                flag_edit_done_done=false;
//    //            Commons._flag_share=false;
                Commons.imageUri=null;
                Commons.imageUriSave=null;
                Commons.imageUriSaveShare=null;
                Commons.imageUriSaveShare=null;
                Commons.imagePathSaveShare="";
                Commons.imagePathSave="";
                Commons.fileShare=null;

                Intent intent=new Intent(getApplicationContext(),TakePhotoActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);

            }
        });

        backButton=(ImageView)findViewById(R.id.exitViewImageButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                frame.setVisibility(View.VISIBLE);
//                editFrame.setVisibility(View.GONE);
//                flag_edit_done=false;
//                flag_edit_done_done=false;
//    //            Commons._flag_share=false;
                Commons.imageUri=null;
                Commons.imageUriSave=null;
                Commons.imageUriSaveShare=null;
                Commons.imageUriSaveShare=null;
                Commons.imagePathSaveShare="";
                Commons.imagePathSave="";
                Commons.fileShare=null;

                Intent intent=new Intent(getApplicationContext(),TakePhotoActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);

            }
        });

        shareLockFrame = (LinearLayout) findViewById(R.id.share_lock_frame);
        share=(ImageView)findViewById(R.id.share);
        lock=(ImageView)findViewById(R.id.lock);

        mScreen = (LinearLayout) findViewById(R.id.myScreen);
        fontSample=(TextView)findViewById(R.id.fontSample);
        redSeekBar = (SeekBar) findViewById(R.id.mySeekingBar_R);
        greenSeekBar = (SeekBar) findViewById(R.id.mySeekingBar_G);
        blueSeekBar = (SeekBar) findViewById(R.id.mySeekingBar_B);
        fontSeekBar = (SeekBar) findViewById(R.id.mySeekingBar_Size);

        fontsize=(TextView)findViewById(R.id.size);

        redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        fontSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        topBar=(LinearLayout)findViewById(R.id.topBar);
        bottomBar=(LinearLayout)findViewById(R.id.bottomBar);

        editTextFrame=(LinearLayout)findViewById(R.id.editTextFrame);
        editTextFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.animate()
                                .x(event.getRawX() + dX - v.getWidth()/2)
                                .y(event.getRawY() + dY - v.getHeight()/2)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        mScreen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.animate()
                                .x(event.getRawX() + dX - v.getWidth()/2)
                                .y(event.getRawY() + dY - v.getHeight()/2)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        editSetting=(TextView)findViewById(R.id.editSetting);
        editSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreen.setVisibility(View.VISIBLE);
            }
        });
        shareButton=(TextView)findViewById(R.id.shareImageButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                frame.setVisibility(View.GONE);
                editFrame.setVisibility(View.VISIBLE);

            }
        });
        TextView set=(TextView)findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreen.setVisibility(View.GONE);
            }
        });

        textArea=(TextView)findViewById(R.id.textArea);
        editArea=(EditText)findViewById(R.id.editArea);
        editCancel=(TextView) findViewById(R.id.editCancel);
        editCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextFrame.setVisibility(View.GONE);
                flag_edit_done=false;
                flag_edit_done_done=false;
            }
        });
        editDone=(TextView) findViewById(R.id.editDone);
        editDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editArea.getText().length()>0 && !flag_edit_done){
                    textArea.setVisibility(View.VISIBLE);
                    textArea.setText(editArea.getText().toString());

                    Log.d("TextSize===>",String.valueOf(textArea.getTextSize()));

                    editArea.setVisibility(View.GONE);
                    editArea.setText("");
                    flag_edit_done=true;
                    editSave.setVisibility(View.VISIBLE);
                    editSetting.setVisibility(View.VISIBLE);
                }else if(flag_edit_done){
                    topBar.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.GONE);
                    editTextFrame.setBackground(null);
//                    imageView.setImageDrawable(writeTextOnDrawable(imageUri.getPath(),comment.getText().toString()));

                    Bitmap bitmap = loadBitmapFromView(commentImageFrame);
                    imageViewEdit.setImageBitmap(bitmap);
                    editTextFrame.setVisibility(View.GONE);
                    flag_edit_done=false;
                    textArea.setVisibility(View.GONE);
                    editSave.setVisibility(View.GONE);
                    editSetting.setVisibility(View.GONE);
                }
            }
        });

        editSave=(TextView) findViewById(R.id.save);
        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_edit_done){
                    topBar.setVisibility(View.GONE);
                    bottomBar.setVisibility(View.GONE);
                    editTextFrame.setBackground(null);
                    Format dateInFilename = new SimpleDateFormat("MM/dd/yyyy_HH:mm:ss");
                    String datetime = String.format(dateInFilename.format(new Date()));
                    datetimeView.setText(datetime);

//                    logo1.setVisibility(View.VISIBLE);
//                    logo2.setVisibility(View.VISIBLE);

                    try {
                        saveImageBitmap(commentImageFrame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    flag_edit_done=false;
                    editSave.setVisibility(View.GONE);
                    editTextFrame.setVisibility(View.GONE);
                    datetimeView.setVisibility(View.GONE);
                }
            }
        });

        imageViewEdit=(ImageView)findViewById(R.id.imageViewEdit);
        imageView = (ImageView)findViewById(R.id.imageView);
        commentFrame=(ScrollView)findViewById(R.id.commentFrame);
        comment=(EditText)findViewById(R.id.comment);
        editImageButton = (TextView) findViewById(R.id.editImageButton);
        editbutton = (TextView) findViewById(R.id.editButton);
        commentImageFrame=(RelativeLayout)findViewById(R.id.imageFrame);
        commentImage=(TextView)findViewById(R.id.commentImage);
        commentbutton = (TextView) findViewById(R.id.commentButton);
        commentbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
           //     commentFrame.setVisibility(View.VISIBLE);
                if(!flag_edit_done_done){
                    editTextFrame.setVisibility(View.VISIBLE);
                    editTextFrame.setBackground(getDrawable(R.drawable.light_blue_fill_round));
                    bottomBar.setVisibility(View.VISIBLE);
                    topBar.setVisibility(View.VISIBLE);
                    editArea.setVisibility(View.VISIBLE);
                }else showToast("Please select other picture.");
            }
        });
        rotationbutton=(TextView) findViewById(R.id.rotateButton);
        rotationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewEdit.setRotation(rotation=rotation+90);
                if(rotation==360)rotation=0;

                Intent intent=new Intent(getApplication(), Main2Activity.class);
                startActivity(intent);
            }
        });
        datetimeView = (TextView) findViewById(R.id.datetime);
        link = (TextView) findViewById(R.id.webLink);
        gallerybutton=(TextView) findViewById(R.id.galleryButton);
        gallerybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_sharing=0;
                is_sharing=false;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);//
                startActivityForResult(intent, Constants.PICK_FROM_ALBUM);
            }
        });

        gallerybutton0=(TextView) findViewById(R.id.galleryButton0);
        gallerybutton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_gallery=true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);//
                startActivityForResult(intent, Constants.PICK_FROM_ALBUM);
            }
        });
        ok=(TextView)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Format dateInFilename = new SimpleDateFormat("MM/dd/yyyy_HH:mm:ss");
                String datetime = String.format(dateInFilename.format(new Date()));

                if(comment.getText().length()>0){
                    Log.d("ImagePath===>",imageUri.getPath());
                    commentImage.setText(comment.getText().toString());
                    datetimeView.setText(datetime);
//                    imageView.setImageDrawable(writeTextOnDrawable(imageUri.getPath(),comment.getText().toString()));
                    Log.d("LoadImage===>",loadBitmapFromView(commentImageFrame).toString());
                    try {
                        saveImageBitmap(commentImageFrame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    comment.setText("");
                    commentFrame.setVisibility(View.GONE);
                    datetimeView.setVisibility(View.GONE);
                }
            }
        });
        cancel=(TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.setText("");
                commentFrame.setVisibility(View.GONE);
            }
        });
        lockbutton = (TextView) findViewById(R.id.lockButton);
        lockbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareLockFrame.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
                shareLockFrame.startAnimation(animation);

                lock.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
                lock.startAnimation(animation);

                share.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
                share.startAnimation(animation);
            }
        });

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(flag_edit_done_done) {
//                    Commons._isSelectedLock = true;
//                    Intent intent = new Intent(getApplicationContext(), DisplayMediaActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(0, 0);
//                    disappearButtons();
//
//                }else {
//                    showToast("Please write about photo...");
//                    disappearButtons();
//                }


                Commons._isSelectedLock = true;

                if(!Commons._edited_photo){
                    try {
                        saveImageBitmap(commentImageFrame);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(getApplicationContext(), DisplayMediaActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                disappearButtons();

//                Intent intent = new Intent(getApplicationContext(), UploadGoogleDriveActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//                disappearButtons();
            }
        });

        updateBackground();

//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                BottomSheet.Builder builder = new BottomSheet.Builder(ViewImageActivity.this);
//                PackageManager pm = ViewImageActivity.this.getPackageManager();
//
//                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setAction(Intent.ACTION_SEND);
//                    File imageFileToShare = new File(Commons.imagePathSave);
//                Uri uri = Uri.fromFile(imageFileToShare);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, Commons.imageUriSave);
//                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.vacaycarpediem.com/posts");
//                shareIntent.setType("image/*");
//                final List<ResolveInfo> list = pm.queryIntentActivities(shareIntent, 0);
//
//                for (int i = 0; i < list.size(); i++) {
//                    builder.sheet(i, list.get(i).loadIcon(pm), list.get(i).loadLabel(pm));
//                }
//
//                builder.listener(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ActivityInfo activityInfo = list.get(which).activityInfo;
//                        ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
//                        Intent newIntent = (Intent) shareIntent.clone();
//                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                        newIntent.setComponent(name);
//                        ViewImageActivity.this.startActivity(newIntent);
//                        ViewImageActivity.this.overridePendingTransition(R.anim.modal_activity_open_enter, R.anim.modal_activity_open_exit);
//                    }
//                });
//                builder.title("Share Photo").grid().build().show();
//                disappearButtons();
//            }
//        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_sharing=true;
                showAlertDialog("Are you sure that you want to share the picture?");

            }
        });

        sharebuttonBar=(LinearLayout)findViewById(R.id.sharebuttonBar);
        facebook=(ImageView) findViewById(R.id.facebooksharebutton);
        facebook.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        facebook.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        facebook.setBackgroundResource(R.drawable.facebookicon);

                        String fileName = Commons.fileShare.getName();//Name of an image
                        String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
                        String myDir = externalStorageDirectory + "/myPictures/"; // the file will be in saved_images
                        Uri uri = Uri.parse("file:///" + myDir + fileName);

                        if(Commons.imageUriSaveShare!=null) {
//                            sharePictureToWeb(Commons.fileShare);
                            sharePictureToFB(Commons.imageUriSaveShare);

//                            Intent share = new Intent(android.content.Intent.ACTION_SEND);
//                            share.setType("image/*");
//                            share.putExtra(Intent.EXTRA_STREAM, uri);
////                            share.setType("*/*");
//                            share.putExtra(Intent.EXTRA_TITLE, "Body text of the new status");
//                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            startActivity(Intent.createChooser(share,"Share as"));

//                            Uri uri1= Uri.parse("http://35.162.12.207/uploadfiles/logo/2017/03/51_14908834082.jpg");
//
//                            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
//                                    .setContentTitle("Title")
//                                    .setContentDescription("https://www.vacaycarpediem.com/posts")
//                                    .setContentUrl(Uri.parse("https://www.vacaycarpediem.com/posts"))
//                                    .setImageUrl(uri1)
//                                    .build();
//                            shareDialog.show(shareLinkContent);

                            //===================================

            //                uploadVirtualPhoto();

                        }
                        else showToast("Try again.");

                        disappearShareButtonBar();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        facebook.getBackground().clearColorFilter();
                        facebook.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
        linkedin=(ImageView) findViewById(R.id.linkedinsharebutton);
        linkedin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        linkedin.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        linkedin.setBackgroundResource(R.drawable.linkedinicon);

                        if(Commons.imageUriSaveShare!=null)
                            sharePictureToLN(Commons.imageUriSaveShare);
                        else showToast("Try again.");
                        disappearShareButtonBar();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        linkedin.getBackground().clearColorFilter();
                        linkedin.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
        twitter=(ImageView) findViewById(R.id.twittersharebutton);
        twitter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        twitter.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        twitter.setBackgroundResource(R.drawable.twittericon);

                        if(Commons.imageUriSaveShare!=null)
                            sharePictureToTW(Commons.imageUriSaveShare);
                        else showToast("Try again.");
                        disappearShareButtonBar();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        twitter.getBackground().clearColorFilter();
                        twitter.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
        instagram=(ImageView) findViewById(R.id.instagrambutton);
        instagram.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        instagram.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        instagram.setBackgroundResource(R.drawable.instagramicon);

                        if(Commons.fileShare!=null)
                            sharePictureToInstagram();
                        else showToast("Try again.");
                        disappearShareButtonBar();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        instagram.getBackground().clearColorFilter();
                        instagram.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        google=(ImageView) findViewById(R.id.googleButton);
        google.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        google.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        google.setBackgroundResource(R.drawable.googleplusicon);

                        if(Commons.fileShare!=null){
                            share_image_text_GPLUS(Commons.imageUriSaveShare);
                        }
                        else showToast("Try again.");
                        disappearShareButtonBar();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        google.getBackground().clearColorFilter();
                        google.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
        logo1=(CircularImageView)findViewById(R.id.logo1);
        logo2=(CircularImageView)findViewById(R.id.logo2);

        imageUri = getIntent().getData();
        Log.d("Uri===>",imageUri.getPath());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_PICK);//
//                startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.PICK_FROM_ALBUM);
            }
        });

        // assume full screen, there's no good way to get notified once layout happens and views have nonzero width/height

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if(Commons.imageUri!=null)
            imageUri=Commons.imageUri;

        try {
            imageView.setImageBitmap(AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri,
                    dm.widthPixels, dm.heightPixels));
            imageViewEdit.setImageBitmap(AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri,
                    dm.widthPixels, dm.heightPixels));

            Commons.imageUri=imageUri;

//            String ImagePath = MediaStore.Images.Media.insertImage(
//                    getContentResolver(),
//                    AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri,
//                            dm.widthPixels, dm.heightPixels),
//                    "demo_image",
//                    "demo_image"
//            );
//
//            Uri URI = Uri.parse(ImagePath);
//
//            Commons.imageUriSave=URI;
//            Commons.imagePathSave=ImagePath;
//
//            Commons.imageUriSaveShare=URI;
//            Commons.imagePathSaveShare=ImagePath;
//            Commons.fileShare=new File(ImagePath);   Log.d("savePathShare===>", Commons.imagePathSaveShare);
        }
        catch(Exception ex) {}

        AndroidUtils.bindOnClickListener(this, this.findViewById(R.id.deleteImageButton), "deleteImage");
 //       AndroidUtils.bindOnClickListener(this, this.findViewById(R.id.shareImageButton), "shareImage");
 //       AndroidUtils.bindOnClickListener(this, this.findViewById(R.id.exitViewImageButton), "goBack");

        Commons.commentImageFrame=commentImageFrame;
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(flag_from_gallery)
//                    showToast("You can't edit this photo. Please select from Library of this app.");
//                else {
//                    try{
//                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", new File(imageUri.getPath()));
//                        Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        galleryIntent.setDataAndType(photoURI, "image/*");
//                        galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(galleryIntent);
//                    }catch (Exception e){
//                        e.printStackTrace();
//
//                        showToast("Msg: "+e.getMessage()+"\n"+
//                        "Cause: "+e.getCause()+"\n"+
//                        "Trace: "+e.getStackTrace()+"\n"+
//                        "Local: "+e.getLocalizedMessage()+"\n"+
//                        "E: "+e.toString());
//                    }
//                }
                Commons.imageUri=imageUri;
                Commons.imageView=imageViewEdit;
                Intent intent=new Intent(getApplicationContext(), EditPhotoActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                frame.setVisibility(View.GONE);
//                editFrame.setVisibility(View.VISIBLE);
    //            Commons._flag_share=false;
                showAlertSelecting("Do you want to edit this picture?");
            }
        });
    }

    public void showCastleSharingPage(){

        if(Commons.imageUri!=null && Commons._edited_photo) {
            Commons._edited_photo=false;
            imageUri = Commons.imageUri;
        }else if(!Commons._edited_photo){
            Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            imageView.draw(canvas);

            String ImagePath = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    "demo_image",
                    "demo_image"
            );

            Uri URI = Uri.parse(ImagePath);

            Commons._edited_photo=false;

            Commons.imageUriSave=URI;
            Commons.imagePathSave=ImagePath;

            Commons.imageUri=URI;

            Commons.imageUriSaveShare=URI;
            Commons.imagePathSaveShare=ImagePath;
            Commons.fileShare=new File(ImagePath);
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        try {
            imageView.setImageBitmap(AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri,
                    dm.widthPixels, dm.heightPixels));
            imageViewEdit.setImageBitmap(AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri,
                    dm.widthPixels, dm.heightPixels));
        }
        catch(Exception ex) {}

        frame.setVisibility(View.GONE);
        editFrame.setVisibility(View.VISIBLE);
    }

    public void showAlertSelecting(String msg){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(msg);

        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Commons.imageUri=imageUri;
                Commons.imageView=imageViewEdit;

                Intent intent=new Intent(getApplicationContext(), EditPhotoActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        dialogBuilder.setNegativeButton("No, Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                frame.setVisibility(View.GONE);
                editFrame.setVisibility(View.VISIBLE);

//                try {
//                    saveImageBitmap(commentImageFrame);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

        Commons.viewImageActivity=this;

        dialogBuilder.show();
    }

    public void share_image_text_GPLUS(Uri uri) {

        try {
            Intent shareIntent = ShareCompat.IntentBuilder
                    .from(ViewImageActivity.this)
                    .setText("https://www.vacaycarpediem.com/posts")
                    .setType("image/jpeg").setStream(uri).getIntent()
                    .setPackage("com.google.android.apps.plus");
            startActivityForResult(shareIntent, REQUEST_FOR_GOOGLE_PLUS);
        }catch (ActivityNotFoundException exception){
            showToast("Please install Google Plus Photos app");
        }catch (Exception e){
            showToast("Please retry again");
        }

    }


    public void sharePictureToInstagram(){

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null)
        {
            Intent shareIntent = new Intent();
            shareIntent.setAction(android.content.Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");
            shareIntent.setType("image/*");
            //                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
//                        Environment.getExternalStorageDirectory()
//                                + File.separator + "test.jpg", "I am Happy", "Share happy !")));
//                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
//                        Commons.imagePathSaveShare, "I am happy", "Share happy !")));

            shareIntent.putExtra(Intent.EXTRA_STREAM, Commons.imageUriSaveShare);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "https://www.vacaycarpediem.com/posts");
            //        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.vacaycarpediem.com/posts"+"\n\n"+"https://www.vacaycarpediem.com/posts");

            startActivity(shareIntent);
        }
        else
        {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
            startActivity(intent);
        }

    }

    public void shareLinkToFB(String imageUrl, String title, String link){
        Uri uri= Uri.parse(imageUrl);
//        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
//                .setContentTitle(title)
//                .setContentDescription(link)
//                .setContentUrl(Uri.parse("https://www.vacaycarpediem.com/posts"))
//                .setImageUrl(uri)
//                .build();
//
//        ShareDialog.show(ViewImageActivity.this, shareLinkContent);
    }

    public void sharePictureToWeb(final File bitmap){

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "https://www.vacaycarpediem.com/posts");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.vacaycarpediem.com/posts");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(bitmap));
        shareIntent.setType("*/*");

        //================================

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(shareIntent, "Share As...");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int ii = 0; ii < resInfo.size(); ii++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(ii);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("facebook") || packageName.contains("twitter") || packageName.contains("linkedin")) {
                shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "https://www.vacaycarpediem.com/posts");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.vacaycarpediem.com/posts");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(bitmap));
                shareIntent.setType("text/plain");
                intentList.add(new LabeledIntent(shareIntent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);

        //============================================

//                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //        startActivity(Intent.createChooser(shareIntent, "Share as..."));



//                        SharePhoto photo = new SharePhoto.Builder()
//                                .setBitmap(bitmap)
//                                .build();
//                        ShareMediaContent content = new ShareMediaContent.Builder()
//                                .addMedium(photo)
////				.addMedium(shareVideo)
//                                .build();
//
//
//                        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

    }
    public void sharePictureToFB(final Uri uri){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.vacaycarpediem.com/posts");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");
        shareIntent.setPackage("com.facebook.katana");
        startActivity(Intent.createChooser(shareIntent, "Share with Facebook:"));
    }

    public void sharePictureToLN(final Uri uri){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.vacaycarpediem.com/posts");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        shareIntent.setPackage("com.linkedin.android");
        startActivity(Intent.createChooser(shareIntent, "Share with Linkedin:"));
    }

    public void sharePictureToTW(final Uri uri){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.vacaycarpediem.com/posts");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");
        shareIntent.setPackage("com.twitter.android");
        startActivity(Intent.createChooser(shareIntent, "Share with Twitter:"));
    }


    public void showAlertDialog(String msg) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(msg);

        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                shareLockFrame.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
                shareLockFrame.startAnimation(animation);

                String linkText="vacaycarpediem.com";
                if(flag_sharing==0){
                    logo1.setBackgroundResource(R.mipmap.vacaylogo);
                    logo2.setBackgroundResource(R.mipmap.may123);

                    link.setText(linkText);

                    try {
                        saveImageBitmap(commentImageFrame);
//                    logo1.setVisibility(View.GONE);
//                    logo2.setVisibility(View.GONE);
                        logo1.setBackgroundResource(0);
                        logo2.setBackgroundResource(0);
                        link.setText("");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                showShareButtonBar();
            }
        });
        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialogBuilder.show();

    }

    public void showShareButtonBar(){
        Animation animation;
        sharebuttonBar.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
        sharebuttonBar.startAnimation(animation);
        facebook.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
        facebook.startAnimation(animation);
        linkedin.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
        linkedin.startAnimation(animation);
        twitter.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
        twitter.startAnimation(animation);
        instagram.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
        instagram.startAnimation(animation);
        google.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_open_enter);
        google.startAnimation(animation);

    }

    public void disappearShareButtonBar(){

        shareLockFrame.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        shareLockFrame.startAnimation(animation);
        sharebuttonBar.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        sharebuttonBar.startAnimation(animation);
        facebook.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        facebook.startAnimation(animation);
        linkedin.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        linkedin.startAnimation(animation);
        twitter.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        twitter.startAnimation(animation);
        instagram.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        instagram.startAnimation(animation);
        google.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        google.startAnimation(animation);

    }

    public void disappearButtons(){
        shareLockFrame.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_enter);
        shareLockFrame.startAnimation(animation);

        lock.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        lock.startAnimation(animation);

        share.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_activity_close_exit);
        share.startAnimation(animation);
    }
    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.PICK_FROM_ALBUM: {

                if (resultCode == RESULT_OK) {
                    try {
                        imageUri = data.getData();
                        Log.d("Uri===>",imageUri.getPath());

//                        showToast("GalleryImagePath: "+imageUri.getPath()+"\n"+
//                        "ImageUri.tostring: "+imageUri.toString());

                        // assume full screen, there's no good way to get notified once layout happens and views have nonzero width/height
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        try {
                            if(flag_gallery){
                                flag_gallery=false;
                                imageView.setImageBitmap(AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri,
                                        dm.widthPixels, dm.heightPixels));
                                imageViewEdit.setImageBitmap(AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri,
                                        dm.widthPixels, dm.heightPixels));
                                Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                imageView.draw(canvas);

                                String ImagePath = MediaStore.Images.Media.insertImage(
                                        getContentResolver(),
                                        bitmap,
                                        "demo_image",
                                        "demo_image"
                                );

                                Uri URI = Uri.parse(ImagePath);

                                Commons._edited_photo=false;

                                Commons.imageUriSave=URI;
                                Commons.imagePathSave=ImagePath;

                                Commons.imageUri=URI;

                                Commons.imageUriSaveShare=URI;
                                Commons.imagePathSaveShare=ImagePath;
                                Commons.fileShare=new File(ImagePath);
                            }
                            else{
                                flag_from_gallery=true;
                                imageViewEdit.setImageBitmap(AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri,
                                        dm.widthPixels, dm.heightPixels));
                            }
                        }
                        catch(Exception ex) {}

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        if (requestCode == REQUEST_FOR_GOOGLE_PLUS) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),
                        "Image uploaded on Google!",
                        Toast.LENGTH_LONG).show();
     //           finish();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Image sharing failed",
                        Toast.LENGTH_LONG).show();
    //            finish();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void goBack() {
        this.finish();
    }

    public void deleteImage() {
        String path = this.getIntent().getData().getPath();
        (new File(path)).delete();
        this.setResult(DELETE_RESULT);
        this.finish();
    }

    public void shareImage() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(this.getIntent().getType());
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Picture Using:"));
    }

    // launch gallery and terminate this activity, so when gallery activity finishes user will go back to main activity
    public void viewImageInGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
        galleryIntent.setDataAndType(this.getIntent().getData(), this.getIntent().getType());
        // FLAG_ACTIVITY_NO_HISTORY tells the OS to not return to the gallery if the user goes to the home screen and relaunches the app
        galleryIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(galleryIntent);
        this.finish();
    }

    private BitmapDrawable writeTextOnDrawable(String ImagePath, String text) {

        Bitmap bm = BitmapFactory.decodeFile(ImagePath)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(getApplicationContext(), 70));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(getApplicationContext(), 70));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);

        return new BitmapDrawable(getResources(), bm);
    }

    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

    public static void hideFile(File file){
        File dstFile = new File(file.getParent(), "." + file.getName());
        file.renameTo(dstFile);
    }

    public Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }


    public void saveImageBitmap(RelativeLayout relativeLayout) throws IOException {
        Format dateInFilename = new SimpleDateFormat("MM/dd/yyyy");
        String datetime = String.format(dateInFilename.format(new Date()));

        Bitmap bitmap = loadBitmapFromView(relativeLayout);

//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "myPictures");
//        if (!dir.exists())
//            dir.mkdirs();
//
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String name = n+"secret.jpg";
//
////        File file = new File(dir, datetime+".jpg");
//
//        File file = new File(dir, name);
//        file.createNewFile();
////        Commons.imageUriSave=Uri.fromFile(file);
//        Commons.imageUriSave=Uri.parse(file.getAbsolutePath());
//        Commons.imagePathSave=file.getAbsolutePath();
//
//        Commons.imageUriSaveShare=Uri.parse(file.getAbsolutePath());
//        Commons.imagePathSaveShare=file.getAbsolutePath();
//        Commons.fileShare=file;
//
//        Log.d("savePath2===>", Commons.imagePathSave);
//        Log.d("saveUri2===>", Commons.imageUriSave.toString()+"/"+Commons.imageUriSave.getPath());
        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            if(!is_sharing) {
                imageViewEdit.setImageBitmap(bitmap);
            }else {
                is_sharing=false;
                flag_sharing=1;
            }

    //        AndroidUtils.scanSavedMediaFile(this, file.getPath());
            flag_edit_done_done=true;

            String ImagePath = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    "demo_image",
                    "demo_image"
            );

            Uri URI = Uri.parse(ImagePath);

            Commons.imageUriSave=URI;
            Commons.imagePathSave=ImagePath;

            Commons.imageUri=imageUri;

            Commons.imageUriSaveShare=URI;
            Commons.imagePathSaveShare=ImagePath;
            Commons.fileShare=new File(ImagePath);   Log.d("savePathShare===>", Commons.imagePathSaveShare);

            Toast.makeText(ViewImageActivity.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();


        } catch (Exception e) {
            Log.d("ViewImageActivity", "Error, " + e);
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener
            = new SeekBar.OnSeekBarChangeListener()
    {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
// TODO Auto-generated method stub
            updateBackground();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
// TODO Auto-generated method stub

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
// TODO Auto-generated method stub
        }
    };

    private void updateBackground()
    {
        seekR = redSeekBar.getProgress();
        seekG = greenSeekBar.getProgress();
        seekB = blueSeekBar.getProgress();
        float seekSize = fontSeekBar.getProgress();
        try{
            fontSample.setTextColor(
                    0xff000000
                            + seekR * 0x10000
                            + seekG * 0x100
                            + seekB
            );
            fontSample.setTextSize(seekSize*4);
            textArea.setTextColor(
                    0xff000000
                            + seekR * 0x10000
                            + seekG * 0x100
                            + seekB
            );
            textArea.setTextSize(seekSize*4);
            fontsize.setText(String.valueOf(seekSize));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void publishPicture(String path){
        Log.i("ShareFragment image", path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(path, options);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, null);
     //   ShareDialog.show(ViewImageActivity.this, content);

    }

    private void publishPicture(Uri uri){

        Log.d("PictureUrl===>", uri.toString());
        SharePhoto photo = new SharePhoto.Builder()
                .setImageUrl(uri).setCaption("Awesome!")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo).setContentUrl(Uri.parse("https://www.vacaycarpediem.com/posts"+"\n\n"+"https://www.vacaycarpediem.com/posts"))
                .build();
    //    ShareApi.share(content, null);
        ShareDialog.show(ViewImageActivity.this, content);

    }

    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Log.e(TAG, "Successfully posted");
            // Write some code to do some operations when you shared content successfully.
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                String title = "Success";
                String id = result.getPostId();
                String alertMessage = "Successfully posted";
                showResult(title, alertMessage);
            }
        }

        @Override
        public void onCancel() {
            Log.e(TAG, "Cancel occurred");
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            Log.e(TAG, error.getMessage());
            // Write some code to do some operations when some error occurs while sharing content.
            String title = "Success";
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }
    };

    private void showResult(String title, String alertMessage) {
        new AlertDialog.Builder(ViewImageActivity.this).setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton("Okay", null).show();
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void showProgress() {
        closeProgress();
        _progressDlg = ProgressDialog.show(this, "", "Loading...",true);
    }

    public void closeProgress() {

        if(_progressDlg == null) {
            return;
        }

        if(_progressDlg!=null && _progressDlg.isShowing()){
            _progressDlg.dismiss();
            _progressDlg = null;
        }
    }

    @Override
    public void onBackPressed() {
        Commons.imageUri=null;
        Commons.imageUriSave=null;
        Commons.imageUriSaveShare=null;
        Commons.imageUriSaveShare=null;
        Commons.imagePathSaveShare="";
        Commons.imagePathSave="";
        Commons.fileShare=null;
        Intent intent=new Intent(getApplicationContext(),TakePhotoActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

}
