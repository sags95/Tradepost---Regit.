package com.sinapp.sharathsind.tradepost;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


import Model.RegisterWebService;
import Model.Variables;

public class FbFragment extends Fragment {

    public static FirstTime f;
    public static CallbackManager b;

    public FbFragment() {
f= (FirstTime) getActivity();
    }

long start,end;
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.activity_first_time, container, false);
        Button authButton = (Button) view.findViewById(R.id.fb_sign_in_btn);
authButton.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
         start = System.currentTimeMillis();

        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));
    }

});
       // authButton.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.fb));
      //  authButton.setBackgroundResource(R.drawable.fb);

authButton.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.mipmap.sign_in_fb));
      //  authButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        b = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(b, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code


                           //     Toast.makeText(getActivity(),"fblogin"+totalTime,Toast.LENGTH_LONG).show();
                                JSONObject c = response.getJSONObject();
                                c = object;
                                try {
                                    Variables.email = (String) c.getString("email");
                                    Variables.id = (String) c.getString("id");
                                    Variables.username = (String) c.getString("name");
                                    URL imageURL = null;
                                    try {
                                        imageURL = new URL("https://graph.facebook.com/" + Variables.id + "/picture?type=large");
                                    } catch (MalformedURLException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    try {
                                        Variables.profilepic = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                                        // bitmap = Variables.profilepic;
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    new AsyncTask<String, String, String>() {
                                        ContentValues cv;
                                        SQLiteDatabase myDB;
ProgressDialog pd;
                                        @Override
                                        protected void onPostExecute(String s) {
                                            super.onPostExecute(s);
                                            long l = Constants.db.insert("login", null, cv);
                                            pd.dismiss();
                                            if(f==null)
                                            {
                                                f =(FirstTime)getActivity();
                                            }

                                            startActivity(new Intent(getActivity(), WelcomeActivity.class));
                                           getActivity(). finish();

                                        }

                                        @Override
                                        protected void onPreExecute() {
pd=ProgressDialog.show(getActivity(),"Please Wait","signing in",true,false);

                                            super.onPreExecute();

                                        }

                                        @Override
                                        protected void onProgressUpdate(String... values) {
                                            super.onProgressUpdate(values);
                                            Toast.makeText(getActivity(),values[0],Toast.LENGTH_LONG).show();

                                        }

                                        @Override
                                        protected String doInBackground(String... params) {
                                            long totalTime = end - start;
                                        //  publishProgress("start"+totalTime);
                                            cv = RegisterWebService.signUp(Variables.username, Variables.email, " ", "fb", Variables.profilepic, true, Constants.db);
                                             totalTime = end - start;
                                      //      publishProgress("start"+totalTime);
                                            return "";

                                        }
                                    }.execute(null, null, null);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Tracker t =
                                            ((TradePost) getActivity().getApplication()).getDefaultTracker();

// Build and send exception.
                                    t.send(new HitBuilders.ExceptionBuilder()
                                            .setDescription(e.getMessage() + ":" + e.toString())

                                            .build());
                                }

                            }

                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name,id");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        Button google = (Button) view.findViewById(R.id.google_sign_in_btn);
        google.setOnClickListener((FirstTime)getActivity());
google.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.mipmap.sign_in_gplus));

        return view;
    }


    private void startIntentSenderForResult(IntentSender intentSender,
                                            int rcSignIn, Object object, int i, int j, int k) throws SendIntentException {
        // TODO Auto-generated method stub

    }

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */


    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;




}
