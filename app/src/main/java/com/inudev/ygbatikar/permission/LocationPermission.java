package com.inudev.ygbatikar.permission;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.Objects;

public class LocationPermission {
    private Context mContext;

    public LocationPermission(Context context) {
        this.mContext = context;
    }

    public boolean isCompassAvailable() {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        return Objects.requireNonNull(sensorManager).getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0;
    }
}
