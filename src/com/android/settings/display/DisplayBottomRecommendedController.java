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

public class DisplayBottomRecommendedController extends BasePreferenceController {

    public static final String KEY_BOTTOM_RECOMMENDED = "display_recommended_key";

    public DisplayBottomRecommendedController(Context context) {
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
            Intent mBiometricAndPsdSettingsIntent = new Intent();
            mBiometricAndPsdSettingsIntent.setClass(mContext, Settings.SecurityDashboardActivity.class);
            arrayList.add(new exTHmRecommendedPreference.RecommendedEntity(mContext.getString(R.string.device_and_security_settings), mBiometricAndPsdSettingsIntent, "", ""));
        }

        exTHmRecommendedPreference.setData(arrayList);
    }
}
