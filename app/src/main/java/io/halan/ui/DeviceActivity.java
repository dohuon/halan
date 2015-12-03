package io.halan.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;

import io.halan.Cons;
import io.halan.Device;
import io.halan.DeviceManager;
import io.halan.R;

/**
 * Created on 2015-11-22.
 */
public class DeviceActivity extends BaseActivity {

    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String address = getIntent().getStringExtra("address");
        Cons.log("DeviceLightActivity address " + address);
        device = DeviceManager.getInstance().getDevice(address);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (device == null) {
            finish();
            return;
        }
        setTitle(device.getName());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    int status = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            Cons.log("onKeyUp1 " + keyCode);
//
//            int status = 0;
//            try {
//                status = device.getJsonObj().getInt("status");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            status = status == 0 ? 1 : 0;
            map.put("status", status);
            device.send(this, map);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public Device getDevice() {
        return device;
    }

    public void setup() {
        showJson();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void update() {
        showJson();
    }

    private void showJson() {

        JsonParser parser = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement el = parser.parse(device.getJsonObj().toString());
        String jsonString = gson.toJson(el); // done
        ((TextView) findViewById(R.id.json)).setText(jsonString);
    }
}
