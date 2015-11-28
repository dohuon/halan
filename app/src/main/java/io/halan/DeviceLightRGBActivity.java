package io.halan;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;

import java.util.HashMap;

import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

/**
 * Created on 2015-11-15.
 */
public class DeviceLightRGBActivity extends DeviceActivity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar seek1;
    private SeekBar seek2;
    private SeekBar seek3;
    private LineColorPicker colorPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_light_rgb);
        setup();
    }

    public void setup() {
        super.setup();
        seek1 = (SeekBar) findViewById(R.id.seek1);
        seek2 = (SeekBar) findViewById(R.id.seek2);
        seek3 = ((SeekBar) findViewById(R.id.seek3));

        try {
            seek1.setProgress(getDevice().getJsonObj().getInt("brightness1"));
            seek2.setProgress(getDevice().getJsonObj().getInt("brightness2"));
            seek3.setProgress(getDevice().getJsonObj().getInt("brightness3"));
            ((TextView) findViewById(R.id.light1)).setText(getDevice().getJsonObj().getString("light1"));
            ((TextView) findViewById(R.id.light2)).setText(getDevice().getJsonObj().getString("light2"));
            ((TextView) findViewById(R.id.light3)).setText(getDevice().getJsonObj().getString("light3"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        seek1.setOnSeekBarChangeListener(this);
        seek2.setOnSeekBarChangeListener(this);
        seek3.setOnSeekBarChangeListener(this);

        colorPicker = (LineColorPicker) findViewById(R.id.picker);
        colorPicker.setSelectedColor(Color.RED);
        colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int c) {
                Cons.log("newcoclor " + Integer.toHexString(c));
                seek1.setProgress(Color.red(c));
                seek2.setProgress(Color.green(c));
                seek3.setProgress(Color.blue(c));
                sendBrightness(1);
            }
        });


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        sendBrightness(1);
    }

    private void sendBrightness(int status) {

        int brightness = seek1.getProgress() + seek2.getProgress() + seek3.getProgress();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("brightness1", seek1.getProgress());
        map.put("brightness2", seek2.getProgress());
        map.put("brightness3", seek3.getProgress());
        map.put("status", status);
        getDevice().send(this, map);
    }

    public void onSceneChanged(View view) {
        int status = 1;
        switch (view.getId()) {
            case R.id.scene_1:
                status = 0;
                break;
            case R.id.scene_2:
                status = 1;
                break;
        }
        sendBrightness(status);
    }
}
