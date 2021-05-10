package bookclub.technion.maymsgphoto.main;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cunoraz.gifview.library.GifView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.adapters.ChatUserListAdapter;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.models.UserEntity;
import bookclub.technion.maymsgphoto.preferences.PrefConst;
import bookclub.technion.maymsgphoto.preferences.Preference;

public class MainActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

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
    ListView listView;
    ImageView imvback;
    private AdView mAdView;
    EditText ui_edtsearch;
    TextView noUsersText;
    ProgressDialog pd;
    GifView gifView;
    String email="", name="", photo="", city="", gender="", lat="", lng="";
    SwipyRefreshLayout ui_RefreshLayout;
    ArrayList<UserEntity> _datas=new ArrayList<>(10000);
    ArrayList<UserEntity> _datas2=new ArrayList<>(10000);
    ChatUserListAdapter chatUserListAdapter=new ChatUserListAdapter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


  //      checkAllPermission();

     //   testData();

        try{
            getMyProfile();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Loading...");

        gifView = (GifView)findViewById(R.id.gif);

        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.userEntities.clear();
                Intent intent=new Intent(getApplicationContext(),BranchActivity.class);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                overridePendingTransition(0,0);
            }
        });

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
    //    ui_edtsearch.setFocusable(false);
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
                    chatUserListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    chatUserListAdapter.setDatas(_datas);
                    listView.setAdapter(chatUserListAdapter);
                }

            }
        });

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
        listView=(ListView)findViewById(R.id.list_friends);
        noUsersText=(TextView)findViewById(R.id.noUsersText);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        _datas.clear();
        ui_RefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                getUsers();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        pd.dismiss();

                        gifView.setVisibility(View.GONE);

                        if(_datas.isEmpty())noUsersText.setVisibility(View.VISIBLE);
                        else noUsersText.setVisibility(View.GONE);
                        chatUserListAdapter.setDatas(_datas);
                        chatUserListAdapter.notifyDataSetChanged();
                        listView.setAdapter(chatUserListAdapter);
                    }
                }, 5000);
            }
        });

        Intent intent=new Intent(getApplicationContext(),RecentUsersActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);

    }

    public void getMyProfile() throws NullPointerException {
        Commons.thisEntity=new UserEntity();

        String email = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USEREMAIL, "");
        String name = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERNAME, "");
        String photo = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERPHOTO, "");
        String address = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERADDRESS, "");
        String latitude = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERLAT, "");
        String longitude = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USERLNG, "");

        Commons.thisEntity.set_name(name);
        Commons.thisEntity.set_email(email);
        Commons.thisEntity.set_photoUrl(photo);
        Commons.thisEntity.set_publicName(address);
        Commons.thisEntity.set_userlat(Double.parseDouble(latitude));
        Commons.thisEntity.set_userlng(Double.parseDouble(longitude));

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
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void getUsers(){

//        pd.show();
        gifView.setVisibility(View.VISIBLE);
        gifView.play();

        String url = "https://maymsgphoto.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {

                try {
                    JSONObject obj = new JSONObject(s);    Log.d("Obj===>",obj.toString());

                    Iterator i = obj.keys();
                    String key = "";

                    while(i.hasNext()){
                        key = i.next().toString();

                        if(!key.equals(Commons.thisEntity.get_email().replace(".com","").replace(".","ddoott"))) {

                            Firebase reference = new Firebase("https://maymsgphoto.firebaseio.com/users/"+key);

                            reference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    Map map = dataSnapshot.getValue(Map.class);
                                    try{
                                        name = map.get("name").toString();
                                        email = map.get("email").toString();
                                        photo = map.get("photo").toString();
                                        gender = map.get("gender").toString();
                                        city = map.get("city").toString();
                                        lat = map.get("lat").toString();
                                        lng = map.get("lng").toString();

                                        UserEntity user=new UserEntity();
                                        user.set_name(name);
                                        user.set_email(email);
                                        user.set_photoUrl(photo);
                                        user.setGender(gender);
                                        user.set_publicName(city);
                                        user.set_userlat(Double.parseDouble(lat));
                                        user.set_userlng(Double.parseDouble(lng));

                                        _datas.add(0,user);

//                                            if(_datas.isEmpty())noUsersText.setVisibility(View.VISIBLE);
//                                            else noUsersText.setVisibility(View.GONE);

                                    }catch (NullPointerException e){}
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                shownot();
//                Toast.makeText(getApplicationContext(), "Data Removed." + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
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

    @Override
    public void onBackPressed() {
        Commons.userEntities.clear();
        Intent intent=new Intent(getApplicationContext(),BranchActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        overridePendingTransition(0,0);
    }
}






































