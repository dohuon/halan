package io.halan;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Created on 2015-11-15.
 */
public class DeviceLightRGBActivity extends DeviceActivity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar seek1;
    private SeekBar seek2;
    private SeekBar seek3;


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

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        sendBrightness();
    }

    private void sendBrightness() {

        int brightness = seek1.getProgress() + seek2.getProgress() + seek3.getProgress();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("brightness1", seek1.getProgress());
        map.put("brightness2", seek2.getProgress());
        map.put("brightness3", seek3.getProgress());
        map.put("status", brightness == 0 ? 0 : 1);
        getDevice().send(this, map);
    }

    public void onSceneChanged(View view) {
        switch (view.getId()) {
            case R.id.scene_1:
                seek1.setProgress(0);
                seek2.setProgress(0);
                seek3.setProgress(0);
                break;
            case R.id.scene_2:
                seek1.setProgress(255);
                seek2.setProgress(255);
                seek3.setProgress(0);
                break;
            case R.id.scene_3:
                seek1.setProgress(255);
                seek2.setProgress(0);
                seek3.setProgress(255);
                break;
            case R.id.scene_4:
                seek1.setProgress(255);
                seek2.setProgress(255);
                seek3.setProgress(255);
                break;
            case R.id.scene_5:
                seek1.setProgress(0);
                seek2.setProgress(255);
                seek3.setProgress(255);
                break;
            case R.id.scene_6:
                seek1.setProgress(100);
                seek2.setProgress(100);
                seek3.setProgress(100);
                break;
        }
        sendBrightness();
    }
}
