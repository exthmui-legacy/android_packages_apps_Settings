package org.exthmui.settings.deviceinfo.hardwareinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.UserHandle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import androidx.preference.PreferenceScreen;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.R;
import com.android.settings.deviceinfo.HardwareInfoPreferenceController;
import com.android.settings.deviceinfo.StorageCategoryFragment;
import com.android.settings.deviceinfo.hardwareinfo.SocModelPreferenceController;

import org.exthmui.settings.preference.ExthmHardwareInfoPreference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExthmHardwareInfoPreferenceController extends BasePreferenceController {

    private final String KEY_HARDWARE_INFO = "exthm_hardware_info";

    public ExthmHardwareInfoPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        List<ExthmHardwareInfoPreference.HardwareInfo> hardwareInfoList = new ArrayList<>();
        ExthmHardwareInfoPreference preference = screen.findPreference(KEY_HARDWARE_INFO);
        if (preference != null) {
            ExthmHardwareInfoPreference.HardwareInfo[] hardwareInfos = {
                    new ExthmHardwareInfoPreference.HardwareInfo(R.drawable.exthm_settings_ic_hardware_info_phone,
                            mContext.getString(R.string.model_info), HardwareInfoPreferenceController.getDeviceModel()),
                    new ExthmHardwareInfoPreference.HardwareInfo(R.drawable.exthm_settings_ic_hardware_info_storage,
                            mContext.getString(R.string.exthm_settings_my_device_storage_title), mContext.getString(R.string.exthm_settings_my_device_storage_regex, getMemoryStatus()[0], getMemoryStatus()[1])),
                    new ExthmHardwareInfoPreference.HardwareInfo(R.drawable.exthm_settings_ic_hardware_info_processor,
                            mContext.getString(R.string.exthm_settings_my_device_processor_title), getProcessorInfo()),
                    new ExthmHardwareInfoPreference.HardwareInfo(R.drawable.exthm_settings_ic_hardware_info_memory,
                            mContext.getString(R.string.exthm_settings_my_device_ram_title), getRamInfo())
            };
            hardwareInfoList.addAll(Arrays.asList(hardwareInfos));
        }
        preference.setData(hardwareInfoList);
    }

    private String getProcessorInfo() {
        String SOC_MANUFACTURER = null;
        if (Build.SOC_MANUFACTURER.equals("Qualcomm")) {
            SOC_MANUFACTURER = mContext.getString(R.string.soc_model_qualcomm);
        } else if (Build.SOC_MANUFACTURER.equals("Mediatek")) {
            SOC_MANUFACTURER = mContext.getString(R.string.soc_model_mediatek);
        }
        return SOC_MANUFACTURER + " " + Build.SOC_MODEL;
    }

    private String getRamInfo() {
        ActivityManager actManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totRam = memInfo.totalMem;
        long kb = totRam / 1024;
        long mb = totRam / 1048576;
        long gb = totRam / 1073741824;
        if (gb > 1) {
            if (totRam % 1073741824 > 0) {
                return String.valueOf((int) gb + 1).concat(" GB");
            }
        } else if (mb > 1) {
            if (totRam % 1048576 > 0) {
                return String.valueOf((int) mb + 1).concat(" MB");
            }
        } else {
            if (totRam % 1024 > 0) {
                return String.valueOf((int) kb + 1).concat(" KB");
            }
        }
        return mContext.getString(R.string.unknown);
    }

    private String[] getMemoryStatus() {
        String externalStorageState = Environment.getExternalStorageState();
        String[] romInfo = new String[2];
        if (externalStorageState.equals("mounted")) {
            try {
                StatFs statFs = new StatFs(new Environment.UserEnvironment(UserHandle.myUserId()).getExternalStorageDirectory().getPath());
                long totalSize = (long) Math.pow(2, ((int) (Math.log((double) statFs.getTotalBytes()) / Math.log(2))) + 1);
                long usedSize = statFs.getTotalBytes() - statFs.getAvailableBytes();
                String totalSizeStr = Formatter.formatFileSize(mContext, totalSize, Formatter.FLAG_IEC_UNITS);
                String usedSizeStr = Formatter.formatFileSize(mContext, usedSize, Formatter.FLAG_IEC_UNITS);
                romInfo[0] = usedSizeStr;
                romInfo[1] = totalSizeStr;
            } catch (IllegalArgumentException ignored) {
            }
        } else {
            romInfo[0] = "Sdcard unavailable";
            romInfo[1] = "";
        }
        return romInfo;
    }

}
