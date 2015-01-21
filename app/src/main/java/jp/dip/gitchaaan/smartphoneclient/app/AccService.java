package jp.dip.gitchaaan.smartphoneclient.app;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Owner on 2015/01/19.
 */
public class AccService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private final int SENSOR_NAME = Sensor.TYPE_ACCELEROMETER;
    private final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
    private final IBinder mBinder = new LocalBinder();

    private float x=0, y=0, z=0;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        AccService getService() {
            return AccService.this;
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }


    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(SENSOR_NAME);
        sensorManager.registerListener(this, sensor, SENSOR_DELAY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("INFO", "AccService is started.");
        return START_STICKY;
    }

}
