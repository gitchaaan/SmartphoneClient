package jp.dip.gitchaaan.smartphoneclient.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.ScanResult;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.os.Environment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.widget.CompoundButton.*;


public class MainActivity extends ActionBarActivity implements OnCheckedChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static AccService mAccService;
    private static GpsService mGpsService;
    private static WifiService mWifiService;
    private static ActRecService mActRecService;
    private static final int DB_VERSION = 1;
    private static SQLiteDatabase mydb;
    private AlarmManager alarmManager;
    private PendingIntent sender;
    private Calendar calendar;
    private final String TAG = "MainActivity";
    GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Google API接続（行動認識）
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        Switch sw_gps = (Switch) findViewById(R.id.switch_gps);
        Switch sw_wifi = (Switch) findViewById(R.id.switch_wifi);
        Switch sw_acc = (Switch) findViewById(R.id.switch_acc);
        Switch sw_rec = (Switch) findViewById(R.id.switch_rec);

        sw_gps.setOnCheckedChangeListener(this);
        sw_wifi.setOnCheckedChangeListener(this);
        sw_acc.setOnCheckedChangeListener(this);
        sw_rec.setOnCheckedChangeListener(this);

        sw_gps.setEnabled(false);
        sw_wifi.setEnabled(false);
        sw_acc.setEnabled(false);

        Button btn_exp = (Button) findViewById(R.id.btn_export);
        Button btn_map = (Button) findViewById(R.id.btn_map);
        btn_exp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDB();
            }
        });
        btn_map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity", "clicked");
            }
        });

        /*
        Bindインテント
         */
        Intent accIntent = new Intent(getApplicationContext(), AccService.class);
        bindService(accIntent, mAccServiceConnection, Context.BIND_AUTO_CREATE);

        Intent gpsIntent = new Intent(getApplicationContext(), GpsService.class);
        bindService(gpsIntent, mGpsServiceConnection, Context.BIND_AUTO_CREATE);

        Intent wifiIntent = new Intent(getApplicationContext(), WifiService.class);
        bindService(wifiIntent, mWifiServiceConnection, Context.BIND_AUTO_CREATE);

        Intent actRecIntent = new Intent(getApplicationContext(), ActRecService.class);
        bindService(actRecIntent, mActRecServiceConnection, Context.BIND_AUTO_CREATE);

        /*
        DB設定
         */
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getApplicationContext());
        mydb = helper.getWritableDatabase();

        /*
        アラーム設定
         */
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    /*
    DBエクスポート
     */
    private void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" +
                this.getBaseContext().getPackageName() +
                "/databases/" + "activity.db";
        String backupDBPath = "activity.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            Toast.makeText(this, "DB Exported", Toast.LENGTH_SHORT).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Bindコネクション管理
     */
    private ServiceConnection mAccServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AccService.LocalBinder binder = (AccService.LocalBinder) service;
            mAccService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private ServiceConnection mGpsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GpsService.LocalBinder binder = (GpsService.LocalBinder) service;
            mGpsService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private ServiceConnection mWifiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiService.LocalBinder binder = (WifiService.LocalBinder) service;
            mWifiService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private ServiceConnection mActRecServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ActRecService.LocalBinder binder = (ActRecService.LocalBinder) service;
            mActRecService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /*
    トグルスイッチ設定
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.switch_rec:
                if(isChecked) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, sender);
                } else {
                    alarmManager.cancel(sender);
                }
                break;
            case R.id.switch_gps:
                if(isChecked) {
                    //startService(new Intent(MainActivity.this, GpsService.class));
                } else {
                    //startService(new Intent(MainActivity.this, GpsService.class));
                }
                break;
            case R.id.switch_wifi:
                if(isChecked) {
                    //startService(new Intent(MainActivity.this, WifiService.class));
                }
                else {
                    //stopService(new Intent(MainActivity.this, WifiService.class));
                }
                break;
            case R.id.switch_acc:
                if(isChecked) {
                    //startService(new Intent(MainActivity.this, AccService.class));
                } else {
                    //stopService(new Intent(MainActivity.this, AccService.class));
                }
                break;
        }
    }

    /*
    行動認識用GoogleAPI接続メソッド
     */
    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, ActRecService.class);
        PendingIntent mActivityRecognitionPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i(TAG, "onConnected");
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, mActivityRecognitionPendingIntent);
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    /*
    タイマー処理用インナークラス
     */
    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Alarm通知", Toast.LENGTH_SHORT).show();

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("#yyyy/MM/dd_HH:mm.ss_SSS");
            String time = simpleDateFormat.format(date);

            ContentValues values;
            List<ScanResult> list = mWifiService.getScanResult();

            Log.i("MainActivity", mActRecService.getMostProbActName());

            try {
                mydb.beginTransaction();
                for (ScanResult sc : list) {
                    values = new ContentValues();
                    values.put("ssid", sc.SSID);
                    values.put("bssid", sc.BSSID);
                    values.put("level", sc.level);
                    values.put("time", time);
                    mydb.insert("wifi_list", null, values);
                }

                values = new ContentValues();
                values.put("longitude", mGpsService.getLongitude());
                values.put("latitude", mGpsService.getLatitude());
                values.put("speed", mGpsService.getSpeed());
                values.put("accuracy", mGpsService.getAccuracy());
                values.put("time", time);
                mydb.insert("gps_list", null, values);

                values = new ContentValues();
                values.put("x_axis", mAccService.getX());
                values.put("y_axis", mAccService.getY());
                values.put("z_axis", mAccService.getZ());
                values.put("time", time);
                mydb.insert("acc_list", null, values);
                mydb.setTransactionSuccessful();

                values = new ContentValues();
                values.put("activity", mActRecService.getMostProbActName());
                values.put("confidence", mActRecService.getConfidence());
                values.put("time", time);
                mydb.insert("activity_list", null, values);
            } finally {
                mydb.endTransaction();
            }
        }
    }

    /*
    SQLiteOpenHelperクラス
     */
    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
        public MySQLiteOpenHelper(Context context) {
            super(context, "activity.db", null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table gps_list " +
                    "(_id integer primary key autoincrement," +
                    "longitude real," +
                    "latitude real," +
                    "speed real," +
                    "accuracy real," +
                    "time text);");
            db.execSQL("create table wifi_list " +
                    "(_id integer primary key autoincrement," +
                    "ssid text," +
                    "bssid text," +
                    "level integer," +
                    "time text);");
            db.execSQL("create table acc_list " +
                    "(_id integer primary key autoincrement," +
                    "x_axis real," +
                    "y_axis real," +
                    "z_axis real," +
                    "time text);");
            db.execSQL("create table activity_list " +
                    "(_id integer primary key autoincrement," +
                    "activity text," +
                    "confidence real," +
                    "time text);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table gps_list;");
            db.execSQL("drop table wifi_list;");
            db.execSQL("drop table acc_list;");
            db.execSQL("drop table activity_list;");
            onCreate(db);
        }
    }



    /**
     * ここから中断時の設定
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        alarmManager.cancel(sender);

        unbindService(mAccServiceConnection);
        unbindService(mGpsServiceConnection);
        unbindService(mWifiServiceConnection);
        unbindService(mActRecServiceConnection);

        stopService(new Intent(MainActivity.this, WifiService.class));
        stopService(new Intent(MainActivity.this, GpsService.class));
        stopService(new Intent(MainActivity.this, AccService.class));
        stopService(new Intent(MainActivity.this, ActRecService.class));
    }

    /**
     * ここからオプションメニュー設定(自動生成)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}