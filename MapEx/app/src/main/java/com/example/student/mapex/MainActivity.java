package com.example.student.mapex;

import android.app.FragmentManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView tv_result;
    EditText et_lng, et_lat, et_addr;
    Button btn_geo, btn_revGeo;
    GoogleMap googleMap;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        tv_result = (TextView)findViewById(R.id.tv_result);
        et_addr = (EditText)findViewById(R.id.et_addr);
        et_lat = (EditText)findViewById(R.id.et_lat);
        et_lng = (EditText)findViewById(R.id.et_lng);
        btn_geo = (Button)findViewById(R.id.btn_geo);
        btn_revGeo = (Button)findViewById(R.id.btn_revGeo);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_geo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GeoThread geoThread = new GeoThread(et_addr.getText().toString());
                geoThread.start();
            }
        });

        btn_revGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = new LatLng(Double.valueOf(et_lng.getText().toString()), Double.valueOf(et_lat.getText().toString()));
                RevGeoThread revGeoThread = new RevGeoThread(latLng);
                revGeoThread.start();
            }
        });


    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        ArrayList<LocationVO> list = new ArrayList<>();
        list.add(new LocationVO(37.5866076, 126.9726223, "청와대", "청와대 주소"));
        list.add(new LocationVO(33.510418, 126.4891647, "제주공항", "제주공항주소"));
        list.add(new LocationVO(37.4750619, 127.0483868, "멀티캠퍼스", "멀티캠퍼스 주소"));

        MarkerOptions markerOptions = new MarkerOptions();
        for (int i = 0; i < list.size(); i++) {
            LatLng SEOUL = new LatLng(list.get(i).getLat(), list.get(i).getLng());
            markerOptions.position(SEOUL);
            markerOptions.title(list.get(i).getName());
            markerOptions.title(list.get(i).getAddress());
            googleMap.addMarker(markerOptions);
        }

        LatLng seoul = new LatLng(37.56, 126.97);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

       /* LatLng cheong = new LatLng(37.58, 126.97);
        LatLng jejuAir = new LatLng(33.510418, 126.4891647);
        LatLng multiCam = new LatLng(37.4750619, 127.0483868);

        final LatLng list[] = {cheong, jejuAir, multiCam};

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<list.length; i++) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(list[i]));

                    if (i == list.length - 1) {
                        i =0;
                    }
                }
            }
        });*/
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {//geo if true
                LatLng latLng = (LatLng)msg.obj;
                Toast.makeText(MainActivity.this, "lat : " + latLng.latitude + " lng : " + latLng.longitude, Toast.LENGTH_LONG).show();
            } else if (msg.what == 2) {//get if false
                Toast.makeText(MainActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {//revgeo if true
                Address pin = (Address)msg.obj;
                Toast.makeText(getApplicationContext(), "주소 : "+pin, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {//revgeo false
                Toast.makeText(getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
            }
        }
    };



    class RevGeoThread extends Thread {
        LatLng latLng;

        public RevGeoThread(LatLng latLng) {
            this.latLng = latLng;
        }

        @Override
        public void run() {
            Geocoder geocoder = new Geocoder(MainActivity.this);
            List<Address> addr = null;
            Message msg = new Message();
            try {
                addr = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addr != null && addr.size() > 0) {
                    Address pin = addr.get(0);

                    msg.what =3;
                    msg.obj = pin;
                    handler.sendMessage(msg);
                    //Toast.makeText(getApplicationContext(), "주소 : "+pin, Toast.LENGTH_SHORT).show();
                } else {
                    msg.what=4;
                    handler.sendMessage(msg);
                    //Toast.makeText(getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class GeoThread extends Thread {
        String address;

        public GeoThread(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> result = null;
            try {
                result = geocoder.getFromLocationName(address, 1);
                Message msg = new Message();
                if (result != null && result.size() > 0) {
                    Address pin = result.get(0);
                    LatLng latLng = new LatLng(pin.getLatitude(), pin.getLongitude());

                    msg.what = 1;
                    msg.obj = latLng;
                    handler.sendMessage(msg);

                    //Toast.makeText(MainActivity.this, "lat : " + latLng.latitude + " lng : " + latLng.longitude, Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(MainActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
                    msg.what = 2;
                    handler.sendMessage(msg);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
