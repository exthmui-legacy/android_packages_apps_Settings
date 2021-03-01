/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.network;

import static androidx.lifecycle.Lifecycle.Event.ON_PAUSE;
import static androidx.lifecycle.Lifecycle.Event.ON_RESUME;

import static com.android.settings.network.telephony.MobileNetworkUtils.NO_CELL_DATA_TYPE_ICON;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.provider.Settings;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.ArraySet;

import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import com.android.settings.network.telephony.DataConnectivityListener;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.SignalStrengthListener;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.net.SignalStrengthUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

/**
 * This populates the entries on a page which lists all available mobile subscriptions. Each entry
 * has the name of the subscription with some subtext giving additional detail, and clicking on the
 * entry brings you to a details page for that network.
 */
public class MobileNetworkListController extends AbstractPreferenceController implements
        LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient,
        MobileDataEnabledListener.Client, DataConnectivityListener.Client,
        SignalStrengthListener.Callback {
    private static final String TAG = "MobileNetworkListCtlr";

    @VisibleForTesting
    static final String KEY_ADD_MORE = "add_more";

    private SubscriptionManager mSubscriptionManager;
    private ConnectivityManager mConnectivityManager;
    private SubscriptionsChangeListener mChangeListener;
    private MobileDataEnabledListener mDataEnabledListener;
    private DataConnectivityListener mConnectivityListener;
    private SignalStrengthListener mSignalStrengthListener;
    private PreferenceScreen mPreferenceScreen;
    private Map<Integer, Preference> mPreferences;

    public MobileNetworkListController(Context context, Lifecycle lifecycle) {
        super(context);
        mSubscriptionManager = context.getSystemService(SubscriptionManager.class);
        mConnectivityManager = mContext.getSystemService(ConnectivityManager.class);
        mDataEnabledListener = new MobileDataEnabledListener(context, this);
        mConnectivityListener = new DataConnectivityListener(context, this);
        mSignalStrengthListener = new SignalStrengthListener(context, this);
        mChangeListener = new SubscriptionsChangeListener(context, this);
        mPreferences = new ArrayMap<>();
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(ON_RESUME)
    public void onResume() {
        mChangeListener.start();
        mDataEnabledListener.start(SubscriptionManager.getDefaultDataSubscriptionId());
        mConnectivityListener.start();
        mSignalStrengthListener.resume();
        update();
    }

    @OnLifecycleEvent(ON_PAUSE)
    public void onPause() {
        mChangeListener.stop();
        mDataEnabledListener.stop();
        mConnectivityListener.stop();
        mSignalStrengthListener.pause();
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreferenceScreen = screen;
        mPreferenceScreen.findPreference(KEY_ADD_MORE).setVisible(
                MobileNetworkUtils.showEuiccSettings(mContext));
        update();
    }

    private void update() {
        if (mPreferenceScreen == null) {
            return;
        }

        // Since we may already have created some preferences previously, we first grab the list of
        // those, then go through the current available subscriptions making sure they are all
        // present in the screen, and finally remove any now-outdated ones.
        final Map<Integer, Preference> existingPreferences = mPreferences;
        mPreferences = new ArrayMap<>();

        final List<SubscriptionInfo> subscriptions = SubscriptionUtil.getAvailableSubscriptions(
                mContext);

        final Set<Integer> activeSubIds = new ArraySet<>();
        final int dataDefaultSubId = SubscriptionManager.getDefaultDataSubscriptionId();

        for (SubscriptionInfo info : subscriptions) {
            final int subId = info.getSubscriptionId();
            Preference pref = existingPreferences.remove(subId);
            if (pref == null) {
                pref = new Preference(mPreferenceScreen.getContext());
                mPreferenceScreen.addPreference(pref);
            }
            pref.setTitle(info.getDisplayName());
            final boolean isDefaultForData = (subId == dataDefaultSubId);

            if (info.isEmbedded()) {
                if (mSubscriptionManager.isActiveSubscriptionId(subId)) {
                    activeSubIds.add(subId);
                    pref.setSummary(getSummary(subId, R.string.mobile_network_active_esim, isDefaultForData));
                } else {
                    pref.setSummary(R.string.mobile_network_inactive_esim);
                }
            } else {
                if (mSubscriptionManager.isActiveSubscriptionId(subId)) {
                    activeSubIds.add(subId);
                    pref.setSummary(getSummary(subId, R.string.mobile_network_active_sim, isDefaultForData));
                } else if (SubscriptionUtil.showToggleForPhysicalSim(mSubscriptionManager)) {
                    pref.setSummary(mContext.getString(R.string.mobile_network_inactive_sim));
                } else {
                    pref.setSummary(mContext.getString(R.string.mobile_network_tap_to_activate,
                            SubscriptionUtil.getDisplayName(info)));
                }
            }

            setIcon(pref, subId, isDefaultForData);

            pref.setOnPreferenceClickListener(clickedPref -> {
                if (!info.isEmbedded() && !mSubscriptionManager.isActiveSubscriptionId(subId)
                        && !SubscriptionUtil.showToggleForPhysicalSim(mSubscriptionManager)) {
                    mSubscriptionManager.setSubscriptionEnabled(subId, true);
                } else {
                    final Intent intent = new Intent(mContext, MobileNetworkActivity.class);
                    intent.putExtra(Settings.EXTRA_SUB_ID, info.getSubscriptionId());
                    mContext.startActivity(intent);
                }
                return true;
            });
            mPreferences.put(subId, pref);
        }
        mSignalStrengthListener.updateSubscriptionIds(activeSubIds);
        for (Preference pref : existingPreferences.values()) {
            mPreferenceScreen.removePreference(pref);
        }
    }

    boolean shouldInflateSignalStrength(int subId) {
        return SignalStrengthUtil.shouldInflateSignalStrength(mContext, subId);
    }

    void setIcon(Preference pref, int subId, boolean isDefaultForData) {
        final TelephonyManager mgr = mContext.getSystemService(
                TelephonyManager.class).createForSubscriptionId(subId);
        final SignalStrength strength = mgr.getSignalStrength();
        int level = (strength == null) ? 0 : strength.getLevel();
        int numLevels = SignalStrength.NUM_SIGNAL_STRENGTH_BINS;
        if (shouldInflateSignalStrength(subId)) {
            level += 1;
            numLevels += 1;
        }
        final boolean showCutOut = !isDefaultForData || !mgr.isDataEnabled();
        pref.setIcon(getIcon(level, numLevels, showCutOut));
    }

    Drawable getIcon(int level, int numLevels, boolean cutOut) {
        return MobileNetworkUtils.getSignalStrengthIcon(mContext, level, numLevels,
                NO_CELL_DATA_TYPE_ICON, cutOut);
    }

    private boolean activeNetworkIsCellular() {
        final Network activeNetwork = mConnectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }
        final NetworkCapabilities networkCapabilities = mConnectivityManager.getNetworkCapabilities(
                activeNetwork);
        if (networkCapabilities == null) {
            return false;
        }
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }

    /**
     * The summary can have either 1 or 2 lines depending on which services (calls, SMS, data) this
     * subscription is the default for.
     *
     * If this subscription is the default for calls and/or SMS, we add a line to show that.
     *
     * If this subscription is the default for data, we add a line with detail about
     * whether the data connection is active.
     *
     * If a subscription isn't the default for anything, we just say it is available.
     */
    protected String getSummary(int subId, int resId, boolean isDefaultForData) {
        final int callsDefaultSubId = SubscriptionManager.getDefaultVoiceSubscriptionId();
        final int smsDefaultSubId = SubscriptionManager.getDefaultSmsSubscriptionId();

        String line0 = mContext.getString(resId);

        String line1 = null;
        if (subId == callsDefaultSubId && subId == smsDefaultSubId) {
            line1 = mContext.getString(R.string.default_for_calls_and_sms);
        } else if (subId == callsDefaultSubId) {
            line1 = mContext.getString(R.string.default_for_calls);
        } else if (subId == smsDefaultSubId) {
            line1 = mContext.getString(R.string.default_for_sms);
        }

        String line2 = null;
        if (isDefaultForData) {
            final TelephonyManager telMgrForSub = mContext.getSystemService(
                    TelephonyManager.class).createForSubscriptionId(subId);
            final boolean dataEnabled = telMgrForSub.isDataEnabled();
            if (dataEnabled && activeNetworkIsCellular()) {
                line2 = mContext.getString(R.string.mobile_data_active);
            } else if (!dataEnabled) {
                line2 = mContext.getString(R.string.mobile_data_off);
            } else {
                line2 = mContext.getString(R.string.default_for_mobile_data);
            }
        }

        if (line1 != null && line2 != null) {
            return String.join(System.lineSeparator(), line0, line1, line2);
        } else if (line1 != null) {
            return String.join(System.lineSeparator(), line0, line1);
        } else if (line2 != null) {
            return String.join(System.lineSeparator(), line0, line2);
        } else {
            return line0;
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return null;
    }

    @Override
    public void onAirplaneModeChanged(boolean airplaneModeEnabled) {
        update();
    }

    @Override
    public void onMobileDataEnabledChange() {
        update();
    }

    @Override
    public void onDataConnectivityChange() {
        update();
    }

    @Override
    public void onSignalStrengthChanged() {
        update();
    }

    @Override
    public void onSubscriptionsChanged() {
        // See if we need to change which sub id we're using to listen for enabled/disabled changes.
        int defaultDataSubId = SubscriptionManager.getDefaultDataSubscriptionId();
        if (defaultDataSubId != mDataEnabledListener.getSubId()) {
            mDataEnabledListener.stop();
            mDataEnabledListener.start(defaultDataSubId);
        }
        update();
    }
}
