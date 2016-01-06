package com.sinapp.sharathsind.tradepost;

import android.*;
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.net.URL;

import Model.RegisterWebService;
import Model.Variables;


public class FirstTime extends FragmentActivity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {case 0:
            if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                if (!mGoogleApiClient.isConnecting()) {
                    mSignInClicked = true;
                    mGoogleApiClient.connect();
                }


            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
  //      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new MyLocationService(this));Pe
        Permission per=new Permission(this,null);
        if(per.checkPermission(Manifest.permission.GET_ACCOUNTS)!= PackageManager.PERMISSION_GRANTED)
        {
            per.askPermission(new String[]{Manifest.permission.GET_ACCOUNTS},3);
        }
        FbFragment mainFragment;
        FbFragment.f = this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((ConnectionCallbacks) this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)    .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new FbFragment(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (FbFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        } else {
            FbFragment.b.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void start() {
        startActivity(new Intent(FirstTime.this, WelcomeActivity.class));
        finish();
    }

    private static final int PROFILE_PIC_SIZE = 400;
    private static final int RC_SIGN_IN = 0;


    // Google client to interact with Google API
    public static GoogleApiClient mGoogleApiClient;


    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;
Snackbar s;
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Permission per= new Permission(this,null);
        if (per.isPermissionDenied(Manifest.permission.GET_ACCOUNTS) == 0 ) {
            if (!mGoogleApiClient.isConnecting()) {
                mSignInClicked = true;
                mGoogleApiClient.connect();
            }

        }
        else    if ((per.isPermissionDenied(Manifest.permission.GET_ACCOUNTS) == 1 )) {
            per.askPermission(new  String[]{Manifest.permission.GET_ACCOUNTS},0);
        }
        else
        {
            s=   Snackbar.make(findViewById(android.R.id.content), "App needs Permission to access your Account", Snackbar.LENGTH_LONG)
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
    public void onConnectionFailed(ConnectionResult result) {
        // TODO Auto-generated method stub
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            Variables.username = personName;
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Variables.email = email;
            String personPhoto = currentPerson.getImage().getUrl();
            try {
                URL imageURL = new URL(personPhoto);
                Variables.profilepic = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String personGooglePlusProfile = currentPerson.getUrl();
            new AsyncTask<String,String,String>()
            {
                SQLiteDatabase myDB;
                ContentValues cv;
                @Override
                protected void onPostExecute(String s) {

                    super.onPostExecute(s);
                    Constants.db.insert("login",null,cv);
                    start();
                }

                @Override
                protected void onPreExecute() {


                    super.onPreExecute();

                }

                @Override
                protected String doInBackground(String... params) {

                 cv=   RegisterWebService.signUp(Variables.username, Variables.email, " ", "g+", Variables.profilepic, true, Constants.db);
                    return " ";

                }
            }.execute(null,null,null);

            start();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
