package bookclub.technion.maymsgphoto.widgets;

/**
 * Created by a on 5/13/2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import bookclub.technion.maymsgphoto.R;

public class FontableButton extends Button {
    public FontableButton(Context context) {
        super(context);
    }

    public FontableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        UiUtil.setCustomFont(this, context, attrs,
                R.styleable.bookclub_technion_maymessageapp_widgets_FontableTextView,
                R.styleable.bookclub_technion_maymessageapp_widgets_FontableTextView_font);
    }

    public FontableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        UiUtil.setCustomFont(this, context, attrs,
                R.styleable.bookclub_technion_maymessageapp_widgets_FontableTextView,
                R.styleable.bookclub_technion_maymessageapp_widgets_FontableTextView_font);
    }
}

