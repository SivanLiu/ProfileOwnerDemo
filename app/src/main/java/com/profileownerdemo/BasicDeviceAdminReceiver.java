/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.profileownerdemo;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Handles events related to managed profile.
 */
@RequiresApi(api = Build.VERSION_CODES.FROYO)
public class BasicDeviceAdminReceiver extends DeviceAdminReceiver {

    /**
     * Called on the new profile when managed profile provisioning has completed. Managed profile
     * provisioning is the process of setting up the device so that it has a separate profile which
     * is managed by the mobile device management(mdm) application that triggered the provisioning.
     * Note that the managed profile is not fully visible until it is enabled.
     */
    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        // EnableProfileActivity is launched with the newly set up profile.
        Intent launch = new Intent(context, SetProfileOwner.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);
        enableProfile(context);
    }

    /**
     * Generates a {@link ComponentName} that is used throughout the app.
     *
     * @return a {@link ComponentName}
     */
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), BasicDeviceAdminReceiver.class);
    }

    private void enableProfile(Context context) {
        DevicePolicyManager manager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = BasicDeviceAdminReceiver.getComponentName(context);
        // This is the name for the newly created managed profile.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.setProfileName(componentName, context.getString(R.string.profile_name));
        }

        // We enable the profile here.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.setProfileEnabled(componentName);
        }

    }
}
