package com.sinapp.sharathsind.tradepost;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import webservices.MainWebService;


/**
 * Created by sharathsind on 2015-10-29.
 */
public class Permission implements  ActivityCompat.OnRequestPermissionsResultCallback {

    Activity context;

    LocationManager locationManager;

    Permission(Activity c, LocationManager l) {
    context=c;
    locationManager=l;
    }
    public  int isPermissionDenied(String permision) {
        int i = 0;
        boolean b = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            b = this.context.shouldShowRequestPermissionRationale(permision);
        } else
            return 0;
        if (checkPermission(permision) == PackageManager.PERMISSION_GRANTED)
            return 0;

        if (!b)
            return 2;
        else return 1;


    }



    public int checkPermission( String permissions) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, permissions);
        return permissionCheck;

    }

    public void askPermission(String permission[],int MY_PERMISSIONS_REQUEST_READ_CONTACTS){
        ActivityCompat.requestPermissions(context, permission, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                Constants.db.insert("LocationPermission",null,cv);

                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED&& checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
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
                    ( (MainActivity)context).locationService();
                    break;

                }
                else{

break;
                }
            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED&& checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                {
                    if(context instanceof ListingProcessActivity)
                    ( (ListingProcessActivity)context).camera();
                    else
                        ((EditListingActivity)context).camera();

                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED&& checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                {
                    if(context instanceof ListingProcessActivity)
                    ( (ListingProcessActivity)context).gallery();
                    else
                        ((EditListingActivity)context).gallery();

                }






        }

    }
}
