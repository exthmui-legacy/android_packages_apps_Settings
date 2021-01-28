package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;

import androidx.preference.PreferenceScreen;

import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;

public class AboutExthmPreferenceController extends BasePreferenceController {

    private static final String KEY_ABOUT_EXTHM = "about_exthmui";

    public AboutExthmPreferenceController(Context context) {
        super(context, KEY_ABOUT_EXTHM);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        Utils.updatePreferenceToSpecificActivityOrRemove(mContext, screen,
                getPreferenceKey(),
                Utils.UPDATE_PREFERENCE_FLAG_SET_TITLE_TO_MATCHING_ACTIVITY);

    }
}
