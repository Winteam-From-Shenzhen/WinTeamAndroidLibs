package net.yt.serialport;

import android.util.Log;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;


class SerialPortFinder {

    private String TAG = "SerialPortFinder";
    private Vector<Driver> mDrivers = null;

    private Vector<Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new Vector<>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null) {
                // Issue 3:
                // Since driver name may contain spaces, we do not extract driver name with split()
                String drivername = l.substring(0, 0x15).trim();
                String[] w = l.split(" +");
                if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
                    Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length - 4]);
                    mDrivers.add(new Driver(drivername, w[w.length - 4]));
                }
            }
            r.close();
        }
        return mDrivers;
    }

    public String[] getAllDevices() {
        Vector<String> devices = new Vector<>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                for (File file : driver.getDevices()) {
                    String device = file.getName();
                    String value = String.format("%s (%s)", device, driver.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[0]);
    }

    public String[] getAllDevicesPath() {
        Vector<String> devices = new Vector<>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                for (File file : driver.getDevices()) {
                    String device = file.getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[0]);
    }

    public class Driver {

        private String mDriverName;
        private String mDeviceRoot;
        private Vector<File> mDevices = null;

        Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        Vector<File> getDevices() {
            if (mDevices == null) {
                mDevices = new Vector<>();
                try {
                    File dev = new File("/dev");
                    File[] files = dev.listFiles();
                    for (File file : files) {
                        if (file.getAbsolutePath().startsWith(mDeviceRoot)) {
                            Log.d(TAG, "Found new device: " + file);
                            mDevices.add(file);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return mDevices;
        }

        public String getName() {
            return mDriverName;
        }
    }
}
