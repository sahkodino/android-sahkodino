package junction.fortumquest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import junction.fortumquest.utils.FontProvider;

/**
 * Created by jyri on 25.11.2017.
 */

public class MyTextView extends android.support.v7.widget.AppCompatTextView {

    public MyTextView(Context context) {
        super(context);
        initialize();
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setTypeface(FontProvider.getTypeface(getContext()));
    }

}
