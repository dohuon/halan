package io.halan;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
        setTitle(device.getName());
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
    void update() {
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
