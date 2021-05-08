package org.exthmui.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;

public class HomepagePreferenceCategory extends PreferenceCategory {

    private boolean mIsTheFirstCategory;
    private View mPreferenceCategoryDividerView;

    public HomepagePreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.homepage_preference_category);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.exTHm_HomepagePreferenceCategory);
        this.mIsTheFirstCategory = obtainStyledAttributes.getBoolean(R.styleable.exTHm_HomepagePreferenceCategory_isTheFirstCategory, false);
        obtainStyledAttributes.recycle();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        this.mPreferenceCategoryDividerView = holder.findViewById(R.id.category_divider_view);
        this.mPreferenceCategoryDividerView.setVisibility(this.mIsTheFirstCategory ? View.GONE : View.VISIBLE);
    }
}
