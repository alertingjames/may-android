package bookclub.technion.maymsgphoto.stickers;

/**
 * Created by sonback123456 on 6/14/2017.
 */

import android.view.MotionEvent;

/**
 * @author wupanjie
 */

public class DeleteIconEvent implements StickerIconEvent {
    @Override public void onActionDown(StickerView stickerView, MotionEvent event) {

    }

    @Override public void onActionMove(StickerView stickerView, MotionEvent event) {

    }

    @Override public void onActionUp(StickerView stickerView, MotionEvent event) {
        stickerView.removeCurrentSticker();
    }
}
