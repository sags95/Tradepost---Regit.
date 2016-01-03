package com.sinapp.sharathsind.tradepost;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
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
Cursor c;
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
                    sleep(5000); //Delay of 10 seconds
                    //If internet connection (Verify Model number)

                } catch (InterruptedException e) {
                    Log.d("thread", "is interrupted!" + e.toString());
                } finally {
                    isSaved(doesDatabaseExist(new ContextWrapper(getBaseContext()), "tradepostdb.db"));
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
                c = Constants.db.rawQuery("select * from login", null);
                c.moveToFirst();
                Constants.userid = c.getInt(c.getColumnIndex("userid"));
                Variables.email = c.getString(c.getColumnIndex("email"));
                Variables.username = c.getString(c.getColumnIndex("username"));
                userdata.name = Variables.username;
                userdata.userid = Constants.userid;

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
                            if (c.getCount() > 0) {
                                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                                c.close();
                                finish();
                                //  locationManager.removeUpdates(service);
                            }
                            pd.dismiss();

                        }

                        @Override
                        protected String doInBackground(String... params) {
                            SoapObject object = new SoapObject("http://webser/", "getuseritems");
                            //SoapObject object = new SoapObject("http://webser/", "getuseritems");
                            object.addProperty("userid", userdata.userid);
                            Vector object1 = MainWebService.getMsg1(object, "http://73.37.238.238:8084/TDserverWeb/Search?wsdl", "http://webser/Search/getuseritemsRequest");
                            userdata.items = new ArrayList<Integer>();


                            if (object1 != null) {
                                for (Object i : object1) {
                                    userdata.items.add(Integer.parseInt(((SoapPrimitive) i).getValue().toString()));
                                }
                            }
                            userdata.i = new ArrayList<ItemResult>();

                            for (int i : userdata.items) {

                                SoapObject obje = new SoapObject("http://webser/", "getItembyId");
                                obje.addProperty("itemid", i);
                                KvmSerializable result1 = MainWebService.getMsg2(obje, "http://73.37.238.238:8084/TDserverWeb/GetItems?wsdl"
                                        , "http://webser/GetItems/getItembyIdRequest");

                                ItemResult ir = new ItemResult();
                                ir.item = new Item();

                                SoapObject object12 = (SoapObject) result1.getProperty(0);
                                //for(int u=0;u<object.getPropertyCount())
                                ir.item.set(object12);
                                //SoapObject o7=(SoapObject)result1;
                                //Object j=       o.getProperty("images");
                                int i1 = result1.getPropertyCount();
                                ir.images = new String[i1 - 1];

                                for (int u1 = 1; u1 < i1; u1++) {
                                    ir.images[u1 - 1] = result1.getProperty(u1).toString();

                                }
                                obje = new SoapObject("http://webser/", "searchbyint");
                                obje.addProperty("name", i);
                                Vector result2 = MainWebService.getMsg1(obje, "http://73.37.238.238:8084/TDserverWeb/NewWebService?wsdl"
                                        , "http://webser/NewWebService/searchbyintRequest");
                                if (result2 != null) {

                                    int index = 0;
                                    ir.tags = new String[result2.size()];

                                    for (Object o : result2) {
                                        ir.tags[index] = ((SoapPrimitive) o).getValue().toString();
                                        index++;

                                    }
                                }

                                userdata.i.add(ir);

                            }
                            return null;
                        }
                    }.execute(null, null, null);

                    //URL url = new URL("http://73.37.238.238:8084/TDserverWeb/images/"+Constants.userid+"/profile.png");
                } else {

                }
                //Variables.profilepic = Picasso.with(this).load(Uri.parse("http://73.37.238.238:8084/TDserverWeb/images/"+Constants.userid+"/profile.png")).get();
                //Constants.username=c.getString(c.getColumnIndex("username"));


            } catch (Exception e) {
                String s = e.toString();
            }


        } else {
            // isDatabaseExist=false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final ImageView launchImg = (ImageView) findViewById(R.id.launch_img);
                    final Button testBtn = (Button) findViewById(R.id.launch_testBtn);
                    testBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
                    testBtn.setVisibility(View.VISIBLE);
                    launchImg.setVisibility(View.GONE);

                }
            });


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


        }

    }




    public static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}
