package bookclub.technion.maymsgphoto.main.photoedit;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cunoraz.gifview.library.GifView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.main.ChatActivity;
import bookclub.technion.maymsgphoto.main.takephoto.ViewImageActivity;
import bookclub.technion.maymsgphoto.stickers.BitmapStickerIcon;
import bookclub.technion.maymsgphoto.stickers.DeleteIconEvent;
import bookclub.technion.maymsgphoto.stickers.DrawableSticker;
import bookclub.technion.maymsgphoto.stickers.FlipHorizontallyEvent;
import bookclub.technion.maymsgphoto.stickers.Sticker;
import bookclub.technion.maymsgphoto.stickers.StickerView;
import bookclub.technion.maymsgphoto.stickers.TextSticker;
import bookclub.technion.maymsgphoto.stickers.ZoomIconEvent;
import bookclub.technion.maymsgphoto.utils.FileUtil;
import uk.co.senab.photoview.PhotoView;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = Main2Activity.class.getSimpleName();
    public static final int PERM_RQST_CODE = 110;
    private StickerView stickerView;
    private TextSticker sticker;
    private EditText editText;
    private PhotoView photoView;
    private TextView savebutton, lockbutton, textbutton, imagebutton;
    LinearLayout textcolorbar, imagebar;
    public static int color=0;
    int year, month, day,hour,minute;
    static String dateTime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        Commons.year=year;
        Commons.month=month;
        Commons.day=day;
        Commons.hour=hour;
        Commons.min=minute;

        stickerView = (StickerView) findViewById(R.id.sticker_view);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //currently you can config your own icons and icon event
        //the event you can custom
        photoView=(PhotoView)findViewById(R.id.backimage) ;

        savebutton=(TextView)findViewById(R.id.saveButton);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

        textbutton=(TextView)findViewById(R.id.textbutton);
        textbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text();
            }
        });

        imagebutton=(TextView)findViewById(R.id.imageButton);
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image();
            }
        });

        textcolorbar=(LinearLayout)findViewById(R.id.textcolorBar);
        imagebar=(LinearLayout)findViewById(R.id.imagebar);

        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                R.drawable.sticker_ic_flip_white_18dp),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        BitmapStickerIcon heartIcon =
                new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp),
                        BitmapStickerIcon.LEFT_BOTTOM);
        heartIcon.setIconEvent(new HelloIconEvent());

        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon, heartIcon));

        //default icon layout
        //stickerView.configDefaultIcons();

        stickerView.setBackgroundColor(Color.WHITE);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);

        sticker = new TextSticker(this);

        sticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.sticker_transparent_background));
        sticker.setTextColor(Color.BLACK);
        sticker.setText("Hello!");
        sticker.setTextAlign(Layout.Alignment.ALIGN_NORMAL);
        sticker.resizeText();

        editText=(EditText)findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                stickerView.remove(sticker);
                sticker = new TextSticker(getApplicationContext());
                sticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.sticker_transparent_background));
                sticker.setTextColor(color);
                sticker.setText(s.toString());
//                sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                sticker.resizeText();
                stickerView.addSticker(sticker);

            }
        });

        loadSticker();

        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                //stickerView.removeAllSticker();
                if (sticker instanceof TextSticker) {
                    ((TextSticker) sticker).setTextColor(Color.RED);
                    stickerView.replace(sticker);
                    stickerView.invalidate();
                }
                Log.d(TAG, "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDragFinished");
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Log.d(TAG, "onDoubleTapped: double tap will be with two click");
            }
        });

//        if (toolbar != null) {
//            toolbar.setTitle(R.string.app_name);
//            toolbar.inflateMenu(R.menu.menu_save);
//            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    if (item.getItemId() == R.id.item_save) {
//                        File file = FileUtil.getNewFile(Main2Activity.this, "Sticker");
//                        if (file != null) {
//                            stickerView.save(file);
//                            Toast.makeText(Main2Activity.this, "saved in " + file.getAbsolutePath(),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(Main2Activity.this, "the file is null", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    //                    stickerView.replace(new DrawableSticker(
//                    //                            ContextCompat.getDrawable(MainActivity.this, R.drawable.haizewang_90)
//                    //                    ));
//                    return false;
//                }
//            });
//        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_RQST_CODE);
        } else {
            loadSticker();
        }
    }

    private void text(){
        Commons._calendar_set=false;
        textcolorbar.setVisibility(View.VISIBLE);
        imagebar.setVisibility(View.GONE);
        loadTextSticker();
    }

    private void image(){
        Commons._calendar_set=false;
        textcolorbar.setVisibility(View.GONE);
        imagebar.setVisibility(View.VISIBLE);
    }

    private void saveImage(){
//        File file = FileUtil.getNewFile(Main2Activity.this, "Sticker");
//
//        if (file != null) {
//            stickerView.save(file);
//            Toast.makeText(Main2Activity.this, "saved in " + file.getAbsolutePath(),
//                    Toast.LENGTH_SHORT).show();
//
//        } else {
//            Toast.makeText(Main2Activity.this, "the file is null", Toast.LENGTH_SHORT).show();
//        }

        stickerView.setLocked(!stickerView.isLocked());

        Bitmap bitmap = Bitmap.createBitmap(stickerView.getWidth(), stickerView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        stickerView.draw(canvas);

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

        if(Commons.chat_photo_edit){
            Commons.imagePortion.setVisibility(View.VISIBLE);
            Commons.mapImage.setVisibility(View.VISIBLE);
            showToast("Please wait...");
            try {
                Commons.map = getBitmapFromUri(URI);
                Commons.mapImage.setImageBitmap(Commons.map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            startActivity(mainIntent);
            finish();
        }
        else {
            Commons.imageView.setImageURI(URI);
            Commons.viewImageActivity.showCastleSharingPage();
            finish();
            overridePendingTransition(0,0);
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

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void loadSticker() {
//        Drawable drawable =
//                ContextCompat.getDrawable(this, R.drawable.haizewang_215);
//        Drawable drawable1 =
//                ContextCompat.getDrawable(this, R.drawable.haizewang_23);
//        stickerView.addSticker(new DrawableSticker(drawable));
//        stickerView.addSticker(new DrawableSticker(drawable1), Sticker.Position.BOTTOM | Sticker.Position.RIGHT);
//
//        Drawable bubble = ContextCompat.getDrawable(this, R.drawable.bubble);
//        stickerView.addSticker(
//                new TextSticker(getApplicationContext())
//                        .setDrawable(bubble)
//                        .setText("Sticker\n")
//                        .setMaxTextSize(14)
//                        .resizeText()
//                , Sticker.Position.TOP);
        if(Commons.imageUriSave!=null) {
            photoView.setImageURI(Commons.imageUriSave);
        }
        else if(Commons.imageUri!=null) {
            photoView.setImageURI(Commons.imageUri);
        }
    }

    private void loadSticker(int imageResourceId) {

        Drawable drawable =
                ContextCompat.getDrawable(this, imageResourceId);
        stickerView.addSticker(new DrawableSticker(drawable), Sticker.Position.CENTER);
    }

    private void loadTextSticker() {

        sticker = new TextSticker(getApplicationContext());
        sticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.sticker_transparent_background));
        if(color==0)
            sticker.setTextColor(Color.parseColor("#ff022b"));
        else
            sticker.setTextColor(color);
        sticker.setText("Hello!");
//        sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        sticker.resizeText();
        stickerView.addSticker(sticker);
    }

    public void addText(int color,String text){

        if(Commons._calendar_set){
            Commons.stickerView.remove(Commons.textSticker);
            Commons.textSticker = new TextSticker(this);
            Commons.textSticker .setDrawable(ContextCompat.getDrawable(this,
                    R.drawable.sticker_transparent_background));
            Commons.textSticker .setTextColor(color);
            Commons.textSticker .setText(dateTime);
            Commons.textSticker .setTextAlign(Layout.Alignment.ALIGN_NORMAL);
            Commons.textSticker .resizeText();
            Commons.stickerView.addSticker(Commons.textSticker);
        }
        else {
            stickerView.remove(sticker);
            sticker = new TextSticker(getApplicationContext());
            sticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.sticker_transparent_background));
            sticker.setTextColor(color);
            sticker.setText(text);
//        sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
            sticker.resizeText();
            stickerView.addSticker(sticker);
        }
    }

    public void skipThisPage(View view){
        Commons.viewImageActivity.showCastleSharingPage();
        finish();
        overridePendingTransition(0,0);
    }

    public void dateTime(View view){

        Commons._calendar_set=true;

        Commons.stickerView=stickerView;

        textcolorbar.setVisibility(View.VISIBLE);
        imagebar.setVisibility(View.GONE);

        showTruitonTimePickerDialog();
        showTruitonDatePickerDialog();
    }

    public void fff(View view){
    //    sticker.setTextColor(Color.parseColor("#ffffff"));
        color=Color.parseColor("#ffffff");
        addText(Color.parseColor("#ffffff"),sticker.getText().toString());
    }
    public void c000(View view){
   //     sticker.setTextColor(Color.parseColor("#000000"));
        color=Color.parseColor("#000000");
        addText(Color.parseColor("#000000"),sticker.getText().toString());
    }

    public void ff022b(View view){
    //    sticker.setTextColor(Color.parseColor("#ff022b"));
        color=Color.parseColor("#ff022b");
        addText(Color.parseColor("#ff022b"),sticker.getText().toString());
    }

    public void ff0294(View view){
    //    sticker.setTextColor(Color.parseColor("#ff0294"));
        color=Color.parseColor("#ff0294");
        addText(Color.parseColor("#ff0294"),sticker.getText().toString());
    }

    public void ff02f2(View view){
    //    sticker.setTextColor(Color.parseColor("#ff02f2"));
        color=Color.parseColor("#ff02f2");
        addText(Color.parseColor("#ff02f2"),sticker.getText().toString());
    }

    public void c002ff(View view){
    //    sticker.setTextColor(Color.parseColor("#002ff"));
        color=Color.parseColor("#c002ff");
        addText(Color.parseColor("#c002ff"),sticker.getText().toString());
    }

    public void c6b02ff(View view){
    //    sticker.setTextColor(Color.parseColor("#6b02ff"));
        color=Color.parseColor("#6b02ff");
        addText(Color.parseColor("#6b02ff"),sticker.getText().toString());
    }

    public void c021bff(View view){
    //    sticker.setTextColor(Color.parseColor("#021bff"));
        color=Color.parseColor("#021bff");
        addText(Color.parseColor("#021bff"),sticker.getText().toString());
    }

    public void c0285ff(View view){
   //     sticker.setTextColor(Color.parseColor("#0285ff"));
        color=Color.parseColor("#0285ff");
        addText(Color.parseColor("#0285ff"),sticker.getText().toString());
    }

    public void c02e6ff(View view){
   //     sticker.setTextColor(Color.parseColor("#02e6ff"));
        color=Color.parseColor("#02e6ff");
        addText(Color.parseColor("#02e6ff"),sticker.getText().toString());
    }

    public void c02ffc8(View view){
   //     sticker.setTextColor(Color.parseColor("#02ffc8"));
        color=Color.parseColor("#02ffc8");
        addText(Color.parseColor("#02ffc8"),sticker.getText().toString());
    }

    public void c02ff41(View view){
   //     sticker.setTextColor(Color.parseColor("#02ff41"));
        color=Color.parseColor("#02ff41");
        addText(Color.parseColor("#02ff41"),sticker.getText().toString());
    }

    public void abff02(View view){
   //     sticker.setTextColor(Color.parseColor("#abff02"));
        color=Color.parseColor("#abff02");
        addText(Color.parseColor("#abff02"),sticker.getText().toString());
    }

    public void eaff02(View view){
   //     sticker.setTextColor(Color.parseColor("#eaff02"));
        color=Color.parseColor("#eaff02");
        addText(Color.parseColor("#eaff02"),sticker.getText().toString());
    }

    public void ffff02(View view){
   //     sticker.setTextColor(Color.parseColor("#ffff02"));
        color=Color.parseColor("#ffff02");
        addText(Color.parseColor("#ffff02"),sticker.getText().toString());
    }

    public void ffdd02(View view){
    //    sticker.setTextColor(Color.parseColor("#ffdd02"));
        color=Color.parseColor("#ffdd02");
        addText(Color.parseColor("#ffdd02"),sticker.getText().toString());
    }

    public void ffab02(View view){
   //     sticker.setTextColor(Color.parseColor("#ffab02"));
        color=Color.parseColor("#ffab02");
        addText(Color.parseColor("#ffab02"),sticker.getText().toString());
    }

    public void ff8902(View view){
    //    sticker.setTextColor(Color.parseColor("#ff8902"));
        color=Color.parseColor("#ff8902");
        addText(Color.parseColor("#ff8902"),sticker.getText().toString());
    }

    public void ff5b02(View view){
   //     sticker.setTextColor(Color.parseColor("#ff5b02"));
        color=Color.parseColor("#ff5b02");
        addText(Color.parseColor("#ff5b02"),sticker.getText().toString());
    }

    public void dance1(View view){
        loadSticker(R.drawable.dance1);
    }
    public void dance2(View view){
        loadSticker(R.drawable.dance2);
    }
    public void dance3(View view){
        loadSticker(R.drawable.dance3);
    }
    public void dance4(View view){
        loadSticker(R.drawable.dance4);
    }
    public void dance5(View view){
        loadSticker(R.drawable.dance5);
    }
    public void dance6(View view){
        loadSticker(R.drawable.dance6);
    }
    public void dance7(View view){
        loadSticker(R.drawable.dance7);
    }
    public void stk4(View view){
        loadSticker(R.drawable.stk4);
    }
    public void stk5(View view){
        loadSticker(R.drawable.stk5);
    }
    public void stk6(View view){
        loadSticker(R.drawable.stk6);
    }
    public void stk7(View view){
        loadSticker(R.drawable.stk7);
    }
    public void stk8(View view){
        loadSticker(R.drawable.stk8);
    }

    public void stk9(View view){
        loadSticker(R.drawable.stk9);
    }
    public void stk10(View view){
        loadSticker(R.drawable.stk10);
    }
    public void stk11(View view){
        loadSticker(R.drawable.stk11);
    }
    public void sticker1(View view){
        loadSticker(R.drawable.sticker1);
    }

    public void sticker2(View view){
        loadSticker(R.drawable.sticker2);
    }
    public void sticker3(View view){
        loadSticker(R.drawable.sticker3);
    }

    public void bakeicon(View view){
        loadSticker(R.drawable.bakeicon);
    }

    public void baseballbat(View view){
        loadSticker(R.drawable.baseballbat);
    }

    public void baseballicon(View view){
        loadSticker(R.drawable.baseballicon);
    }

    public void footballcap(View view){
        loadSticker(R.drawable.footballcap);
    }
    public void footballicon(View view){
        loadSticker(R.drawable.footballicon);
    }

    public void footballicon2(View view){
        loadSticker(R.drawable.footballicon2);
    }

    public void hockeypuck(View view){
        loadSticker(R.drawable.hockeypuck);
    }

    public void hockeystick(View view){
        loadSticker(R.drawable.hockeystick);
    }

    public void lightpink(View view){
        loadSticker(R.drawable.lightpink);
    }

    public void lip(View view){
        loadSticker(R.drawable.lip);
    }

    public void lip3(View view){
        loadSticker(R.drawable.lip3);
    }
    public void lip10(View view){
        loadSticker(R.drawable.lip10);
    }

    public void lipblue(View view){
        loadSticker(R.drawable.lipblue);
    }
    public void lipdark(View view){
        loadSticker(R.drawable.lipdark);
    }
    public void lipneon(View view){
        loadSticker(R.drawable.lipneon);
    }
    public void lippink(View view){
        loadSticker(R.drawable.lippink);
    }
    public void lippurple(View view){
        loadSticker(R.drawable.lippurple);
    }
    public void lipred(View view){
        loadSticker(R.drawable.lipred);
    }
    public void lipred2(View view){
        loadSticker(R.drawable.lipred2);
    }
    public void lipturq(View view){
        loadSticker(R.drawable.lipturq);
    }
    public void lipturq2(View view){
        loadSticker(R.drawable.lipturq2);
    }
    public void shop(View view){
        loadSticker(R.drawable.shop);
    }
    public void shopping(View view){
        loadSticker(R.drawable.shopping);
    }
    public void tennisball(View view){
        loadSticker(R.drawable.tennisball);
    }
    public void tennisracket(View view){
        loadSticker(R.drawable.tennisracket);
    }
    public void volleyballs(View view){
        loadSticker(R.drawable.volleyballs);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_RQST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSticker();
        }
    }

    public void testReplace(View view) {
        if (stickerView.replace(sticker)) {
            Toast.makeText(Main2Activity.this, "Replace Sticker successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Main2Activity.this, "Replace Sticker failed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void testLock(View view) {
        stickerView.setLocked(!stickerView.isLocked());
    }

    public void testRemove(View view) {
        if (stickerView.removeCurrentSticker()) {
            Toast.makeText(Main2Activity.this, "Remove current Sticker successfully!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(Main2Activity.this, "Remove current Sticker failed!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void testRemoveAll(View view) {
        stickerView.removeAllStickers();
    }

    public void reset(View view) {
        stickerView.removeAllStickers();
        loadSticker();
    }

    public void testAdd(View view) {
        final TextSticker sticker = new TextSticker(this);
        sticker.setText("Hello, world!");
        sticker.setTextColor(Color.BLUE);
        sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        sticker.resizeText();

        stickerView.addSticker(sticker);
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, Commons.hour, Commons.min,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            if(hourOfDay>12){
                hourOfDay=hourOfDay-12;
                dateTime=dateTime + "_" + hourOfDay + ":" + minute+" PM";
            }else {
                dateTime=dateTime + "_" + hourOfDay + ":" + minute+" AM";
            }

            Commons.textSticker = new TextSticker(getActivity());
            Commons.textSticker .setDrawable(ContextCompat.getDrawable(getActivity(),
                    R.drawable.sticker_transparent_background));
            if(color==0)
                Commons.textSticker .setTextColor(Color.parseColor("#ff022b"));
            else
                Commons.textSticker .setTextColor(color);
            Commons.textSticker .setText(dateTime);
            Commons.textSticker .setTextAlign(Layout.Alignment.ALIGN_NORMAL);
            Commons.textSticker .resizeText();
            Commons.stickerView.addSticker(Commons.textSticker);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        String[] monthes={"January","February","March","April","May","June","July","August","September","October","November","December"};

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this,Commons.year, Commons.month, Commons.day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            dateTime=monthes[month] + " " + day + "," + year;
        }
    }

    public void showTruitonTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTruitonDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
}

