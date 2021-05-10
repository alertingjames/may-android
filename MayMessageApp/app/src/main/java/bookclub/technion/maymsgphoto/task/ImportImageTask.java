package bookclub.technion.maymsgphoto.task;

/**
 * Created by a on 5/20/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

import bookclub.technion.maymsgphoto.main.castle.ImportMediaActivity;
import bookclub.technion.maymsgphoto.utils.castle.ImageInfo;
import bookclub.technion.maymsgphoto.utils.castle.ImportUtil;

import static com.adobe.creativesdk.foundation.internal.utils.Util.getContext;


public class ImportImageTask extends AsyncTask<ImageInfo, Integer, Integer> {

    ImportMediaActivity activity = null;
    ProgressDialog dialog = null;


    public ImportImageTask(ImportMediaActivity activity) {
        super();
        this.activity = activity;
        dialog = ProgressDialog.show(activity, null, "Securing in MediaCastle...", true, false);
    }

    @Override
    protected Integer doInBackground(ImageInfo... params) {
        try {
            if (params != null) {
                ImageInfo inII = params[0];

                ImportUtil.handleImport(activity.getContentResolver(), inII);

                Uri photoURI = FileProvider.getUriForFile(getContext(), "bookclub.technion.maymsgphoto.provider", new File("file://" + Environment.getExternalStorageDirectory()));
                // This tells the Android cache to refresh
        //        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, photoURI));

            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error========>",e.toString());
            return 1;
        }
    }

    protected void onPostExecute(Integer result) {
        dialog.dismiss();
//        if (result.intValue() == 0) {
//            activity.showShortToast("Import Successful");
//        } else if (result.intValue() == 1) {
//            activity.showShortToast("Image could not be imported");
//        }
        activity.showShortToast("Import Successful");
    }

}

