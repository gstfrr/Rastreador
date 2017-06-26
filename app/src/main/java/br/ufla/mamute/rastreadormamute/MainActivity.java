package br.ufla.mamute.rastreadormamute;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button b;
    private TextView t;
    private LocationManager locationManager;
    private LocationListener listener_GPS;
    private LocationListener listener_NETWORK;
    private static String locus;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        t = (TextView) findViewById(R.id.textView);
        b = (Button) findViewById(R.id.button);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

//        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) t.append("\nGPS OK\n");
//        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) t.append("\nNETWORK OK\n");


        listener_GPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                locus = (sdf.format(new Date()) + " " + location.getLatitude() + " " + location.getLongitude());

//                try {
//                    udpmsg(locus);
//                    locus = "M " + locus;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                t.append("\nG " + locus);
                //t.setText(locus);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };


        listener_NETWORK = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                locus = (sdf.format(new Date()) + " " + location.getLatitude() + " " + location.getLongitude());

//                try {
//                    udpmsg(locus);
//                    locus = "M " + locus;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                t.append("\nN " + locus);
                //t.setText(locus);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, listener_GPS);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 0, listener_NETWORK);

            }
        });
    }


    public void udpmsg(String text) throws IOException {
        String address = "200.131.250.1";
        int port=6666;

        InetAddress host = InetAddress.getByName(address);

        byte[] data = text.getBytes();
        DatagramPacket pac = new DatagramPacket(data, data.length, host, port);
        DatagramSocket soc = new DatagramSocket();
        soc.send(pac);

    }
}
