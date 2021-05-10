package bookclub.technion.maymsgphoto.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.adapters.NotiUserListAdapter;
import bookclub.technion.maymsgphoto.adapters.RecentUserListAdapter;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.database.DBManager;
import bookclub.technion.maymsgphoto.models.UserEntity;

public class NotificationListActivity extends AppCompatActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {

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
            android.Manifest.permission.LOCATION_HARDWARE};

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private DBManager dbManager;
    ListView listView;
    ImageView imvback;
    private AdView mAdView;
    EditText ui_edtsearch;
    LinearLayout allclear;
    ProgressDialog pd=null;
    Bitmap bitmapPhoto=null;
    int i, messages=0;
    String email="", sender="", name="", photo="", message="";
    SwipyRefreshLayout ui_RefreshLayout;
    ArrayList<UserEntity> _datas=new ArrayList<>(10000);
    ArrayList<UserEntity> _datas2=new ArrayList<>(10000);
    ArrayList<UserEntity> _datas_user=new ArrayList<>();
    NotiUserListAdapter notiUserListAdapter=new NotiUserListAdapter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

//        checkAllPermission();

        dbManager = new DBManager(this);
        dbManager.open();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy (HH:mm:ss)");
        String currentDateandTime = sdf.format(new Date());

        pd = new ProgressDialog(NotificationListActivity.this);
        pd.setMessage("Loading...");

        getAllNotis();

        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.list_friends);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        ui_RefreshLayout.post(new Runnable() {
            @Override

            public void run() {
                if(Commons.userEntities.isEmpty())finish();
                notiUserListAdapter.setDatas(Commons.userEntities);
                notiUserListAdapter.notifyDataSetChanged();
                listView.setAdapter(notiUserListAdapter);
                Commons.nList = listView;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                overridePendingTransition(0,0);
        }
    }
    public void gotoUserProfile(Context context, UserEntity userEntity){

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(0,0);
    }


    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        //       getAllUsers();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
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

    public void getAllNotis(){
        for(int i=0; i<Commons.userEntities.size(); i++){
            Log.d("UserEmail===>", Commons.userEntities.get(i).get_email());
            Log.d("UserRegular===>", Commons.userEntities.get(i).getRegular());
            Log.d("UserSecret===>", Commons.userEntities.get(i).getSecret());
            Log.d("UserCarmode===>", Commons.userEntities.get(i).getCarmode());
        }
    }
}

