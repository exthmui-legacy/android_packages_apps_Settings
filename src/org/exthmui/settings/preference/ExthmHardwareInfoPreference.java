package org.exthmui.settings.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.settings.R;

import java.util.ArrayList;
import java.util.List;

public class ExthmHardwareInfoPreference extends Preference {

    private List<HardwareInfo> mHardwareInfoList;

    public ExthmHardwareInfoPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ExthmHardwareInfoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ExthmHardwareInfoPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExthmHardwareInfoPreference(Context context) {
        this(context, null);
    }

    private void init() {
        setLayoutResource(R.layout.exthm_settings_my_device_hardware_info_preference);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.setDividerAllowedAbove(false);
        holder.setDividerAllowedBelow(false);
        RecyclerView recyclerView = (RecyclerView) holder.findViewById(R.id.hardware_info_container);
        recyclerView.setFocusable(false);
        recyclerView.setFocusableInTouchMode(false);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerView.setAdapter(new HardwareInfoAdapter(getContext(), this.mHardwareInfoList));
            return;
        }
        ((HardwareInfoAdapter) adapter).setData(this.mHardwareInfoList);
    }

    public void setData(List<HardwareInfo> hardwareInfoList) {
        if (hardwareInfoList == null || hardwareInfoList.isEmpty()) {
            Log.e("Col_or Settings", "hardwareInfoList is null");
            setVisible(false);
            return;
        }
        Log.e("Col_or Settings", "hardwareInfoList is not null");
        setVisible(true);
        this.mHardwareInfoList = hardwareInfoList;
        notifyChanged();
    }

    public static class HardwareInfo {

        private int hardwareIcon;
        private String hardwareTitle;
        private String hardwareContent;

        public HardwareInfo(int hardwareIcon, String hardwareTitle, String hardwareContent) {
            this.hardwareIcon = hardwareIcon;
            this.hardwareTitle = hardwareTitle;
            this.hardwareContent = hardwareContent;
        }

        public int getHardwareIcon() {
            return hardwareIcon;
        }

        public void setHardwareIcon(int hardwareIcon) {
            this.hardwareIcon = hardwareIcon;
        }

        public String getHardwareTitle() {
            return hardwareTitle;
        }

        public void setHardwareTitle(String hardwareTitle) {
            this.hardwareTitle = hardwareTitle;
        }

        public String getHardwareContent() {
            return hardwareContent;
        }

        public void setHardwareContent(String hardwareContent) {
            this.hardwareContent = hardwareContent;
        }
    }

    public static class HardwareInfoAdapter extends RecyclerView.Adapter<HardwareInfoAdapter.HardwareInfoVH> {

        private Context mContext;
        private List<HardwareInfo> mHardwareInfoList = new ArrayList<>();

        public HardwareInfoAdapter(Context context, List<HardwareInfo> hardwareInfoList) {
            this.mContext = context;
//            this.mHardwareInfoList = hardwareInfoList;
            setData(hardwareInfoList);
        }

        private void setData(List<HardwareInfo> hardwareInfoList) {
            this.mHardwareInfoList.clear();
            if (hardwareInfoList != null) {
                this.mHardwareInfoList.addAll(hardwareInfoList);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public HardwareInfoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HardwareInfoVH(LayoutInflater.from(this.mContext).inflate(R.layout.exthm_settings_my_device_hardware_info_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull HardwareInfoVH holder, int position) {
            HardwareInfo hardwareInfo = this.mHardwareInfoList.get(position);
            holder.hardwareIconView.setImageResource(hardwareInfo.hardwareIcon);
            holder.hardwareTitleView.setText(hardwareInfo.hardwareTitle);
            holder.hardwareContentView.setText(hardwareInfo.hardwareContent);
        }

        @Override
        public int getItemCount() {
            return this.mHardwareInfoList.size();
        }

        private static class HardwareInfoVH extends RecyclerView.ViewHolder {

            private ImageView hardwareIconView;
            private TextView hardwareTitleView;
            private TextView hardwareContentView;

            HardwareInfoVH(@NonNull View itemView) {
                super(itemView);
                this.hardwareIconView = itemView.findViewById(R.id.hardware_info_logo);
                this.hardwareTitleView = itemView.findViewById(R.id.hardware_info_title);
                this.hardwareContentView = itemView.findViewById(R.id.hardware_info_content);
            }
        }

    }

}
