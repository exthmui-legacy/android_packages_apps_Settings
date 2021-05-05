/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.settings.homepage;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.core.FeatureFlags;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.development.featureflags.FeatureFlagPersistent;
import com.android.settings.network.MobileNetworkPreferenceController;
import com.android.settings.network.MobileNetworkSummaryController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.support.SupportPreferenceController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.wifi.WifiMasterSwitchPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.drawer.ProviderTile;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.settings.search.actionbar.SearchMenuController.NEED_SEARCH_ICON_IN_ACTION_BAR;
import static com.android.settingslib.search.SearchIndexable.MOBILE;

import org.exthmui.settings.widget.HomepagePreference;
import org.exthmui.settings.widget.exTHmMasterSwitchPreference;

@SearchIndexable(forTarget = MOBILE)
public class TopLevelSettings extends DashboardFragment implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TAG = "TopLevelSettings";

    public TopLevelSettings() {
        final Bundle args = new Bundle();
        // Disable the search icon because this page uses a full search view in actionbar.
        args.putBoolean(NEED_SEARCH_ICON_IN_ACTION_BAR, false);
        setArguments(args);
        setInHomePage(true);
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.top_level_settings;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DASHBOARD_SUMMARY;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        use(SupportPreferenceController.class).setActivity(getActivity());
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), mMetricsFeatureProvider,
                this /* fragment */ /* mobilePlanHost */);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context,
                   Lifecycle lifecycle, MetricsFeatureProvider metricsFeatureProvider, Fragment fragment) {
        final WifiMasterSwitchPreferenceController wifiPreferenceController =
                new WifiMasterSwitchPreferenceController(context, metricsFeatureProvider);

        if (lifecycle != null) {
            lifecycle.addObserver(wifiPreferenceController);
        }

        final List<AbstractPreferenceController> controllers = new ArrayList<>();

        controllers.add(new MobileNetworkSummaryController(context, lifecycle));
        controllers.add(wifiPreferenceController);
        return controllers;
    }

    @Override
    public int getHelpResource() {
        // Disable the help icon because this page uses a full search view in actionbar.
        return 0;
    }

    @Override
    public Fragment getCallbackFragment() {
        return this;
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        new SubSettingLauncher(getActivity())
                .setDestination(pref.getFragment())
                .setArguments(pref.getExtras())
                .setSourceMetricsCategory(caller instanceof Instrumentable
                        ? ((Instrumentable) caller).getMetricsCategory()
                        : Instrumentable.METRICS_CATEGORY_UNKNOWN)
                .setTitleRes(-1)
                .launch();
        return true;
    }

    @Override
    public Preference createPreference(Tile tile) {
        return tile instanceof ProviderTile
                ? new SwitchPreference(getPrefContext())
                : tile.hasSwitch()
                ? new exTHmMasterSwitchPreference(getPrefContext())
                : new HomepagePreference(getPrefContext());
    }

    @Override
    protected boolean shouldForceRoundedIcon() {
        return getContext().getResources()
                .getBoolean(R.bool.config_force_rounded_icon_TopLevelSettings);
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.top_level_settings) {

                @Override
                protected boolean isPageSearchEnabled(Context context) {
                    // Never searchable, all entries in this page are already indexed elsewhere.
                    return false;
                }
            };
}
