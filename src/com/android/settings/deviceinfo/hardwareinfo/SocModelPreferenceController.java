/*
 * Copyright (C) 2019 ArrowOS
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

package com.android.settings.deviceinfo.hardwareinfo;

import android.content.Context;
import android.os.Build;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class SocModelPreferenceController extends BasePreferenceController {

    private static final String TAG = "SocModelDeteceCtrl";

    String SOC_MANUFACTURER;

    public SocModelPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        if(Build.SOC_MANUFACTURER.equals("Qualcomm")){
            SOC_MANUFACTURER=mContext.getString(R.string.soc_model_qualcomm);
        }else if(Build.SOC_MANUFACTURER.equals("Mediatek")){
            SOC_MANUFACTURER=mContext.getString(R.string.soc_model_mediatek);
        }
        return SOC_MANUFACTURER+" "+Build.SOC_MODEL;
    }
}