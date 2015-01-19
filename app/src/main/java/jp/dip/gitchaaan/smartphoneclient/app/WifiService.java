package jp.dip.gitchaaan.smartphoneclient.app;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Owner on 2015/01/19.
 */
public class WifiService extends Service {
    WifiManager wm;
    ReceiveWifi receiveWifi;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

        //onPauseで解除
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("INFO", "WifiService is started.");
        return START_STICKY;
    }
}