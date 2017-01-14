package com.placepickexample.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

public class MyService extends Service  {

    private int mNotificationId = 001;

    LocationManager locationManager;
    LocationListener listener;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(MyService.this, "SERVICE RUNNING", Toast.LENGTH_SHORT).show();

        return Service.START_STICKY;
    }



}
