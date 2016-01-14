package com.sinapp.sharathsind.tradepost;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import CustomWidget.CustomClickableTextView;
import CustomWidget.CustomTextView;
import Model.Variables;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HenryChiang on 2016-01-05.
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customToolBarTitle = getLayoutInflater().inflate(R.layout.layout_toolbar_custom_title, null);
        getSupportActionBar().setCustomView(customToolBarTitle);
        CustomTextView title2 = (CustomTextView)customToolBarTitle.findViewById(R.id.toolbar_title2);
        title2.setVisibility(View.GONE);

        final CustomClickableTextView start = (CustomClickableTextView)findViewById(R.id.welcome_startBtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        CircleImageView profileImg = (CircleImageView)findViewById(R.id.welcome_userImg);
        Picasso.with(this).load(Uri.parse("http://services.tradepost.me:8084/TDserverWeb/images/" + userdata.userid + "/profile.png")).into(profileImg);

        CustomTextView welcomeName = (CustomTextView)findViewById(R.id.welcome_username);
        String[] username = Variables.username.split("\\s+");
        welcomeName.setText("Welcome " + username[0] + "!");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem shareOption;

        shareOption = menu.add("Share");

        shareOption.setIcon(R.mipmap.ic_share_white_24dp);
        MenuItemCompat.setShowAsAction(shareOption, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

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



}
