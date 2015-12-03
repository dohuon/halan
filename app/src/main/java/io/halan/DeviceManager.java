package io.halan;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import io.halan.ui.MainActivity;

/**
 * Created on 2015-11-14.
 */
public class DeviceManager {

    public static int PORT = 8087;
    public static int PORT_SCAN_TIMEOUT = 200;
    public static int scanRange = 30;

    public ArrayList<Device> deviceList = new ArrayList<Device>();

    private static DeviceManager instance;

    public static DeviceManager getInstance() {
        if(instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    public void findDevice(MainActivity mainActivity) {
        deviceList.clear();
        for (int i = 1; i < scanRange; i++) {
            new CheckPort(mainActivity).execute("192.168.0." + i);
        }
    }

    public ArrayList<Device> getDeviceList() {
        return deviceList;
    }

    public Device getDevice(String address) {
        for(Device device : getDeviceList()) {
            if(device.getAddress().equals(address)) {
                return device;
            }
        }
        return null;
    }


    public class CheckPort extends AsyncTask<String, Integer, Boolean> {

        private final MainActivity mainActivity;
        private String address = "";

        public CheckPort(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public Boolean doInBackground(String[] string) {
            this.address = string[0];
            try {
                Socket socket = new Socket();
                SocketAddress address = new InetSocketAddress(this.address, 8087);
                socket.connect(address, PORT_SCAN_TIMEOUT);
                socket.close();
            } catch (UnknownHostException e) {
                return false;
            } catch (SocketTimeoutException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Cons.log("FOUND " + address);
                Device device = new Device(address);
                device.updateStatus(mainActivity, false);
                deviceList.add(device);
                mainActivity.update();
            }
        }
    }
}
