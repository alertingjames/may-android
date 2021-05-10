package bookclub.technion.maymsgphoto;

/**
 * Created by a on 5/13/2017.
 */

import android.app.Application;
import android.text.TextUtils;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;

import bookclub.technion.maymsgphoto.classes.FileUtils;
import bookclub.technion.maymsgphoto.utils.LruBitmapCache;
/**
 * Created by a on 2016.10.22.
 */
public class MayMsgPhotoApplication extends Application implements IAdobeAuthClientCredentials {
    public static final String TAG = MayMsgPhotoApplication.class.getSimpleName();

    public RequestQueue _requestQueue;
    public ImageLoader _imageLoader;

    private String m_gsmToken = "";

    private static MayMsgPhotoApplication _instance;

    private static final String CREATIVE_SDK_CLIENT_ID      = "cc379bdf7a9c46058c5251d11546051a";
    private static final String CREATIVE_SDK_CLIENT_SECRET  = "5141b74b-0adf-488f-9d62-80bb73e05480";
    private static final String CREATIVE_SDK_REDIRECT_URI   = "ams+8c5c931faf3ffc021e42d51b24f16e82a8792ecf://adobeid/cc379bdf7a9c46058c5251d11546051a";
    private static final String[] CREATIVE_SDK_SCOPES       = {"email", "profile", "address"};

    @Override

    public void onCreate(){

        super.onCreate();
        _instance = this;

        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);

        FileUtils.createApplicationFolder();

       /* FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.android.facebookloginsample",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
    }

    public String getGcmToken() {
        return m_gsmToken;
    }

    public void setGcmToken(String p_strGsmToken) {
        m_gsmToken = p_strGsmToken;
    }



    public static synchronized MayMsgPhotoApplication getInstance(){

        return _instance;
    }

    public RequestQueue getRequestQueue(){

        if(_requestQueue == null){
            _requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return _requestQueue;
    }

    public ImageLoader getImageLoader(){

        getRequestQueue();
        if(_imageLoader == null){
            _imageLoader = new ImageLoader(this._requestQueue, new LruBitmapCache());
        }
        return this._imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){

        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (_requestQueue != null) {
            _requestQueue.cancelAll(tag);
        }
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String[] getAdditionalScopesList() {
        return CREATIVE_SDK_SCOPES;
    }

    @Override
    public String getRedirectURI() {
        return CREATIVE_SDK_REDIRECT_URI;
    }
}



