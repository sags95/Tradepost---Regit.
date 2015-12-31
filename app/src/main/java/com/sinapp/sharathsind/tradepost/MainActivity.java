package com.sinapp.sharathsind.tradepost;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

        TextView findCommunity = (TextView)findViewById(R.id.community_setBtn);
        findCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item;

        item = menu.add("Settings");
        item.setIcon(R.mipmap.ic_send);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }


    private void showLocationDialog(){
        int dialogLayout = R.layout.layout_location_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);

        final View dialogView = getLayoutInflater().inflate(dialogLayout, null, false);
        final RadioButton rBtnLocScr = (RadioButton) dialogView.findViewById(R.id.radioButton_locService);
        final RadioButton rBtnPosCode = (RadioButton) dialogView.findViewById(R.id.radioButton_postalCode);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (rBtnLocScr.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Location Service", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }else if (rBtnPosCode.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Postal Code", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.setView(getLayoutInflater().inflate(dialogLayout, null, false));

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
//      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        rBtnLocScr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rBtnPosCode.setChecked(!isChecked);
            }
        });

        rBtnPosCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rBtnLocScr.setChecked(!isChecked);
            }
        });



    }


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
