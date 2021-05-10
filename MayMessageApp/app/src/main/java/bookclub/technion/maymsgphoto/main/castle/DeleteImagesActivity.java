package bookclub.technion.maymsgphoto.main.castle;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.adapters.SecureImageAdapter;
import bookclub.technion.maymsgphoto.utils.castle.ImageInfo;
import bookclub.technion.maymsgphoto.utils.castle.ImageUtil;

public class DeleteImagesActivity extends AbstractActivity implements OnItemClickListener, OnClickListener {

    private static final String TAG = "DeleteImagesActivity";
    Uri[] securedImages = null;
    ListView listview = null;
    SecureImageAdapter adapter = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasValidSession()) {
            setContentView(R.layout.activity_delete_images);
            prepListImages();
            Button b = (Button) findViewById(R.id.done_deleting);
            b.setOnClickListener(this);
        } else {
            finish();
        }
    }

    public void onClick(View v) {
        setResult(RESULT_OK);
        finish();
    }

    void prepListImages() {
        listview = (ListView) findViewById(R.id.images_for_deletion);
        securedImages = ImageUtil.getSecuredImageUris();
        adapter = new SecureImageAdapter(this, securedImages, "Delete");
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long rowId) {
        try {
            Uri uri = securedImages[position];
            // Delete image and thumbnail
            deleteSecuredFile(uri.getPath());
            deleteSecuredFile(ImageInfo.getSecuredThumbnailPath(uri));
            securedImages = ImageUtil.getSecuredImageUris();
            adapter.setSecureImageUris(securedImages);
            adapter.notifyDataSetChanged();
            showShortToast("Image deleted");

        } catch (Exception e) {
            Log.e(TAG, "delete image: " + e.getStackTrace());
            showShortToast("Error deleting file. Error ");
        }
    }
}
