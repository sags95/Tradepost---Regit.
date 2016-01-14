package com.sinapp.sharathsind.tradepost;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import Model.Item;
import Model.ItemResult;
import Model.Variables;
import webservices.MainWebService;

/**
 * Created by HenryChiang on 2015-12-28.
 */
public class LaunchActivity extends AppCompatActivity {

    Cursor c,c1;
    private InstanceID instanceID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        final Thread launchThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(2000); //Delay of 10 seconds
                    //If internet connection (Verify Model number)

                } catch (InterruptedException e) {
                    Log.d("thread", "is interrupted!" + e.toString());
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isSaved(doesDatabaseExist(new ContextWrapper(getBaseContext()), "tradepostdb.db"));

                        }
                    });
                }
            }
        };

        launchThread.start();

    }


    private void isSaved(Boolean b){
        if (b) {
            //  isDatabaseExist=true;
            try {
                Constants.db = openOrCreateDatabase("tradepostdb.db", MODE_PRIVATE, null);
                c1 =Constants.db.rawQuery("select * from login", null);
                c1.moveToFirst();
                if(c1.getCount()>0) {
                    Constants.userid = c1.getInt(c1.getColumnIndex("userid"));
                    Variables.email = c1.getString(c1.getColumnIndex("email"));
                    Variables.username = c1.getString(c1.getColumnIndex("username"));
                    userdata.name = Variables.username;
                    userdata.userid = Constants.userid;
                }
                else{
                    startActivity(new Intent(LaunchActivity.this, FirstTime.class));
                    finish();
                }
                c = Constants.db.rawQuery("select * from gcm", null);

                if (c.getCount() > 0) {
                    c.moveToFirst();
                    Constants.GCM_Key = c.getString(0);


                    new AsyncTask<String, String, String>() {
                        ProgressDialog pd;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            pd = new ProgressDialog(LaunchActivity.this);
                            pd.setMessage("Loading...");
                            pd.setCancelable(false);
                            pd.show();

                        }

                        @Override
                        protected void onPostExecute(String s) {

                            super.onPostExecute(s);
                            if (c1.getCount() > 0) {
                                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                                c.close();
                                c1.close();
                                finish();
                                //  locationManager.removeUpdates(service);
                            }
                            else{
                                startActivity(new Intent(LaunchActivity.this, FirstTime.class));
                                c.close();
                                c1.close();
                                finish();
                            }
                            pd.dismiss();

                        }

                        @Override
                        protected String doInBackground(String... params) {
                            SharedPreferences prefs =LaunchActivity.this.getSharedPreferences("loctradepost", LaunchActivity.MODE_PRIVATE);
                         float restoredText = prefs.getFloat("lat", 0);
                          float restoredText1 = prefs.getFloat("long", 0);
                            String restoredText2 = prefs.getString("city", null);
                            if(restoredText2!=null) {
                                userdata.mylocation = new UserLocation();
                                userdata.mylocation.city=restoredText2;
                                userdata.mylocation.latitude=restoredText;
                                userdata.mylocation.Longitude=restoredText1;
                            }
                            return null;
                        }
                    }.execute(null, null, null);

                    //URL url = new URL("http://services.tradepost.me:8084/TDserverWeb/images/"+Constants.userid+"/profile.png");
                } else {
                    c.close();
                    startActivity(new Intent(LaunchActivity.this,FirstTime.class));
                    finish();
                }
                //Variables.profilepic = Picasso.with(this).load(Uri.parse("http://services.tradepost.me:8084/TDserverWeb/images/"+Constants.userid+"/profile.png")).get();
                //Constants.username=c.getString(c.getColumnIndex("username"));


            } catch (Exception e) {
                String s = e.toString();
                c.close();
            }


        } else {
            // isDatabaseExist=false;

            try {
                Constants.db = openOrCreateDatabase("tradepostdb.db", MODE_PRIVATE, null);
                try {
                    try {
                        Constants.db.execSQL("Create table IF NOT EXISTS login (" +
                                "  username varchar ," +
                                "  password varchar ," +
                                "  email varchar," +
                                "  itype varchar  ," +
                                "  profilepicture varchar ," +
                                "  emailconfirm varchar ," +
                                "  userid int(10))");
                        Constants.db.execSQL("Create table IF NOT EXISTS LocationPermission (permssion int(10))");
                        Constants.db.execSQL("Create table IF NOT EXISTS Location (latitude DECIMAL(10,5),longutude DECIMAL(10,5) ,city varchar(50))");
                        Constants.db.execSQL("Create table IF NOT EXISTS GCM (gcmkey varchar)");


                        instanceID = InstanceID.getInstance(this);

                        try {
                            new AsyncTask<String, String, String>() {
                                @Override
                                protected String doInBackground(String... params) {
                                    try {
                                        String token = instanceID.getToken("923650940708",
                                                GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                                        Constants.GCM_Key = token;
                                        ContentValues cv = new ContentValues();
                                        cv.put("gcmkey", token);
                                        Constants.db.insert("GCM", null, cv);

                                    } catch (Exception e) {
                                        String s = e.toString();
                                    }
                                    return null;
                                }
                            }.execute(null, null, null);

                        } catch (Exception e) {
                            String s = e.toString();
                        }
                    } catch (Exception e) {
                        String s = e.toString();
                    }
                } catch (Exception e) {
                    String s = e.toString();
                }

            } catch (Exception e) {
                String s = e.toString();
            }

            startActivity(new Intent(getApplicationContext(), FirstTime.class));
            finish();

        }



    }




    public static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}
