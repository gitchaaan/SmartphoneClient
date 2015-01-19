package jp.dip.gitchaaan.smartphoneclient.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Owner on 2015/01/19.
 */
public class ReceiveWifi extends BroadcastReceiver {
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
