/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.bluetooth;

import android.app.settings.SettingsEnums;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.deletionhelper.ActivationWarningFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.MasterSwitchController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.SummaryUpdater;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.Utils;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

import java.util.concurrent.FutureTask;

public class BluetoothMasterSwitchPreferenceController extends
        BasePreferenceController implements LifecycleObserver, OnStop, OnPause, OnResume,
        SummaryUpdater.OnSummaryChangeListener {
    @VisibleForTesting

    private static final String TAG = "BluetoothMasterSwitchPreferenceController";

    private MasterSwitchPreference mSwitch;
    private MasterSwitchController mSwitchController;
    private BluetoothEnabler mBluetoothEnabler;
    private FragmentManager mFragmentManager;
    private BluetoothSummaryUpdater mSummaryUpdater;
    private LocalBluetoothManager mLocalBluetoothManager;

    private final RestrictionUtils mRestrictionUtils = new RestrictionUtils();

    public BluetoothMasterSwitchPreferenceController(Context context, String key) {
        super(context, key);
        mLocalBluetoothManager = getLocalBluetoothManager();
        mSummaryUpdater = new BluetoothSummaryUpdater(context, this, mLocalBluetoothManager);
    }

    public BluetoothMasterSwitchPreferenceController setFragmentManager(
            FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        return this;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        if (isAvailable()) {
            mSwitch = screen.findPreference(getPreferenceKey());
            mSwitchController = new MasterSwitchController(mSwitch);
            mBluetoothEnabler = new BluetoothEnabler(mContext,
                mSwitchController,
                FeatureFactory.getFactory(mContext).getMetricsFeatureProvider(),
                SettingsEnums.ACTION_SETTINGS_MASTER_SWITCH_BLUETOOTH_TOGGLE,
                mRestrictionUtils);
            
        }
    }

    private LocalBluetoothManager getLocalBluetoothManager() {
        final FutureTask<LocalBluetoothManager> localBtManagerFutureTask = new FutureTask<>(
                // Avoid StrictMode ThreadPolicy violation
                () -> com.android.settings.bluetooth.Utils.getLocalBtManager(mContext));
        try {
            localBtManagerFutureTask.run();
            return localBtManagerFutureTask.get();
        } catch (Exception e) {
            Log.w(TAG, "Error getting LocalBluetoothManager.", e);
            return null;
        }
    }

    @Override
    public int getAvailabilityStatus() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
                ? AVAILABLE
                : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void onResume() {
        mSummaryUpdater.register(true);
        if (mBluetoothEnabler != null) mBluetoothEnabler.resume(mContext);
    }

    @Override
    public void onPause() {
        if (mBluetoothEnabler != null) mBluetoothEnabler.pause();
        mSummaryUpdater.register(false);
    }

    @Override
    public void onStop() {
        if (mBluetoothEnabler != null) mBluetoothEnabler.teardownSwitchController();
    }

    @Override
    public void onSummaryChanged(String summary) {
        if (mSwitch != null) {
            mSwitch.setSummary(summary);
        }
    }

}