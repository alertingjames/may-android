package bookclub.technion.maymsgphoto.main.photoedit;

/**
 * Created by sonback123456 on 6/14/2017.
 */

import android.view.MotionEvent;
import android.widget.Toast;

import bookclub.technion.maymsgphoto.stickers.StickerIconEvent;
import bookclub.technion.maymsgphoto.stickers.StickerView;

/**
 * @author wupanjie
 * @see StickerIconEvent
 */

public class HelloIconEvent implements StickerIconEvent {
    @Override public void onActionDown(StickerView stickerView, MotionEvent event) {

    }

    @Override public void onActionMove(StickerView stickerView, MotionEvent event) {

    }

    @Override public void onActionUp(StickerView stickerView, MotionEvent event) {
        Toast.makeText(stickerView.getContext(), "Hello World!", Toast.LENGTH_SHORT).show();
    }
}
