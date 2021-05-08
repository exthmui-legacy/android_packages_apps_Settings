package org.exthmui.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.Preference;

import com.android.settings.R;

public class HomepagePreference extends Preference {

    private boolean mShowSummary;
    private Context mContext;

    public HomepagePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        init(attrs);
    }

    public HomepagePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HomepagePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.customPreferenceStyle);
    }

    public HomepagePreference(Context context) {
        this(context, null);
    }

    private void init(AttributeSet attrs) {
        setLayoutResource(R.layout.homepage_preference);
        setWidgetLayoutResource(R.layout.homepage_preference_arrow);
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attrs, R.styleable.exTHm_HomepagePreference);
        this.mShowSummary = obtainStyledAttributes.getBoolean(R.styleable.exTHm_HomepagePreference_showSummary, false);
        obtainStyledAttributes.recycle();
    }

    @Override
    public void setSummary(int summaryResId) {
        if (this.mShowSummary) {
            super.setSummary(summaryResId);
        }
    }

    @Override
    public void setSummary(CharSequence summary) {
        if (this.mShowSummary) {
            super.setSummary(summary);
        }
    }

}
