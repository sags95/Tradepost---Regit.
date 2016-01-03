package com.sinapp.sharathsind.tradepost;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import webservices.MainWebService;

/**
 * Created by HenryChiang on 2015-12-28.
 */
public class MainActivity extends AppCompatActivity implements LocationListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Welcome Back Sample User!");
        setSupportActionBar(toolbar);

        TextView addItem = (TextView)findViewById(R.id.add_item_addBtn);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ListingProcessActivity.class));
            }
        });
      LocationManager  locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null) {
            userdata.mylocation=new UserLocation();

            userdata.mylocation.Longitude =(float) location.getLongitude();
            userdata.mylocation.latitude = (float)location.getLatitude();
          //  userdata.mylocation.city=cityName+","+stateName;
            SharedPreferences.Editor editor = getSharedPreferences("loctradepost", MODE_PRIVATE).edit();
            //editor.putInt("rad", radius);
            editor.putFloat("lat", userdata.mylocation.latitude);
            editor.putFloat("long",userdata. mylocation.Longitude);
            editor.commit();



        }

        TextView findCommunity = (TextView)findViewById(R.id.community_setBtn);
        findCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //permission
            }
        });

        TextView viewItem = (TextView)findViewById(R.id.add_item_viewBtn);
        viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MyItemActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item;

        item = menu.add("Share");

        item.setIcon(R.mipmap.ic_send);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getTitle().toString()) {

            case "Share":
                Constants c = new Constants();
                c.shareTextUrl(this);
                break;
        }

        return true;
    }


//    private void showLocationDialog(){
//        int dialogLayout = R.layout.layout_location_dialog;
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
//
//        final View dialogView = getLayoutInflater().inflate(dialogLayout, null, false);
//        final RadioButton rBtnLocScr = (RadioButton) dialogView.findViewById(R.id.radioButton_locService);
//        final RadioButton rBtnPosCode = (RadioButton) dialogView.findViewById(R.id.radioButton_postalCode);
//
//        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                SoapObject soapObject = new SoapObject("http://webser/", "setLogin");
//                soapObject.addProperty("userid", Constants.userid);
//                soapObject.addProperty("lat", String.format("%.2f", userdata.mylocation.latitude));
//
//                SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("loctradepost", MainActivity.this.MODE_PRIVATE).edit();
//                //editor.putInt("rad", radius);
//                editor.putFloat("lat", userdata.mylocation.latitude);
//                editor.putFloat("long", userdata.mylocation.Longitude);
//                editor.putString("city",userdata.mylocation.city);
//                editor.commit();
//                //object.addProperty("tags",tag);
//                soapObject.addProperty("longi", String.format("%.2f", userdata.mylocation.Longitude));
//                soapObject.addProperty("city", userdata.mylocation.city);
//                SoapPrimitive msg = MainWebService.getMsg(soapObject, "http://73.37.238.238:8084/TDserverWeb/NewWebServi?wsdl", "http://webser/NewWebServi/setLoginRequest");
//            }
//        });
//
////        builder.setNegativeButton("Cancel", null);
//        builder.setView(getLayoutInflater().inflate(dialogLayout, null, false));
//
//        AlertDialog dialog = builder.create();
//        dialog.setCancelable(false);
//        dialog.show();
////      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
//
//        rBtnLocScr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                rBtnPosCode.setChecked(!isChecked);
//            }
//        });
//
//        rBtnPosCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                rBtnLocScr.setChecked(!isChecked);
//            }
//        });
//
//
//
//    }


    @Override
    public void onLocationChanged(Location location) {
        userdata.mylocation=new UserLocation();
        userdata.mylocation.Longitude=(float) location.getLongitude();

        userdata.mylocation.latitude = (float) location.getLatitude();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(userdata.mylocation.latitude, userdata.mylocation.Longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getLocality();
        String stateName = addresses.get(0).getAdminArea();
userdata.mylocation.city=cityName+","+stateName;
     //   customTextView.setText(cityName + "," + stateName);
       // AsyncTaskRunner runner = new AsyncTaskRunner();
        //runner.execute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
