package bookclub.technion.maymsgphoto.main.takephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.utils.photoutils.AsyncImageLoader;
import bookclub.technion.maymsgphoto.utils.photoutils.ScaledBitmapCache;

public class LibraryActivity extends Activity {

    static int CELL_WIDTH = 92;
    static int CELL_HEIGHT = 69;

    String imageDirectory;
    ImageView back;

    GridView gridView;
    int selectedGridIndex;

    List imageMaps = new ArrayList();
    static String IMAGE_URI_KEY = "imageUri";

    // A cache of scaled Bitmaps for the image files, so we can avoid reloading them as the user scrolls.
    ScaledBitmapCache bitmapCache;
    AsyncImageLoader imageLoader = new AsyncImageLoader();

    public static Intent intentWithImageDirectory(Context parent, String imageDirectory) {
        Intent intent = new Intent(parent, LibraryActivity.class);
        intent.putExtra("imageDirectory", imageDirectory);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),TakePhotoActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(0,0);
            }
        });
        imageDirectory = getIntent().getStringExtra("imageDirectory");
        bitmapCache = new ScaledBitmapCache(this, imageDirectory + File.separator + "thumbnails");

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectedGridIndex = position;
                ViewImageActivity.startActivityWithImageURI(LibraryActivity.this,
                        (Uri)((Map)imageMaps.get(position)).get(IMAGE_URI_KEY), "image/jpeg");
           //     finish();
            }
        });
        readImageThumbnails();
        displayGrid();

        showToast("If you want to refer from Gallery, please select any picture here.");
    }

    void readImageThumbnails() {
        List<String> filenames = Collections.emptyList();
        File dir = new File(imageDirectory);
        if (dir.isDirectory()) {
            filenames = Arrays.asList(dir.list());
        }
        Collections.sort(filenames);
        Collections.reverse(filenames);

        imageMaps.clear();
        for(String fname : filenames) {
            if (fname.endsWith(".jpg")) {
                Uri imageUri = Uri.fromFile(new File(imageDirectory + File.separator + fname));
                Map dmap = new HashMap();
                dmap.put(IMAGE_URI_KEY, imageUri);
                imageMaps.add(dmap);
            }
        }
    }

    void displayGrid() {
        SimpleAdapter adapter = new SimpleAdapter(this, imageMaps,
                R.layout.library_cell,
                new String[] {IMAGE_URI_KEY},
                new int[] {R.id.grid_image});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                Uri imageUri = (Uri)data;
                imageLoader.loadImageIntoViewAsync(bitmapCache, imageUri, (ImageView)view, CELL_WIDTH, CELL_HEIGHT, getResources());
                return true;
            }
        });
        gridView.setAdapter(adapter);

        // show text message if no images available
        View noImagesView = findViewById(R.id.noImagesTextView);
        noImagesView.setVisibility(imageMaps.size()>0 ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ViewImageActivity.DELETE_RESULT) {
            bitmapCache.removeUri((Uri)((Map)imageMaps.get(selectedGridIndex)).get(IMAGE_URI_KEY));
            imageMaps.remove(selectedGridIndex);
            displayGrid();
        }
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

    @Override
    public void onBackPressed() {
        Intent i=new Intent(getApplicationContext(),TakePhotoActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(0,0);
    }
}
