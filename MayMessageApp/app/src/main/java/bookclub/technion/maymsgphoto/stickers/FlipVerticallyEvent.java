package bookclub.technion.maymsgphoto.stickers;

/**
 * Created by sonback123456 on 6/14/2017.
 */

public class FlipVerticallyEvent extends AbstractFlipEvent {

    @Override @StickerView.Flip protected int getFlipDirection() {
        return StickerView.FLIP_VERTICALLY;
    }
}
