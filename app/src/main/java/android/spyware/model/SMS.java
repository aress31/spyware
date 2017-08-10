package android.spyware.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

public class SMS {
    private String       address;
    private String       body;
    private Date         date;
    private List<String> email_addresses;
    private long         id;
    private long         thread_id;
    private int          type;

    public SMS(String address, String body, Date date, List<String> email_addresses, long id, long thread_id, int type) {
        this.address = address;
        this.body = body;
        this.date = date;
        this.email_addresses = email_addresses;
        this.id = id;
        this.thread_id = thread_id;
        this.type = type;
    }

    public JSONObject toJSON() {
        JSONObject JSONSMS = new JSONObject();

        try {
            JSONSMS.put("address", this.address);
            JSONSMS.put("body", this.body);
            JSONSMS.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(this.date));
            JSONSMS.put("email_addresses", new JSONArray(this.email_addresses));
            JSONSMS.put("id", this.id);
            JSONSMS.put("thread_id", this.thread_id);
            JSONSMS.put("type", this.type);
        } catch (JSONException ex) {
            Log.wtf(TAG, SMS.class.getName() + "->toJSON: " + Log.getStackTraceString(ex));
        }

        return JSONSMS;
    }
}