package jp.dip.gitchaaan.smartphoneclient.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;


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

    public static class ReceiveWifi extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> list = wm.getScanResults();

                String message = "Wi-Fi State\n";

                for(ScanResult sc:list) {
                    message += "BSSID：" + sc.BSSID + "\n";
                }

                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }
    }


}