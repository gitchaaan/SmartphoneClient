package jp.dip.gitchaaan.smartphoneclient.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;


/**
 * Created by Owner on 2015/01/19.
 */
public class WifiService extends Service {
    private WifiManager wm;
    private ReceiveWifi receiveWifi;
    private static List<ScanResult> list;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        WifiService getService() {
            return WifiService.this;
        }
    }

    public List<ScanResult> getScanResult () {
        return list;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WifiManager)getSystemService(WIFI_SERVICE);
        wm.startScan();

        setReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiveWifi);
    }

    //レシーバーをアンドロイドシステムに登録する処理
    private void setReceiver() {
        receiveWifi = new ReceiveWifi();

        // 受信する情報の種類を設定
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        registerReceiver(receiveWifi,  filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("INFO", "WifiService is started.");
        return START_STICKY;
    }

    public static class ReceiveWifi extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                list = wm.getScanResults();
            }
        }
    }
}