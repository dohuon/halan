package io.halan;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.halan.ui.BaseActivity;
import io.halan.ui.DeviceLightActivity;
import io.halan.ui.DeviceLightRGBActivity;
import io.halan.ui.DeviceRemoteLightActivity;
import io.halan.ui.DeviceTemperatureHumidityMeter;
import io.halan.ui.MainActivity;

/**
 * Created on 2015-11-14.
 */
public class Device {

    private final String address;
    private String name;
    private String type;
    private JSONObject jsonObj;
    private HashMap<String, Object> data;
    int failCount = 0;
    private int iconResId;

    public Device(String address) {
        this.address = address;
    }

    public void updateStatus(final BaseActivity mainActivity, final boolean newUpdate) {

        String url = "http://" + address;
        Cons.log("updateStatus1 " + url);
        String body = "{}";
        if (newUpdate) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");

            for (String key : data.keySet()) {
                Object value = data.get(key);
                Cons.log("key " + key);
                if (value instanceof String) {
                    sb.append("\"" + key + "\":\"");
                    sb.append((String) value);
                    sb.append("\",");
                } else {
                    sb.append("\"" + key + "\":");
                    sb.append((Integer) value);
                    sb.append(",");
                }
            }

            if (jsonObj.length() > 0) sb.deleteCharAt(sb.length() - 1);
            sb.append("}");
            body = sb.toString();
        }

        Cons.log("updateStatus2 " + newUpdate + " " + body);

        AsyncHttpClient client = new AsyncHttpClient(DeviceManager.PORT);
        client.setResponseTimeout(1500);
        StringEntity entity;
        try

        {
            entity = new StringEntity(body);
            client.post(mainActivity, url, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    failCount = 0;
                    Cons.log("onSuccess " + statusCode + " " + response.toString());
                    try {
                        type = response.getString("type");
                        name = response.getString("name");

                        jsonObj = response;

//                        Iterator<String> itr = response.keys();
//                        while (itr.hasNext()) {
//                            String key = itr.next();
//                            Object value = response.get(key);
//                            }
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mainActivity.update();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    failCount++;
                    Cons.log("onFailure " + failCount);
                    if (failCount < 2) {
                        updateStatus(mainActivity, newUpdate);
                    }
                }
            });
        } catch (
                UnsupportedEncodingException e1
                )

        {
            e1.printStackTrace();
        }

    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            updateStatus((BaseActivity) message.obj, true);
            return true;
        }
    });

    long lastSend;

    public void send(BaseActivity activity, HashMap<String, Object> data) {
        this.data = data;
        long sendTime = SystemClock.uptimeMillis() + 50;
        if (sendTime < lastSend + 2000) {
            sendTime = lastSend + 2000;
        }
        Message msg = handler.obtainMessage(0, activity);
        handler.sendMessageAtTime(msg, sendTime);

//        handler.sendEmptyMessageAtTime(0, sendTime);
        lastSend = sendTime;
    }

    public void startActivity(MainActivity mainActivity) {

        if (getType() == null) return;
        Intent intent = null;
        if (getType().equals("light_led_warm_cool")) {
            intent = new Intent(mainActivity, DeviceLightActivity.class);
        }
        if (getType().equals("light_ir_remote")) {
            intent = new Intent(mainActivity, DeviceRemoteLightActivity.class);
        }
        if (getType().equals("light_led_rgb")) {
            intent = new Intent(mainActivity, DeviceLightRGBActivity.class);
        }
        if (getType().equals("temperature_humidity_meter")) {
            intent = new Intent(mainActivity, DeviceTemperatureHumidityMeter.class);
        }

        intent.putExtra("address", getAddress());
        Cons.log("onclick " + getAddress());
        mainActivity.startActivity(intent);
    }

    public String getType() {
        return type;
    }

    public JSONObject getJsonObj() {
        return jsonObj;
    }

    public int getIconResId() {

        if (getType().equals("light_led_warm_cool")) {
            return R.drawable.ic_wb_iridescent;
        }
        if (getType().equals("light_ir_remote")) {
            return R.drawable.ic_settings_remote;
        }
        if (getType().equals("light_led_rgb")) {
            return R.drawable.ic_color_lens;
        }
        if (getType().equals("temperature_humidity_meter")) {
            return R.drawable.ic_insert_chart_black_48dp;
        }
        return iconResId;
    }
}
