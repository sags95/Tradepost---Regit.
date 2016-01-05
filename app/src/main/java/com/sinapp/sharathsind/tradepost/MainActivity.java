package com.sinapp.sharathsind.tradepost;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.plus.Plus;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import CustomWidget.CustomTextView;
import Model.Variables;
import webservices.MainWebService;

/**
 * Created by HenryChiang on 2015-12-28.
 */
public class MainActivity extends AppCompatActivity implements LocationListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
public GoogleApiClient mGoogleApiClient;
    boolean restoredText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        SharedPreferences prefs =this.getSharedPreferences("loctradepost", LaunchActivity.MODE_PRIVATE);
         restoredText = prefs.getBoolean("done", false);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Permission per=new Permission(MainActivity.this,locationManager);
        if(per.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED||per.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            per.askPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},7);

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customToolBarTitle = getLayoutInflater().inflate(R.layout.layout_toolbar_custom_title, null);
        getSupportActionBar().setCustomView(customToolBarTitle);
        CustomTextView name = (CustomTextView)customToolBarTitle.findViewById(R.id.toolbar_title2);

        TextView addItem = (TextView) findViewById(R.id.add_item_addBtn);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restoredText)
                startActivity(new Intent(getApplicationContext(), ListingProcessActivity.class));
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Please Setup yoour comunity first ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                        }
                    });
                }

            }
        });


        TextView findCommunity = (TextView)findViewById(R.id.community_setBtn);
        findCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //Ask the user to enable GPS
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Location Manager");
                    builder.setMessage("Please Enable the gps ");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Launch settings, allowing user to make a change
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                            dialog.cancel();
                        }
                    });

                    builder.create().show();
                }
                else
                {
                    locationService();
                }


            }
        });

        CustomTextView viewItem = (CustomTextView)findViewById(R.id.add_item_viewBtn);
        viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
 if(restoredText)
                startActivity(new Intent(getApplicationContext(),MyItemActivity.class));
 else
 {
     AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
     builder.setTitle("Alert");
     builder.setMessage("Please Setup yoour comunity first ");
     builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {


         }
     });
 }
            }
        });

    }
    Snackbar s;
    public void signOut()
    {
        Constants.db=openOrCreateDatabase("tradepostdb.db", MODE_PRIVATE, null);
        Cursor c=Constants.db.rawQuery("select * from login",null);
        c.moveToFirst();
        if(c.getString(c.getColumnIndex("itype")).equals("fb")) {

            LoginManager l = LoginManager.getInstance();
            l.logOut();


        }
        else if(c.getString(c.getColumnIndex("itype")).equals("g+")){
            mGoogleApiClient.disconnect();

        }
        try {
            InstanceID.getInstance(this).deleteInstanceID();
        } catch (IOException e) {
            e.printStackTrace();
        }
        c.close();
        Constants.db.close();
        deleteDatabase("tradepostdb.db");
        startActivity(new Intent(this, LaunchActivity.class));


      //  getActivity().finish();


    }
public void locationService()
{
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Permission per=new Permission(MainActivity.this,locationManager);

            if (per.isPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION) == 0 && per.isPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION) == 0) {


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            userdata.mylocation = new UserLocation();

            userdata.mylocation.Longitude = (float) location.getLongitude();
            userdata.mylocation.latitude = (float) location.getLatitude();
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(userdata.mylocation.latitude, userdata.mylocation.Longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
            userdata.mylocation.city = cityName + "," + stateName;

            SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("loctradepost", MainActivity.this.MODE_PRIVATE).edit();
            //editor.putInt("rad", radius);
            editor.putFloat("lat", userdata.mylocation.latitude);
            editor.putFloat("long", userdata.mylocation.Longitude);
            editor.putBoolean("done",true);
            editor.commit();
            SoapObject soapObject = new SoapObject("http://webser/", "setLogin");
            soapObject.addProperty("userid", Constants.userid);
            soapObject.addProperty("lat", String.format("%.2f", userdata.mylocation.latitude));

restoredText=true;
            //object.addProperty("tags",tag);
            soapObject.addProperty("longi", String.format("%.2f", userdata.mylocation.Longitude));
            soapObject.addProperty("city", userdata.mylocation.city);
            SoapPrimitive msg = MainWebService.getMsg(soapObject, "http://services.tradepost.me:8084/TDserverWeb/NewWebServi?wsdl", "http://webser/NewWebServi/setLoginRequest");


        }
                else {
            Toast.makeText(this,"please check your internet connection ",Toast.LENGTH_LONG).show();
        }
    }
else    if (per.isPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION) == 1 && per.isPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION) == 1) {
                per.askPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);


            }
        else {
        s=   Snackbar.make(findViewById(android.R.id.content), "App needs Permission to access your external storage", Snackbar.LENGTH_LONG)
                .setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


s.dismiss();
                    }
                })
                .setActionTextColor(Color.RED);
              s.show();

    }

}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        MenuItem shareOption;
//
//        shareOption = menu.add("Share");
//
//        shareOption.setIcon(R.mipmap.ic_share_white_24dp);
//        MenuItemCompat.setShowAsAction(shareOption, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
//
//        SubMenu submenu = menu.addSubMenu(0, Menu.NONE, 1, "More").setIcon(R.mipmap.ic_more_vert_white_24dp);
//        submenu.add("Settings").setIcon(R.mipmap.ic_share_white_24dp);
//        submenu.add("Log Out").setIcon(R.mipmap.ic_share_white_24dp);

        getMenuInflater().inflate(R.menu.menu_item, menu);




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
//                SoapPrimitive msg = MainWebService.getMsg(soapObject, "http://services.tradepost.me:8084/TDserverWeb/NewWebServi?wsdl", "http://webser/NewWebServi/setLoginRequest");
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
