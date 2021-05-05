package org.exthmui.settings.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.android.settings.R;
import com.android.settings.widget.MasterSwitchPreference;

public class exTHmMasterSwitchPreference extends MasterSwitchPreference {

    public exTHmMasterSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public exTHmMasterSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public exTHmMasterSwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.customPreferenceStyle);
    }

    public exTHmMasterSwitchPreference(Context context) {
        this(context, null);
    }

    private void init() {
        setLayoutResource(R.layout.exthm_two_target_preference);
    }

}
