package com.android.settings.network;

import android.content.Context;
import android.content.Intent;

import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.Settings;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.widget.exTHmRecommendedPreference;

import java.util.ArrayList;
import java.util.List;

public class NetworkBottomRecommendedController extends BasePreferenceController {

    public static final String KEY_BOTTOM_RECOMMENDED = "network_recommended_key";

    public NetworkBottomRecommendedController(Context context) {
        super(context, KEY_BOTTOM_RECOMMENDED);
    }

    @Override
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        List<exTHmRecommendedPreference.RecommendedEntity> arrayList = new ArrayList<>();

        exTHmRecommendedPreference exTHmRecommendedPreference = screen.findPreference(KEY_BOTTOM_RECOMMENDED);
        if(exTHmRecommendedPreference != null){
            Intent mWiFiSettingsIntent = new Intent();
            mWiFiSettingsIntent.setClass(mContext, Settings.WifiSettingsActivity.class);
            arrayList.add(new exTHmRecommendedPreference.RecommendedEntity(mContext.getString(R.string.wifi_settings), mWiFiSettingsIntent, "", ""));

            Intent mWLANHotspotSettingsIntent = new Intent();
            mWLANHotspotSettingsIntent.setClass(mContext, Settings.WifiTetherSettingsActivity.class);
            arrayList.add(new exTHmRecommendedPreference.RecommendedEntity(mContext.getString(R.string.wifi_hotspot_checkbox_text), mWLANHotspotSettingsIntent, "", ""));

            Intent mBluetoothSettingsIntent = new Intent();
            mBluetoothSettingsIntent.setClass(mContext, Settings.BluetoothSettingsActivity.class);
            arrayList.add(new exTHmRecommendedPreference.RecommendedEntity(mContext.getString(R.string.bluetooth), mBluetoothSettingsIntent, "", ""));
        }

        exTHmRecommendedPreference.setData(arrayList);
    }
}
