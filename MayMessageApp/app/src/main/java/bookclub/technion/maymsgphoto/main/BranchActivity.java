package bookclub.technion.maymsgphoto.main;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.gifview.library.GifView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.database.DBManager;
import bookclub.technion.maymsgphoto.main.castle.DisplayMediaActivity;
import bookclub.technion.maymsgphoto.main.takephoto.TakePhotoActivity;
import bookclub.technion.maymsgphoto.models.UserEntity;
import bookclub.technion.maymsgphoto.preferences.PrefConst;
import bookclub.technion.maymsgphoto.preferences.Preference;
import bookclub.technion.maymsgphoto.utils.CircularImageView;
import bookclub.technion.maymsgphoto.utils.CircularNetworkImageView;

public class BranchActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INSTALL_PACKAGES,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR,
            android.Manifest.permission.SET_TIME,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            android.Manifest.permission.LOCATION_HARDWARE,
            android.Manifest.permission.WRITE_SETTINGS};

    private LinearLayout logout, buttonPage;
    private TextView photo1, talk;
    ProgressDialog pd=null;
    Bitmap bitmapPhoto=null;
    int i, messages=0;
    int notiusers = 0 ,snotiusers = 0, messagedusers = 0;
    AlertDialog b;
    View background;
    GifView gifView;
    FrameLayout badgeSet, talkframe;
    TextView msgNum, welcometext;
    ImageView badge, logo;
    private DBManager dbManager;
    String currentDateandTime = "";
    String email="", sender="", name="", photo="", message="";
    ArrayList<UserEntity> _datas=new ArrayList<>(10000);
    ArrayList<String> _emails=new ArrayList<>(10000);
    ArrayList<UserEntity> _datas_user=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);

//        checkAllPermission();

        dbManager = new DBManager(this);
        dbManager.open();

        String welcome = Preference.getInstance().getValue(this, PrefConst.PREFKEY_WELCOMETEXT, "");

        pd = new ProgressDialog(BranchActivity.this);
        pd.setMessage("Loading...");

        _datas_user.clear();
        Commons.userEntities.clear();
        _emails.clear();

        try{
            getMyProfile();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        logo=(ImageView)findViewById(R.id.logo);
        logo.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        logo.startAnimation(animation);

        background = (View)findViewById(R.id.background);

        welcometext = (TextView)findViewById(R.id.welcometext);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy (HH:mm:ss)");
        currentDateandTime = sdf.format(new Date());

        gifView = (GifView)findViewById(R.id.gif);

        badgeSet = (FrameLayout)findViewById(R.id.badgeset);
        talkframe = (FrameLayout)findViewById(R.id.talkframe);
        msgNum = (TextView)findViewById(R.id.num);
        badge = (ImageView)findViewById(R.id.badge);

        logout=(LinearLayout)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        buttonPage=(LinearLayout)findViewById(R.id.buttonPage);
        photo1=(TextView)findViewById(R.id.photo);
        photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(welcome.length()==0) {
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_WELCOMETEXT, "welcome");
                    if(Commons.thisEntity.getGender().equals("male")) welcometext.setText("Hello there, Hotstuff!");
                    else if(Commons.thisEntity.getGender().equals("female")) welcometext.setText("Hello there, Gorgeous!");
                    background.setVisibility(View.VISIBLE);
                    welcometext.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
                    welcometext.startAnimation(animation);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout);
                            welcometext.startAnimation(animation);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    welcometext.setVisibility(View.GONE);
                                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.space);
                                    welcometext.startAnimation(animation);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            background.setVisibility(View.GONE);
                                            showColorDialog();
                                        }
                                    }, 1000);
                                }
                            }, 2000);
                        }
                    }, 1500);
                }
                else {
                    showColorDialog();
                }
            }
        });
        talk=(TextView)findViewById(R.id.talk);
        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(welcome.length()==0) {
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_WELCOMETEXT, "welcome");
                    if(Commons.thisEntity.getGender().equals("male")) welcometext.setText("Hello there, Hotstuff!");
                    else if(Commons.thisEntity.getGender().equals("female")) welcometext.setText("Hello there, Gorgeous!");
                    background.setVisibility(View.VISIBLE);
                    welcometext.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
                    welcometext.startAnimation(animation);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout);
                            welcometext.startAnimation(animation);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    welcometext.setVisibility(View.GONE);
                                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.space);
                                    welcometext.startAnimation(animation);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            background.setVisibility(View.GONE);
                                            buttonPage.setVisibility(View.GONE);
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(0, 0);
                                        }
                                    }, 1000);
                                }
                            }, 2000);
                        }
                    }, 1500);
                }
                else {
                    buttonPage.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });

//        showToast("Please wait...");
        getRecentUsers();
    }

    public void getMyProfile() throws NullPointerException {
        Commons.thisEntity=new UserEntity();

        String email = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USEREMAIL, "");
        String name = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERNAME, "");
        String photo = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERPHOTO, "");
        String address = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERADDRESS, "");
        String latitude = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERLAT, "");
        String longitude = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERLNG, "");
        String gender = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERGENDER, "");

        Commons.thisEntity.set_name(name);      Log.d("Name===>", Commons.thisEntity.get_name());     Log.d("FullName===>", Commons.thisEntity.get_fullName());
        Commons.thisEntity.set_email(email);
        Commons.thisEntity.set_photoUrl(photo);
        Commons.thisEntity.set_publicName(address);
        Commons.thisEntity.setGender(gender);
        Commons.thisEntity.set_userlat(Double.parseDouble(latitude));
        Commons.thisEntity.set_userlng(Double.parseDouble(longitude));
    }

    public void logout() {
        Preference.getInstance().put(this,
                PrefConst.PREFKEY_WELCOMETEXT, "");
        Intent intent = new Intent(BranchActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Preference.getInstance().put(this, PrefConst.PREFKEY_USEREMAIL, "");
        startActivity(intent);
        finish();
    }

    private void showButtons() {
        buttonPage.setVisibility(View.VISIBLE);
        talkframe.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromright);
        talkframe.startAnimation(animation);
        photo1.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromleft);
        photo1.startAnimation(animation);
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

    public void getRecentUsers(){

        gifView.setVisibility(View.VISIBLE);
        gifView.play();
        _datas_user.clear();
        _emails.clear();
        Commons.userEntities.clear();

        try{
            pushNotification(Commons.thisEntity.get_email().toString());
        }catch (Exception e){
            e.printStackTrace();
            Log.d("BranchLog===>", e.getMessage());
            showToast(e.getMessage());
        }

    }

    public void pushNotification(final String email) {

        final Firebase reference = new Firebase("https://maymsgphoto.firebaseio.com/notification/"+ email.replace(".com","").replace(".","ddoott"));

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("Count===>", String.valueOf(dataSnapshot.getChildrenCount()));
                notiusers = (int) dataSnapshot.getChildrenCount();

                final Firebase reference1 = new Firebase("https://maymsgphoto.firebaseio.com/notification/"+ email.replace(".com","").replace(".","ddoott")+"/"+dataSnapshot.getKey());
                Log.d("Reference===>", reference1.toString());

                reference1.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Map map = dataSnapshot.getValue(Map.class);
                        try{
                            message = map.get("msg").toString();
                            sender = map.get("sender").toString();
                            photo = map.get("senderPhoto").toString();
                            name = map.get("senderName").toString();
                            //        time = map.get("time").toString();
                            Commons.notiEmail = sender + ".com";
                            Commons.firebase = reference1;
                            Commons.mapping=map;

                            String carmode = map.get("carmode").toString();
                            String secret = map.get("secret").toString();

                            UserEntity user = new UserEntity();
                            user.set_name(name);
                            user.set_email(sender+".com");     Log.d("NEmail===>",user.get_email());
                            user.set_photoUrl(photo);
                            user.setSecret(secret);             Log.d("Nsecret===>",user.getSecret());
                            user.setCarmode(carmode);        Log.d("NcarMode===>",user.getCarmode());
                            if(user.getSecret().length()==0 && user.getCarmode().length()==0){
                                user.setRegular("regular");        Log.d("Nregular===>",user.getRegular());
                            }

                            if(user.get_name().length()>0){

                                if(!_emails.contains(user.get_email())){
                                    _emails.add(user.get_email());
                                    Commons.userEntities.add(user);
                                    shownot();
                                }

                                if(_emails.contains(user.get_email())){

                                    int index = Commons.userEntities.indexOf(user);

                                    try{
                                        if (user.getRegular().length()>0) {
                                            Commons.userEntities.get(index).setRegular(user.getRegular());   Log.d("NRegular===>",user.getRegular());
                                        }else {}

                                        if (user.getCarmode().length()>0) {
                                            Commons.userEntities.get(index).setCarmode(user.getCarmode());
                                        }else {}

                                    }catch (ArrayIndexOutOfBoundsException e){
                                        e.printStackTrace();
                                    }
                                }

                                if(Commons.userEntities.size()>0){
                                    badgeSet.setVisibility(View.VISIBLE);
                                    msgNum.setText(String.valueOf(Commons.userEntities.size()));
                                }
                            }

                            //        showToast("You received a message!");
                        }catch (NullPointerException e){}
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pushNotificationSecret(Commons.thisEntity.get_email().toString());
            }
        }, notiusers*500);

    }

    public void pushNotificationSecret(final String email) {

        final Firebase referenceSecret = new Firebase("https://maymsgphoto.firebaseio.com/secretnoti/"+ email.replace(".com","").replace(".","ddoott"));

        referenceSecret.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("SCount===>", String.valueOf(dataSnapshot.getChildrenCount()));
                snotiusers = (int) dataSnapshot.getChildrenCount();

                Log.d("Key===>",dataSnapshot.getKey().toString());
                final Firebase referenceSecret1 = new Firebase("https://maymsgphoto.firebaseio.com/secretnoti/"+ email.replace(".com","").replace(".","ddoott")+"/"+dataSnapshot.getKey());

                referenceSecret1.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Map map = dataSnapshot.getValue(Map.class);
                        try{
                            message = map.get("msg").toString();
                            sender = map.get("sender").toString();
                            photo = map.get("senderPhoto").toString();
                            name = map.get("senderName").toString();
                            //        time = map.get("time").toString();
                            Commons.notiEmail = sender + ".com";
                            Commons.firebaseSecret = referenceSecret1;
                            Commons.mapping=map;

                            String carmode = map.get("carmode").toString();
                            String secret = map.get("secret").toString();

                            UserEntity user = new UserEntity();
                            user.set_name(name);
                            user.set_email(sender+".com");        Log.d("SEmail===>",user.get_email());
                            user.set_photoUrl(photo);
                            user.setSecret(secret);           Log.d("SSecret===>",user.getSecret());
                            user.setCarmode(carmode);           Log.d("SCarmode===>",user.getCarmode());

                            if(user.get_name().length()>0){
                                if(!_emails.contains(user.get_email())){
                                    _emails.add(user.get_email());
                                    Commons.userEntities.add(user);
                                    shownot();
                                }

                                if(_emails.contains(user.get_email())){

                                    int index = Commons.userEntities.indexOf(user);
                                    try{
                                        if (user.getSecret().length()>0) {
                                            Commons.userEntities.get(index).setSecret(user.getSecret());
                                        }
                                    }catch (ArrayIndexOutOfBoundsException e){
                                        e.printStackTrace();
                                    }
                                }

                                if(Commons.userEntities.size()>0){
                                    badgeSet.setVisibility(View.VISIBLE);
                                    msgNum.setText(String.valueOf(Commons.userEntities.size()));
                                }
                            }

                            //        showToast("You received a message!");
                        }catch (NullPointerException e){}

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showButtons();
                gifView.setVisibility(View.GONE);
            }
        }, (notiusers+snotiusers)*500);

    }

    public void shownot() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        long[] v = {500,1000};

        Commons.userEntity=new UserEntity();
        Commons.userEntity.set_photoUrl(photo);
        Commons.userEntity.set_name(name);
        Commons.userEntity.set_email(Commons.notiEmail);    Log.d("NotiEmail===>",Commons.notiEmail);

        if(photo.length()>0){
            try {
                bitmapPhoto= BitmapFactory.decodeStream((InputStream) new URL(photo).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                bitmapPhoto=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.messages);
            }
        }else bitmapPhoto=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.messages);

        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        android.app.Notification n = new android.app.Notification.Builder(this)
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.drawable.noti).setLargeIcon(bitmapPhoto)
                .setContentIntent(pIntent)
                .setSound(uri)
//                .setVibrate(v)
                .setAutoCancel(true).build();

        notificationManager.notify(0, n);
    }

    public void checkAllPermission() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showColorDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.select_photo_castle, null);
        dialogBuilder.setView(dialogView);

        final TextView selfie = (TextView) dialogView.findViewById(R.id.selfie);
        selfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.chat_photo_edit = false;
                Intent intent=new Intent(getApplicationContext(), TakePhotoActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
                b.dismiss();
            }
        });
        final TextView castle = (TextView) dialogView.findViewById(R.id.castle);
        castle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("Castle is your secret photo album!");
                b.dismiss();
            }
        });

        dialogBuilder.setTitle("What do you want to select?");

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        b = dialogBuilder.create();
        b.show();
    }

    public void showAlertDialog(String msg) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setIcon(R.drawable.noti);
        dialogBuilder.setTitle("Hint!");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView image=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        image.setVisibility(View.GONE);
        final TextView text=(TextView)dialogView.findViewById(R.id.customView);
        text.setText(msg);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getApplicationContext(), DisplayMediaActivity.class);
                startActivity(intent);
                //        finish();
                overridePendingTransition(0,0);
            }
        });

        dialogBuilder.show();

    }

    public boolean checkMessagedUserIn(UserEntity user, ArrayList<UserEntity> userEntities){
        for(int i=0; i<userEntities.size(); i++){
            if(userEntities.get(i).get_email().equals(user.get_email())){
                return true;
            }
        }
        return false;
    }

    public boolean checkUser(UserEntity user){

        for(int i=0; i<Commons.userEntities.size(); i++){
            Log.d("CommonUsers===>", Commons.userEntities.get(i).get_email());
            Log.d("User===>", user.get_email());
            if(Commons.userEntities.get(i).get_email().equals(user.get_email())){
                return true;
            }
        }
        return false;
    }

}
