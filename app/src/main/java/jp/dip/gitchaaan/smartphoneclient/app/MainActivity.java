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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.widget.CompoundButton.*;


public class MainActivity extends ActionBarActivity implements OnCheckedChangeListener {

    private static AccService mAccService;
    private static GpsService mGpsService;
    private static WifiService mWifiService;
    private static final int DB_VERSION = 1;
    private static SQLiteDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch sw_gps = (Switch) findViewById(R.id.switch_gps);
        Switch sw_wifi = (Switch) findViewById(R.id.switch_wifi);
        Switch sw_acc = (Switch) findViewById(R.id.switch_acc);

        sw_gps.setOnCheckedChangeListener(this);
        sw_wifi.setOnCheckedChangeListener(this);
        sw_acc.setOnCheckedChangeListener(this);

        sw_gps.setEnabled(false);
        sw_wifi.setEnabled(false);
        sw_acc.setEnabled(false);

        /*
        Bindインテント
         */
        Intent accIntent = new Intent(getApplicationContext(), AccService.class);
        bindService(accIntent, mAccServiceConnection, Context.BIND_AUTO_CREATE);

        Intent gpsIntent = new Intent(getApplicationContext(), GpsService.class);
        bindService(gpsIntent, mGpsServiceConnection, Context.BIND_AUTO_CREATE);

        Intent wifiIntent = new Intent(getApplicationContext(), WifiService.class);
        bindService(wifiIntent, mWifiServiceConnection, Context.BIND_AUTO_CREATE);

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
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, sender);
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

    /*
    トグルスイッチ設定
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.switch_gps:
                if(isChecked) {
                    Toast.makeText(this, "gps on", Toast.LENGTH_SHORT).show();
                    //startService(new Intent(MainActivity.this, GpsService.class));
                } else {
                    Toast.makeText(this, "gps off", Toast.LENGTH_SHORT).show();
                    //startService(new Intent(MainActivity.this, GpsService.class));
                }
                break;
            case R.id.switch_wifi:
                if(isChecked) {
                    Toast.makeText(this, "wifi on", Toast.LENGTH_SHORT).show();
                    //startService(new Intent(MainActivity.this, WifiService.class));
                }
                else {
                    Toast.makeText(this, "wifi off", Toast.LENGTH_SHORT).show();
                    //stopService(new Intent(MainActivity.this, WifiService.class));
                }
                break;
            case R.id.switch_acc:
                if(isChecked) {
                    Toast.makeText(this, "acc on", Toast.LENGTH_SHORT).show();
                    //startService(new Intent(MainActivity.this, AccService.class));
                } else {
                    Toast.makeText(this, "acc off", Toast.LENGTH_SHORT).show();
                    //stopService(new Intent(MainActivity.this, AccService.class));
                }
                break;
        }
    }

    /*
    タイマー処理用インナークラス
     */
    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Alarm通知", Toast.LENGTH_SHORT).show();

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("#yyy/MM/dd_HH:mm.ss_SSS");
            String time = simpleDateFormat.format(date);

            List<ScanResult> list = mWifiService.getScanResult();
            for(ScanResult sc:list) {
                ContentValues values = new ContentValues();
                values.put("bssid", sc.BSSID);
                values.put("time", time);
                if((mydb.insert("wifi_list", null, values)) != -1) {
                    Log.i("MainActivity", "Insert成功");
                } else {
                    Log.i("MainActivity", "Insert失敗");
                }
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
                    "time text);");
            db.execSQL("create table wifi_list " +
                    "(_id integer primary key autoincrement," +
                    "bssid text, " +
                    "time text);");
            db.execSQL("create table acc_list " +
                    "(_id integer primary key autoincrement," +
                    "x_axis real," +
                    "y_axis real," +
                    "z_axis real," +
                    "time text);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table gps_list;");
            db.execSQL("drop table wifi_list;");
            db.execSQL("drop table acc_list;");
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
