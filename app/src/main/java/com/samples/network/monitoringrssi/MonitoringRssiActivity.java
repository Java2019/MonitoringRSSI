package com.samples.network.monitoringrssi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class MonitoringRssiActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener{

    private TextView text;
    private CheckBox cbEnable;
    private WifiManager manager;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            switch(wifiState){
                case WifiManager.WIFI_STATE_ENABLING:
                    text.setText("Wi-Fi state enabling");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    text.setText("Wi-Fi state enabled");
                    // Запускаем мониторинг уровня сигнала
                    startMonitoringRssi();
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    text.setText("Wi-Fi state disabling");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    text.setText("Wi-Fi state disabled");
                // Останавливаем мониторинг уровня сигнала
                    stopMonitoringRssi();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    text.setText("Wi-Fi state unknown");
                    break;
            }
        }
    };

     private BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiInfo info = manager.getConnectionInfo();
            // Выводим идентификатор сети,
            // уровень сигнала и скорость передачи данных
            text.append("\nChange signal in " + info.getSSID());
            text.append("\n\tSignal level:\t" +
                    WifiManager.calculateSignalLevel(info.getRssi(), 5));
            text.append("\n\tLink speed:\t" + info.getLinkSpeed() +
                    " " + WifiInfo.LINK_SPEED_UNITS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_rssi);

        text = (TextView)findViewById(R.id.text);
        cbEnable = (CheckBox)findViewById(R.id.checkbox);
        manager = (WifiManager)getSystemService(WIFI_SERVICE);
        this.registerReceiver(this.receiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        cbEnable.setChecked(manager.isWifiEnabled());
        cbEnable.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        manager.setWifiEnabled(b);
    }

    private void stopMonitoringRssi() {
        this.registerReceiver(rssiReceiver,
                new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    private void startMonitoringRssi() {
        if (this.rssiReceiver.isInitialStickyBroadcast())
            this.unregisterReceiver(rssiReceiver);
    }
}
