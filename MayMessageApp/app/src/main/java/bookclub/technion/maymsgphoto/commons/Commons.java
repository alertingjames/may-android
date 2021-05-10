package bookclub.technion.maymsgphoto.commons;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cunoraz.gifview.library.GifView;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import bookclub.technion.maymsgphoto.main.takephoto.ViewImageActivity;
import bookclub.technion.maymsgphoto.models.UserEntity;
import bookclub.technion.maymsgphoto.stickers.StickerView;
import bookclub.technion.maymsgphoto.stickers.TextSticker;

/**
 * Created by a on 5/13/2017.
 */

public class Commons {
    public static UserEntity userEntity=new UserEntity();
    public static UserEntity thisEntity=new UserEntity();
    public static ArrayList<UserEntity> userEntities=new ArrayList<>();
    public static String loc_url="";
    public static Firebase firebase=null;
    public static Firebase firebaseSecret=null;
    public static Map mapping=null;
    public static String notiEmail="";
    public static ArrayList<String> speeches=new ArrayList<>();
    public static ArrayList<String> secspeeches=new ArrayList<>();
    public static LatLng requestLatlng=null;
    public static LinearLayout imagePortion=null;
    public static ImageView mapImage=null;
    public static String compressedvideoUrl="";
    public static Uri videouri=null;
    public static int REQ_CODE_SPEECH_INPUT_CAR =200;
    public static Bitmap bitmap=null;
    public static LatLng latLng=null;
    public static String photoUrl="";
    public static int resId=0;
    public static File destination=null;
    public static File tempFile=null;
    public static String imageUrl="";
    public static Bitmap map=null;
    public static String videoUrl="";
    public static Bitmap thumb=null;
    public static boolean _video_post_flag=false;
    public static File file=null;
    public static File fileShare=null;
    public static boolean _location_activity=false;
    public static Bitmap bitmap_activity=null;
    public static boolean isClickedSecretButton=false;
    public static int cameraWidth=0;
    public static int cameraHeight=0;
    public static int notificationStart=0;
    public static boolean _isSelectedLock=false;
    public static Uri imageUri=null;
    public static ImageView imageView=null;
    public static GifView gifView = null;

    public static Uri imageUriSave=null;
    public static String imagePathSave="";
    public static Uri imageUriSaveShare=null;
    public static String imagePathSaveShare="";
    public static boolean _flag_share=false;
    public static boolean _flag_chathistory=false;
    public static boolean f_msg=false;
    public static ListView nList = null;

    public static int year=0;
    public static int month=0;
    public static int day=0;
    public static int hour=0;
    public static int min=0;
    public static String _datetime="";
    public static boolean chat_photo_edit = false;
    public static TextSticker textSticker=null;
    public static StickerView stickerView=null;
    public static boolean _calendar_set=false;
    public static boolean _edited_photo=false;
    public static ViewImageActivity viewImageActivity=null;
    public static RelativeLayout commentImageFrame=null;
}



































