package io.halan.ui;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.HashMap;

import io.halan.R;

/**
 * Created on 2015-12-02.
 */
public class DeviceTemperatureHumidityMeter extends DeviceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_temperature_humidity_meter);
        setup();
    }

    @Override
    public void setup() {
        super.setup();
        try {
            String temperature = new DecimalFormat("##.#").format(getDevice().getJsonObj().getDouble("temperatureCelsius"));
            String humidity = new DecimalFormat("##.#").format(getDevice().getJsonObj().getDouble("humidity"));
            ((TextView) findViewById(R.id.temperature)).setText("Temperature \n" + temperature + "ºC");
            ((TextView) findViewById(R.id.humidity)).setText("Humidity \n" + humidity + "%");
            ((VerticalSeekBar) findViewById(R.id.temperature_bar)).setProgress(getDevice().getJsonObj().getInt("temperatureCelsius"));
            ((VerticalSeekBar) findViewById(R.id.humidity_bar)).setProgress(getDevice().getJsonObj().getInt("humidity"));

            ((VerticalSeekBar) findViewById(R.id.temperature_bar)).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            ((VerticalSeekBar) findViewById(R.id.humidity_bar)).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        setup();
    }

    public void onRefresh(View view) {
        status = 1;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        getDevice().send(this, map);
    }
}
