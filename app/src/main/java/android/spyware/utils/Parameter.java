package android.spyware.utils;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class Parameter {
    private String     action;
    private JSONArray  JSONArray;
    private JSONObject JSONObject;

    public Parameter(String action, JSONArray JSONArray) {
        this.action = action;
        this.JSONArray = JSONArray;
    }

    public Parameter(String action, JSONObject JSONObject) {
        this.action = action;
        this.JSONObject = JSONObject;
    }

    String getAction() {
        return this.action;
    }

    JSONArray getJArray() {
        return this.JSONArray;
    }

    JSONObject getJObject() {
        return this.JSONObject;
    }

    boolean isJSONArray() {
        return this.JSONArray instanceof JSONArray;
    }
}
