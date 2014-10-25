package pl.kroljakub.EBOLOID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.kontakt.sdk.android.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.configuration.MonitorPeriod;
import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.Beacon;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.manager.BeaconManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class EboloidService extends Service {

    BeaconManager beaconManager;

    Intent intent;

    MyBeacons beacons = null;

    String mPhoneNumber;
    String IMEI;

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {


        intent = i;

        beacons = new MyBeacons();

        beaconManager = BeaconManager.newInstance(this);
        MonitorPeriod mp = new MonitorPeriod(10000, 5000);
        beaconManager.setMonitorPeriod(mp);
        beaconManager.setForceScanConfiguration(ForceScanConfiguration.DEFAULT);


        beaconManager.registerMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onMonitorStart() {

            }

            @Override
            public void onMonitorStop() {

            }

            @Override
            public void onBeaconsUpdated(Region region, List<Beacon> bcs) {
                for(Beacon b : bcs) {
                    Log.d("UPDATE", b.getName());
                    beacons.UpdateBeacon(b);
                }
            }

            @Override
            public void onBeaconAppeared(Region region, Beacon b) {
                Log.d("NEW", b.getName());
                beacons.UpdateBeacon(b);
            }

            @Override
            public void onRegionEntered(Region region) {

            }

            @Override
            public void onRegionAbandoned(Region region) {

            }
        });

        try {
            Log.d("CONNECT", "MAYBE");
            connect();
        }
        catch(Exception e) {
            Log.e("MainActivity->onStart", e.toString());
        }

        mPhoneNumber = "tmp";//intent.getStringExtra("number");
        IMEI = "tmp";//ntent.getStringExtra("imei");


        Log.d("START", "START");

        new Timer().schedule(new TimerTask() {
                                 @Override
                                 public void run() {
                                     //Log.d("SERVICE", "DZIALAM");

                                     for(MyBeacon b : beacons.beacons) {
                                         if(b.Visible() && b.Valid())
                                             Log.d("BEACON", b.toString());
                                     }


                                     String url ="http://kroljakub.pl/hack/update.php?phone=" + mPhoneNumber + "&imei=" + IMEI + "&gpslocationx=10&gpslocationy=20&beacon=dupa";


                                     try {
                                         new SendData().execute(url);
                                         //sleep(500);
                                     }
                                     catch(Exception e) {
                                         Log.e("TO SIE WYJEBALO", e.toString());
                                     }
                                 }
                             },
                10,
                1000*10);

        Log.v("DUPA", "KUPA");

        return START_STICKY;
    }

    private void connect() {
        try {
            Log.d("JEBNIE?", "CZY NIE JEBNIE?");
            OnServiceBoundListener onServiceBoundListener = new OnServiceBoundListener() {
                @Override
                public void onServiceBound() throws RemoteException {

                }
            };
            Log.d("JEBNIE?", "CZY NIE JEBNIE?");

            beaconManager.connect(onServiceBoundListener);
        }catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {



        return null;
    }

}
