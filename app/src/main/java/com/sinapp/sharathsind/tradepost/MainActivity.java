package com.sinapp.sharathsind.tradepost;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
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

import CustomWidget.CustomClickableTextView;
import CustomWidget.CustomTextView;
import Model.Variables;
import webservices.MainWebService;

/**
 * Created by HenryChiang on 2015-12-28.
 */
public class MainActivity extends AppCompatActivity implements LocationListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
public GoogleApiClient mGoogleApiClient;
    boolean restoredText;
    private boolean canGetLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());

        SharedPreferences prefs =this.getSharedPreferences("loctradepost", LaunchActivity.MODE_PRIVATE);
        restoredText = prefs.getBoolean("done", false);

        if(!restoredText){
            CardView addItemCard = (CardView) findViewById(R.id.add_item_card_view);
            addItemCard.setVisibility(View.GONE);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customToolBarTitle = getLayoutInflater().inflate(R.layout.layout_toolbar_custom_title, null);
        getSupportActionBar().setCustomView(customToolBarTitle);
        CustomTextView title1 = (CustomTextView)customToolBarTitle.findViewById(R.id.toolbar_title1);
        CustomTextView title2 = (CustomTextView)customToolBarTitle.findViewById(R.id.toolbar_title2);
        title1.setText("Get Set up");
        title2.setVisibility(View.GONE);


        CustomClickableTextView addItem = (CustomClickableTextView) findViewById(R.id.add_item_addBtn);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restoredText)
                startActivity(new Intent(getApplicationContext(), ListingProcessActivity.class));
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Please Setup Your Community First ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    builder.create().show();
                }

            }
        });


        CustomClickableTextView findCommunity = (CustomClickableTextView)findViewById(R.id.community_setBtn);
        findCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
                } else {
                    locationService();
                }
            }
        });

        CustomClickableTextView viewItem = (CustomClickableTextView)findViewById(R.id.add_item_viewBtn);
        viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(restoredText)
                    startActivity(new Intent(getApplicationContext(),MyItemActivity.class));

                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Please Setup Your Community First ");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                }
            }
        });

        final Activity a = this;
        FloatingActionButton shareFab = (FloatingActionButton)findViewById(R.id.main_shareFab);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants c = new Constants();
                c.shareTextUrl(a);
            }
        });

    }
    Snackbar s;
    public void signOut()
    {


new AsyncTask<Void, Void,Void>(){
    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
        startActivity(new Intent(MainActivity.this, LaunchActivity.class));
    }

    @Override
    protected Void doInBackground(Void... params) {
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
            InstanceID.getInstance(MainActivity.this).deleteInstanceID();
        } catch (IOException e) {
            e.printStackTrace();
        }
        c.close();
        Constants.db.close();
        deleteDatabase("tradepostdb.db");
        return null;
    }
}.execute();

      //  getActivity().finish();


    }
    LocationManager locationManager;
    Location    location;
    public Location getLocation1() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
           boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
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
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0,
                            0, this);
                 //   Log.d("Network", "Network Enabled");if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                         //   latitude = location.getLatitude();
                          //  longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                            0,
                                0, this);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {

                              //  latitude = location.getLatitude();
                               // longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

         catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
    public boolean getLocation()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location =getLocation1();

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
            userdata.city=cityName;
            SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("loctradepost", MainActivity.this.MODE_PRIVATE).edit();
            //editor.putInt("rad", radius);
            editor.putFloat("lat", userdata.mylocation.latitude);
            editor.putFloat("long", userdata.mylocation.Longitude);
            editor.putString("city",userdata.mylocation.city);
            editor.putBoolean("done", true);
            boolean b=   editor.commit();
            SoapObject soapObject = new SoapObject("http://webser/", "setLogin");
            soapObject.addProperty("userid", Constants.userid);
            soapObject.addProperty("lat", String.format("%.2f", userdata.mylocation.latitude));

            restoredText=true;
            //object.addProperty("tags",tag);
            soapObject.addProperty("longi", String.format("%.2f", userdata.mylocation.Longitude));
            soapObject.addProperty("city", userdata.mylocation.city);

            SoapPrimitive msg = MainWebService.getMsg(soapObject, "http://services.tradepost.me:8084/TDserverWeb/NewWebServi?wsdl", "http://webser/NewWebServi/setLoginRequest");

            if(msg!=null) {
                return  true;
            } else {
                return false;
            }
        } else {

            return false;
        }
    }

    public void locationService() {
        int count;
        Cursor c=Constants.db.rawQuery("select * from LocationPermission",null);
        c.moveToFirst();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Permission per=new Permission(MainActivity.this,locationManager);


        if (per.isPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION) == 0 && per.isPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION) == 0) {

            new AsyncTask<Boolean,Boolean,Boolean>() {

                ProgressDialog pd;

                @Override

                protected void onPreExecute() {
                    super.onPreExecute();
                    pd=ProgressDialog.show(MainActivity.this,"Finding","Looking for the closest community...");
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    pd.hide();
                    if(aBoolean) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Message");
                        builder.setMessage("You have been added to " + userdata.city + " community. Don't worry, you'll be able to see trades from surrounding areas as well.");
                        builder.setPositiveButton("Sounds Good", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Launch settings, allowing user to make a change

                                dialog.cancel();
                            }
                        }).show();
                        CardView addItemCard = (CardView) findViewById(R.id.add_item_card_view);
                        addItemCard.setVisibility(View.VISIBLE);
                    }
                }


                @Override
                protected Boolean doInBackground(Boolean... booleans) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getLocation();
                        }
                    });
                    return true;
                }
            }.execute();

        } else if (c.getCount()==0||(per.isPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION) == 1 && per.isPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION) == 1)) {
                    per.askPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);

        } else {
            s = Snackbar.make(findViewById(android.R.id.content), "App needs Permission to access your location", Snackbar.LENGTH_LONG).setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            s.dismiss();
                        }
                    }).setActionTextColor(Color.RED);
            s.show();

        }

        c.close();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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
            case "Log Out":
                signOut();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 0:
                int count;
                Cursor c=Constants.db.rawQuery("select * from LocationPermission",null);
                c.moveToFirst();
                if(c.getCount()>0)
                {
                    count  =c.getInt(c.getColumnIndex("permssion"));
                    count++;

                }
                else
                {
                    count=1;
                }
                c.close();
                ContentValues cv=new ContentValues();
                cv.put("permssion",count);
                Constants.db.insert("LocationPermission", null, cv);

                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED&& grantResults.length>0&&grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
              /*     // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Welcome.service);
                   // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Welcome.service);
                    Criteria criteria = new Criteria();
                    String provider = locationManager.getBestProvider(criteria, false);
                    Location location = locationManager.getLastKnownLocation(provider);

                    if(location!=null) {
                        userdata.mylocation=new UserLocation();

                        userdata.mylocation.Longitude =(float) location.getLongitude();
                        userdata.mylocation.latitude = (float)location.getLatitude();
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(userdata.mylocation.latitude, userdata.mylocation.Longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String cityName = addresses.get(0).getLocality();
                        String stateName = addresses.get(0).getAdminArea();
                        userdata.mylocation.city=cityName+","+stateName;

                        SharedPreferences.Editor editor = context.getSharedPreferences("loctradepost", context.MODE_PRIVATE).edit();
                        //editor.putInt("rad", radius);
                        editor.putFloat("lat", userdata.mylocation.latitude);
                        editor.putFloat("long",userdata. mylocation.Longitude);
                        editor.putBoolean("done",true);
                    boolean b=    editor.commit();
                        SoapObject soapObject =new SoapObject("http://webser/","setLogin");
                        soapObject.addProperty("userid",Constants.userid);
                        soapObject.addProperty("lat", String.format("%.2f", userdata.mylocation.latitude));


                        //object.addProperty("tags",tag);
                        soapObject.addProperty("longi", String.format("%.2f", userdata.mylocation.Longitude));
                        soapObject.addProperty("city", userdata.mylocation.city);
                        SoapPrimitive msg= MainWebService.getMsg(soapObject, "http://services.tradepost.me:8084/TDserverWeb/NewWebServi?wsdl", "http://webser/NewWebServi/setLoginRequest");


                        break;
                    }*/
                 locationService();
                    break;

                }
                else{

                    break;
                }







        }

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
        userdata.city=cityName;
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
