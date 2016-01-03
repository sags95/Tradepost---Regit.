package com.sinapp.sharathsind.tradepost;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;



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
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED&& checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                {
                   // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Welcome.service);
                   // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Welcome.service);
                    Criteria criteria = new Criteria();
                    String provider = locationManager.getBestProvider(criteria, false);
                    Location location = locationManager.getLastKnownLocation(provider);

                    if(location!=null) {
                        userdata.mylocation=new UserLocation();

                        userdata.mylocation.Longitude =(float) location.getLongitude();
                        userdata.mylocation.latitude = (float)location.getLatitude();
                        SharedPreferences.Editor editor = context.getSharedPreferences("loctradepost", context.MODE_PRIVATE).edit();
                        //editor.putInt("rad", radius);
                        editor.putFloat("lat", userdata.mylocation.latitude);
                        editor.putFloat("long",userdata. mylocation.Longitude);
                        editor.commit();



                    }

                }
                else{


                }







        }

    }
}
