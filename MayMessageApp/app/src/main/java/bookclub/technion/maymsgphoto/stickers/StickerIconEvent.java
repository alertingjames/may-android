package bookclub.technion.maymsgphoto.stickers;

/**
 * Created by sonback123456 on 6/14/2017.
 */

import android.view.MotionEvent;

/**
 * @author wupanjie
 */

public interface StickerIconEvent {
    void onActionDown(StickerView stickerView, MotionEvent event);

    void onActionMove(StickerView stickerView, MotionEvent event);

    void onActionUp(StickerView stickerView, MotionEvent event);
}
