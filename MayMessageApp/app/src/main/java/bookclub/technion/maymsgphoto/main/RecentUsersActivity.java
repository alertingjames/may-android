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
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.adapters.RecentUserListAdapter;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.database.DBManager;
import bookclub.technion.maymsgphoto.models.UserEntity;

public class RecentUsersActivity extends AppCompatActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {

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
    RecentUserListAdapter recentUserListAdapter=new RecentUserListAdapter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_users);

        dbManager = new DBManager(this);
        dbManager.open();

        _datas.addAll(dbManager.getAllMembers());

        pd = new ProgressDialog(RecentUsersActivity.this);
        pd.setMessage("Loading...");

        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.list_friends);

        ImageView speechButton=(ImageView)findViewById(R.id.search_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });
        ImageView delete=(ImageView)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui_edtsearch.setText("");
            }
        });

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        allclear=(LinearLayout)findViewById(R.id.allclear);
        allclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<dbManager.getAllMembers().size();i++){
                    dbManager.delete(dbManager.getAllMembers().get(i).get_idx());
                }

                _datas.clear();
                _datas.addAll(dbManager.getAllMembers());
                if(_datas.isEmpty())finish();
                recentUserListAdapter.setDatas(_datas);
                recentUserListAdapter.notifyDataSetChanged();
                listView.setAdapter(recentUserListAdapter);
            }
        });

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.setFocusable(false);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().toLowerCase(Locale.getDefault());
                if (text.length() != 0) {
                    recentUserListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    recentUserListAdapter.setDatas(_datas);
                    listView.setAdapter(recentUserListAdapter);
                }

            }
        });

        ui_RefreshLayout.post(new Runnable() {
            @Override

            public void run() {

                recentUserListAdapter.setDatas(_datas);
                recentUserListAdapter.notifyDataSetChanged();
                listView.setAdapter(recentUserListAdapter);

                Intent intent=new Intent(getApplicationContext(),NotificationListActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);

                if(_datas.isEmpty())finish();
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

    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showToast("Sorry! Your device doesn\'t support speech input");
        }catch (NullPointerException a) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {

            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            ui_edtsearch.setText(matches.get(0));

        }
    }

    @Override
    public void onBackPressed() {
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

}

