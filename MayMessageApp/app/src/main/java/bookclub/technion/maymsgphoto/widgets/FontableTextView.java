package bookclub.technion.maymsgphoto.widgets;

/**
 * Created by a on 5/13/2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import bookclub.technion.maymsgphoto.R;

public class FontableTextView extends TextView {

    public FontableTextView(Context context) {
        super(context);
    }

    public FontableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        UiUtil.setCustomFont(this,context,attrs,
                R.styleable.bookclub_technion_maymessageapp_widgets_FontableTextView,
                R.styleable.bookclub_technion_maymessageapp_widgets_FontableTextView_font);
    }

    public FontableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        UiUtil.setCustomFont(this,context,attrs,
                R.styleable.bookclub_technion_maymessageapp_widgets_FontableTextView,
                R.styleable.bookclub_technion_maymessageapp_widgets_FontableTextView_font);
    }
}
