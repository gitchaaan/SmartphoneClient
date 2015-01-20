package jp.dip.gitchaaan.smartphoneclient.app;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Owner on 2015/01/19.
 */
public class GpsService extends Service {
    private LocationManager lm;
    private PendingIntent pi;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("INFO", "onCreate");

        lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);

        Intent nextIntent = new Intent(this, ReceiveLocation.class);
        pi = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        lm.requestLocationUpdates(1000, 1, criteria, pi);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //Intent処理
        Log.i("INFO", "サービス開始");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class ReceiveLocation extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)){
                LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                String message = "Location\n"
                        + "Longitude：" + location.getLongitude()
                        + "\n"
                        + "Latitude：" + location.getLatitude();
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}