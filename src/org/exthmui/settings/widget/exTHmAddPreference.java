package org.exthmui.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;
import com.android.settings.widget.AddPreference;

public class exTHmAddPreference extends AddPreference {

    private boolean mShowSummary;
    private boolean mIsInHomepage;
    private Context mContext;

    public exTHmAddPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public exTHmAddPreference(Context context) {
        this(context, null);
//        init(null);
    }

    private void init(AttributeSet attrs) {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(attrs, R.styleable.exTHm_RestrictedPreference);
        this.mShowSummary = obtainStyledAttributes.getBoolean(
                R.styleable.exTHm_RestrictedPreference_showSummary, false);
        this.mIsInHomepage = obtainStyledAttributes.getBoolean(
                R.styleable.exTHm_RestrictedPreference_isInHomepage, false);
        Log.d("Settings", "initLayout, isInHomepage="+this.mIsInHomepage);
        obtainStyledAttributes.recycle();
        setLayoutResource(R.layout.exthm_two_target_preference);
        if (this.mIsInHomepage) {
            setWidgetLayoutResource(R.layout.homepage_preference_arrow);
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        View widgetView = view.findViewById(android.R.id.widget_frame);
        final View divider = view.findViewById(R.id.two_target_divider);
        if (this.mIsInHomepage) {
            widgetView.setMinimumWidth(this.mContext.getResources().getDimensionPixelSize(R.dimen.exthm_preference_arrow_widget_min_width));
            divider.setVisibility(View.GONE);
        }
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

    @Override
    protected boolean shouldHideSecondTarget() {
        return !mIsInHomepage;
    }

}
