package io.halan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private SwipeRefreshLayout mSwipeRefresh;
    private DeviceAdapter adapter;
    private CircularProgressView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new DeviceAdapter();

        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        progress = (CircularProgressView) findViewById(R.id.progress_view);
        if (getWifiName(this).contains("dd")) {
            refreshDevice();
        } else {
            connect();
            registerWifiReceiver();
        }
    }

    private void registerWifiReceiver() {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                String msg = null;
                switch (state) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        msg = "Wifi is disabled";
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        msg = "Wifi is enabled, Connecting to 'dd' AP"; // need delay
                        progress.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Cons.log("oiqwdj 222 " + getWifiName(MainActivity.this));
                                if (getWifiName(MainActivity.this).contains("dd")) {
                                    Cons.log("oiqwdj 333");
                                    refreshDevice();
                                } else {
                                    Cons.log("oiqwdj 444");
                                    progress.setVisibility(View.GONE);
                                }
                            }
                        }, 5000);
                        break;
                }
                if (msg != null) {
//                    Cons.log("************%%%%%%%%wifi , " + msg);
//                    Toast.makeText(context, "Wifi, " + msg, Toast.LENGTH_LONG)
//                            .show();
                }
            }
        }, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
    }

    public String getWifiName(Context context) {
        String ssid = "none";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        if (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED) {
//        }
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            ssid = wifiInfo.getSSID();
        }
        Cons.log("ssdid " + ssid);
        return ssid;
    }

    private void connect() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = saveWPAConfig("dddddddd", "dd");
        wifi.setWifiEnabled(true);
        wifi.saveConfiguration();
        int added = wifi.addNetwork(wc);
        System.out.println("added network: " + added);
        boolean enabled = wifi.enableNetwork(added, true);
        System.out.println("enableNetwork returned: " + enabled);
    }

    @Override
    protected void onDestroy() {
        if (getWifiName(this).contains("dd")) {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifi.disconnect();
        }
        super.onDestroy();
    }

    private WifiConfiguration saveWPAConfig(String password, String networkSSID) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.preSharedKey = "\"" + password + "\"";
        conf.status = WifiConfiguration.Status.ENABLED;
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        return conf;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_favorite:
                if (progress.getVisibility() == View.GONE) {
                    refreshDevice();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void refreshDevice() {
        progress.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        DeviceManager.getInstance().findDevice(this);
//        handler.sendEmptyMessage(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                progress.setProgress(0);
            }
        }, 5000);
    }

    public void update() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Device item = adapter.getItem(i);
        item.startActivity(this);
    }

    public class DeviceAdapter extends BaseAdapter {

        private ArrayList<Device> deviceList = new ArrayList<Device>();

        @Override
        public void notifyDataSetChanged() {
            deviceList = DeviceManager.getInstance().getDeviceList();
//            if (deviceList.size() > 0) {
//                findViewById(R.id.progress_view).setVisibility(View.GONE);
//            }
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public Device getItem(int i) {
            return deviceList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_device, null);
            }

            Device item = getItem(i);
            TextView tv;
            tv = (TextView) convertView.findViewById(R.id.title);
            if (getItem(i).getName() == null) {
                tv.setText("Connecting...");
                tv = (TextView) convertView.findViewById(R.id.description);
                tv.setText(getItem(i).getAddress());
            } else {
                tv.setText(item.getName());
                tv = (TextView) convertView.findViewById(R.id.description);
                tv.setText(getItem(i).getAddress());
                ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(item.getIconResId());
            }
            return convertView;
        }
    }
}
