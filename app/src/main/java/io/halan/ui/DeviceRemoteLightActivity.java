package io.halan.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import org.json.JSONException;

import java.util.HashMap;

import io.halan.R;
import io.halan.ui.DeviceActivity;

/**
 * Created on 2015-11-15.
 */
public class DeviceRemoteLightActivity extends DeviceActivity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar seek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_light_ir_remote);
        setup();
    }

    public void setup() {
        super.setup();
        seek = (SeekBar) findViewById(R.id.seek);
        seek.setMax(4);
        try {
            seek.setProgress(getDevice().getJsonObj().getInt("brightness"));
            seek.setOnSeekBarChangeListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onSceneChanged(View view) {
        switch (view.getId()) {
            case R.id.scene_1:
                sendStatus(0);
                break;
            case R.id.scene_2:
                sendStatus(1);
                break;
            case R.id.scene_3:
                sendStatus(2);
                break;
            case R.id.scene_4:
                sendStatus(3);
                break;
            case R.id.scene_5:
                sendStatus(4);
                break;
            case R.id.scene_6:
                sendStatus(5);
                break;
        }

    }

    private void sendStatus(int status) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        getDevice().send(this, map);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        sendStatus(seekBar.getProgress() + 2);
    }
}
