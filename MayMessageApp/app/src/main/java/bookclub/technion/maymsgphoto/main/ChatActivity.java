package bookclub.technion.maymsgphoto.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.cunoraz.gifview.library.GifView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.mobindustry.emojilib.EmojiPanel;
import net.mobindustry.emojilib.EmojiParser;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import bookclub.technion.maymsgphoto.MayMsgPhotoApplication;
import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.adapters.NotiUserListAdapter;
import bookclub.technion.maymsgphoto.classes.UserDetails;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.commons.Constants;
import bookclub.technion.maymsgphoto.database.DBManager;
import bookclub.technion.maymsgphoto.main.takephoto.TakePhotoActivity;
import bookclub.technion.maymsgphoto.models.ChatEntity;
import bookclub.technion.maymsgphoto.models.UserEntity;
import bookclub.technion.maymsgphoto.utils.BitmapUtils;
import bookclub.technion.maymsgphoto.utils.CircularNetworkImageView;
import pl.tajchert.nammu.PermissionCallback;

import static android.os.Environment.getExternalStorageDirectory;


public class ChatActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private DBManager dbManager;

    LinearLayout layout,layout1;
    LinearLayout layout_2, layout_21;
    ImageView sendButton, attachbutton, location,attachmentButton, cancelSecretPortion;
    EditText messageArea, audioMessageArea;
    SeekBar seekBar;
    ScrollView scrollView, scrollView1;
    TextView statusView, typeStatus1, ok, cancel, history,audioMessageSend,
            typeStatus, sendAudio, cancelAudio, record, play, speeches, secretTitle, senderTime, receiverTime, senderTimeText, receiverTimeText;
    Firebase reference1, reference2, reference3, reference4, reference5, reference6, reference7, reference8;
    CircularNetworkImageView photo;
    int is_talking=0;
    int is_talkingR=0;
    int index = 0;
    boolean is_typing=false;

    boolean recordFlag=false;
    boolean playFlag=false;
    boolean isCarMode=false;
    boolean isCrop=true;
    int receivedMessages=0;

    private MediaRecorder myAudioRecorder=null, myAudioRecorder2=null;
    private MediaPlayer myPlayer, myPlayer1;
    private String outputFile = "", vOutputFile="";
    private Handler mHandler = new Handler();

    TextToSpeech textToSpeech; int i=0;

    ImageView image, carMode, carImage;
    private Uri _imageCaptureUri;
    String _photoPath = "";
    Bitmap bitmap;
    ImageLoader _imageLoader;
    File file=null;
    GifView gifView, gifView2;
    View backgroundframe;
    boolean _takePhoto=false;
    private AdView mAdView;
    LinearLayout imagePortion, audiomessagePortion, attachGroupPortion, audioPortion, carModePortion, carModeDescriptionPortion;
    LinearLayout voicePortion, senderTimeFrame, receiverTimeFrame;
    Bitmap thumbnail;
    String imageFile="", speechMessage="", audio="";
    Uri downloadUrl=null;
//    NotiUserListAdapter notiUserListAdapter=new NotiUserListAdapter(NotificationListActivity.class);

    private ProgressDialog mProgresDialog;
    private ProgressDialog mProgresDialog0;

    PermissionHelper permissionHelper;

    boolean is_speech=false;
    private FrameLayout mFrameLayout;
    private EmojiPanel mPanel;
    private EmojiParser mParser;

    boolean attachPortion=false;
    boolean startTalking=false;
    boolean secretTalking=false;
    boolean isClickedSecretButton=false;
    boolean _flag_chathistory=false;

    Firebase reference=null;
    ArrayList<View> viewArrayList=new ArrayList<>();

    public static final boolean NATIVE_WEB_P_SUPPORT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 29;
    public static final int RECORD_AUDIO = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Commons.isClickedSecretButton=false;

        UserDetails.username= Commons.thisEntity.get_email().replace(".com","").replace(".","ddoott");
        UserDetails.chatWith= Commons.userEntity.get_email().replace(".com","").replace(".","ddoott");

        Firebase.setAndroidContext(this);
//        reference1 = new Firebase("https://androidchatapp-76776.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference1 = new Firebase("https://maymsgphoto.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://maymsgphoto.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        reference3 = new Firebase("https://maymsgphoto.firebaseio.com/notification/" + UserDetails.chatWith + "/"+UserDetails.username);
        reference4 = new Firebase("https://maymsgphoto.firebaseio.com/secret/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference5 = new Firebase("https://maymsgphoto.firebaseio.com/secret/" + UserDetails.chatWith + "_" + UserDetails.username);
        reference6 = new Firebase("https://maymsgphoto.firebaseio.com/secretnoti/" + UserDetails.chatWith + "/"+UserDetails.username);

        reference7 = new Firebase("https://maymsgphoto.firebaseio.com/notification/" + UserDetails.username +"/"+UserDetails.chatWith);
        reference8 = new Firebase("https://maymsgphoto.firebaseio.com/secretnoti/" + UserDetails.username +"/"+UserDetails.chatWith);

        registerChatRoom();

        Commons.notificationStart=Commons.notificationStart + 1;   Log.d("Noticount===>", String.valueOf(Commons.notificationStart));

        Commons.speeches.clear();

        dbManager = new DBManager(this);
        dbManager.open();

//        for(int i=0;i<dbManager.getChatEntities().size();i++){
//            dbManager.delete(dbManager.getChatEntities().get(i).get_chid());
//        }

        permissionHelper = PermissionHelper.getInstance(this);

        _imageLoader = MayMsgPhotoApplication.getInstance().getImageLoader();

//        if(Commons.firebase!=null){
//            Commons.firebase.removeValue();
//            Commons.firebase=null;
//        }

        try{
            checkMessageIssue();
            checkSecretMessageIssue();
        }catch (Exception e){e.printStackTrace();}

        Commons.requestLatlng=null;

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (LinearLayout) findViewById(R.id.layout2);

        layout1 = (LinearLayout) findViewById(R.id.layout11);
        layout_21 = (LinearLayout) findViewById(R.id.layout21);

        senderTimeFrame=(LinearLayout)findViewById(R.id.sendertimeFrame);
        receiverTimeFrame=(LinearLayout)findViewById(R.id.receivertimeFrame);

        senderTime=(TextView)findViewById(R.id.senderTime);
        receiverTime=(TextView)findViewById(R.id.receiverTime);

        senderTimeText=(TextView)findViewById(R.id.senderTimeText);
        receiverTimeText=(TextView)findViewById(R.id.receiverTimeText);

        gifView=(GifView)findViewById(R.id.gif);
        gifView2=(GifView)findViewById(R.id.gif2);
        backgroundframe = (View)findViewById(R.id.backgroundframe);

        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);

        cancelSecretPortion = (ImageView)findViewById(R.id.cancelSecret);
        cancelSecretPortion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSecretPortion.setVisibility(View.GONE);
                scrollView1.setVisibility(View.GONE);
                secretTitle.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                secretTalking=false;
                attachmentButton.setImageResource(R.drawable.attachmentbutton);
                Commons.isClickedSecretButton=false;
                Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView1 = (ScrollView)findViewById(R.id.scrollView1);
        statusView=(TextView)findViewById(R.id.status);
        secretTitle=(TextView)findViewById(R.id.secretTitle);
        speeches=(TextView)findViewById(R.id.speechTextMessage);

        attachGroupPortion = (LinearLayout) findViewById(R.id.attachIconGroup);
        imagePortion=(LinearLayout)findViewById(R.id.imagePortion);
        Commons.imagePortion=imagePortion;
        image=(ImageView) findViewById(R.id.image);
        Commons.mapImage=image;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);

        } else {
            seekBar=(SeekBar)findViewById(R.id.seekBar);

            play=(TextView)findViewById(R.id.play);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!playFlag){
                        playFlag=true;
                        try {
                            myPlayer = new MediaPlayer();
                            myPlayer.setDataSource(outputFile);
                            myPlayer.prepare();
                            myPlayer.start();
                            seekBar.setVisibility(View.VISIBLE);
                            seekBar.setMax(myPlayer.getDuration());

//Make sure you update Seekbar on UI thread
                            ChatActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if(myPlayer != null){
                                        int mCurrentPosition = myPlayer.getCurrentPosition();
                                        seekBar.setProgress(mCurrentPosition);
                                        if(mCurrentPosition>=seekBar.getMax()){
                                            play.setText("Start Playing");
                                            //        play.setTextSize(11.00f);
                                            record.setEnabled(true);
                                            playFlag=false;
                                        }
                                    }
                                    mHandler.postDelayed(this, 1000);
                                }
                            });

                            Toast.makeText(getApplicationContext(), "Playing audio",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        play.setText("Stop Playing");
                        //        play.setTextSize(11.00f);
                        record.setEnabled(false);
                    }else {
                        playFlag=false;
                        try {
                            if (myPlayer != null) {
                                myPlayer.stop();
                                myPlayer.release();
                                myPlayer = null;

                                Toast.makeText(getApplicationContext(), "Stop playing the recording...",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        play.setText("Start Playing");
                        //        play.setTextSize(11.00f);
                        record.setEnabled(true);
                    }
                }
            });
            record=(TextView)findViewById(R.id.record);
            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!recordFlag){
                        recordFlag=true;
                        try {
                            //        initAudio();
                            outputFile = Environment.getExternalStorageDirectory().
                                    getAbsolutePath() + "/"+"my_audio"+new Date().getTime()+".3gp";
                            myAudioRecorder = new MediaRecorder();
                            myAudioRecorder.reset();
                            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            myAudioRecorder.setOutputFile(outputFile);
                            seekBar.setVisibility(View.GONE);
                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        record.setText("Stop Recording");
                        //        record.setTextSize(11.00f);
                        play.setEnabled(false);
                        Toast.makeText(getApplicationContext(), "Recording started",
                                Toast.LENGTH_LONG).show();
                    }else{
                        recordFlag=false;
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                        myAudioRecorder = null;

                        record.setText("Start Recording");
                        //        record.setTextSize(11.00f);
                        play.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
                                Toast.LENGTH_LONG).show();
                    }

                }
            });
            audioPortion=(LinearLayout)findViewById(R.id.audioPortion);
            sendAudio=(TextView)findViewById(R.id.sendAudio);
            sendAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!record.getText().toString().startsWith("Start")){
                        return;
                    }
                    File f=new File(outputFile);
                    Log.d("FILExtension===>", FilenameUtils.getExtension(f.getName()));

                    if(f!=null && FilenameUtils.getExtension(f.getName()).length()>0) {
                        showAlertDialogFileUpload(Uri.fromFile(f));

                        record.setText("Start Recording");
                        //        record.setTextSize(11.00f);
                        play.setEnabled(true);
                        record.setEnabled(true);
                        seekBar.setVisibility(View.GONE);
                        audioPortion.setVisibility(View.GONE);
                        recordFlag=false;
                    }
                    else showAlertDialog("Please record audio.");
                }
            });
            cancelAudio=(TextView)findViewById(R.id.cancelA);
            cancelAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    record.setText("Start Recording");
                    //        record.setTextSize(11.00f);
                    play.setEnabled(true);
                    play.setText("Start Playing");
                    //        play.setTextSize(11.00f);
                    record.setEnabled(true);
                    seekBar.setVisibility(View.GONE);

                    audioPortion.setVisibility(View.GONE);
                    recordFlag=false;
                }
            });
        }

        try{
            textToSpeech = new TextToSpeech(ChatActivity.this, ChatActivity.this);
        }catch (NullPointerException e){}
        catch (Exception e){}


        history=(TextView)findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RecentUsersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });
        cancel=(TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePortion.setVisibility(View.GONE);
            }
        });
        ok=(TextView)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //            Log.d("Bitmap===>",bitmap.toString());

                if(Commons.compressedvideoUrl.length()>0) {

                    Log.d("CompressFileUrl===>",Commons.compressedvideoUrl);
                    Log.d("CompressFileUri===>",Commons.videouri.toString());
                    Log.d("CompressFileUril===>",String.valueOf(Uri.fromFile(new File(Commons.compressedvideoUrl))));

                    startUploadVideo(Uri.fromFile(new File(Commons.compressedvideoUrl)));
                }
                else {
                    imageBitmapToString();
                    uploadImage();
                    imagePortion.setVisibility(View.GONE);
                    attachGroupPortion.setVisibility(View.GONE);
                    mFrameLayout.setVisibility(View.GONE);
                    mPanel.dissmissEmojiPopup();
                    attachmentButton.setImageResource(R.drawable.attachmentbutton);
                    attachPortion=false;
                    voicePortion.setVisibility(View.GONE);
                    if(Commons.imagePortion != null) {
                        Commons.imagePortion.setVisibility(View.GONE);
                    }
                }

            }
        });

        mFrameLayout = (FrameLayout) findViewById(R.id.root_frame_layout);
        mPanel = new EmojiPanel(this, mFrameLayout, new EmojiPanel.EmojiClickCallback() {
            @Override
            public void sendClicked(Spannable span) {
                messageArea.setText(span);
            }

            @Override
            public void stickerClicked(String path) {
                Uri uri=Uri.parse(path); Log.d("URI===>",uri.getPath());
                imagePortion.setVisibility(View.VISIBLE);
                image.setImageURI(uri);

                bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

            }
        });

        //Set default icons for buttons
        //panel.iconsInit();

        //or if you need custom icons for buttons
        mPanel.iconsInit(R.drawable.ic_send_smile_levels, R.drawable.forward_blue);

        //initialise panel
        mPanel.init();
        mParser = mPanel.getParser();
        Spannable parsedString = mParser.parse(messageArea.getText().toString());

        audiomessagePortion=(LinearLayout)findViewById(R.id.audiomessagepage);
        audioMessageArea = (EditText)findViewById(R.id.audiomessage);
        audioMessageSend=(TextView)findViewById(R.id.send);
        audioMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(audioMessageArea.getText().length()>0){
//                    messageArea.setText(audioMessageArea.getText().toString());

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", audioMessageArea.getText().toString());
                    map.put("istyping", "false");
                    map.put("online", "true");
                    map.put("time", String.valueOf(new Date().getTime()));
                    map.put("image", "");
                    map.put("video", "");
                    map.put("lat", "");
                    map.put("lon", "");
                    map.put("user", UserDetails.username);
                    if(!secretTalking) {
                        reference1.push().setValue(map);
                        reference2.push().setValue(map);
                    }else {
                        reference4.push().setValue(map);
                        reference5.push().setValue(map);
                    }
                    messageArea.setText("");
                    is_typing=false;

                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("sender", UserDetails.username.replace("ddoott","."));
                    if(Commons.thisEntity.get_fullName().length()>0)
                        map1.put("senderName", Commons.thisEntity.get_fullName());
                    if(Commons.thisEntity.get_name().length()>0)
                        map1.put("senderName", Commons.thisEntity.get_name());
                    map1.put("senderPhoto", Commons.thisEntity.get_photoUrl());
                    map1.put("msg", audioMessageArea.getText().toString());
                    if(!secretTalking) {
                        map1.put("secret", "");
                        if(isCarMode)
                            map1.put("carmode", "carmode");
                        else map1.put("carmode", "");
                        reference3.push().setValue(map1);
                    }
                    else {
                        map1.put("secret", "secret");
                        map1.put("carmode", "");
                        reference6.push().setValue(map1);
                    }
                }
                audioMessageArea.setText("");
                audiomessagePortion.setVisibility(View.GONE);
            }
        });

        carModePortion=(LinearLayout)findViewById(R.id.carmodePortion);
        carModePortion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carModeDescriptionPortion.setVisibility(View.VISIBLE);
                attachGroupPortion.setVisibility(View.GONE);
                voicePortion.setVisibility(View.GONE);
                carModePortion.setVisibility(View.GONE);
                receivedMessages=0;
                Commons.REQ_CODE_SPEECH_INPUT_CAR=200;
                attachPortion=false;
                attachmentButton.setImageResource(R.drawable.attachmentbutton);
                mFrameLayout.setVisibility(View.GONE);
                mPanel.dissmissEmojiPopup();
                secretTalking=false;
            }
        });

        carModePortion.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageArea.getWindowToken(), 0);
                return false;
            }
        });

        carModeDescriptionPortion=(LinearLayout)findViewById(R.id.carmodeDescPortion);
        carModeDescriptionPortion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCarMode=true;
                startVoiceRecognitionActivityCarMode();
            }
        });

        carMode=(ImageView)findViewById(R.id.carmode);
        carImage=(ImageView)findViewById(R.id.carImage);

        TextView cancelAudio=(TextView)findViewById(R.id.cancelAudio);
        cancelAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioMessageArea.setText("");
                audiomessagePortion.setVisibility(View.GONE);
            }
        });

        TextView delete=(TextView)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                audioMessageArea.setText("");
            }
        });

        ImageView speechButton=(ImageView)findViewById(R.id.speech_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_speech=true;

                if(!is_typing){
                    is_typing=true;
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", "");
                    map.put("istyping", "true");
                    map.put("image", "");
                    map.put("video", "");
                    map.put("lat", "");
                    map.put("lon", "");
                    map.put("online", "true");
                    map.put("time", String.valueOf(new Date().getTime()));
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    reference4.push().setValue(map);
                    reference5.push().setValue(map);
                }

                startVoiceRecognitionActivity();
            }
        });

        ImageView secretButton=(ImageView)findViewById(R.id.secret_button);
        secretButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView1.setVisibility(View.VISIBLE);
                secretTitle.setVisibility(View.VISIBLE);
                voicePortion.setVisibility(View.GONE);
                scrollView.setVisibility(View.INVISIBLE);
                attachGroupPortion.setVisibility(View.GONE);
                secretTalking=true;
                cancelSecretPortion.setVisibility(View.VISIBLE);
//                if(Commons.firebaseSecret!=null){
//                    Commons.firebaseSecret.removeValue();
//                    Commons.firebaseSecret=null;
//                }
//                index = Commons.userEntities.indexOf(Commons.userEntity);
                Log.d("SSecret===>", Commons.thisEntity.getSecret());
                try{
                    Commons.userEntities.get(index).setSecret("");
                }
                catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                }
//                notiUserListAdapter.setDatas(Commons.userEntities);
//                notiUserListAdapter.notifyDataSetChanged();
//                Commons.nList.setAdapter(notiUserListAdapter);
                Commons.isClickedSecretButton=true;
                getSecretMessages(Commons.isClickedSecretButton);
            }
        });

        secretButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageArea.getWindowToken(), 0);
                return false;
            }
        });

        voicePortion=(LinearLayout)findViewById(R.id.voiceMessagePortion);
        voicePortion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        voicePortion.setBackground(getDrawable(R.drawable.magenda_fill_round));
                        try {
                            vOutputFile = Environment.getExternalStorageDirectory().
                                    getAbsolutePath() + "/"+"voice_message"+new Date().getTime()+".3gp";
                            myAudioRecorder2 = new MediaRecorder();
                            myAudioRecorder2.reset();
                            myAudioRecorder2.setAudioSource(MediaRecorder.AudioSource.MIC);
                            myAudioRecorder2.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            myAudioRecorder2.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            myAudioRecorder2.setOutputFile(vOutputFile);
                            myAudioRecorder2.prepare();
                            myAudioRecorder2.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        voicePortion.setBackground(getDrawable(R.drawable.blue_fill_white_stroke));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                myAudioRecorder2.stop();
                                myAudioRecorder2.release();
                                myAudioRecorder2 = null;
                                File f=new File(vOutputFile);
                                startUploadDocument(Uri.fromFile(f));
                                voicePortion.setVisibility(View.GONE);
                            }
                        }, 1000);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        voicePortion.getBackground().clearColorFilter();
                        voicePortion.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        index = Commons.userEntities.indexOf(Commons.userEntity);
        try{
            Commons.userEntities.get(index).setRegular("");
            Commons.userEntities.get(index).setCarmode("");
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
//        notiUserListAdapter.setDatas(Commons.userEntities);
//        notiUserListAdapter.notifyDataSetChanged();
//        Commons.nList.setAdapter(notiUserListAdapter);

        for(int i=0;i<dbManager.getAllMembers().size();i++){
            if(dbManager.getAllMembers().get(i).get_email().equals(Commons.userEntity.get_email())){
                dbManager.delete(dbManager.getAllMembers().get(i).get_idx());
            }
        }
        if(Commons.userEntity.get_num_org().length()==0)Commons.userEntity.set_num_org("0");
        if(Commons.userEntity.get_num().length()==0)Commons.userEntity.set_num("0");
        dbManager.insert(Commons.userEntity.get_name(), Commons.userEntity.get_email(),Commons.userEntity.get_photoUrl(),"0");
        Log.d("NewOrg===>",Commons.userEntity.get_name()+"/"+Commons.userEntity.get_num());

        ImageView emojiButton=(ImageView)findViewById(R.id.emojiButton);
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrameLayout.setVisibility(View.VISIBLE);
                attachGroupPortion.setVisibility(View.GONE);
                voicePortion.setVisibility(View.GONE);
                attachmentButton.setImageResource(R.drawable.attachmentbutton);
                attachPortion=false;
            }
        });

        location=(ImageView)findViewById(R.id.locationbutton);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),TalkLocationCaptureActivity.class);
                startActivity(intent);
            }
        });

        photo=(CircularNetworkImageView)findViewById(R.id.imv_photo);
        if(Commons.userEntity.get_photoUrl().length()>0)
            photo.setImageUrl(Commons.userEntity.get_photoUrl(),_imageLoader);

        attachbutton=(ImageView)findViewById(R.id.attachbutton);
        attachbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuItems();
            }
        });

        attachmentButton=(ImageView)findViewById(R.id.attachmentButton);
        attachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!attachPortion) {
                    attachPortion=true;
                    attachGroupPortion.setVisibility(View.VISIBLE);
                    voicePortion.setVisibility(View.VISIBLE);
                    carModePortion.setVisibility(View.VISIBLE);
                    carModeDescriptionPortion.setVisibility(View.GONE);
                    Commons.REQ_CODE_SPEECH_INPUT_CAR=500;
                    carImage.setImageResource(R.drawable.redcaricon);
                    carMode.setImageResource(R.drawable.redcaricon);
                    attachmentButton.setImageResource(R.drawable.cancel_icon);
                }else {
                    attachPortion=false;
                    isCarMode=false;
                    attachGroupPortion.setVisibility(View.GONE);
                    voicePortion.setVisibility(View.GONE);
                    carModePortion.setVisibility(View.GONE);
                    Commons.REQ_CODE_SPEECH_INPUT_CAR=500;
                    carModeDescriptionPortion.setVisibility(View.GONE);
                    attachmentButton.setImageResource(R.drawable.attachmentbutton);
                }
            }
        });

        attachmentButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageArea.getWindowToken(), 0);
                return false;
            }
        });

        TextView name = (TextView)findViewById(R.id.txv_name);
        typeStatus = (TextView)findViewById(R.id.typeStatus);
        typeStatus1 = (TextView)findViewById(R.id.typeStatus1);
        if(Commons.userEntity.get_name().length()>0)
            name.setText(Commons.userEntity.get_name());

//        loadChatHistory(Commons._flag_chathistory);


        messageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {

                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String messageText = messageArea.getText().toString();
                if(!is_typing){
                    is_typing=true;
                    if(!messageText.equals("")){
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", "");
                        map.put("istyping", "true");
                        map.put("image", "");
                        map.put("video", "");
                        map.put("lat", "");
                        map.put("lon", "");
                        map.put("online", "true");
                        map.put("time", String.valueOf(new Date().getTime()));
                        map.put("user", UserDetails.username);
                        reference1.push().setValue(map);
                        reference2.push().setValue(map);
                        reference4.push().setValue(map);
                        reference5.push().setValue(map);
                    }
                }else {
                    if(messageText.length()==0){
                        is_typing=false;
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", "");
                        map.put("istyping", "false");
                        map.put("image", "");
                        map.put("video", "");
                        map.put("lat", "");
                        map.put("lon", "");
                        map.put("online", "true");
                        map.put("time", String.valueOf(new Date().getTime()));
                        map.put("user", UserDetails.username);
                        reference1.push().setValue(map);
                        reference2.push().setValue(map);
                        reference4.push().setValue(map);
                        reference5.push().setValue(map);
                    }
                }
            }
        });

        online("true");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    if(!secretTalking){

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", messageText);
                        map.put("istyping", "false");
                        map.put("online", "true");
                        map.put("time", String.valueOf(new Date().getTime()));
                        map.put("image", "");
                        map.put("video", "");
                        map.put("lat", "");
                        map.put("lon", "");
                        map.put("user", UserDetails.username);
                        reference1.push().setValue(map);
                        reference2.push().setValue(map);
                        messageArea.setText("");
                        is_typing=false;

                        Map<String, String> map0 = new HashMap<String, String>();
                        map0.put("sender", UserDetails.username.replace("ddoott","."));
                        if(Commons.thisEntity.get_fullName().length()>0)
                            map0.put("senderName", Commons.thisEntity.get_fullName());
                        if(Commons.thisEntity.get_name().length()>0)
                            map0.put("senderName", Commons.thisEntity.get_name());
                        map0.put("senderPhoto", Commons.thisEntity.get_photoUrl());
                        map0.put("msg", messageText);

                        map0.put("secret", "");
                        if(isCarMode)
                            map0.put("carmode", "carmode");
                        else map0.put("carmode", "");

                        reference3.push().setValue(map0);
                    }
                    else {

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", messageText);
                        map.put("istyping", "false");
                        map.put("online", "true");
                        map.put("time", String.valueOf(new Date().getTime()));
                        map.put("image", "");
                        map.put("video", "");
                        map.put("lat", "");
                        map.put("lon", "");
                        map.put("user", UserDetails.username);
                        reference4.push().setValue(map);
                        reference5.push().setValue(map);
                        messageArea.setText("");
                        is_typing=false;

                        Map<String, String> map1 = new HashMap<String, String>();
                        map1.put("sender", UserDetails.username.replace("ddoott","."));
                        if(Commons.thisEntity.get_fullName().length()>0)
                            map1.put("senderName", Commons.thisEntity.get_fullName());
                        if(Commons.thisEntity.get_name().length()>0)
                            map1.put("senderName", Commons.thisEntity.get_name());
                        map1.put("senderPhoto", Commons.thisEntity.get_photoUrl());
                        map1.put("msg", messageText);

                        map1.put("secret", "secret");
                        map1.put("carmode", "");

                        reference6.push().setValue(map1);
                    }

                }
                else {
                    try{
                        if(!secretTalking) {
                            TextToSpeechFunction(Commons.speeches.get(0));
                            i++;
                            if (i == Commons.speeches.size()) {
                                i = 0;
                            }
                        }else {
                            TextToSpeechFunction(Commons.secspeeches.get(0));
                            i++;
                            if (i == Commons.secspeeches.size()) {
                                i = 0;
                            }
                        }
                    }catch (NullPointerException e){
                    }catch (IndexOutOfBoundsException e){}
                }
                attachGroupPortion.setVisibility(View.GONE);
                mFrameLayout.setVisibility(View.GONE);
                voicePortion.setVisibility(View.GONE);
                mPanel.dissmissEmojiPopup();
                attachmentButton.setImageResource(R.drawable.attachmentbutton);
                attachPortion=false;
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String status = map.get("istyping").toString();
                String image = map.get("image").toString();
                String video = map.get("video").toString();
                String latStr = map.get("lat").toString();
                String lonStr = map.get("lon").toString();
                String online = map.get("online").toString();
                String userName = map.get("user").toString();
                String time = map.get("time").toString();

                Log.d("22222===>", dataSnapshot.getKey());

                if(!startTalking){
                    gifView.setVisibility(View.VISIBLE);
                    gifView.play();

                }else {
                    gifView.setVisibility(View.GONE);
                    gifView2.setVisibility(View.GONE);
                    backgroundframe.setVisibility(View.GONE);
                }

                LatLng latLng=null;
                if(latStr.length()>0 && lonStr.length()>0){
                    latLng=new LatLng(Double.parseDouble(latStr),Double.parseDouble(lonStr));
                }else latLng=null;

                if(userName.equals(UserDetails.username)){
                    if(status.equals("true")){

                    }else {
                        if(message.length()>0) {
                            addMessageBox(message, time, image, video, latLng, 1, dataSnapshot.getKey()); // Commons.speeches.add(0,message);
                        }
                        else if(image.length()>0)
                            addMessageBox(message, time,image,video,latLng, 1, dataSnapshot.getKey());
                    }
                }
                else{

                    if(online.equals("true")){
                        statusView.setVisibility(View.VISIBLE);
                        statusView.setText("Online");
                    }else {
                        statusView.setVisibility(View.VISIBLE);
                        statusView.setText("Last seen at "+DateFormat.format("MM/dd/yyyy (HH:mm:ss)",
                                Long.parseLong(time)));
                    }

                    if(status.equals("true")){

                        statusView.setVisibility(View.VISIBLE);
                        statusView.setText("is typing . . .");
                        typeStatus.setVisibility(View.VISIBLE);
                        try{
                            if(Commons.userEntity.get_name()!=null){
                                typeStatus.setText(Commons.userEntity.get_name()+" "+"is typing ...");
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }catch (Exception e){}


                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {

                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });

                    }else {

                        typeStatus.setVisibility(View.GONE);
                        if(online.equals("true")){
                            statusView.setVisibility(View.VISIBLE);
                            statusView.setText("Online");
                        }else if(online.equals("false")){
                            statusView.setVisibility(View.VISIBLE);
                            statusView.setText("Last seen at "+DateFormat.format("MM/dd/yyyy (HH:mm:ss)",
                                    Long.parseLong(time)));
                            if(time.length()==0)statusView.setVisibility(View.GONE);
                        }
                        if(message.length()>0) {
                            addMessageBox(message, time, image, video, latLng, 2, dataSnapshot.getKey());
                        }
                        else if(image.length()>0)
                            addMessageBox(message, time,image,video,latLng, 2, dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        if(Commons.chat_photo_edit && Commons.imagePortion != null){
            Commons.imagePortion.setVisibility(View.VISIBLE);
            Commons.mapImage.setVisibility(View.VISIBLE);
            Commons.mapImage.setImageBitmap(Commons.map);

            Commons.chat_photo_edit = false;
        }

    }

    public void getMessages(final boolean flag){

    }

    public void getSecretMessages(final boolean flag){
        if(flag) {
//            Commons.isClickedSecretButton=false;
            reference4.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Map map = dataSnapshot.getValue(Map.class);
                    String message = map.get("message").toString();
                    String status = map.get("istyping").toString();
                    String image = map.get("image").toString();
                    String video = map.get("video").toString();
                    String latStr = map.get("lat").toString();
                    String lonStr = map.get("lon").toString();
                    String online = map.get("online").toString();
                    String userName = map.get("user").toString();
                    String time = map.get("time").toString();

                    LatLng latLng = null;
                    if (latStr.length() > 0 && lonStr.length() > 0) {
                        latLng = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lonStr));
                    } else latLng = null;

                    if (userName.equals(UserDetails.username)) {
                        if (status.equals("true")) {

                        } else {
                            if (secretTalking) {
                                if (message.length() > 0) {
                                    addMessageBoxSecret(message, time, image, video, latLng, 1, dataSnapshot.getKey()); // Commons.speeches.add(0,message);
                                } else if (image.length() > 0)
                                    addMessageBoxSecret(message, time, image, video, latLng, 1, dataSnapshot.getKey());
                            }
                        }
                    } else {

                        if (online.equals("true")) {
                            statusView.setVisibility(View.VISIBLE);
                            statusView.setText("Online");
                        } else {
                            statusView.setVisibility(View.VISIBLE);
                            statusView.setText("Last seen at " + DateFormat.format("MM/dd/yyyy (HH:mm:ss)",
                                    Long.parseLong(time)));
                        }

                        if (status.equals("true")) {

                            statusView.setVisibility(View.VISIBLE);
                            statusView.setText("is typing . . .");
                            typeStatus1.setVisibility(View.VISIBLE);
                            try {
                                if (Commons.userEntity.get_name() != null) {
                                    typeStatus1.setText(Commons.userEntity.get_name() + " " + "is typing ...");
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                            }


                            scrollView1.post(new Runnable() {
                                @Override
                                public void run() {

                                    scrollView1.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });

                        } else {

                            typeStatus1.setVisibility(View.GONE);
                            if (online.equals("true")) {
                                statusView.setVisibility(View.VISIBLE);
                                statusView.setText("Online");
                            } else if (online.equals("false")) {
                                statusView.setVisibility(View.VISIBLE);
                                statusView.setText("Last seen at " + DateFormat.format("MM/dd/yyyy (HH:mm:ss)",
                                        Long.parseLong(time)));
                                if (time.length() == 0) statusView.setVisibility(View.GONE);
                            }
                            if (secretTalking && Commons.isClickedSecretButton) {
                                if (message.length() > 0) {
                                    addMessageBoxSecret(message, time, image, video, latLng, 2, dataSnapshot.getKey());
                                } else if (image.length() > 0)
                                    addMessageBoxSecret(message, time, image, video, latLng, 2, dataSnapshot.getKey());
                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    public void initAudio(){
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }

    public void TextToSpeechFunction(String msg)
    {

        String textholder = msg;
        textToSpeech.speak(textholder, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void addMessageBox(String message, String time, final String imageFile,final String video, final LatLng location, int type, String childId){

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.chat_history_area, null);

        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        try{
            if(Commons.userEntity.get_photoUrl().length()>0)
                photo.setImageUrl(Commons.userEntity.get_photoUrl(),_imageLoader);
        }catch (NullPointerException e){}
        final LinearLayout read=(LinearLayout)dialogView.findViewById(R.id.read);
        final LinearLayout write=(LinearLayout)dialogView.findViewById(R.id.write);
        final LinearLayout dotrec=(LinearLayout)dialogView.findViewById(R.id.receiverdots);
        final LinearLayout dotsend=(LinearLayout)dialogView.findViewById(R.id.senderdots);
        final TextView text = (TextView) dialogView.findViewById(R.id.text);
        final TextView text2 = (TextView) dialogView.findViewById(R.id.text2);
        final TextView datetime = (TextView) dialogView.findViewById(R.id.datetime);
        final TextView datetime2 = (TextView) dialogView.findViewById(R.id.datetime2);
        final TextView writespace = (TextView) dialogView.findViewById(R.id.writespace);
        ImageView image=(ImageView) dialogView.findViewById(R.id.image);
        ImageView image2=(ImageView) dialogView.findViewById(R.id.image2);
        ImageView play=(ImageView) dialogView.findViewById(R.id.playicon);
        ImageView play2=(ImageView) dialogView.findViewById(R.id.playicon2);

        //       if(type==1)photo.setVisibility(View.GONE);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {

            if(is_talking==1){
                dotsend.setVisibility(View.INVISIBLE);
            }else {
                dotsend.setVisibility(View.VISIBLE);
            }

            photo.setVisibility(View.GONE);
            read.setVisibility(View.GONE);

            dotrec.setVisibility(View.GONE);
            writespace.setVisibility(View.VISIBLE);
            write.setVisibility(View.VISIBLE);
            if(video.length()>0){
                text2.setText("Sent a file:\n"+message);
            }
            else
                text2.setText(message);

            text2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(text2.getText().length()>0){

                        if(video.length()>0){
                            if(text2.getText().toString().endsWith(".3gp") || text2.getText().toString().endsWith(".mp3")){

                                showAlertDialogFileDownload("If you want, please click this link:\n"+video);
                                //            showAlertDialogFileDownload(video);
                                myPlayer = new MediaPlayer();
                                try {
                                    myPlayer.setDataSource(video);
                                    myPlayer.prepare();
                                    myPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else
                                showAlertDialogFileDownload("If you want, please click this link:\n"+video);
                            //                showAlertDialogFileDownload(video);
                        }
                        else {
//                            Log.d("11111===>", reference1.push().getKey());
//                           Firebase ref = reference1.child(childId);
//                            showAlertDialogTextEditOrDelete(ref);
                        }
                    }
                }
            });
            datetime2.setText(DateFormat.format("MM/dd/yyyy (HH:mm:ss)",
                    Long.parseLong(time)));

            if(imageFile.length()>0){
                image2.setVisibility(View.VISIBLE);
                image2.setImageBitmap(stringToBitMap(imageFile));
                if(video.length()>0)
                    play2.setVisibility(View.VISIBLE);
                else play2.setVisibility(View.GONE);
                image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Commons.bitmap=stringToBitMap(imageFile);
                        Commons.latLng=location;
                        if(video.length()>0){
                            Commons.videouri=Uri.parse(video); Log.d("VIDEO===>",video);

//                            String videoUrl = Commons.videouri.toString();
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setDataAndType(Uri.parse(videoUrl),"video/mp4");
//                            startActivity(i);

                            Intent intent=new Intent(getApplicationContext(),VideoPlayActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Intent intent=new Intent(getApplicationContext(), ViewActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                text2.setVisibility(View.GONE);
            }else {
                image2.setVisibility(View.GONE);
                text2.setVisibility(View.VISIBLE);
            }

            if(is_talking==0){
                is_talking=1; is_talkingR=0;
            }
//            lp2.gravity = Gravity.RIGHT;
//            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            if(is_talkingR==1){
                photo.setVisibility(View.INVISIBLE);
                dotrec.setVisibility(View.INVISIBLE);
            }else {
                photo.setVisibility(View.VISIBLE);
                dotrec.setVisibility(View.VISIBLE);
            }

            read.setVisibility(View.VISIBLE);
            dotsend.setVisibility(View.GONE);

            writespace.setVisibility(View.GONE);
            write.setVisibility(View.GONE);

//            textToSpeech = new TextToSpeech(ChatActivity.this, ChatActivity.this);
            //        if(!isCarMode){
            if(video.length()>0){
                text.setText("Shared a file:\n"+message);
                Commons.speeches.add(0,"You have received a file");
                speechMessage=speechMessage+". "+"You have received a file";

                if(startTalking==true){
                    TextToSpeechFunction("You have received a file");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(text.getText().toString().endsWith(".3gp") || text.getText().toString().endsWith(".mp3")){

                                audio=video;
                                myPlayer = new MediaPlayer();
                                try {
                                    myPlayer.setDataSource(video);
                                    myPlayer.prepare();
                                    myPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 800);
                }
            }
            else {
                text.setText(message);
                Commons.speeches.add(0,message);
                speechMessage=speechMessage+". "+message;
                if(startTalking==true){
                    try{
                        TextToSpeechFunction(message);
                    }catch (NullPointerException e){}
                }
            }
            //        }else  {
            //            Commons.speeches.add(0,message);
            //        }

            receivedMessages=receivedMessages+1;   Log.d("ReceivedMessages===>",String.valueOf(receivedMessages));

            //        reference.removeValue();

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(text.getText().length()>0){

                        if(video.length()>0){
                            if(text.getText().toString().endsWith(".3gp") || text.getText().toString().endsWith(".mp3")){

                                showAlertDialogFileDownload("If you want, please click this link:\n"+video);
                                myPlayer = new MediaPlayer();
                                try {
                                    myPlayer.setDataSource(video);
                                    myPlayer.prepare();
                                    myPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }else
                                showAlertDialogFileDownload("If you want, please click this link:\n"+video);
                        }
                        else {
//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.setType("text/plain");
//                            shareIntent.putExtra(Intent.EXTRA_TEXT, text.getText().toString());
//                            startActivity(Intent.createChooser(shareIntent, "Share via"));
                        }
                    }
                }
            });
            datetime.setText(DateFormat.format("MM/dd/yyyy (HH:mm:ss)",
                    Long.parseLong(time)));

            if(imageFile.length()>0){
                image.setVisibility(View.VISIBLE);
                image.setImageBitmap(stringToBitMap(imageFile));
                if(video.length()>0)
                    play.setVisibility(View.VISIBLE);
                else play.setVisibility(View.GONE);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Commons.bitmap=stringToBitMap(imageFile);
                        Commons.latLng=location;
                        if(video.length()>0){
                            Commons.videouri=Uri.parse(video);

//                            String videoUrl = Commons.videouri.toString();
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setDataAndType(Uri.parse(videoUrl),"video/mp4");
//                            startActivity(i);

                            Intent intent=new Intent(getApplicationContext(),VideoPlayActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Intent intent=new Intent(getApplicationContext(), ViewActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                text.setVisibility(View.GONE);
            }else {
                image.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
            }

            if(is_talkingR==0){
                is_talking=0; is_talkingR=1;
            }
        }

        layout.addView(dialogView);

        scrollView.post(new Runnable() {
            @Override
            public void run() {

                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                startTalking=true;

                gifView.setVisibility(View.GONE);
                gifView2.setVisibility(View.GONE);
                backgroundframe.setVisibility(View.GONE);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startTalking=true;
            }
        }, 2000);
    }

    public void addMessageBoxSecret(String message, String time, final String imageFile, final String video, final LatLng location, final int type, String childId){

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.chat_history_area, null);

        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        try{
            if(Commons.userEntity.get_photoUrl().length()>0)
                photo.setImageUrl(Commons.userEntity.get_photoUrl(),_imageLoader);
        }catch (NullPointerException e){}
        final LinearLayout read=(LinearLayout)dialogView.findViewById(R.id.read);
        final LinearLayout write=(LinearLayout)dialogView.findViewById(R.id.write);
        final LinearLayout dotrec=(LinearLayout)dialogView.findViewById(R.id.receiverdots);
        final LinearLayout dotsend=(LinearLayout)dialogView.findViewById(R.id.senderdots);
        final TextView text = (TextView) dialogView.findViewById(R.id.text);
        final TextView text2 = (TextView) dialogView.findViewById(R.id.text2);
        final TextView datetime = (TextView) dialogView.findViewById(R.id.datetime);
        final TextView datetime2 = (TextView) dialogView.findViewById(R.id.datetime2);
        final TextView writespace = (TextView) dialogView.findViewById(R.id.writespace);
        ImageView image=(ImageView) dialogView.findViewById(R.id.image);
        ImageView image2=(ImageView) dialogView.findViewById(R.id.image2);
        ImageView play=(ImageView) dialogView.findViewById(R.id.playicon);
        ImageView play2=(ImageView) dialogView.findViewById(R.id.playicon2);

        //       if(type==1)photo.setVisibility(View.GONE);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {

            if(is_talking==1){
                dotsend.setVisibility(View.INVISIBLE);
            }else {
                dotsend.setVisibility(View.VISIBLE);
            }

            photo.setVisibility(View.GONE);
            read.setVisibility(View.GONE);

            dotrec.setVisibility(View.GONE);
            writespace.setVisibility(View.VISIBLE);
            write.setVisibility(View.VISIBLE);
            if(video.length()>0){
                text2.setText("Sent a file:\n"+message);
            }
            else
                text2.setText(message);

            text2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(text2.getText().length()>0){

                        if(video.length()>0){
                            if(text2.getText().toString().endsWith(".3gp") || text2.getText().toString().endsWith(".mp3")){

                                showAlertDialogFileDownload("If you want, please click this link:\n"+video);
                                //            showAlertDialogFileDownload(video);
                                myPlayer = new MediaPlayer();
                                try {
                                    myPlayer.setDataSource(video);
                                    myPlayer.prepare();
                                    myPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else
                                showAlertDialogFileDownload("If you want, please click this link:\n"+video);
                            //                showAlertDialogFileDownload(video);
                        }
                        else {
//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.setType("text/plain");
//                            shareIntent.putExtra(Intent.EXTRA_TEXT, text2.getText().toString());
//                            startActivity(Intent.createChooser(shareIntent, "Share via"));
                        }
                    }
                }
            });
            datetime2.setText(DateFormat.format("MM/dd/yyyy (HH:mm:ss)",
                    Long.parseLong(time)));

            if(imageFile.length()>0){
                image2.setVisibility(View.VISIBLE);
                image2.setImageBitmap(stringToBitMap(imageFile));
                if(video.length()>0)
                    play2.setVisibility(View.VISIBLE);
                else play2.setVisibility(View.GONE);
                image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Commons.bitmap=stringToBitMap(imageFile);
                        Commons.latLng=location;
                        if(video.length()>0){
                            Commons.videouri=Uri.parse(video); Log.d("VIDEO===>",video);

//                            String videoUrl = Commons.videouri.toString();
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setDataAndType(Uri.parse(videoUrl),"video/mp4");
//                            startActivity(i);

                            Intent intent=new Intent(getApplicationContext(),VideoPlayActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Intent intent=new Intent(getApplicationContext(), ViewActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                text2.setVisibility(View.GONE);
            }else {
                image2.setVisibility(View.GONE);
                text2.setVisibility(View.VISIBLE);
            }

            if(is_talking==0){
                is_talking=1; is_talkingR=0;
            }
//            lp2.gravity = Gravity.RIGHT;
//            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            if(is_talkingR==1){
                photo.setVisibility(View.INVISIBLE);
                dotrec.setVisibility(View.INVISIBLE);
            }else {
                photo.setVisibility(View.VISIBLE);
                dotrec.setVisibility(View.VISIBLE);
            }

            read.setVisibility(View.VISIBLE);
            dotsend.setVisibility(View.GONE);

            writespace.setVisibility(View.GONE);
            write.setVisibility(View.GONE);

//            textToSpeech = new TextToSpeech(ChatActivity.this, ChatActivity.this);
            //        if(!isCarMode){
            if(video.length()>0){
                text.setText("Shared a file:\n"+message);
                Commons.speeches.add(0,"You have received a file");
                speechMessage=speechMessage+". "+"You have received a file";

                if(startTalking==true){
        //            TextToSpeechFunction("You have received a file");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(text.getText().toString().endsWith(".3gp") || text.getText().toString().endsWith(".mp3")){

                                audio=video;
                                myPlayer = new MediaPlayer();
                                try {
                                    myPlayer.setDataSource(video);
                                    myPlayer.prepare();
                                    myPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 800);
                }
            }
            else {
                text.setText(message);
                Commons.speeches.add(0,message);
                speechMessage=speechMessage+". "+message;
                if(startTalking==true){
//                    try{
//                        TextToSpeechFunction(message);
//                    }catch (NullPointerException e){}
                }
            }
            //        }else  {
            //            Commons.speeches.add(0,message);
            //        }

            receivedMessages=receivedMessages+1;   Log.d("ReceivedMessages===>",String.valueOf(receivedMessages));

            //        reference.removeValue();

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(text.getText().length()>0){

                        if(video.length()>0){
                            if(text.getText().toString().endsWith(".3gp") || text.getText().toString().endsWith(".mp3")){

                                showAlertDialogFileDownload("If you want, please click this link:\n"+video);
                                myPlayer = new MediaPlayer();
                                try {
                                    myPlayer.setDataSource(video);
                                    myPlayer.prepare();
                                    myPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }else
                                showAlertDialogFileDownload("If you want, please click this link:\n"+video);
                        }
                        else {
//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.setType("text/plain");
//                            shareIntent.putExtra(Intent.EXTRA_TEXT, text.getText().toString());
//                            startActivity(Intent.createChooser(shareIntent, "Share via"));
                        }
                    }
                }
            });
            datetime.setText(DateFormat.format("MM/dd/yyyy (HH:mm:ss)",
                    Long.parseLong(time)));

            if(imageFile.length()>0){
                image.setVisibility(View.VISIBLE);
                image.setImageBitmap(stringToBitMap(imageFile));
                if(video.length()>0)
                    play.setVisibility(View.VISIBLE);
                else play.setVisibility(View.GONE);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Commons.bitmap=stringToBitMap(imageFile);
                        Commons.latLng=location;
                        if(video.length()>0){
                            Commons.videouri=Uri.parse(video);

//                            String videoUrl = Commons.videouri.toString();
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setDataAndType(Uri.parse(videoUrl),"video/mp4");
//                            startActivity(i);

                            Intent intent=new Intent(getApplicationContext(),VideoPlayActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Intent intent=new Intent(getApplicationContext(), ViewActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                text.setVisibility(View.GONE);
            }else {
                image.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
            }

            if(is_talkingR==0){
                is_talking=0; is_talkingR=1;
            }
        }

        layout1.addView(dialogView);

        viewArrayList.add(dialogView); int size=viewArrayList.size();

        scrollView.post(new Runnable() {
            @Override
            public void run() {

                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startTalking=true;
            }
        }, 2000);

        for(int i=0; i<size; i++){
            final int finalI = i;
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (type==1){
                        senderTimeFrame.setVisibility(View.VISIBLE);
                        senderTimeText.setVisibility(View.VISIBLE);
                        senderTime.setText(String.valueOf(millisUntilFinished / 1000));
                    }
                    else if(type==2){
                        receiverTimeFrame.setVisibility(View.VISIBLE);
                        receiverTimeText.setVisibility(View.VISIBLE);
                        receiverTime.setText(String.valueOf(millisUntilFinished/1000));
                    }

                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    if(type==1) {
                        senderTimeText.setVisibility(View.GONE);
                        senderTime.setText("Done!");
                    }
                    else if(type==2) {
                        receiverTimeText.setVisibility(View.GONE);
                        receiverTime.setText("Done!");
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reference4.removeValue();
                            reference8.removeValue();
                            layout.removeView(viewArrayList.get(finalI));
                            viewArrayList.get(finalI).setVisibility(View.GONE);
                            if(type==1) {
                                senderTimeFrame.setVisibility(View.GONE);
                            }
                            else if(type==2) {
                                receiverTimeFrame.setVisibility(View.GONE);
                            }
                        }
                    }, 1000);
                }

            }.start();
        }


    }


    public void online(String status){
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "");
        map.put("istyping", "false");
        map.put("image", "");
        map.put("video", "");
        map.put("lat", "");
        map.put("lon", "");
        map.put("online", status);
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("user", UserDetails.username);
        reference1.push().setValue(map);
        reference2.push().setValue(map);
        if(status.equals("false")){
//            Intent intent=new Intent(this, ChatHistoryActivity.class);
//            startActivity(intent);
            finish();
            overridePendingTransition(0,0);
        }
    }

    public void showCropDialog() {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);

        dialogBuilder.setTitle("Hint!");
        dialogBuilder.setIcon(R.drawable.noti);
        dialogBuilder.setMessage("Do you want to crop?");

        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                isCrop=false;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.PICK_FROM_ALBUM);
            }
        });
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                isCrop=true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.PICK_FROM_ALBUM);
            }
        });
        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void fromPhotoEdit(MenuItem menuItem) {
        Commons.chat_photo_edit = true;
        Commons.imagePortion=imagePortion;
        Commons.mapImage=image;
        Intent intent = new Intent(getApplicationContext(), TakePhotoActivity.class);
        startActivity(intent);
//        finish();
        overridePendingTransition(0,0);
    }

    public void fromGallery(MenuItem menuItem) {

//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);
        showCropDialog();
    }

    public void takePhoto(MenuItem menuItem) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.CROP_FROM_CAMERA: {

                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap_buf=null;
                        File saveFile = BitmapUtils.getOutputMediaFile(this);

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

                        in.close();

                        //set The bitmap data to image View
                        Commons.bitmap=bitmap;

                        imagePortion.setVisibility(View.VISIBLE);
                        image.setImageBitmap(bitmap);
                        //           Constants.userphoto=ui_imvphoto.getDrawable();
                        _photoPath = saveFile.getAbsolutePath();
                        Commons.destination=new File(_photoPath);
                        Commons.imageUrl=_photoPath;    Log.d("PHOTOPATH===",_photoPath.toString());
                        imageBitmapToString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case Constants.PICK_FROM_ALBUM:

                if (resultCode == RESULT_OK) {
                    _imageCaptureUri = data.getData();   Log.d("PHOTOURL===",_imageCaptureUri.toString());
                }

            case Constants.PICK_FROM_CAMERA: {

                if(!is_speech){
                    try {

                        permissionHelper.verifyPermission(
                                new String[]{"take picture"},
                                new String[]{Manifest.permission.CAMERA},
                                new PermissionCallback() {
                                    @Override
                                    public void permissionGranted() {
                                        //action to perform when permission granteed
                                    }

                                    @Override
                                    public void permissionRefused() {
                                        //action to perform when permission refused
                                    }
                                }
                        );

                        try{
                            onCaptureImageResult(data);
                            return;
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if(!isCrop){

                            imagePortion.setVisibility(View.VISIBLE);

                            bitmap=getBitmapFromUri(_imageCaptureUri);
                            image.setImageBitmap(bitmap);

                            imageBitmapToString();

                            return;
                        }

                        _imageCaptureUri = data.getData();

//                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);  Log.d("PHOTOPATH0000===",_photoPath.toString());
                        //        Commons.imageUrl=_photoPath;

//                    this.grantUriPermission("com.android.camera",_imageCaptureUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(_imageCaptureUri, "image/*");

                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                        intent.putExtra("crop", true);
                        intent.putExtra("scale", true);
                        intent.putExtra("outputX", Constants.PROFILE_IMAGE_SIZE);
                        intent.putExtra("outputY", Constants.PROFILE_IMAGE_SIZE);
//                    intent.putExtra("outputX", 800);
//                    intent.putExtra("outputY", 800);
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("noFaceDetection", true);
//                    intent.putExtra("return-data", true);
                        intent.putExtra("output", Uri.fromFile(BitmapUtils.getOutputMediaFile(this)));

                        startActivityForResult(intent, Constants.CROP_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
        }

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {

            if(is_speech){
                is_speech=false;
                audiomessagePortion.setVisibility(View.VISIBLE);
                Log.d("ResultCode===>",String.valueOf(456));
                ArrayList<String> matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(audioMessageArea.getText().length()>0)
                    audioMessageArea.setText(audioMessageArea.getText().toString()+"\n"+matches.get(0));
                else audioMessageArea.setText(matches.get(0));

                //        startVoiceRecognitionActivity();
            }

        }

        if (requestCode == Commons.REQ_CODE_SPEECH_INPUT_CAR && resultCode == RESULT_OK && data!=null) {

            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(speeches.getText().length()>0){
                if (matches.get(0).equals("send")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", speeches.getText().toString());
                    map.put("istyping", "false");
                    map.put("online", "true");
                    map.put("time", String.valueOf(new Date().getTime()));
                    map.put("image", "");
                    map.put("video", "");
                    map.put("lat", "");
                    map.put("lon", "");
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                    is_typing=false;

                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("sender", UserDetails.username.replace("ddoott","."));
                    if(Commons.thisEntity.get_fullName().length()>0)
                        map1.put("senderName", Commons.thisEntity.get_fullName());
                    if(Commons.thisEntity.get_name().length()>0)
                        map1.put("senderName", Commons.thisEntity.get_name());
                    map1.put("senderPhoto", Commons.thisEntity.get_photoUrl());
                    map1.put("msg", speeches.getText().toString());

                    map1.put("secret", "");
                    if(isCarMode)
                        map1.put("carmode", "carmode");
                    else map1.put("carmode", "");

                    reference3.push().setValue(map1);
                    carImage.setImageResource(R.drawable.greencaricon);

                    speeches.setText("");

                    Log.d("ReceivedMessages===>",String.valueOf(receivedMessages));

                    if(receivedMessages>0){
                        receivedMessages=0;
                        final String message=speechMessage;
                        speechMessage="";
                        TextToSpeechFunction("Yours sent. Here replies");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TextToSpeechFunction(message);
                            }
                        }, 2000);

                        if(!textToSpeech.isSpeaking()){
                            if(audio.length()>0){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        myPlayer1 = new MediaPlayer();
                                        try {
                                            myPlayer1.setDataSource(audio);
                                            myPlayer1.prepare();
                                            myPlayer1.start();
                                            audio="";
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, 1000);
                                if(!myPlayer.isPlaying()){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextToSpeechFunction("Speak more");
                                            startVoiceRecognitionActivityCarMode();
                                        }
                                    }, 2000);
                                }
                            }else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextToSpeechFunction("Speak more");
                                        startVoiceRecognitionActivityCarMode();
                                    }
                                }, 2000);
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TextToSpeechFunction("Speak more");
                                startVoiceRecognitionActivityCarMode();
                            }
                        }, 8000);


                    }else {
                        TextToSpeechFunction("No replies. speak more");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startVoiceRecognitionActivityCarMode();
                            }
                        }, 2000);
                    }

                }
                else {
                    speeches.setText(speeches.getText().toString()+". "+matches.get(0));
                    TextToSpeechFunction("Speak more");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startVoiceRecognitionActivityCarMode();
                        }
                    }, 1500);
                }
            }
            else if(matches.get(0).equals("new")){
                carImage.setImageResource(R.drawable.redcaricon);
                TextToSpeechFunction("Please speak your message");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startVoiceRecognitionActivityCarMode();
                    }
                }, 2000);
            }
            else {
                if(!matches.get(0).equals("new")) {
                    speeches.setText(matches.get(0));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startVoiceRecognitionActivityCarMode();
                        }
                    }, 1500);
                }
            }

        }
//        else if (requestCode == 200) {
//            if(requestCode != RESULT_OK){
//                TextToSpeechFunction("Retry speaking");
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startVoiceRecognitionActivityCarMode();
//                    }
//                }, 3000);
//            }
//        }else if(requestCode == 500){
//            return;
//        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                File f=new File(uri.getPath());
                Log.d("FILExtension===>", FilenameUtils.getExtension(f.getName()));

                Toast.makeText(getApplicationContext(),"URI=>"+String.valueOf(uri),Toast.LENGTH_SHORT).show();

                if(FilenameUtils.getExtension(f.getName()).length()>0)
                    showAlertDialogFileUpload(uri);
                else showAlertDialog("Please select a file has a extension(.pdf, .apk, .xls and etc.).");
            }

        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                File f=new File(uri.getPath());
                Log.d("FILExtension===>", FilenameUtils.getExtension(f.getName()));

                Toast.makeText(getApplicationContext(),"URI=>"+String.valueOf(uri),Toast.LENGTH_SHORT).show();

                if(FilenameUtils.getExtension(f.getName()).length()>0 && (FilenameUtils.getExtension(f.getName()).equals("3gp") ||
                        FilenameUtils.getExtension(f.getName()).equals("mp3")))
                    showAlertDialogFileUpload(uri);
                else showAlertDialog("Please select an Audio file.");
            }

        }

    }

    private void onCaptureImageResult(Intent data) {

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Pictures");
        if (!dir.exists())
            dir.mkdirs();

        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        Commons.destination = new File(getExternalStorageDirectory()+"/Pictures",
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            Commons.destination.createNewFile();
            fo = new FileOutputStream(Commons.destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagePortion.setVisibility(View.VISIBLE);
        image.setImageBitmap(thumbnail);
        imageBitmapToString();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openMenuItems() {
        View view = findViewById(R.id.attachbutton);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.chat_attach_menu);
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = android.widget.PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("Error====>", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
        popupMenu.show();

    }

    public void imageBitmapToString(){
        if(bitmap!=null){
            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
            //        bitmap.recycle();
            byte[] byteArray = bYtE.toByteArray();
            imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
            bitmap=null;
        }else if(thumbnail!=null){
            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
            //        thumbnail.recycle();
            byte[] byteArray = bYtE.toByteArray();
            imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
            thumbnail=null;
        }else if(Commons.map!=null){
            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
            Commons.map.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
            //        thumbnail.recycle();
            byte[] byteArray = bYtE.toByteArray();
            imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Commons.map=null;
        }
    }

    public Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void uploadImage(){

        if(imageFile.length()>0){

            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "");
            map.put("istyping", "false");
            map.put("online", "true");
            if(downloadUrl!=null){
                map.put("video", downloadUrl.toString());
                downloadUrl=null;
            }else map.put("video", "");
            map.put("time", String.valueOf(new Date().getTime()));
            map.put("user", UserDetails.username);
            map.put("image", imageFile);
            if(Commons.requestLatlng!=null){
                map.put("lat", String.valueOf(Commons.requestLatlng.latitude));
                map.put("lon", String.valueOf(Commons.requestLatlng.longitude));
                Commons.requestLatlng=null;
            }else {
                map.put("lat", "");
                map.put("lon", "");
            }
            if(!secretTalking) {
                reference1.push().setValue(map);
                reference2.push().setValue(map);
            }else {
                reference4.push().setValue(map);
                reference5.push().setValue(map);
            }
            is_typing=false;

            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("sender", UserDetails.username.replace("ddoott","."));
            if(Commons.thisEntity.get_fullName().length()>0)
                map1.put("senderName", Commons.thisEntity.get_fullName());
            if(Commons.thisEntity.get_name().length()>0)
                map1.put("senderName", Commons.thisEntity.get_name());
            map1.put("senderPhoto", Commons.thisEntity.get_photoUrl());
            map1.put("msg", "Shared a file");
            if(!secretTalking) {

                map1.put("secret", "");
                if(isCarMode)
                    map1.put("carmode", "carmode");
                else map1.put("carmode", "");

                reference3.push().setValue(map1);
            }
            else {

                map1.put("secret", "secret");
                map1.put("carmode", "");

                reference6.push().setValue(map1);
            }

            imageFile="";
        }
    }

    public void checkMessageIssue(){

        String url = "https://maymsgphoto.firebaseio.com/messages.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {

                if(s.equals("null")) {

                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);   Log.d("Message===>",obj.toString());  Log.d("MessageNames===>",obj.names().toString());

                        for(int i=0;i<obj.names().length();i++){

                            if(!obj.names().get(i).toString().contains("@")|| obj.names().get(i).toString().endsWith("_")|| obj.names().get(i).toString().startsWith("_")){
                                String issueName=obj.names().get(i).toString();
//                                obj.remove(issueName);
                                Firebase issueReference=new Firebase("https://maymsgphoto.firebaseio.com/messages/"+issueName);
                                issueReference.removeValue();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatActivity.this);
        rQueue.add(request);
    }

    public void checkSecretMessageIssue(){

        String url = "https://maymsgphoto.firebaseio.com/secret.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {

                if(s.equals("null")) {

                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);   Log.d("Secret===>",obj.toString());  Log.d("SecretNames===>",obj.names().toString());

                        for(int i=0;i<obj.names().length();i++){

                            if(!obj.names().get(i).toString().contains("@")|| obj.names().get(i).toString().endsWith("_")|| obj.names().get(i).toString().startsWith("_")){
                                String issueName=obj.names().get(i).toString();
//                                obj.remove(issueName);
                                Firebase issueReference=new Firebase("https://maymsgphoto.firebaseio.com/secret/"+issueName);
                                issueReference.removeValue();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatActivity.this);
        rQueue.add(request);
    }


    public void registerChatRoom(){

        String url = "https://maymsgphoto.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://maymsgphoto.firebaseio.com/users/"+Commons.userEntity.get_email().replace(".com","").replace(".","ddoott"));

                if(s.equals("null")) {

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("email", Commons.userEntity.get_email());
                    if(Commons.userEntity.get_fullName().length()>0)
                        map.put("name", Commons.userEntity.get_fullName());
                    if(Commons.userEntity.get_name().length()>0)
                        map.put("name", Commons.userEntity.get_name());
                    map.put("photo", Commons.userEntity.get_photoUrl());
                    map.put("gender", Commons.userEntity.getGender());
                    try{
                        map.put("city", Commons.userEntity.get_publicName());
                        map.put("lat", String.valueOf(Commons.userEntity.get_userlat()));
                        map.put("lng", String.valueOf(Commons.userEntity.get_userlng()));
                    }catch (NullPointerException e){}
                    reference.push().setValue(map);

                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);   Log.d("Users===>",obj.toString());  Log.d("UserNames===>",obj.names().toString());

                        for(int i=0;i<obj.names().length();i++){

                            if(!obj.names().get(i).toString().contains("@")|| obj.names().get(i).toString().endsWith("_")|| obj.names().get(i).toString().startsWith("_")){
                                String issueName=obj.names().get(i).toString();
//                                obj.remove(issueName);
                                Firebase issueReference=new Firebase("https://maymsgphoto.firebaseio.com/users/"+issueName);
                                issueReference.removeValue();
                            }
                        }
                        if (!obj.has(Commons.userEntity.get_email().replace(".com","").replace(".","ddoott"))) {

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("email", Commons.userEntity.get_email());
                            if(Commons.userEntity.get_fullName().length()>0)
                                map.put("name", Commons.userEntity.get_fullName());
                            if(Commons.userEntity.get_name().length()>0)
                                map.put("name", Commons.userEntity.get_name());
                            map.put("photo", Commons.userEntity.get_photoUrl());
                            map.put("gender", Commons.userEntity.getGender());
                            try{
                                map.put("city", Commons.userEntity.get_publicName());
                                map.put("lat", String.valueOf(Commons.userEntity.get_userlat()));
                                map.put("lng", String.valueOf(Commons.userEntity.get_userlng()));
                            }catch (NullPointerException e){}
                            reference.push().setValue(map);

                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatActivity.this);
        rQueue.add(request);

    }


    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");

        try {
            Log.d("ResultCode===>",String.valueOf(123));
            this.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showAlertDialog("Sorry! Your device doesn\'t support speech input");
        }catch (NullPointerException a) {

        }

    }

    public void startVoiceRecognitionActivityCarMode() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");

        try {
            Log.d("ResultCode===>",String.valueOf(123));
            this.startActivityForResult(intent, Commons.REQ_CODE_SPEECH_INPUT_CAR);
        } catch (ActivityNotFoundException a) {
            showAlertDialog("Sorry! Your device doesn\'t support speech input");
        }catch (NullPointerException a) {

        }

    }

    public void showAlertDialogFileDownload(String msg) {

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Do you want to download this file?");

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        photo.setVisibility(View.GONE);
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);

        textview.setText(msg);
        textview.setMaxLines(2);

        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "CANCEL",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialogFileUpload(final Uri uri) {

        File f=new File(uri.getPath());
        Log.d("FILExtension===>", FilenameUtils.getExtension(f.getName()));

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Do you want to send this file?");
        alertDialog.setMessage(f.getName());

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startUploadDocument(uri);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialogBackPress(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        online("false");
                        if (mPanel.isEmojiAttached()) {
                            mPanel.dissmissEmojiPopup();
                        }
                        Commons.isClickedSecretButton=false;
                        reference7.removeValue();
                        if(textToSpeech != null) {

                            textToSpeech.stop();
                            textToSpeech.shutdown();
                            Log.d("TAG===>", "TTS Destroyed");
                        }
                        Intent intent = new Intent(getApplicationContext(), BranchActivity.class);
                        ComponentName cn = intent.getComponent();
                        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                        startActivity(mainIntent);
                        finish();
                        overridePendingTransition(0,0);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void finishActivity(){
        finish();
    }

    public void showAlertDialogMenuDocument() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Hint!");
        alertDialog.setMessage("You won't be able to send the file that has been downloaded via this portion once before ");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent;
                        if (Build.VERSION.SDK_INT >= 19) {
                            intent = new Intent("android.intent.action.OPEN_DOCUMENT");
                            intent.setType("*/*");

                        } else {
                            intent = new Intent("android.intent.action.GET_CONTENT");
                            intent.setType("file*//*");
                        }
                        startActivityForResult(intent, 1);
                    }
                });

        alertDialog.show();

    }

    public void showAlertDialogMenuAudio() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Hint!");
        alertDialog.setMessage("You won't be able to send the file that has been downloaded via this portion once before ");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent;
                        if (Build.VERSION.SDK_INT >= 19) {
                            intent = new Intent("android.intent.action.OPEN_DOCUMENT");
                            intent.setType("*/*");

                        } else {
                            intent = new Intent("android.intent.action.GET_CONTENT");
                            intent.setType("file*//*");
                        }
                        startActivityForResult(intent, 2);
                    }
                });

        alertDialog.show();

    }

    public void recordAudio(MenuItem menuItem){
        audioPortion.setVisibility(View.VISIBLE);
    }

    public void pickDocument(MenuItem menuItem){
        showAlertDialogMenuDocument();
    }

    public void pickAudio(MenuItem menuItem){
        showAlertDialogMenuAudio();
    }

    public void pickVideo(MenuItem menuItem){
        gotoTakeVideo("pick");
    }
    public void takeVideo(MenuItem menuItem){
        gotoTakeVideo("capture");
    }
    private void gotoTakeVideo(String option) {
        Commons.imagePortion=imagePortion;
        Commons.mapImage=image;
        Intent intent=new Intent(this,VideoCaptureActivity.class);
        intent.putExtra("OPTION",option);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void startUploadVideo(Uri uri){

        showLoading(true);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://maymsgphoto.appspot.com");

        StorageReference videoReference = firebaseStorage.getReference();

        UploadTask uploadTask = videoReference.child(uri.getLastPathSegment()+ ".mp4").putFile(uri);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ChatActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                downloadUrl = taskSnapshot.getDownloadUrl(); Log.d("DOWNLOADURL===>",downloadUrl.getPath());
                Commons.videouri=null;
                Commons.compressedvideoUrl="";
                imageBitmapToString();
                uploadImage();
                imagePortion.setVisibility(View.GONE);
                attachGroupPortion.setVisibility(View.GONE);
                mFrameLayout.setVisibility(View.GONE);
                mPanel.dissmissEmojiPopup();
                attachmentButton.setImageResource(R.drawable.attachmentbutton);
                attachPortion=false;
                showLoading(false);
            }
        });

    }

    private void startUploadDocument(final Uri uri){

        showLoading(true);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://maymsgphoto.appspot.com");

        StorageReference videoReference = firebaseStorage.getReference();

        UploadTask uploadTask = videoReference.child(uri.getLastPathSegment()).putFile(uri);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ChatActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                downloadUrl = taskSnapshot.getDownloadUrl(); Log.d("PDFDOWNLOADURL===>",downloadUrl.getPath());

                File f=new File(uri.getPath());

                Map<String, String> map = new HashMap<String, String>();
                map.put("message", f.getName());
                map.put("istyping", "false");
                map.put("online", "true");
                map.put("time", String.valueOf(new Date().getTime()));
                map.put("image", "");
                map.put("video", downloadUrl.toString());
                map.put("lat", "");
                map.put("lon", "");
                map.put("user", UserDetails.username);
                if(!secretTalking) {
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }else {
                    reference4.push().setValue(map);
                    reference5.push().setValue(map);
                }
                messageArea.setText("");
                is_typing=false;

                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("sender", UserDetails.username.replace("ddoott","."));
                if(Commons.thisEntity.get_fullName().length()>0)
                    map1.put("senderName", Commons.thisEntity.get_fullName());
                if(Commons.thisEntity.get_name().length()>0)
                    map1.put("senderName", Commons.thisEntity.get_name());
                map1.put("senderPhoto", Commons.thisEntity.get_photoUrl());
                map1.put("msg", "Shared a file");
                if(!secretTalking) {

                    map1.put("secret", "");
                    if(isCarMode)
                        map1.put("carmode", "carmode");
                    else map1.put("carmode", "");

                    reference3.push().setValue(map1);
                }
                else {

                    map1.put("secret", "secret");
                    map1.put("carmode", "");

                    reference6.push().setValue(map1);
                }

                attachGroupPortion.setVisibility(View.GONE);
                mFrameLayout.setVisibility(View.GONE);
                mPanel.dissmissEmojiPopup();
                attachmentButton.setImageResource(R.drawable.attachmentbutton);
                attachPortion=false;
                downloadUrl=null;

                showLoading(false);
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

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        Bitmap image=null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onDestroy() {
        permissionHelper.finish();
        if(textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();
            Log.d("TAG===>", "TTS Destroyed");
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPanel.dissmissEmojiPopup();
    }

    @Override
    public void onBackPressed() {
        //    super.onBackPressed();
        if(mPanel.isEmojiAttached()) {
            mPanel.dissmissEmojiPopup();
            mFrameLayout.setVisibility(View.GONE);
        }
        else {
            textToSpeech=null;
            showAlertDialogBackPress("Do you want to exit?");
        }
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {

            textToSpeech.setLanguage(Locale.US);
            TextToSpeechFunction("");
        }
    }

    public void showAlertDialogTextEditOrDelete(Firebase ref) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Delete",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Edit",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

}
