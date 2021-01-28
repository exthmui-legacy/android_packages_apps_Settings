package com.android.settings.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.settings.Utils;

import com.android.settings.R;

import java.util.ArrayList;
import java.util.List;

public class exTHmRecommendedPreference extends Preference {
    private final ArrayList<RecommendedEntity> mRecommendedEntityList = new ArrayList<>();

    public exTHmRecommendedPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.exthm_recommended_preference_layout);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecommendedPreference, defStyleAttr, defStyleRes);
        CharSequence[] arrEntries = TypedArrayUtils.getTextArray(
                                        typedArray,
                                        R.styleable.RecommendedPreference_entries,
                                        R.styleable.RecommendedPreference_android_entries);
        CharSequence[] arrValues = TypedArrayUtils.getTextArray(
                                        typedArray, 
                                        R.styleable.RecommendedPreference_entryValues,
                                        R.styleable.RecommendedPreference_android_entryValues);

        if (arrEntries == null || arrValues == null) {
            return;
        }

        for (int i = 0; i < arrEntries.length; i++) {
            String[] val = arrValues[i].toString().split("\\|");
            Intent intent = new Intent();
            ComponentName cn;
            if (val.length == 1) {
                cn = new ComponentName(context, val[0]);
            } else {
                cn = new ComponentName(val[0], val[1]);
            }
            intent.setComponent(cn);
            mRecommendedEntityList.add(new RecommendedEntity(arrEntries[i], intent));
        }
    }

    public exTHmRecommendedPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public exTHmRecommendedPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public exTHmRecommendedPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        RecyclerView recyclerView = (RecyclerView) preferenceViewHolder.itemView.findViewById(R.id.recommended_recycler_view);
        recyclerView.setFocusable(false);
        recyclerView.setFocusableInTouchMode(false);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new RecommendedAdapter(getContext(), this.mRecommendedEntityList));
            return;
        }
        ((RecommendedAdapter) adapter).setData(this.mRecommendedEntityList);
    }

    public void setData(List<RecommendedEntity> list) {
        mRecommendedEntityList.clear();
        if (list == null || list.isEmpty()) {
            setVisible(false);
            return;
        }
        setVisible(true);
        this.mRecommendedEntityList.addAll(list);
        notifyChanged();
    }

    public static class RecommendedEntity {
        private final Intent mIntent;
        private final CharSequence mTitle;

        public RecommendedEntity(CharSequence title, Intent intent) {
            this.mTitle = title;
            this.mIntent = intent;
        }

        public RecommendedEntity(String str, Intent intent, String str2, String str3) {
            this(str, intent);
        }

        public CharSequence getTitle() {
            return this.mTitle;
        }

        public Intent getIntent() {
            return this.mIntent;
        }
    }

    public static class RecommendedAdapter extends RecyclerView.Adapter<RecommendedVH> {
        private Context mContext;
        private List<RecommendedEntity> mEntities = new ArrayList();

        @Override
        public int getItemViewType(int i) {
            return i == 0 ? 0 : 1;
        }

        public RecommendedAdapter(Context context, List<RecommendedEntity> list) {
            this.mContext = context;
            setData(list);
        }

        private void setData(List<RecommendedEntity> list) {
            this.mEntities.clear();
            if (list != null) {
                this.mEntities.add(0, new RecommendedEntity(mContext.getString(R.string.recommended_settings), null, "", ""));
                this.mEntities.addAll(list);
            }
            notifyDataSetChanged();
        }

        @Override
        public RecommendedVH onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecommendedVH(
                LayoutInflater.from(this.mContext).inflate(
                    getItemViewType(i) == 0 ? 
                    R.layout.item_recommended_head_textview : 
                    R.layout.item_recommended_common_textview, 
                    viewGroup, false));
        }

        public void onBindViewHolder(RecommendedVH recommendedVH, int i) {
            RecommendedEntity recommendedEntity = this.mEntities.get(i);
            if (recommendedEntity != null) {
                recommendedVH.mTitleView.setText(recommendedEntity.getTitle());
                if (i > 0) {
                    recommendedVH.mTitleView.setOnClickListener(view -> {
                        Utils.startActivitySafely(this.mContext, recommendedEntity.getIntent());
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return this.mEntities.size();
        }
    }

    private static class RecommendedVH extends RecyclerView.ViewHolder {
        TextView mTitleView;

        RecommendedVH(View view) {
            super(view);
            this.mTitleView = (TextView) view;
        }
    }
}