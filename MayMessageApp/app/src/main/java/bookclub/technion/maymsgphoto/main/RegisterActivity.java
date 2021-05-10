package bookclub.technion.maymsgphoto.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import bookclub.technion.maymsgphoto.MayMsgPhotoApplication;
import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.commons.Constants;
import bookclub.technion.maymsgphoto.models.FacebookUser;
import bookclub.technion.maymsgphoto.preferences.PrefConst;
import bookclub.technion.maymsgphoto.preferences.Preference;
import bookclub.technion.maymsgphoto.utils.CircularNetworkImageView;

public class RegisterActivity extends AppCompatActivity {

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

    private ProgressBar progressBar;
    private LoginButton loginButton;
    ImageLoader _imageLoader;
    public static CallbackManager callbackManager;

    private String FEmail, Name, Firstname, Lastname, Id, Gender, Image_url,Birthday, Education;

    boolean _isFromLogout = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        checkAllPermission();

        initValue();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        _imageLoader = MayMsgPhotoApplication.getInstance().getImageLoader();

        try {

            PackageInfo info = getPackageManager().getPackageInfo("bookclub.technion.maymsgphoto", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());
                Log.i("KeyHash::", Base64.encodeToString(md.digest(), Base64.DEFAULT));//will give developer key hash
                //            Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show(); //will give app key hash or release key hash

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        final LinearLayout ui_facebook = (LinearLayout) findViewById(R.id.lytfacebook);
        ui_facebook.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ui_facebook.setBackground(getDrawable(R.drawable.green_fillrect));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        ui_facebook.setBackground(getDrawable(R.drawable.facebook_fillrect));
                        loginWithFB();
                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        ui_facebook.getBackground().clearColorFilter();
                        ui_facebook.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        if (_isFromLogout) {

            //save user to empty.
            Preference.getInstance().put(this,
                    PrefConst.PREFKEY_USEREMAIL, "");

        }else {
            String email = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USEREMAIL, "");

            if (email.length() > 0) {
                Intent intent=new Intent(this,BranchActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }

        }

        loginButton= (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","publish_actions");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    //            getProfileInfo();
            }
        });
    }

    private void initValue(){

        Intent intent = getIntent();
        try {
            _isFromLogout = intent.getBooleanExtra(Constants.KEY_LOGOUT, false);
        } catch (Exception e) {
        }

    }

    private void loginWithFB() {

        callbackManager = CallbackManager.Factory.create();

        // set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_photos","user_birthday", "public_profile"));//, "public_profile"   "email",

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());

                                try{
                                    Birthday=object.getString("birthday");
                                    Log.d("Birthday: ",Birthday);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }

                                try {
                                    if (android.os.Build.VERSION.SDK_INT > 9) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                        URL fb_url = new URL(profilePicUrl);//small | noraml | large
                                        HttpsURLConnection conn1 = (HttpsURLConnection) fb_url.openConnection();
                                        HttpsURLConnection.setFollowRedirects(true);
                                        conn1.setInstanceFollowRedirects(true);
                                        Bitmap fb_img = BitmapFactory.decodeStream(conn1.getInputStream());
                                        Commons.thisEntity.set_bitmap(fb_img);
                                    }
                                }catch (Exception ex) {
                                    ex.printStackTrace();
                                }



                                try {
                                    Name = object.getString("name");
                                    Name.replace(" ", "");
                                    Id = object.getString("id");
                                    Firstname = object.getString("first_name");
                                    Lastname = object.getString("last_name");
                                    Gender = object.getString("gender");
                                    FEmail = object.getString("email");
                                    Image_url = "http://graph.facebook.com/(Id)/picture?type=large";
                                    Image_url = URLEncoder.encode(Image_url);
                                    Log.d("Email = ", " " + FEmail);
                                    Log.d("Name======", Name);
                                    Log.d("Image====",Image_url.toString());
                                    Log.d("firstName======", Firstname);
                                    Log.d("lastName======", Lastname);
                                    Log.d("Gender======", Gender);
                                    Log.d("id======", Id);
                                    Log.d("Object=====>", object.toString());
                                    Log.d("photourl======", Image_url.toString());

                                    if (object.has("picture")) {
                                        JSONObject jsonPicture = object.getJSONObject("picture");
                                        if (jsonPicture.has("data")) {
                                            JSONObject jsonData = jsonPicture.getJSONObject("data");
                                            if (jsonData.has("url"))
                                                Commons.thisEntity.set_photoUrl(jsonData.getString("url"));
                                        }
                                    }

                                    // SocialLogin(Firstname, Lastname,Gender,FEmail,Image_url,Id,"facebook");
                                    showInfo("first name: "+Firstname+"\n"+"Last name: "+Lastname+"\n"+"Gender: "+Gender+"\n"+"Email: "+FEmail+"\n");
                                    //    showInfo(object.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }


            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    private  void showInfo(String infomation) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Please register your location.");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        photo.setImageUrl(Commons.thisEntity.get_photoUrl(),_imageLoader);
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(infomation);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Commons.thisEntity.set_firstName(Firstname);
                Commons.thisEntity.set_lastName(Lastname);
                Commons.thisEntity.set_email(FEmail);
                Commons.thisEntity.setGender(Gender);

                Commons.thisEntity.set_adminId(0);

//                registerChatRoom();
                Intent intent=new Intent(getApplicationContext(),MyLocationCaptureActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        showToast("Please wait...");
    }
    //==================================Face book Login End====================================

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

    private void getProfileInfo(){

        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");
        permissions.add("user_birthday");
        loginButton.setReadPermissions(permissions);

        callbackManager = new CallbackManager.Factory().create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final FacebookUser fbUser = new FacebookUser();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("response", object.toString());
                        try {

                            fbUser.setId(object.getString("id"));
                            fbUser.setFullName(object.getString("name"));
                            fbUser.setFirstName(object.getString("first_name"));
                            fbUser.setLastName(object.getString("last_name"));
                            if (object.has("gender"))
                                fbUser.setGender(object.getString("gender"));
                            if (object.has("birthday"))
                                fbUser.setBirthday(object.getString("birthday"));
                            if (object.has("picture")) {
                                JSONObject jsonPicture = object.getJSONObject("picture");
                                if (jsonPicture.has("data")) {
                                    JSONObject jsonData = jsonPicture.getJSONObject("data");
                                    if (jsonData.has("url"))
                                        fbUser.setAvatar(jsonData.getString("url"));
                                }
                            }
                            fbUser.setEmail(object.getString("email"));

                            //            signUpwithFacebookUser(fbUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle params = new Bundle();
                params.putString("fields", "id,name,email,gender,birthday,locale,cover,picture,first_name,last_name");
                request.setParameters(params);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });

    }
}

































