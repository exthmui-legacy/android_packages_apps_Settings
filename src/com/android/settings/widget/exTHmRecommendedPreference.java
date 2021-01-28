package com.android.settings.widget;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.settings.Utils;

import com.android.settings.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class exTHmRecommendedPreference extends Preference {
    private List<RecommendedEntity> mRecommendedEntityList;

    public exTHmRecommendedPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    public exTHmRecommendedPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public exTHmRecommendedPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public exTHmRecommendedPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.exthm_recommended_preference_layout);
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
        if (list == null || list.isEmpty()) {
            setVisible(false);
            return;
        }
        setVisible(true);
        this.mRecommendedEntityList = list;
        notifyChanged();
    }

    public static class RecommendedEntity {
        private Intent mIntent;
        private String mItemDcsEvent;
        private String mLogTag;
        private String mTitle;
        private String mUiDcsEvent;

        public boolean onClick() {
            return false;
        }

        public RecommendedEntity(String str, Intent intent, String str2, String str3) {
            this.mTitle = str;
            this.mIntent = intent;
            this.mItemDcsEvent = str2;
            this.mUiDcsEvent = str3;
        }

        public String getTitle() {
            return this.mTitle;
        }

        public Intent getIntent() {
            return this.mIntent;
        }

        public String getLogTag() {
            return this.mLogTag;
        }

        public String getItemDcsEvent() {
            return this.mItemDcsEvent;
        }

        public String getUiDcsEvent() {
            return this.mUiDcsEvent;
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
                this.mEntities.addAll(list);
                this.mEntities.add(0, new RecommendedEntity(mContext.getString(R.string.recommended_settings), null, "", ""));
            }
            notifyDataSetChanged();
        }

        @Override
        public RecommendedVH onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecommendedVH(LayoutInflater.from(this.mContext).inflate(i == 0 ? R.layout.item_recommended_head_textview : R.layout.item_recommended_common_textview, viewGroup, false));
        }

        public void onBindViewHolder(RecommendedVH recommendedVH, int i) {
            RecommendedEntity recommendedEntity = this.mEntities.get(i);
            if (recommendedEntity != null) {
                recommendedVH.mTitleView.setText(recommendedEntity.getTitle());
                if (i > 0) {
                    recommendedVH.mTitleView.setOnClickListener(view -> {
                        if (recommendedEntity.mLogTag != null) {
                            recommendedEntity.getLogTag().length();
                        }
                        String itemDcsEvent = recommendedEntity.getItemDcsEvent();
                        if (!TextUtils.isEmpty(itemDcsEvent)) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("extra_bottom_recommended_item", itemDcsEvent);
                            hashMap.put("extra_bottom_recommended_ui", recommendedEntity.getUiDcsEvent());
                        }
                        if (!recommendedEntity.onClick()) {
                            Utils.startActivitySafely(this.mContext, recommendedEntity.getIntent());
                        }
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