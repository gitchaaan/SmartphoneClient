package jp.dip.gitchaaan.smartphoneclient.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import static android.widget.CompoundButton.*;


public class MainActivity extends ActionBarActivity implements OnCheckedChangeListener {

    private Switch sw_gps, sw_wifi, sw_acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sw_gps = (Switch) findViewById(R.id.switch_gps);
        sw_wifi = (Switch) findViewById(R.id.switch_wifi);
        sw_acc = (Switch) findViewById(R.id.switch_acc);

        sw_gps.setOnCheckedChangeListener(this);
        sw_wifi.setOnCheckedChangeListener(this);
        sw_acc.setOnCheckedChangeListener(this);
    }

    /*
    トグルスイッチ設定
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()) {
            case R.id.switch_gps:
                if(isChecked == true) {
                    Toast.makeText(this, "gps on", Toast.LENGTH_SHORT).show();
                    startService(new Intent(MainActivity.this, GpsService.class));
                } else {
                    Toast.makeText(this, "gps off", Toast.LENGTH_SHORT).show();
                    startService(new Intent(MainActivity.this, GpsService.class));
                }
                break;
            case R.id.switch_wifi:
                if(isChecked == true) {
                    Toast.makeText(this, "wifi on", Toast.LENGTH_SHORT).show();
                    startService(new Intent(MainActivity.this, WifiService.class));
                }
                else {
                    Toast.makeText(this, "wifi off", Toast.LENGTH_SHORT).show();
                    stopService(new Intent(MainActivity.this, WifiService.class));
                }
                break;
            case R.id.switch_acc:
                if(isChecked == true) {
                    Toast.makeText(this, "acc on", Toast.LENGTH_SHORT).show();
                    startService(new Intent(MainActivity.this, AccService.class));
                } else {
                    Toast.makeText(this, "acc off", Toast.LENGTH_SHORT).show();
                    stopService(new Intent(MainActivity.this, AccService.class));
                }
                break;
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
