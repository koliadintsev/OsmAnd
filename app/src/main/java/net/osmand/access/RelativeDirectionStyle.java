package net.osmand.access;

import com.operasoft.snowboard.R;
import android.content.Context;

public enum RelativeDirectionStyle {

    SIDEWISE(R.string.direction_style_sidewise),
    CLOCKWISE(R.string.direction_style_clockwise);

    private final int key;

    RelativeDirectionStyle(int key) {
        this.key = key;
    }

    public String toHumanString(Context ctx) {
        return ctx.getResources().getString(key);
    }

}
